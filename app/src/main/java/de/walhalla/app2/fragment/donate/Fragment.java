package de.walhalla.app2.fragment.donate;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;

/**
 * The IBANs to donate money to the fraternity are shown.
 * If the user clicks on one IBAN, it will be copied to the clipboard.
 */
@SuppressLint({"StaticFieldLeak", "InflateParams"})
public class Fragment extends CustomFragment {
    private static final String TAG = "donate.Fragment";
    public static ClipboardManager clipboardManager;
    protected static Button aktive, hbv, kstv, hbv_donate;
    protected static View view;
    private final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    @Override
    public void start() {
    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {
        Fragment.view = inflater.inflate(R.layout.fragment_donate, null);

        //On click saves the text of the button into the clipboard.
        aktive = Fragment.view.findViewById(R.id.konto_aktive);
        hbv = Fragment.view.findViewById(R.id.konto_hbv);
        kstv = Fragment.view.findViewById(R.id.konto_kstv);
        hbv_donate = Fragment.view.findViewById(R.id.konto_hbd_donate);

        clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

        aktive.setOnClickListener(new OnClick());
        hbv.setOnClickListener(new OnClick());
        kstv.setOnClickListener(new OnClick());
        hbv_donate.setOnClickListener(new OnClick());

        LinearLayout layout = view.findViewById(R.id.fragment_container);
        layout.removeAllViewsInLayout();
        layout.addView(Fragment.view);
    }

    @Override
    public void viewCreated() {
        //Set button text with remote config variables
        String iban_active = remoteConfig.getString("iban_active");
        aktive.setText(iban_active);
        String iban_hbv = remoteConfig.getString("iban_hbv");
        hbv.setText(iban_hbv);
        String iban_kstv = remoteConfig.getString("iban_kstv");
        kstv.setText(iban_kstv);
        String iban_donations = remoteConfig.getString("iban_donations");
        hbv_donate.setText(iban_donations);
    }

    @Override
    public void toolbarContent() {
        toolbar.setTitle(R.string.menu_donate);
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
