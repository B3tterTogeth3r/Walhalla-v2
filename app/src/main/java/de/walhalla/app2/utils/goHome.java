package de.walhalla.app2.utils;

import android.preference.PreferenceManager;

import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.fragment.home.Fragment;

/**
 * Send the user to the chosen home site
 *
 * @author B3tterTogeth3r
 * @version 1.0
 * @see de.walhalla.app2.fragment.settings.Fragment SettingsFragment
 * @since 2.4
 */
public class goHome {
    /**
     * Send the user to the chosen home site
     *
     * @param manager FragmentManager
     * @see de.walhalla.app2.fragment.settings.Fragment SettingsFragment
     * @since 1.0
     */
    public goHome(@NotNull FragmentManager manager) {
        String value = PreferenceManager
                .getDefaultSharedPreferences(App.getContext())
                .getString(Variables.START_PAGE, "");
        androidx.fragment.app.Fragment home;
        switch (value) {
            case "program":
                home = new de.walhalla.app2.fragment.program.Fragment();
                break;
            case "news":
                home = new de.walhalla.app2.fragment.news.Fragment();
                break;
            case "drink":
                home = new de.walhalla.app2.fragment.drink.Fragment();
                break;
            case "home":
            default:
                home = new Fragment();
                break;
        }
        manager.beginTransaction().replace(R.id.fragment_container, home).commit();
    }
}
