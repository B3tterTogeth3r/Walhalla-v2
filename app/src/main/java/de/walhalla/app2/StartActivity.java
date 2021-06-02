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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.model.Semester;
import de.walhalla.app2.utils.Variables;

/**
 * This Activity is the loading screen of the app. Every needed data is being loaded and the user
 * is displayed a progressbar. If an error occurred, the user gets an alert dialog with that error.
 * Also most variables, that could change over time are created here.
 *
 * @author B3tterTogeth3r
 * @version 2.2
 * @see AppCompatActivity
 * @since 1.1
 */
public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";
    @SuppressWarnings("FieldCanBeLocal")
    private final int totalAsks = 10;
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
        if (Variables.SHARED_PREFERENCES.getAll() == null) {
            Log.e(TAG, "onCreate: Fetching shared preferences did not work");
            Toast.makeText(getApplicationContext(), "Fetching shared preferences did not work", Toast.LENGTH_LONG).show();
        }
        updateProgressbar();
        // [END Initialize SharedPreferences]

        // [START update Variables with Firebase Remote Config]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean updated = task.getResult();
                        Log.d(TAG, "onCreate: Config params updated: " + updated +
                                "\nFetch and activate succeeded");
                    } else {
                        Log.e(TAG, "onCreate: Fetch failed");
                    }
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
        while (!Firebase.setFirebase()) {
            Log.e(TAG, "onCreate: error setting the firebase");
            //error();
        }
        try {
            // [START get current Semester]
            loadCurrentSemester();
            // [END get current Semester]
            updateProgressbar();
        } catch (Exception e) {
            Log.d(TAG, "Error loading data from firestore", e);
            error();
        }
        // [END check Firebase.setFirebase() results]

        // [START Firebase Analytics Audience set up]
        if (Firebase.USER == null) {
            Firebase.ANALYTICS.setUserProperty("user_rank", "guest");
        } else {
            Firebase.FIRESTORE.collection("Person")
                    .whereEqualTo("uid", Firebase.USER.getUid())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            try {
                                User userdata = new User();
                                Person p = d.toObject(Person.class);
                                userdata.setId(d.getId());
                                userdata.setUid(Firebase.USER.getUid());
                                userdata.setImage(Firebase.USER.getPhotoUrl());
                                userdata.setEmail(Firebase.USER.getEmail());
                                userdata.setData(p);
                                Firebase.ANALYTICS.setUserProperty("user_rank", p.getRank());
                                App.setUser(userdata);
                            } catch (Exception e) {
                                Log.e(TAG, "onCreate: loading the userdata did not work", e);
                                Firebase.CRASHLYTICS.log("StartActivity.onCreate: loading the userdata did not work");
                                Firebase.CRASHLYTICS.recordException(e);
                            }
                        }
                    });
        }
        updateProgressbar();
        // [END Firebase Analytics Audience set up]
    }

    /**
     * Display an error message if any error occurred while loading.
     */
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

    /**
     * Check if user has given the app the permission to take pictures.
     * Right now not activated because no function needs to take a picture. all just read from
     * the device storage.
     */
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

    /**
     * Downloading the data of the current semester from Firestore.
     */
    private void loadCurrentSemester() {
        String id = remoteConfig.getString("current_semester_id");

        Firebase.FIRESTORE.collection("Semester")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Semester sem = documentSnapshot.toObject(Semester.class);
                    App.setCurrentSemester(sem);
                    updateProgressbar();
                    loadCurrentChargen();
                });
    }

    /**
     * If the user has a charge in the current semester, it is saved in the cash of the device.
     */
    private void loadCurrentChargen() {
        String current_semester_id = remoteConfig.getString("current_semester_id");

        if (Firebase.USER != null) {
            Firebase.FIRESTORE.collection("Semester")
                    .document(current_semester_id)
                    .collection("Chargen")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot s : queryDocumentSnapshots) {
                            Firebase.CRASHLYTICS.log("Finding chargen");
                            try {
                                if (Firebase.USER.getUid().equals(s.getString("uid"))) {
                                    //give the user editorial rights
                                    SharedPreferences.Editor editor = Variables.SHARED_PREFERENCES.edit();
                                    editor.putStringSet(Variables.Rights.TAG, Variables.Rights.charge());
                                    editor.apply();
                                }
                            } catch (NullPointerException nullPointerException) {
                                Log.e(TAG, "loadCurrentChargen: user has no uid", nullPointerException);
                                Firebase.CRASHLYTICS.recordException(nullPointerException);
                            } catch (Exception exception) {
                                Log.e(TAG, "loadCurrentChargen: Exception", exception);
                                Firebase.CRASHLYTICS.recordException(exception);
                            }
                        }
                        updateProgressbar();
                        loadAdmins();
                    })
                    .addOnFailureListener(e -> {
                        updateProgressbar();
                        loadAdmins();
                        Log.e(TAG, "loadCurrentChargen: no chargen in current semester " + current_semester_id, e);
                    });
        } else {
            updateProgressbar();
            loadAdmins();
        }
    }

    /**
     * If the user is in the super-admin list, this is to be saved in the cash of the device.
     */
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
                        updateProgressbar();
                    });
        } else {
            updateProgressbar();
        }
    }

    /**
     * Updating the value of the progress bar. If the bar is complete, this activity gets distroyed
     * and the {@link MainActivity} gets displayed.
     */
    private void updateProgressbar() {
        downloadProgress += (100f / totalAsks);
        progressBar.setProgress((int) downloadProgress);
        Log.d(TAG, String.valueOf(downloadProgress));
        if (downloadProgress == 100) {
            downloadProgress = 0;
            /*Go to MainActivity */
            Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }
}