package de.walhalla.app2;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.model.Semester;
import de.walhalla.app2.utils.Variables;

import static de.walhalla.app2.firebase.Firebase.CRASHLYTICS;

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
        Log.d(TAG, "onCreate: Initialize SharedPreferences");
        updateProgressbar();
        // [END Initialize SharedPreferences]

        // [START update Variables with Firebase Remote Config]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
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
                    Log.d(TAG, "onCreate: Firebase Remote Config");
                    updateProgressbar();
                });
        // [END update Variables with Firebase Remote Config]

        // [START check Firebase.setFirebase() results]
        while (!Firebase.setFirebase()) {
            Log.e(TAG, "onCreate: error setting the firebase");
        }
        try {
            // [START get current Semester]
            loadCurrentSemester();
            // [END get current Semester]
            Log.d(TAG, "onCreate: get current Semester");
            updateProgressbar();
        } catch (Exception e) {
            Log.d(TAG, "Error loading data from firestore", e);
        }
        // [END check Firebase.setFirebase() results]

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
                    if (Firebase.AUTHENTICATION.getCurrentUser() != null) {
                        Firebase.FIRESTORE
                                .collection("Person")
                                .whereEqualTo(Person.UID, Firebase.AUTHENTICATION
                                        .getCurrentUser().getUid())
                                .limit(1)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.getException() != null) {
                                        Log.e(TAG,
                                                "onComplete: couldn't find signed in person",
                                                task1.getException());
                                        updateProgressbar();
                                        return;
                                    }
                                    QuerySnapshot querySnapshot = task1.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        for (DocumentSnapshot d : querySnapshot) {
                                            Person p = d.toObject(Person.class);
                                            if (p != null) {
                                                p.setFcm_token(token);
                                                DocumentReference dr = d.getReference();
                                                Firebase.FIRESTORE
                                                        .document(dr.getPath())
                                                        .update(p.toMap())
                                                        .addOnSuccessListener(unused ->
                                                                Log.d(TAG, "fcm: updated")
                                                        )
                                                        .addOnFailureListener(e -> {
                                                            Log.e(TAG, "fcm: error", e);
                                                            CRASHLYTICS.log("fcm update error");
                                                            CRASHLYTICS.recordException(e);
                                                        });
                                            }
                                        }
                                        Log.d(TAG, "onCreate: FCM with signed in user");
                                        updateProgressbar();
                                    }
                                });
                    }
                    Log.d(TAG, "onCreate: FCM without signed in user");
                    updateProgressbar();
                });
        // [END get current FCM token]

        // [START Ask for CAMERA permission]
        int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    Variables.REQUEST_CODE_ASK_PERMISSIONS);
        }
        Log.d(TAG, "onCreate: CAMERA permission");
        updateProgressbar();
        // [END Ask for CAMERA permission]

        // [START Ask for CALENDAR permission]
        int hasCalendarPermission = checkSelfPermission(Manifest.permission.WRITE_CALENDAR);
        if (hasCalendarPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR},
                    124);
        }
        Log.d(TAG, "onCreate: CALENDAR permission");
        updateProgressbar();
        // [End Ask for CAMERA permission]

        // [START Firebase Analytics Audience set up and get full Userdata]
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
                                CRASHLYTICS.log("StartActivity.onCreate: loading the userdata did not work");
                                CRASHLYTICS.recordException(e);
                            }
                        }
                    });
        }
        Log.d(TAG, "onCreate: Analytics");
        updateProgressbar();
        // [END Firebase Analytics Audience set up and get full Userdata]
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
        Log.d(TAG, "loadCurrentSemester: " + id);
        Firebase.FIRESTORE.collection("Semester")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Semester sem = documentSnapshot.toObject(Semester.class);
                    App.setCurrentSemester(sem);
                    Log.d(TAG, "loadCurrentSemester: found semester");
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
                            CRASHLYTICS.log("Finding chargen");
                            try {
                                if (Firebase.USER.getUid().equals(s.getString("uid"))) {
                                    //give the user editorial rights
                                    SharedPreferences.Editor editor = Variables.SHARED_PREFERENCES.edit();
                                    editor.putStringSet(Variables.Rights.TAG, Variables.Rights.charge());
                                    editor.apply();
                                }
                            } catch (NullPointerException nullPointerException) {
                                Log.e(TAG, "loadCurrentChargen: user has no uid", nullPointerException);
                                CRASHLYTICS.recordException(nullPointerException);
                            } catch (Exception exception) {
                                Log.e(TAG, "loadCurrentChargen: Exception", exception);
                                CRASHLYTICS.recordException(exception);
                            }
                        }
                        Log.d(TAG, "loadCurrentChargen: found charge");
                        updateProgressbar();
                        loadAdmins();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "loadCurrentChargen: no chargen in current semester " + current_semester_id, e);
                        updateProgressbar();
                        loadAdmins();
                    });
        } else {
            Log.d(TAG, "loadCurrentChargen: no user signed in");
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
            Log.d(TAG, "loadAdmins: no user signed in");
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
        if (downloadProgress >= 100) {
            downloadProgress = 0;
            /*Go to MainActivity */
            Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }
}