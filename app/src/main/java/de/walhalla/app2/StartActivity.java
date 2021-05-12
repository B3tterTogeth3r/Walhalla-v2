package de.walhalla.app2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Semester;
import de.walhalla.app2.utils.Variables;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";
    private final int totalAsks = 7;
    private final int maxAmount = 98;
    private int downloadProgress = 0;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        progressBar = findViewById(R.id.start_progressBar);
        Log.i(TAG, "StartActivity should show the shield.");

        // [START get current FCM token]
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "onCreate: get current fcm token");
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt) + " " + token;
                    Log.d(TAG, msg);
                    //Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();

                    /* Set default notification channel */
                    Firebase.Messaging.SubscribeTopic(Firebase.Messaging.TOPIC_DEFAULT);

                    updateProgressbar(maxAmount / totalAsks);
                });
        // [END get current FCM token]

        // [START Ask for CAMERA permission]
        int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    Variables.REQUEST_CODE_ASK_PERMISSIONS);
        }
        updateProgressbar(maxAmount / totalAsks);
        // [END Ask for CAMERA permission]

        // [START Ask for CALENDAR permission]
        int hasCalendarPermission = checkSelfPermission(Manifest.permission.WRITE_CALENDAR);
        if (hasCalendarPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR},
                    124);
        }
        updateProgressbar(maxAmount / totalAsks);
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
                updateProgressbar(maxAmount / totalAsks);
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
        Firebase.FIRESTORE.collection("Current")
                .document("Semester")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    final String id = String.valueOf(documentSnapshot.get("id"));
                    updateProgressbar(maxAmount / totalAsks / 2);
                    if (id != null && !id.equals("0")) {
                        Firebase.FIRESTORE.collection("Semester")
                                .document(id)
                                .get()
                                .addOnSuccessListener(documentSnapshot1 -> {
                                    Semester sem = documentSnapshot1.toObject(Semester.class);
                                    App.setCurrentSemester(sem);
                                    updateProgressbar(maxAmount / totalAsks / 2);
                                    loadCurrentChargen(id);
                                });
                    } else {
                        Log.e(TAG, "loadCurrentSemester: found no semester");
                        error();
                    }
                });
    }

    private void loadCurrentChargen(String current_semester_id) {
        Firebase.FIRESTORE.collection("Semester")
                .document(current_semester_id)
                .collection("Chargen")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> currentChargen = new ArrayList<>();
                    for (QueryDocumentSnapshot s : queryDocumentSnapshots) {
                        currentChargen.add((String) s.get("uid"));
                    }
                    App.setCurrentChargen(currentChargen);
                    updateProgressbar(maxAmount / totalAsks);
                    loadAdmins();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "loadCurrentChargen: no chargen in current semester " + current_semester_id, e);
                    updateProgressbar(maxAmount / totalAsks);
                    loadAdmins();
                });
    }

    private void loadAdmins() {
        Firebase.FIRESTORE
                .collection("Editors")
                .document("private")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        Map<String, Object> admins = documentSnapshot.getData();
                        if (admins != null && !admins.isEmpty()) {
                            try {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> list = (Map<String, Object>) admins.get("roles");
                                //TODO save them somewhere
                            } catch (Exception ignored) {
                            }
                        }
                        updateProgressbar(maxAmount / totalAsks);
                    } catch (Exception ignored) {
                    }
                });
    }

    private void updateProgressbar(int amount) {
        downloadProgress += amount;
        progressBar.setProgress(downloadProgress);
        Log.d(TAG, String.valueOf(downloadProgress));
        if (downloadProgress == maxAmount) {
            onDone();
        }
    }

    public void onDone() {
        if (downloadProgress == maxAmount) {
            downloadProgress = 0;
            /*Go to MainActivity */
            Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

}