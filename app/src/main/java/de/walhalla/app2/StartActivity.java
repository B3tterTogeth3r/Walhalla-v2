package de.walhalla.app2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Semester;
import de.walhalla.app2.utils.Variables;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";
    private final int totalAsks = 8;
    private final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
    private float downloadProgress = 0f;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        progressBar = findViewById(R.id.start_progressBar);
        Log.i(TAG, "onCreate: shield should now be shown");
        // [START Initialize SharedPreferences]
        Variables.SHARED_PREFERENCES = getSharedPreferences(Variables.SHARED_PREFERENCES_PATH_DEFAULT, MODE_PRIVATE);
        // [END Initialize SharedPreferences]

        // [START update Variables with Firebase Remote Config]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("current_semester_id", 316);
        remoteConfig.setDefaultsAsync(defaults);
        remoteConfig.fetch()
                .addOnSuccessListener(this, unused -> {
                    remoteConfig.activate();
                    updateProgressbar();
                });
        // [END update Variables with Firebase Remote Config]

        // [START get current FCM token]
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt) + ": " + token;
                    Log.d(TAG, "onCreate: " + msg);
                    //Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();

                    if (!Variables.SHARED_PREFERENCES.contains("Messaging")) {
                        /* Set default notification channel on first app start*/
                        Firebase.Messaging.SubscribeTopic(Firebase.Messaging.TOPIC_DEFAULT);
                    }
                    updateProgressbar();
                });
        // [END get current FCM token]

        // [START Ask for CAMERA permission]
        int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    Variables.REQUEST_CODE_ASK_PERMISSIONS);
        }
        updateProgressbar();
        // [END Ask for CAMERA permission]

        // [START Ask for CALENDAR permission]
        int hasCalendarPermission = checkSelfPermission(Manifest.permission.WRITE_CALENDAR);
        if (hasCalendarPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR},
                    124);
        }
        updateProgressbar();
        // [End Ask for CAMERA permission]

        // [START check Firebase.setFirebase() results]
        if (!Firebase.setFirebase()) {
            Log.e(TAG, "onCreate: error setting the firebase");
            error();
        } else {
            try {
                //Log.d(TAG, "Creating successfully finished.");
                // [START get current Semester]
                loadCurrentSemester();
                // [END get current Semester]
                updateProgressbar();
            } catch (Exception e) {
                Log.d(TAG, "Error loading data from firestore", e);
                error();
            }
        }
        // [END check Firebase.setFirebase() results]
    }

    private void error() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(R.string.error_title)
                .setMessage(R.string.error_close_app)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    ((Activity) getApplicationContext()).finish();
                })
                .create();
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == Variables.REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Toast.makeText(App.getContext(), "CAMERA allowed", Toast.LENGTH_SHORT)
                        .show();/*
            } else {
                 Permission Denied
                Toast.makeText(App.getContext(), "CAMERA Denied", Toast.LENGTH_SHORT)
                        .show();*/
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void loadCurrentSemester() {
        final String id = remoteConfig.getString("current_semester_id");
        Firebase.FIRESTORE.collection("Semester")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    Semester sem = documentSnapshot1.toObject(Semester.class);
                    App.setCurrentSemester(sem);
                });
        updateProgressbar();
        loadCurrentChargen(id);
    }

    private void loadCurrentChargen(String current_semester_id) {
        if (Firebase.USER != null) {
            Firebase.FIRESTORE.collection("Semester")
                    .document(current_semester_id)
                    .collection("Chargen")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot s : queryDocumentSnapshots) {
                            if (Firebase.USER.getUid().equals(s.getString("uid"))) {
                                //give the user editorial rights
                                SharedPreferences.Editor editor = Variables.SHARED_PREFERENCES.edit();
                                editor.putStringSet(Variables.Rights.TAG, Variables.Rights.charge());
                                editor.apply();
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "loadCurrentChargen: no chargen in current semester " + current_semester_id, e));
        }
        updateProgressbar();
        loadAdmins();
    }

    private void loadAdmins() {
        if (Firebase.USER != null) {
            Firebase.FIRESTORE
                    .collection("Editors")
                    .whereArrayContains("super-admin", Firebase.USER.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "loadAdmins: user is an admin");
                            SharedPreferences.Editor editor = Variables.SHARED_PREFERENCES.edit();
                            editor.putStringSet(Variables.Rights.TAG, Variables.Rights.admin());
                            editor.apply();
                        } else {
                            Log.d(TAG, "loadAdmins: user is NO admin");
                        }
                    });
        }
        updateProgressbar();
    }

    private void updateProgressbar() {
        downloadProgress += (100f / totalAsks);
        progressBar.setProgress((int) downloadProgress);
        Log.d(TAG, String.valueOf(downloadProgress));
        if (downloadProgress == 100) {
            onDone();
        }
    }

    public void onDone() {
        if (downloadProgress == 100) {
            downloadProgress = 0;
            /*Go to MainActivity */
            Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

}