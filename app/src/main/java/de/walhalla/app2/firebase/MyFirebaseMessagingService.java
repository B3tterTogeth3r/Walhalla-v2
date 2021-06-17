package de.walhalla.app2.firebase;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;
import de.walhalla.app2.utils.Variables;

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

        try {
            boolean tester = Variables.SHARED_PREFERENCES
                    .edit()
                    .putString(Variables.GOT_NEWS, "true")
                    .commit();
            Log.d(TAG, "onMessageReceived: " + tester);
        } catch (Exception e) {
            Log.e(TAG, "onMessageReceived: error occurred", e);
        }

        if (remoteMessage.getNotification() != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "101")
                    .setSmallIcon(R.drawable.wappen_herz)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(1, builder.build());
        }
    }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        Log.d(TAG, "onNewToken: The token refreshed" + s);
        // send token to server
        // sendRegistrationToServer("1", s);
    }
}
