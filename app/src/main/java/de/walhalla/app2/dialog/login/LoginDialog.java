package de.walhalla.app2.dialog.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.interfaces.CustomDialogListener;
import de.walhalla.app2.interfaces.Kinds;
import de.walhalla.app2.model.Person;

import static android.app.Activity.RESULT_OK;
import static de.walhalla.app2.interfaces.Kinds.CONTACT_INFORMATION;
import static de.walhalla.app2.interfaces.Kinds.CONTROL_DATA;
import static de.walhalla.app2.interfaces.Kinds.FRATERNITY_DATA;
import static de.walhalla.app2.interfaces.Kinds.NameOfState;
import static de.walhalla.app2.interfaces.Kinds.PROFILE_DATA;
import static de.walhalla.app2.interfaces.Kinds.SET_IMAGE;
import static de.walhalla.app2.interfaces.Kinds.SET_PASSWORD;
import static de.walhalla.app2.interfaces.Kinds.SIGN_IN;
import static de.walhalla.app2.interfaces.Kinds.START;

/**
 * This dialog is to guide the user through the process of registration or sign in.
 *
 * @author B3tterTogeth3r
 * @version 1.5
 * @since 2.0
 */
@SuppressLint("StaticFieldLeak")
public class LoginDialog extends DialogFragment implements CustomDialogListener {
    private static final String TAG = "LoginDialog";
    protected static RelativeLayout layout;
    protected static LayoutInflater inflater;
    protected static ViewGroup root;
    /**
     * The icon to show user that input is ok
     */
    protected static Drawable allGood;
    /**
     * The icon to show user that input format is <b>not</b> ok
     */
    protected static Drawable error;
    //In this object the user password is temporarily saved, while the other data is incomplete.
    protected static String userPW;
    //In this Object the data a user sets while registering is saved.
    protected static Person user = new Person();
    protected static FragmentManager fragmentManager;
    protected static CustomDialogListener customDialogListener;
    protected static CircleImageView imageView;
    protected static Bitmap imageBitmap;

    /**
     * This function sets the UI for the user while trying to
     * log in or sign up into the app. The String {@link NameOfState kind }
     * has to be one of the set Variables for the ui to display
     * anything at all.
     *
     * @see Load
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
                layout.addView(new Load(inflater, root).setFratData());
                break;
            case SET_IMAGE:
                //set a profile image.
                layout.addView(new Load(inflater, root).setImage());
                break;
            case CONTROL_DATA:
                //control the data and make changes, if needed.
                Log.d(TAG, "setState: " + CONTROL_DATA);
                layout.addView(new Load(inflater, root).controlData());
                break;
            case PROFILE_DATA:
                Log.d(TAG, "setState: " + PROFILE_DATA);
                layout.addView(new Load(inflater, root).profileData());
                break;
            default:
                this.dismissDialog();
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
        Firebase.ANALYTICS = FirebaseAnalytics.getInstance(requireContext());
    }

    /**
     * Display the selected image in the image view
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == RESULT_OK) {
                try {
                    assert data != null;
                    Uri selectedImage = data.getData();
                    imageView.setImageURI(selectedImage);
                    imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.dialog, container, true);
        //Set variables for subclass Load.class
        layout = view.findViewById(R.id.dialog_layout);
        root = container;
        inflater = getLayoutInflater();
        fragmentManager = getChildFragmentManager();
        customDialogListener = this;

        Toolbar toolbar = view.findViewById(R.id.dialog_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            user = new Person();
            dismiss();
        });
        toolbar.setTitle(R.string.menu_login);

        //Reset confidential data on new start of fragment
        user = new Person();
        userPW = "";
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

    @Override
    public void dismissDialog() {
        this.dismiss();
    }

    @Override
    public void startIntent() {
        try {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        } catch (Exception e) {
            Log.e(TAG, "setImage: choosing a photo did not work", e);
        }
    }
}