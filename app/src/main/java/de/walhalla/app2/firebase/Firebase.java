package de.walhalla.app2.firebase;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.interfaces.AuthCustomListener;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.utils.Variables;

public class Firebase {
    private static final String TAG = "Firebase";
    private static final boolean[] setters = new boolean[4];
    public static StorageReference IMAGES;
    public static StorageReference RECEIPTS;
    public static FirebaseAuth AUTHENTICATION;
    public static boolean isUserLogin = false;
    public static FirebaseUser user;
    public static FirebaseFirestore FIRESTORE;

    public static boolean setFirebase() {
        setters[0] = Variables.setAllSemesters();
        setters[1] = Firebase.setFirestoreDB();
        setters[2] = Firebase.setAuth();
        setters[3] = Firebase.setStorage();
        //Log.d(TAG, "setFirebase: " + setters[0] + " " + setters[1] + " " + setters[2] + " " + setters[3]);
        return (setters[0] && setters[1] && setters[2] && setters[3]);
    }

    public static boolean setFirestoreDB() {
        try {
            FIRESTORE = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean setAuth() {
        try {
            AUTHENTICATION = FirebaseAuth.getInstance();
            //com.example.walhalla.firebase.Firebase
            AUTHENTICATION.addAuthStateListener(new AuthCustomListener());
            Firebase.user = AUTHENTICATION.getCurrentUser();
            if (user != null) {
                Firebase.FIRESTORE
                        .collection("Person")
                        .whereEqualTo("uid", user.getUid())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    Person p = snapshot.toObject(Person.class);
                                    try {
                                        if (p != null) {
                                            //TODO User.setData(p, Objects.requireNonNull(user.getEmail()));
                                        } else {
                                            throw new Exception("No person for that user");
                                        }
                                    } catch (Exception e) {
                                        AUTHENTICATION.signOut();
                                        user = null;
                                    }
                                }
                            }
                        });
            } else {
                AUTHENTICATION.signOut();
                user = null;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean setStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://walhallaapp.appspot.com");
        try {
            IMAGES = storage.getReference().child("pictures");
            RECEIPTS = storage.getReference().child("receipts");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public abstract static class AuthCustom implements FirebaseAuth.AuthStateListener, FirebaseAuth.IdTokenListener {
        @Override
        public void onAuthStateChanged(@androidx.annotation.NonNull FirebaseAuth firebaseAuth) {
            changer(firebaseAuth);
        }

        @Override
        public void onIdTokenChanged(@androidx.annotation.NonNull FirebaseAuth firebaseAuth) {
            //changer(firebaseAuth);
        }

        protected abstract void changer(@androidx.annotation.NonNull FirebaseAuth firebaseAuth);
    }

    public static class Messaging {
        private static final String TAG = "Firebase.Messaging";
        private static final String SENDER_ID = "159729181477";
        private static final ArrayList<String> SUBSCRIBED_TO = new ArrayList<>();
        public static String TOPIC_INTERNAL = "internal";
        public static String TOPIC_DEFAULT = "public";
        private static boolean appLaunch = true;

        public static void SubscribeTopic(String topic) {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(topic)
                    .addOnCompleteListener(task -> {
                        String msg = App.getContext().getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = App.getContext().getString(R.string.msg_subscribe_failed);
                        } else {
                            SUBSCRIBED_TO.add(topic);
                        }
                        Log.d(TAG, "SubscribeTopic: " + topic + ": " + msg);
                        if (!appLaunch) {
                            Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
                        } else {
                            appLaunch = false;
                        }
                    });
        }

        public static void UnsubscribeTopic(String topic) {
            FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(topic)
                    .addOnCompleteListener(task -> {
                        String msg = App.getContext().getString(R.string.msg_unsubscribed);
                        if (!task.isSuccessful()) {
                            msg = App.getContext().getString(R.string.msg_unsubscribe_failed);
                        } else {
                            SUBSCRIBED_TO.remove(topic);
                        }
                        Log.d(TAG, "UnsubscribeTopic: " + topic + ": " + msg);
                        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
                    });
        }

        public static boolean isSubscribed(String topic) {
            return SUBSCRIBED_TO.contains(topic);
        }
    }
}
