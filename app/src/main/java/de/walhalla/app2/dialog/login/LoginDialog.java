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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.walhalla.app2.R;
import de.walhalla.app2.interfaces.Kinds;
import de.walhalla.app2.model.Person;

import static de.walhalla.app2.interfaces.Kinds.CONTACT_INFORMATION;
import static de.walhalla.app2.interfaces.Kinds.CONTROL_DATA;
import static de.walhalla.app2.interfaces.Kinds.FRATERNITY_DATA;
import static de.walhalla.app2.interfaces.Kinds.NameOfState;
import static de.walhalla.app2.interfaces.Kinds.SET_IMAGE;
import static de.walhalla.app2.interfaces.Kinds.SET_PASSWORD;
import static de.walhalla.app2.interfaces.Kinds.SIGN_IN;
import static de.walhalla.app2.interfaces.Kinds.START;

/**
 * @since 2.0
 */
@SuppressLint("StaticFieldLeak")
public class LoginDialog extends DialogFragment {
    private static final String TAG = "LoginDialog";
    protected static RelativeLayout layout;
    protected static LayoutInflater inflater;
    protected static ViewGroup root;
    /**
     * The icon to show the user that its input is in an ok format
     */
    protected static Drawable allGood;
    /**
     * The icon to show the user that its input is <b>not</b> in an ok format
     */
    protected static Drawable error;
    protected static String userPW;
    protected static Person user = new Person();
    protected static FragmentManager fragmentManager;

    /**
     * This function sets the UI for the user while trying to
     * log in or sign up into the app. The String {@link NameOfState kind }
     * has to be one of the set Variables for the ui to display
     * anything at all.
     */
    @Contract(pure = true)
    protected void setState(@NotNull @Kinds.NameOfState String kind) {
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
                fragmentManager = getChildFragmentManager();
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
     * Make the dialog full screen with {@link #onStart()}
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    /**
     * Make the dialog full screen with {@link #onCreate(Bundle)}
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

}