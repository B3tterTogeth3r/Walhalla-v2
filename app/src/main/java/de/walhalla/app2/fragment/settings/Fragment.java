package de.walhalla.app2.fragment.settings;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.utils.Variables;

/**
 * In this fragment the user has the possibility to change some settings. Like which kind of
 * messages he wants to receive, which page the app should start to and maybe more in the future.
 *
 * @author B3tterTogeth3r
 * @version 1.1
 * @since 2.1
 */
public class Fragment extends CustomFragment {
    private static final String TAG = "settings.Fragment";
    private final String[] sites = {"home", "program", "news", "drink"};
    private String[] choices;

    @Override
    public void start() {

    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {
        LinearLayout l = view.findViewById(R.id.fragment_container);
        l.setOrientation(LinearLayout.VERTICAL);
        ScrollView scrollView = new ScrollView(requireContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        if (Firebase.USER != null) {
            try {
                if (App.getUser().getData().getRank().equals("Aktiver") || App.getUser().getData().getRank().equals("Alter Herr")) {
                    choices = new String[]{"Start", "Semesterprogramm", "Mitteilungen", "Getränke"};
                }
            } catch (Exception ignored) {
                choices = new String[]{"Start", "Semesterprogramm", "Mitteilungen"};
            }
        } else {
            choices = new String[]{"Start", "Semesterprogramm", "Mitteilungen"};
        }

        AlertDialog ad = new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setTitle(R.string.menu_settings)
                .setItems(choices, (dialog, which) -> {
                    String food = sites[which];
                    setStartSite(food);
                }).create();
        ad.show();

        scrollView.addView(layout);
        l.addView(scrollView);
    }

    @Override
    public void viewCreated() {

    }

    @Override
    public void toolbarContent() {
        toolbar.setTitle(R.string.menu_settings);
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

    private void setStartSite(String page) {
        Log.d(TAG, "setStartSite: " + page);

        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                .putString(Variables.START_PAGE, page)
                .apply();

        // [START user_property]
        Firebase.ANALYTICS.setUserProperty(Variables.START_PAGE, page);
        // [END user_property]
    }
}