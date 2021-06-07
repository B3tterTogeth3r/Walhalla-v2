package de.walhalla.app2.fragment.new_person;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;

/**
 * This fragment is so a signed in admin or charge can add a new person. This new person than does
 * not has to register itself. This site will close with a "reset password" mail to the new persons
 * email.
 *
 * @author B3tterTogeth3r
 * @version 1.0
 * @since 2.3
 */
public class Fragment extends CustomFragment {
    private static final String TAG = "new_person.Fragment";

    @Override
    public void start() {

    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {
        LinearLayout layout = view.findViewById(R.id.fragment_container);
        layout.removeAllViewsInLayout();
        View profileLayout = inflater.inflate(R.layout.item_profile, null);
        layout.addView(profileLayout);
    }

    @Override
    public void viewCreated() {

    }

    @Override
    public void toolbarContent() {
        toolbar.setTitle(R.string.menu_add_person);
    }

    @Override
    public void authChange() {

    }

    @Override
    public void displayChange() {

    }

    @Override
    public void stop() {

    }
}