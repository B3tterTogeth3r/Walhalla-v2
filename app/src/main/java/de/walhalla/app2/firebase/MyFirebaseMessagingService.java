package de.walhalla.app2.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;
import de.walhalla.app2.model.Person;

import static de.walhalla.app2.firebase.Firebase.CRASHLYTICS;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final long SENDER_ID = 159729181477L;
    public static final String SERVER_KEY = "AAAAJTCZ4yU:APA91bEq5Qao6e4_WMvS3eUk0BVJIom5Nrfky5gvLOs1K2xe6938E-_R4uVFmOBty5hDZsdS5L0OUHXlOrDQblNRZPg1lb9TH0NxDN5UFjAQaF3cMBqxBPudOWfLZeeWk8CkPsdsVYNB";
    private static final String TAG = "FCM";

    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (remoteMessage.getNotification() != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "101")
                    .setSmallIcon(R.drawable.wappen_herz)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("1", "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(1, builder.build());
        }
    }

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        Log.d(TAG, "onNewToken: The token refreshed" + token);

        //send token to Firestore
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
                                            .document(String.valueOf(dr))
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
                        }
                    });
        }
    }
}
