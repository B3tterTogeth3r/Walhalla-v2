package de.walhalla.app2.interfaces;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import de.walhalla.app2.MainActivity;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Person;

public class AuthCustomListener extends Firebase.AuthCustom {

    @Override
    public void changer(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Firebase.FIRESTORE
                    .collection("Person")
                    .whereEqualTo("uid", user.getUid())
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (!snapshot.isEmpty()) {
                                try {
                                    List<Person> p = snapshot.toObjects(Person.class);
                                    MainActivity.authChange.onAuthChange();
                                    CustomFragment.authChange.onAuthChange();
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    });
        } else {
            try {
                MainActivity.authChange.onAuthChange();
                CustomFragment.authChange.onAuthChange();
            } catch (Exception ignored) {
            }
        }

    }

    public interface send {
        void onAuthChange();
    }
}