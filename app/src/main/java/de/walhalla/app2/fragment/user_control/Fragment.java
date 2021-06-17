package de.walhalla.app2.fragment.user_control;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.utils.goHome;

public class Fragment extends CustomFragment {
    private static final String TAG = "user_control.Fragment";

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
            new goHome(getParentFragmentManager());
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