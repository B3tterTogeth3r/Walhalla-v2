package de.walhalla.app2.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final long SENDER_ID = 159729181477L;
    public static final String SERVER_KEY = "AAAAJTCZ4yU:APA91bEq5Qao6e4_WMvS3eUk0BVJIom5Nrfky5gvLOs1K2xe6938E-_R4uVFmOBty5hDZsdS5L0OUHXlOrDQblNRZPg1lb9TH0NxDN5UFjAQaF3cMBqxBPudOWfLZeeWk8CkPsdsVYNB";
    private static final String TAG = "FCM";

    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
