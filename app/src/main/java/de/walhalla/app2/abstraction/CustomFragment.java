package de.walhalla.app2.abstraction;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.interfaces.AuthCustomListener;
import de.walhalla.app2.interfaces.SemesterListener;

public abstract class CustomFragment extends Fragment implements AuthCustomListener.send, SemesterListener {
    public static AuthCustomListener.send authChange;
    protected final String TAG = "CustomFragment";
    public ArrayList<ListenerRegistration> registration;
    public Toolbar toolbar;

    @Override
    public void onStart() {
        try {
            super.onStart();
            registration = new ArrayList<>();
        } finally {
            start();
        }
    }

    public abstract void start();

    /**
     * Don't call in the extending classes of this <b>CustomFragment</b>
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        authChange = this;

        try {
            if (Firebase.AUTHENTICATION.getCurrentUser() != null) {
                FirebaseUser user = Firebase.AUTHENTICATION.getCurrentUser();
                if (!user.isEmailVerified()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.error_title))
                            .setMessage(getString(R.string.error_email_not_verified_message))
                            .setPositiveButton(getString(R.string.yes),
                                    (dialog, which) ->
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "Email send");
                                                        }
                                                        dialog.dismiss();
                                                    }))
                            .setNeutralButton(getString(R.string.later), ((dialog, which) -> dialog.dismiss()))
                            .show();
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        createView(view, inflater);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: running");
        try {
            toolbar = requireActivity().findViewById(R.id.toolbar);
            super.onViewCreated(view, savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            viewCreated();
            toolbarContent();
        }
    }

    public abstract void createView(View view, LayoutInflater inflater);

    public abstract void viewCreated();

    /**
     * Called before {@link #viewCreated() viewCreated} returns a result. It is to format the toolbar in every Subclass the same way.
     */
    public abstract void toolbarContent();

    @Override
    public void onAuthChange() {
        try {
            Log.d(TAG, "onAuthChange");
        } finally {
            authChange();
        }

    }

    /**
     * The state of the authentication changed in {@link AuthCustomListener customAuthListener}
     */
    public abstract void authChange();

    @Override
    public void displayChangeDone() {
        try {
            Log.d(TAG, "displayChangeDone");
        } finally {
            displayChange();
        }
    }

    public abstract void displayChange();

    @Override
    public void onStop() {
        super.onStop();
        try {
            for (ListenerRegistration reg : registration) {
                reg.remove();
            }
            registration.clear();
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong while removing the snapshot listener", e);
        } finally {
            stop();
        }
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to {@link #onStop() onStop} of the containing
     * Activity's lifecycle.
     */
    public abstract void stop();
}
