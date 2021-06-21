package de.walhalla.app2.fragment.transcript;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.utils.goHome;

public class Fragment extends CustomFragment {
    private static final String TAG = "settings.Fragment";

    @Override
    public void start() {

    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {

    }

    @Override
    public void viewCreated() {

    }

    @Override
    public void toolbarContent() {

    }

    @Override
    public void authChange() {
        //Go to home
        try {
            if (Firebase.AUTHENTICATION.getCurrentUser() == null) {
                new goHome(getParentFragmentManager());
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }

    @Override
    public void displayChange() {

    }

    @Override
    public void stop() {

    }
}