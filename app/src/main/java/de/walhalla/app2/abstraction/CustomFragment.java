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

/**
 * This class is so that every fragment inside the app looks the same,
 * has similar runtime and a similar looking basic code for better understanding.
 * Because every {@code fragment} inside the app needs the same start method,
 * the same variable for the {@link #toolbar}, should register all its
 * {@link #registration realtimelistener} in the same list, so they can be all
 * stopped at {@link #onStop()} this super class was created.
 *
 * @author B3tterTogeth3r
 * @version 1.4
 * @see de.walhalla.app2.interfaces.AuthCustomListener.send
 * @see de.walhalla.app2.interfaces.SemesterListener
 * @since 1.7
 */
public abstract class CustomFragment extends Fragment implements AuthCustomListener.send, SemesterListener {
    public static AuthCustomListener.send authChange;
    protected final String TAG = "CustomFragment";
    /**
     * A list to collect all realtime listeners into the firestore database.
     */
    public ArrayList<ListenerRegistration> registration;
    /**
     * The top Toolbar of the whole application
     */
    public Toolbar toolbar;

    /**
     * called after new {@link #registration} got reset and the site can start
     */
    public abstract void start();

    /**
     * Don't call in the extending classes of this {@link CustomFragment}
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
        } catch (Exception exception) {
            Firebase.CRASHLYTICS.recordException(exception);
        }
    }

    @Nullable
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
            toolbar.findViewById(R.id.custom_title).setVisibility(View.GONE);
            toolbar.setTitle("Walhalla");
            toolbar.getMenu().clear();
            super.onViewCreated(view, savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            viewCreated();
            toolbarContent();
        }
    }

    @Override
    public void onStart() {
        try {
            super.onStart();
            registration = new ArrayList<>();
        } finally {
            start();
        }
    }

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
            Firebase.CRASHLYTICS.log("Something went wrong while removing the snapshot listener");
            Firebase.CRASHLYTICS.recordException(e);
        } finally {
            toolbar.getMenu().clear();
            toolbar.setTitle(R.string.app_name);
            toolbar.setSubtitle("");
            stop();
        }
    }

    /**
     * Create the view and initialize the necessary variables for the site.
     * <p>
     * <b>DON'T CALL FUNCTIONS THAT WORK WITH DATA OF {@link #start()} IN HERE</b>
     *
     * @param view     inflated View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @param inflater LayoutInflater for inflating new Layouts into the view
     * @see #onViewCreated(View, Bundle)
     */
    public abstract void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater);

    public abstract void viewCreated();

    /**
     * Called before {@link #viewCreated() viewCreated} returns a result.
     * This is to format the toolbar in every Subclass the same way.
     */
    public abstract void toolbarContent();

    @Override
    public void onAuthChange() {
        authChange();
    }

    /**
     * Called on state change of {@link AuthCustomListener customAuthListener}
     */
    public abstract void authChange();

    @Override
    public void displayChangeDone() {
        displayChange();
    }

    /**
     * Called if the user changed the semester to display the board or the program of
     */
    public abstract void displayChange();

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to {@link #onStop() onStop} of the containing
     * Activity's lifecycle.
     * <p>
     * Called after every entry in {@link #registration} got stopped and the list cleared.
     */
    public abstract void stop();
}
