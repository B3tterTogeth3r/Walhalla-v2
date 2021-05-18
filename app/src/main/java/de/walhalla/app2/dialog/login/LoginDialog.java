package de.walhalla.app2.dialog.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.walhalla.app2.R;
import de.walhalla.app2.model.Person;

@SuppressLint("StaticFieldLeak")
public class LoginDialog extends DialogFragment {
    public static final String START = "start";
    public static final String SIGN_IN = "sign_in";
    public static final String SET_PASSWORD = "set_password";
    public static final String CONTACT_INFORMATION = "set_contact_information";
    public static final String FRATERNITY_DATA = "set_fraternity_data";
    public static final String SET_IMAGE = "set_image";
    public static final String CONTROL_DATA = "control_data";
    private static final String TAG = "LoginDialog";
    public static RelativeLayout layout;
    public static LayoutInflater inflater;
    public static ViewGroup root;
    /**
     * An icon to show the user that its input is in an ok format
     */
    public static Drawable allGood;
    /**
     * An icon to show the user that its input is <b>not</b> in an ok format
     */
    public static Drawable error;
    public static String userPW;
    public static Person user = new Person();

    /**
     * This function sets the UI for the user while trying to
     * log in or sign up into the app. The String {@link StateName kind }
     * has to be one of the set Variables for the ui to display
     * anything at all.
     */
    @Contract(pure = true)
    public void setState(@NotNull @StateName String kind) {
        try {
            layout.removeAllViewsInLayout();
            layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.
                    LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        } catch (Exception e) {
            Log.e(TAG, "setState: removeAllViewsInLayout() error", e);
        }
        try {
            layout.removeAllViews();
            layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.
                    LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        } catch (Exception e) {
            Log.e(TAG, "setState: removeAllViews() error", e);
        }
        switch (kind) {
            case START:
                //Start of the fragment
                layout.addView(new Load(inflater, root).start());
                break;
            case SIGN_IN:
                //entered email was found
                layout.addView(new Load(inflater, root).signIn());
                break;
            case SET_PASSWORD:
                //Entered email was not found -> User can register, starting with the password
                layout.addView(new Load(inflater, root).setPassword());
                break;
            case CONTACT_INFORMATION:
                //set contact information: First and last name, dob, address, phone number.
                layout.addView(new Load(inflater, root).setContactInfo());
                break;
            case FRATERNITY_DATA:
                //set data, the frat needs like pob, major, joined semester and rank.
                layout.addView(new Load(inflater, root).setFratData());
                break;
            case SET_IMAGE:
                //set a profile image.
                Log.d(TAG, "setState: " + SET_IMAGE);
                break;
            case CONTROL_DATA:
                //control the data and make changes, if needed.
                Log.d(TAG, "setState: " + CONTROL_DATA);
                layout.addView(new Load(inflater, root).controlData());
                break;
        }
    }

    /**
     * Make the dialog full screen
     */
    @Override
    public void onStart() {
        super.onStart();
        Dialog DIALOG = getDialog();
        if (DIALOG != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(DIALOG.getWindow()).setLayout(width, height);
            DIALOG.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.dialog, container, true);
        layout = view.findViewById(R.id.dialog_layout);
        root = container;
        inflater = getLayoutInflater();
        Toolbar toolbar = view.findViewById(R.id.dialog_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            dismiss();
            user = new Person();
        });
        toolbar.setTitle(R.string.menu_login);
        setState(START);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allGood = DrawableCompat.wrap(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_task_alt)));
        DrawableCompat.setTint(allGood, Color.GREEN);

        error = DrawableCompat.wrap(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_error_outline)));
        DrawableCompat.setTint(error, Color.RED);
    }

    /**
     * To get less errors while programming I chose to make
     * the function that way. The function does only work
     * with a value of the following strings:
     * <ul>
     *     <li>START</li>
     *     <li>SIGN_IN</li>
     *     <li>SET_PASSWORD</li>
     *     <li>CONTACT_INFORMATION</li>
     *     <li>FRATERNITY_DATA</li>
     *     <li>SET_IMAGE</li>
     *     <li>CONTROL_DATA</li>
     * </ul>
     */
    @StringDef({START, SIGN_IN, SET_PASSWORD, CONTACT_INFORMATION, FRATERNITY_DATA, SET_IMAGE, CONTROL_DATA})
    public @interface StateName {
    }

}