package de.walhalla.app2.firebase;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.interfaces.AuthCustomListener;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.utils.Variables;

@SuppressLint("StaticFieldLeak")
public class Firebase {
    private static final String TAG = "Firebase";
    public static StorageReference IMAGES;
    public static StorageReference RECEIPTS;
    public static FirebaseAuth AUTHENTICATION;
    public static boolean isUserLogin = false;
    public static FirebaseUser USER;
    public static FirebaseFirestore FIRESTORE;
    public static FirebaseAnalytics ANALYTICS;
    public static FirebaseCrashlytics CRASHLYTICS;
    private static int counter = -1;

    public static boolean setFirebase() {
        //Log.d(TAG, "setFirebase: " + setters[0] + " " + setters[1] + " " + setters[2] + " " + setters[3] + " " + setters[4]);
        return (initNext());
    }

    public static boolean initNext() {
        counter++;
        Log.d(TAG, "initNext: counter = " + counter);
        switch (counter) {
            case 0:
                Log.d(TAG, "initNext: creating all semesters");
                Variables.setAllSemesters();
                break;
            case 1:
                Log.d(TAG, "initNext: setting firebase auth");
                setAuth();
                break;
            case 2:
                Log.d(TAG, "initNext: setting firebase/google analytics");
                ANALYTICS = FirebaseAnalytics.getInstance(App.getContext());
                initNext();
                break;
            case 3:
                Log.d(TAG, "initNext: setting firebase cloud firestore");
                FIRESTORE = FirebaseFirestore.getInstance();
                initNext();
                break;
            case 4:
                Log.d(TAG, "initNext: setting firebase storage");
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://walhallaapp.appspot.com");
                IMAGES = storage.getReference().child("pictures");
                RECEIPTS = storage.getReference().child("receipts");
                initNext();
                break;
            case 5:
                Log.d(TAG, "initNext: setting firebase Crashlytics");
                CRASHLYTICS = FirebaseCrashlytics.getInstance();
                initNext();
                break;
            default:
                return true;
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static void setAuth() {
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
                                        Person p = new Person();
                                        try {
                                            p.setAddress((Map<String, Object>) snapshot.get(Person.ADDRESS));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setAddress_2((Map<String, Object>) snapshot.get(Person.ADDRESS_2));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setBalance(((Double) snapshot.get(Person.BALANCE)).floatValue());
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setDoB((Timestamp) snapshot.get(Person.DOB));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setFirst_Name((String) snapshot.get(Person.FIRST_NAME));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setJoined(((Long) snapshot.get(Person.JOINED)).intValue());
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setLast_Name((String) snapshot.get(Person.LAST_NAME));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setMail((String) snapshot.get(Person.MAIL));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setMajor((String) snapshot.get(Person.MAJOR));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setMobile((String) snapshot.get(Person.MOBILE));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setPicture_path((String) snapshot.get(Person.PICTURE_PATH));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setPoB((String) snapshot.get(Person.POB));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setRank((String) snapshot.get(Person.RANK));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setRankSettings((Map<String, Object>) snapshot.get(Person.RANK_SETTINGS));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
                                        try {
                                            p.setUid((String) snapshot.get(Person.UID));
                                        } catch (Exception e) {
                                            CRASHLYTICS.recordException(e);
                                            Log.e(TAG, "setAuth: ", e);
                                        }
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
                            initNext();
                        });
            } else {
                AUTHENTICATION.signOut();
                USER = null;
            }
        } catch (Exception ignored) {
        }
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
