package de.walhalla.app2.firebase;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.interfaces.AuthCustomListener;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.utils.Variables;

@SuppressLint("StaticFieldLeak")
public class Firebase {
    private static final String TAG = "Firebase";
    private static final boolean[] setters = new boolean[5];
    public static StorageReference IMAGES;
    public static StorageReference RECEIPTS;
    public static FirebaseAuth AUTHENTICATION;
    public static boolean isUserLogin = false;
    public static FirebaseUser USER;
    public static FirebaseFirestore FIRESTORE;
    public static FirebaseAnalytics ANALYTICS;

    public static boolean setFirebase() {
        setters[0] = Variables.setAllSemesters();
        setters[1] = Firebase.setAnalytics();
        setters[2] = Firebase.setFirestoreDB();
        setters[3] = Firebase.setAuth();
        setters[4] = Firebase.setStorage();
        //Log.d(TAG, "setFirebase: " + setters[0] + " " + setters[1] + " " + setters[2] + " " + setters[3] + " " + setters[4]);
        return (setters[0] && setters[1] && setters[2] && setters[3] && setters[4]);
    }

    private static boolean setAnalytics() {
        try {
            ANALYTICS = FirebaseAnalytics.getInstance(App.getContext());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static boolean setFirestoreDB() {
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
            Firebase.USER = AUTHENTICATION.getCurrentUser();
            if (USER != null) {
                Firebase.FIRESTORE
                        .collection("Person")
                        .whereEqualTo("uid", USER.getUid())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    try {
                                        Person p = snapshot.toObject(Person.class);
                                        if (p != null) {
                                            //TODO User.setData(p, Objects.requireNonNull(user.getEmail()));
                                        } else {
                                            throw new Exception("No person for that user");
                                        }
                                    } catch (Exception e) {
                                        AUTHENTICATION.signOut();
                                        USER = null;
                                    }
                                }
                            }
                        });
            } else {
                AUTHENTICATION.signOut();
                USER = null;
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

    public interface Event {
        void oneSemester(int semester_id);
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
        private static final ArrayList<String> SUBSCRIBED_TO = new ArrayList<>();
        public static String TOPIC_INTERNAL = "internal";
        public static String TOPIC_DEFAULT = "public";

        public static void SubscribeTopic(String topic) {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(topic)
                    .addOnCompleteListener(task -> {
                        String msg = App.getContext().getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = App.getContext().getString(R.string.msg_subscribe_failed);
                        } else {
                            SUBSCRIBED_TO.add(topic);
                            SharedPreferences.Editor editor = Variables.SHARED_PREFERENCES.edit();
                            editor.putStringSet("Messaging", Firebase.Messaging.getSubscribedTo());
                            editor.apply();
                        }
                        Log.d(TAG, "SubscribeTopic: " + topic + ": " + msg);
                        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
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
                            SharedPreferences.Editor editor = Variables.SHARED_PREFERENCES.edit();
                            editor.putStringSet("Messaging", Firebase.Messaging.getSubscribedTo());
                            editor.apply();
                        }
                        Log.d(TAG, "UnsubscribeTopic: " + topic + ": " + msg);
                        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
                    });
        }

        @NotNull
        @Contract(" -> new")
        private static Set<String> getSubscribedTo() {
            return new HashSet<>(SUBSCRIBED_TO);
        }

        /**
         * test for a subscribed topic
         *
         * @param topic name to test
         * @return if the user is subscribed to the topic
         */
        public static boolean isSubscribed(String topic) {
            return SUBSCRIBED_TO.contains(topic);
        }
    }
}
