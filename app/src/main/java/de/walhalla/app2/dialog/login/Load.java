package de.walhalla.app2.dialog.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import de.walhalla.app2.App;
import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;
import de.walhalla.app2.User;
import de.walhalla.app2.dialog.ChangeSemesterDialog;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.interfaces.SemesterListener;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.model.Rank;
import de.walhalla.app2.model.Semester;
import de.walhalla.app2.utils.ImageDownload;
import de.walhalla.app2.utils.Variables;

import static de.walhalla.app2.interfaces.Kinds.CONTACT_INFORMATION;
import static de.walhalla.app2.interfaces.Kinds.CONTROL_DATA;
import static de.walhalla.app2.interfaces.Kinds.FRATERNITY_DATA;
import static de.walhalla.app2.interfaces.Kinds.PROFILE_DATA;
import static de.walhalla.app2.interfaces.Kinds.SET_IMAGE;
import static de.walhalla.app2.interfaces.Kinds.SET_PASSWORD;
import static de.walhalla.app2.interfaces.Kinds.SIGN_IN;
import static de.walhalla.app2.interfaces.Kinds.START;

/**
 * @author B3tterTogeth3r
 * @version 1.6
 * @since 2.1
 */
@SuppressWarnings("deprecation")
public class Load extends LoginDialog {
    private static final String TAG = "Load";
    private final ArrayList<Rank> ranks = new ArrayList<>();
    private final AtomicBoolean imageGotSelected = new AtomicBoolean(false);
    private LayoutInflater inflater;
    private ViewGroup root;
    private Uri image_uri;
    private TextView rank;
    private LinearLayout settingsLayout;
    private String image_path = "";
    private int stepNumber = 0;
    private float downloadProgress = 0;
    private ProgressBar progressBar;

    /**
     * @see #Load(LayoutInflater, ViewGroup)
     */
    public Load() {
    }

    /**
     * Loads the different states of the login process. one
     * of the functions has to be chosen or the dialog is
     * getting dismissed.
     */
    public Load(@NotNull LayoutInflater inflater, @NotNull ViewGroup root) {
        this.inflater = inflater;
        this.root = root;
    }

    /**
     * Checks via the android native pattern recognition the input
     *
     * @param email the input to check
     * @return true, if the input is an email, false, if input isn't
     */
    private boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * This function creates a view with the round shield at the top and an
     * input for an email address. if the user has written its email into
     * the text field, the input is validated. If it is an email address,
     * the firebase authentication api will check whether the address belongs
     * to an existing account or not. <br>
     * If the input has an account, the user shall be brought to the
     * {@link #signIn()} site. <br>
     * If the input has no account, the user shall be register to the app by
     * the then displayed {@link #setPassword()}.
     *
     * @return The layout of the start screen for the user to sign in or up
     * @since 1.0
     */
    @NotNull
    public LinearLayout start() {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_item_login, root, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        EditText email = view.findViewById(R.id.login_email);
        if (user.getMail() != null && !user.getMail().isEmpty()) {
            email.setText(user.getMail());
        }
        Button next = view.findViewById(R.id.login_next);
        next.setOnClickListener(v -> {
            String emailStr = email.getText().toString();
            if (emailStr.length() != 0 && isValidEmail(emailStr)) {
                Log.d(TAG, "start: email: " + emailStr);
                Firebase.AUTHENTICATION.signInWithEmailAndPassword(emailStr, "123456")
                        .addOnCompleteListener(task -> {
                            try {
                                //noinspection ConstantConditions
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                // if mail not found send
                                Log.d(TAG, "start: email not found");
                                LoginDialog.user.setMail(emailStr);
                                setState(SET_PASSWORD);
                            } catch (FirebaseAuthInvalidCredentialsException invalidPassword) {
                                // if mail found send
                                Log.d(TAG, "start: email found, but pw wrong, as expected");
                                LoginDialog.user.setMail(emailStr);
                                setState(SIGN_IN);
                            } catch (Exception e) {
                                Log.d(TAG, "start: auth error", e);
                                try {
                                    new MainActivity().hideKeyboard(getView());
                                } catch (Exception ignored) {
                                }
                                Toast.makeText(App.getContext(), R.string.fui_error_too_many_attempts, Toast.LENGTH_LONG).show();
                                Snackbar.make(requireView(), R.string.fui_error_too_many_attempts, Snackbar.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(App.getContext(), R.string.email_invalid, Toast.LENGTH_LONG).show();
            }
        });
        Log.d(TAG, "load: I am loading");
        return view;
    }

    /**
     * This function create a view with the round shield at the top and
     * one input for the users password below.
     * <p>If the auth is true, the user gets sign in, the dialog gets dismissed,
     * the current fragment and the left side nav are getting refreshed.
     * <br>If the auth is false, the user gets a message.
     * <p>The user can also choose to reset his password with a reset-email.
     *
     * @return The screen to sign in
     * @see de.walhalla.app2.interfaces.AuthCustomListener AuthCustomListener
     * @since 1.1
     */
    @SuppressWarnings("ConstantConditions")
    @NotNull
    public LinearLayout signIn() {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_item_login_sign_in, root, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button next = view.findViewById(R.id.login_sign_in);
        EditText password = view.findViewById(R.id.login_password);

        next.setOnClickListener(v -> {
            String passwordStr = password.getText().toString();
            if (!passwordStr.isEmpty() && 7 < passwordStr.length()) {
                String emailStr = user.getMail();
                Firebase.AUTHENTICATION.signInWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                try {
                                    Log.d(TAG, "signIn: successful");
                                    Firebase.USER = Firebase.AUTHENTICATION.getCurrentUser();
                                    Snackbar.make(MainActivity.parentLayout.getRootView(), R.string.login_success, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.close, v1 -> {
                                            })
                                            .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark, null))
                                            .show();
                                    Log.d(TAG, "signIn: " + Firebase.USER.getUid());
                                    Firebase.FIRESTORE.collection("Person")
                                            .whereEqualTo("uid", Firebase.USER.getUid())
                                            .limit(1)
                                            .get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                for (DocumentSnapshot d : queryDocumentSnapshots) {
                                                    try {
                                                        User userdata = new User();
                                                        Person p = d.toObject(Person.class);
                                                        userdata.setUid(Firebase.USER.getUid());
                                                        userdata.setImage(Firebase.USER.getPhotoUrl());
                                                        userdata.setEmail(Firebase.USER.getEmail());
                                                        userdata.setData(p);
                                                        Firebase.ANALYTICS.setUserProperty("user_rank", p.getRank());
                                                        App.setUser(userdata);
                                                        customDialogListener.dismissDialog();
                                                    } catch (Exception e) {
                                                        Log.e(TAG, "onCreate: loading the userdata did not work", e);
                                                        Firebase.CRASHLYTICS.log("StartActivity.onCreate: loading the userdata did not work");
                                                        Firebase.CRASHLYTICS.recordException(e);
                                                    }
                                                }
                                            });
                                } catch (NullPointerException nullPointerException) {
                                    Log.e(TAG, "signIn: dismiss did not work.", nullPointerException);
                                    Firebase.CRASHLYTICS.log("Load.signIn: Auth: dismiss did not work");
                                    Firebase.CRASHLYTICS.recordException(nullPointerException);
                                } catch (Exception e) {
                                    Log.e(TAG, "signIn: some other error occurred.", e);
                                    Firebase.CRASHLYTICS.log("Load.signIn: some other error occurred.");
                                    Firebase.CRASHLYTICS.recordException(e);
                                }
                            } else {
                                try {
                                    //noinspection ConstantConditions
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidCredentialsException invalidPassword) {
                                    // if mail found send
                                    Log.d(TAG, "signIn: email found, but pw wrong");
                                    Toast.makeText(App.getContext(), R.string.fui_error_invalid_password, Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Log.e(TAG, "signIn: auth error", e);
                                }
                            }
                        });
            } else {
                Toast.makeText(App.getContext(), R.string.fui_error_invalid_password, Toast.LENGTH_LONG).show();
            }
        });

        Button goBack = view.findViewById(R.id.login_sign_in_back);
        goBack.setOnClickListener(v -> setState(START));

        TextView resetPW = view.findViewById(R.id.login_forgot_password);
        resetPW.setOnClickListener(v -> FirebaseAuth.getInstance().sendPasswordResetEmail(user.getMail())
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "onComplete: Email send.");
                    LoginDialog.customDialogListener.dismissDialog();
                    Snackbar.make(MainActivity.parentLayout, "Email with reset link send.", Snackbar.LENGTH_LONG).show();
                }));
        return view;
    }

    /**
     * This function create a view with the round shield at the top and
     * two inputs for passwords below.
     * <p>Only if the password fulfills the required security measurements
     * and both fields are the same, the user can resume its registration.
     * <br>The password is needed to sign in.
     *
     * @return The screen set a password for sign up
     * @since 1.2
     */
    @NotNull
    public LinearLayout setPassword() {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_item_login_sign_up_pw, root, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Button next = view.findViewById(R.id.login_next);
        EditText pw1 = view.findViewById(R.id.login_sign_up_password);
        EditText pw2 = view.findViewById(R.id.login_sign_up_password_control);

        if (userPW != null && !userPW.isEmpty()) {
            pw1.setText(userPW);
            pw2.setText(userPW);
        }

        next.setOnClickListener(v -> {
            String password;
            if (pw1.getText().toString().equals(pw2.getText().toString())) {
                password = pw1.getText().toString();
                if (!password.isEmpty() && 7 < password.length()) {
                    Log.d(TAG, "setPassword: " + pw1.getText().toString());
                    // Save the password
                    userPW = pw1.getText().toString();
                    setState(PROFILE_DATA);
                }
            }
        });

        Button goBack = view.findViewById(R.id.login_previous);
        goBack.setOnClickListener(v -> setState(START));

        pw1.addTextChangedListener(new TextWatcher(view, 0));
        pw2.addTextChangedListener(new TextWatcher(view, 1));
        Log.d(TAG, "load: I am loading");
        return view;
    }

    /**
     * This function create a view with the round shield at the top and
     * multiple inputs.
     * <p>The user has to give the fraternity certain contact information so the print
     * version of the program and all other print versions can be send to him.
     * <br>For a card for a round birthday, the user has to set his birthday.
     * <br>The mobile number is needed for a fast contact outside this app.
     *
     * @return The screen to set some data to contact the user
     * @since 1.3
     * @deprecated since version 1.7 all that is made in {@link #profileData()}
     */
    public LinearLayout setContactInfo() {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_item_set_contact_info, root, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Button next = view.findViewById(R.id.login_next);
        EditText first_name = view.findViewById(R.id.profile_firstName);
        EditText last_name = view.findViewById(R.id.profile_lastName);
        EditText street = view.findViewById(R.id.profile_address_street);
        EditText number = view.findViewById(R.id.profile_address_number);
        EditText zip = view.findViewById(R.id.profile_address_zip);
        EditText city = view.findViewById(R.id.profile_address_city);
        Button dob = view.findViewById(R.id.profile_dob);
        EditText mobile = view.findViewById(R.id.profile_mobile);
        AtomicReference<Timestamp> dobTime = new AtomicReference<>();

        dob.setOnClickListener(v -> {
            try {
                new MainActivity().hideKeyboard(getView());
            } catch (Exception ignored) {
            }
            Log.d(TAG, "setContactInfo: select a birthday");
            //open Date-picker
            Date date;
            try {
                date = user.getDoB().toDate();
            } catch (Exception e) {
                date = new Date();
            }
            int[] DoB = new int[3];
            SimpleDateFormat getYear = new SimpleDateFormat("yyyy", Variables.LOCALE);
            SimpleDateFormat getMonth = new SimpleDateFormat("MM", Variables.LOCALE);
            SimpleDateFormat getDay = new SimpleDateFormat("dd", Variables.LOCALE);
            DoB[0] = Integer.parseInt(getYear.format(date));
            DoB[1] = Integer.parseInt(getMonth.format(date)) - 1;
            DoB[2] = Integer.parseInt(getDay.format(date));
            try {
                new DatePickerDialog(inflater.getContext(),
                        (view1, year, monthOfYear, dayOfMonth) -> {
                            String month = String.valueOf(monthOfYear + 1);
                            if (Integer.parseInt(month) < 10) {
                                month = "0" + month;
                            }
                            String day = String.valueOf(dayOfMonth);
                            if (Integer.parseInt(day) < 10) {
                                day = "0" + day;
                            }
                            String result = day + "." + month + "." + year;
                            dob.setText(result);
                            result = year + "-" + month + "-" + day;
                            Log.i(TAG, result);
                            Calendar c = Calendar.getInstance();
                            c.set(year, monthOfYear, dayOfMonth);
                            dobTime.set(new Timestamp(c.getTime()));
                        }, DoB[0], DoB[1], DoB[2]).show();
            } catch (Exception e) {
                Log.e(TAG, "setContactInfo: ", e);
            }
        });

        next.setOnClickListener(v -> {
            Log.d(TAG, "setContactInfo: next");
            String first_NameStr = first_name.getText().toString();
            String last_nameStr = last_name.getText().toString();
            String streetStr = street.getText().toString();
            String numberStr = number.getText().toString();
            String zipStr = zip.getText().toString();
            String cityStr = city.getText().toString();
            String mobileStr = mobile.getText().toString();

            if (first_NameStr.length() != 0 &&
                    last_nameStr.length() != 0 &&
                    streetStr.length() != 0 &&
                    numberStr.length() != 0 &&
                    zipStr.length() != 0 &&
                    cityStr.length() != 0 &&
                    mobileStr.length() != 0 &&
                    dobTime.get() != null &&
                    dobTime.get().toString().length() != 0) {
                Map<String, Object> address = new HashMap<>();
                address.put(Person.ADDRESS_CITY, cityStr);
                address.put(Person.ADDRESS_NUMBER, numberStr);
                address.put(Person.ADDRESS_STREET, streetStr);
                address.put(Person.ADDRESS_ZIP_CODE, zipStr);

                user.setFirst_Name(first_NameStr);
                user.setLast_Name(last_nameStr);
                user.setMobile(mobileStr);
                user.setAddress(address);
                user.setDoB(new Timestamp(dobTime.get().toDate()));

                setState(FRATERNITY_DATA);
            } else {
                try {
                    new MainActivity().hideKeyboard(getView());
                } catch (Exception ignored) {
                }
                Toast.makeText(App.getContext(), "An error occurred", Toast.LENGTH_LONG).show();
            }
        });
        Button goBack = view.findViewById(R.id.login_previous);
        goBack.setOnClickListener(v -> setState(SET_PASSWORD));
        return view;
    }

    /**
     * This function create a view with the round shield at the top and
     * multiple inputs.
     * <p>The user has to fill all fields for being able to continue the registration.
     * <br>The fields are necessary for the fraternity and a reliable account administration.
     *
     * @return The screen
     * @since 1.5
     * @deprecated since version 1.7 all that is made in {@link #profileData()}
     */
    public LinearLayout setFratData() {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_item_set_frat_data, root, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Button next = view.findViewById(R.id.login_next);
        settingsLayout = view.findViewById(R.id.profile_settings);
        settingsLayout.setVisibility(View.GONE);
        Button joined = view.findViewById(R.id.profile_joined);
        joined.setOnClickListener(v -> { // Select a semester the user joined.
            ChangeSemesterDialog dialog = new ChangeSemesterDialog(new SemesterListener() {
                @Override
                public void displayChangeDone() {

                }

                @Override
                public void joinedSelectionDone(Semester chosenSemester) {
                    try {
                        Log.d(TAG, "joinedSelectionDone: chosenSemester.getLong() == " + chosenSemester.getLong());
                        user.setJoined(chosenSemester.getID());
                    } catch (Exception e) {
                        Log.e(TAG, "joinedSelectionDone: getJoined() did not work", e);
                    }
                }
            }, Person.JOINED);
            try {
                dialog.show(LoginDialog.fragmentManager,
                        TAG);
            } catch (Exception e) {
                Log.e(TAG, "setFratData: opening semester dialog did not work", e);
            }
        });
        Button rankBT = view.findViewById(R.id.profile_rank);
        rankBT.setOnClickListener(v -> downloadRank());
        Button goBack = view.findViewById(R.id.login_previous);
        goBack.setOnClickListener(v -> setState(CONTACT_INFORMATION));
        AtomicBoolean firstFrat = new AtomicBoolean(false);
        (view.findViewById(R.id.profile_first_fraternity)).setOnClickListener(v -> {
            if ((view.findViewById(R.id.profile_full_member_layout)).getVisibility() == View.VISIBLE) {
                (view.findViewById(R.id.profile_full_member_layout)).setVisibility(View.VISIBLE);
            } else {
                (view.findViewById(R.id.profile_full_member_layout)).setVisibility(View.GONE);
            }
        });
        TextView pob = view.findViewById(R.id.profile_pob);

        next.setOnClickListener(v -> {
            String pobStr = pob.getText().toString();
            if (user.getJoined() != 0 && !user.getRank().isEmpty() && !pobStr.isEmpty()) {
                boolean inLoco = ((Switch) view.findViewById(R.id.profile_in_loco)).isChecked();
                boolean full_member = ((Switch) view.findViewById(R.id.profile_full_member)).isChecked();

                Rank rank = user.getRankSetting();
                rank.setFirst_fraternity(firstFrat.get());
                rank.setFull_member(full_member);
                rank.setIn_loco(inLoco);
                user.setPoB(pobStr);
                user.setRankSetting(rank);
                setState(SET_IMAGE);
            } else {
                Snackbar.make(requireView(), R.string.error_fill_all_fields, Snackbar.LENGTH_LONG).show();
            }
        });
        return view;
    }

    /**
     * This function create a view with the round shield at the top and
     * multiple inputs.
     * <p>The user can choose a profile picture for himself. This image is
     * only for the profile, not for the "chargen".
     *
     * @return the screen
     * @since 1.6
     * @deprecated since version 1.7 all that is made in {@link #profileData()}
     */
    public LinearLayout setImage() {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_item_image, root, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout imageChange = view.findViewById(R.id.login_image_select);
        LoginDialog.imageView = view.findViewById(R.id.login_profile_image);
        AtomicBoolean imageGotSelected = new AtomicBoolean(false);
        imageChange.setOnClickListener(v -> {
            LoginDialog.customDialogListener.startIntent();
            imageGotSelected.set(true);
        });
        Button next = view.findViewById(R.id.login_next);
        next.setOnClickListener(v -> {
            //Upload image and save the path
            if (imageGotSelected.get()) {
                //upload image
                image_path = user.getFullName().replace(" ", "_");
                user.setPicture_path("pictures/" + image_path);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, ((Long) Variables.ONE_MEGABYTE).intValue(), baos);
                byte[] data = baos.toByteArray();

                StorageReference reference = Firebase.IMAGES.child(image_path);
                UploadTask uploadTask = reference.putBytes(data);
                uploadTask.addOnFailureListener(e -> Log.d(TAG, "Image available: upload error", e))
                        .addOnSuccessListener(taskSnapshot -> Log.d(TAG, Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getName())));
            }
            setState(CONTROL_DATA);
        });
        Button goBack = view.findViewById(R.id.login_previous);
        goBack.setOnClickListener(v -> setState(FRATERNITY_DATA));
        return view;
    }

    /**
     * This function creates a view in which the user can check all the input data and change it.
     *
     * @return the View
     * @since 1.7
     * @deprecated since version 1.7 all that is made in {@link #profileData()}
     */
    public LinearLayout controlData() {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.item_profile, root, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Button next = view.findViewById(R.id.login_next);
        EditText email = view.findViewById(R.id.login_email);
        next.setOnClickListener(v -> {
            Log.d(TAG, "load: clicked with email: " + email.getText().toString());
            setState(SET_PASSWORD);
            /* After user create is successful, set name and uri to the auth-profile.
            UserProfileChangeRequest profileChange = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user.getFullName())
                    .setPhotoUri(user.getPicture_path())
                    .build();
            Firebase.USER.updateProfile(profileChange)
                    .addOnCompleteListener(update -> {
                        if(update.isSuccessful())
                            Log.d(TAG, "onComplete: User profile updated.");
                    });*/
        });
        return view;
    }

    /**
     * This function displays all the values the user has to fill out
     * in order to complete registration. The same function can be used
     * to edit the profile later. Every row belongs to at least one value
     * of {@link Person Person}. This function combines most
     * of previous functions from version 1.3 till 1.7
     * <p>
     * The user has to fill the fields
     * <code>email, name, address, dob, mobile, pob, major, rank</code> and
     * <code>joined</code>. The user has to select a <code>profile image</code>,
     * too. If any except the image is <tt>null</tt>, the user can't proceed
     * with the registration.
     *
     * @return the view
     * @see Person
     * @since 1.8
     */
    @SuppressLint("InflateParams")
    @NotNull
    public View profileData() {
        View view = inflater.inflate(R.layout.item_profile, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        view.findViewById(R.id.button_row).setVisibility(View.GONE);
        view.findViewById(R.id.profile_title_image).setVisibility(View.GONE);
        view.findViewById(R.id.profile_title_text).setVisibility(View.GONE);
        TextView email = view.findViewById(R.id.profile_mail);
        TextView name = view.findViewById(R.id.profile_name);
        TextView address = view.findViewById(R.id.profile_address);
        TextView dob = view.findViewById(R.id.profile_dob);
        TextView mobile = view.findViewById(R.id.profile_mobile);
        TextView pob = view.findViewById(R.id.profile_pob);
        TextView major = view.findViewById(R.id.profile_major);
        rank = view.findViewById(R.id.profile_rank);
        TextView joined = view.findViewById(R.id.profile_joined);
        imageView = view.findViewById(R.id.login_profile_image);
        progressBar = view.findViewById(R.id.profile_loader);

        // [START Field filling]
        // Fill all fields if user is not null
        // and has a value for that object
        if (user != null) {
            try {
                name.setText(user.getFullName());
            } catch (Exception ignored) {
            }
            try {
                address.setText(user.getAddressString());
            } catch (Exception ignored) {
            }
            try {
                dob.setText(user.getDoBString());
            } catch (Exception ignored) {
            }
            try {
                mobile.setText(user.getMobile());
            } catch (Exception ignored) {
            }
            try {
                pob.setText(user.getPoB());
            } catch (Exception ignored) {
            }
            try {
                major.setText(user.getMajor());
            } catch (Exception ignored) {
            }
            try {
                rank.setText(user.getRank());
            } catch (Exception ignored) {
            }
            try {
                if (user.getJoined() != 0) {
                    joined.setText(Variables.SEMESTER_ARRAY_LIST.get(user.getJoined() - 1).getLong());
                }
            } catch (Exception ignored) {
            }
            try {
                if (!user.getPicture_path().equals("")) {
                    //Get image
                    final ImageView pictureFinal = imageView;
                    new ImageDownload(pictureFinal::setImageBitmap, user.getPicture_path()).execute();
                }
            } catch (Exception ignored) {
            }
        }
        // [END Field filling]

        // Email is not editable, so the arrow has to be disabled.
        (view.findViewById(R.id.profile_email_arrow)).setVisibility(View.GONE);
        email.setText(user.getMail());

        // [START first & last name]
        // If dialog is closed with the positive button, the input is saved and the TV updated.
        view.findViewById(R.id.profile_name_layout)
                .setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog, null);
                    RelativeLayout layoutRL = dialogView.findViewById(R.id.dialog_layout);
                    LinearLayout layout = new LinearLayout(inflater.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    View firstNameView = inflater.inflate(R.layout.layout_custom_edit_text_hint, null);
                    EditText firstName = firstNameView.findViewById(R.id.customEditText);
                    firstName.setHint(R.string.profile_first_name);
                    View lastNameView = inflater.inflate(R.layout.layout_custom_edit_text_hint, null);
                    EditText lastName = lastNameView.findViewById(R.id.customEditText);
                    lastName.setHint(R.string.profile_lastName);
                    layoutRL.removeAllViewsInLayout();
                    layout.removeAllViewsInLayout();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layout.addView(firstNameView);
                    layout.addView(lastNameView);
                    layoutRL.addView(layout);
                    layoutRL.setLayoutParams(params);
                    try {
                        firstName.setText(user.getFirst_Name());
                        lastName.setText(user.getLast_Name());
                    } catch (Exception ignored) {
                    }
                    builder.setCancelable(true)
                            .setView(dialogView)
                            .setNegativeButton(R.string.abort, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                //Read user input and write it into the user object
                                try {
                                    user.setFirst_Name(firstName.getText().toString());
                                    user.setLast_Name(lastName.getText().toString());

                                    //write full name in the TV
                                    name.setText(user.getFullName());
                                } catch (Exception e) {
                                    Log.e(TAG, "profileData: name too short", e);
                                    Toast.makeText(inflater.getContext(), R.string.error_no_name, Toast.LENGTH_LONG).show();
                                }
                                //Close dialog
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    Toolbar toolbar = dialogView.findViewById(R.id.dialog_toolbar);
                    toolbar.setTitle(R.string.profile_name);
                    toolbar.setNavigationOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                });
        // [END first & last name]

        // [START address]
        view.findViewById(R.id.profile_address_layout)
                .setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog_address, null);
                    EditText city, number, street, zip;
                    city = dialogView.findViewById(R.id.profile_address_city);
                    number = dialogView.findViewById(R.id.profile_address_number);
                    street = dialogView.findViewById(R.id.profile_address_street);
                    zip = dialogView.findViewById(R.id.profile_address_zip);
                    try {
                        city.setText((String) user.getAddress().get(Person.ADDRESS_CITY));
                        number.setText((String) user.getAddress().get(Person.ADDRESS_NUMBER));
                        street.setText((String) user.getAddress().get(Person.ADDRESS_STREET));
                        zip.setText((String) user.getAddress().get(Person.ADDRESS_ZIP_CODE));
                    } catch (Exception ignored) {
                    }
                    builder.setCancelable(true)
                            .setView(dialogView)
                            .setNegativeButton(R.string.abort, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                try {
                                    //Read user input and write it into the user object
                                    Map<String, Object> addressMap = new HashMap<>();
                                    addressMap.put(Person.ADDRESS_CITY, city.getText().toString());
                                    addressMap.put(Person.ADDRESS_NUMBER, number.getText().toString());
                                    addressMap.put(Person.ADDRESS_STREET, street.getText().toString());
                                    addressMap.put(Person.ADDRESS_ZIP_CODE, zip.getText().toString());
                                    user.setAddress(addressMap);

                                    //write address in the TV
                                    address.setText(user.getAddressString());
                                } catch (Exception e) {
                                    Log.e(TAG, "profileData: address error", e);
                                    Toast.makeText(inflater.getContext(), R.string.error_no_name, Toast.LENGTH_LONG).show();
                                }
                                //Close dialog
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    Toolbar toolbar = dialogView.findViewById(R.id.dialog_toolbar);
                    toolbar.setTitle(R.string.profile_name);
                    toolbar.setNavigationOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                });
        // [END address]

        // [START Birthday]
        view.findViewById(R.id.profile_dob_layout)
                .setOnClickListener(v -> {
                    try {
                        new MainActivity().hideKeyboard(getView());
                    } catch (Exception ignored) {
                    }
                    Log.d(TAG, "profileData: select a birthday");
                    //open Date-picker
                    Date date;
                    try {
                        date = user.getDoB().toDate();
                    } catch (Exception e) {
                        date = new Date();
                    }
                    int[] DoB = new int[3];
                    SimpleDateFormat getYear = new SimpleDateFormat("yyyy", Variables.LOCALE);
                    SimpleDateFormat getMonth = new SimpleDateFormat("MM", Variables.LOCALE);
                    SimpleDateFormat getDay = new SimpleDateFormat("dd", Variables.LOCALE);
                    DoB[0] = Integer.parseInt(getYear.format(date));
                    DoB[1] = Integer.parseInt(getMonth.format(date)) - 1;
                    DoB[2] = Integer.parseInt(getDay.format(date));
                    try {
                        new DatePickerDialog(inflater.getContext(),
                                (view1, year, monthOfYear, dayOfMonth) -> {
                                    String month = String.valueOf(monthOfYear + 1);
                                    if (Integer.parseInt(month) < 10) {
                                        month = "0" + month;
                                    }
                                    String day = String.valueOf(dayOfMonth);
                                    if (Integer.parseInt(day) < 10) {
                                        day = "0" + day;
                                    }
                                    String result = day + "." + month + "." + year;
                                    dob.setText(result);
                                    result = year + "-" + month + "-" + day;
                                    Log.i(TAG, result);
                                    Calendar c = Calendar.getInstance();
                                    c.set(year, monthOfYear, dayOfMonth);
                                    user.setDoB(new Timestamp(c.getTime()));
                                }, DoB[0], DoB[1], DoB[2]).show();
                    } catch (Exception e) {
                        Log.e(TAG, "profileData: ", e);
                    }
                });
        // [END Birthday]

        // [START phone number]
        view.findViewById(R.id.profile_mobile_layout)
                .setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog, null);
                    RelativeLayout layoutRL = dialogView.findViewById(R.id.dialog_layout);
                    LinearLayout layout = new LinearLayout(inflater.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    View mobileView = inflater.inflate(R.layout.layout_custom_edit_text_hint, null);
                    EditText mobileET = mobileView.findViewById(R.id.customEditText);
                    mobileET.setHint(R.string.profile_mobile);
                    mobileET.setInputType(InputType.TYPE_CLASS_PHONE);
                    layoutRL.removeAllViewsInLayout();
                    layout.removeAllViewsInLayout();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layout.addView(mobileView);
                    layoutRL.addView(layout);
                    layoutRL.setLayoutParams(params);
                    try {
                        mobileET.setText(user.getMobile());
                    } catch (Exception ignored) {
                    }
                    builder.setCancelable(true)
                            .setView(dialogView)
                            .setNegativeButton(R.string.abort, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                //Read user input and write it into the user object
                                try {
                                    user.setMobile(mobileET.getText().toString());

                                    //write mobile number in the TV
                                    mobile.setText(user.getMobile());
                                } catch (Exception e) {
                                    Log.e(TAG, "profileData: number too short", e);
                                    Toast.makeText(inflater.getContext(), R.string.error_no_name, Toast.LENGTH_LONG).show();
                                }
                                //Close dialog
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    Toolbar toolbar = dialogView.findViewById(R.id.dialog_toolbar);
                    toolbar.setTitle(R.string.profile_name);
                    toolbar.setNavigationOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                });
        // [END phone number]

        // [START origin / place of birth]
        view.findViewById(R.id.profile_pob_layout)
                .setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog, null);
                    RelativeLayout layoutRL = dialogView.findViewById(R.id.dialog_layout);
                    LinearLayout layout = new LinearLayout(inflater.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    View pobView = inflater.inflate(R.layout.layout_custom_edit_text_hint, null);
                    EditText pobET = pobView.findViewById(R.id.customEditText);
                    pobET.setHint(R.string.profile_pob);
                    layoutRL.removeAllViewsInLayout();
                    layout.removeAllViewsInLayout();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layout.addView(pobView);
                    layoutRL.addView(layout);
                    layoutRL.setLayoutParams(params);
                    try {
                        pobET.setText(user.getPoB());
                    } catch (Exception ignored) {
                    }
                    builder.setCancelable(true)
                            .setView(dialogView)
                            .setNegativeButton(R.string.abort, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                //Read user input and write it into the user object
                                try {
                                    user.setPoB(pobET.getText().toString());

                                    //write mobile number in the TV
                                    pob.setText(user.getPoB());
                                } catch (Exception e) {
                                    Log.e(TAG, "profileData: number too short", e);
                                    Toast.makeText(inflater.getContext(), R.string.error_no_name, Toast.LENGTH_LONG).show();
                                }
                                //Close dialog
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    Toolbar toolbar = dialogView.findViewById(R.id.dialog_toolbar);
                    toolbar.setTitle(R.string.profile_name);
                    toolbar.setNavigationOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                });
        // [END origin / place of birth]

        // [START major or occupation]
        view.findViewById(R.id.profile_major_layout)
                .setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog, null);
                    RelativeLayout layoutRL = dialogView.findViewById(R.id.dialog_layout);
                    LinearLayout layout = new LinearLayout(inflater.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    View majorView = inflater.inflate(R.layout.layout_custom_edit_text_hint, null);
                    EditText majorET = majorView.findViewById(R.id.customEditText);
                    majorET.setHint(R.string.profile_major);
                    layoutRL.removeAllViewsInLayout();
                    layout.removeAllViewsInLayout();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layout.addView(majorView);
                    layoutRL.addView(layout);
                    layoutRL.setLayoutParams(params);
                    try {
                        majorET.setText(user.getMajor());
                    } catch (Exception ignored) {
                    }
                    builder.setCancelable(true)
                            .setView(dialogView)
                            .setNegativeButton(R.string.abort, null)
                            .setPositiveButton(R.string.send, (dialog, which) -> {
                                //Read user input and write it into the user object
                                try {
                                    user.setMajor(majorET.getText().toString());

                                    //write mobile number in the TV
                                    major.setText(user.getMajor());
                                } catch (Exception e) {
                                    Log.e(TAG, "profileData: number too short", e);
                                    Toast.makeText(inflater.getContext(), R.string.error_no_name, Toast.LENGTH_LONG).show();
                                }
                                //Close dialog
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    Toolbar toolbar = dialogView.findViewById(R.id.dialog_toolbar);
                    toolbar.setTitle(R.string.profile_name);
                    toolbar.setNavigationOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                });
        // [END major or occupation]

        // [START rank]
        view.findViewById(R.id.profile_rank_layout)
                .setOnClickListener(v ->
                        downloadRank()
                );
        // [END rank]

        // [START joined semester]
        //select users current as start semester
        view.findViewById(R.id.profile_joined_layout)
                .setOnClickListener(v -> {
                    user.setJoined(App.getCurrentSemester().getID());
                    ChangeSemesterDialog dialog = new ChangeSemesterDialog(new SemesterListener() {
                        @Override
                        public void displayChangeDone() {

                        }

                        @Override
                        public void joinedSelectionDone(Semester chosenSemester) {
                            try {
                                Log.d(TAG, "joinedSelectionDone: chosenSemester.getLong() == " + chosenSemester.getLong());
                                user.setJoined(chosenSemester.getID());
                                joined.setText(chosenSemester.getLong());
                            } catch (Exception e) {
                                Log.e(TAG, "joinedSelectionDone: getJoined() did not work", e);
                            }
                        }
                    }, Person.JOINED, user.getJoined());
                    try {
                        dialog.show(getChildFragmentManager(),
                                TAG);
                    } catch (Exception e) {
                        Log.e(TAG, "setFratData: opening semester dialog did not work", e);
                    }
                });
        // [END joined semester]

        // [START profile picture]
        view.findViewById(R.id.login_profile_image_layout)
                .setOnClickListener(v -> {
                    LoginDialog.customDialogListener.startIntent();
                    imageGotSelected.set(true);
                });
        // [END profile picture]

        // [START register button]
        view.findViewById(R.id.button_sign_in)
                .setOnClickListener(v -> {
                    //Check that all fields are filled
                    boolean errorExists = false;
                    try {
                        user.getFullName();
                    } catch (Exception ignored) {
                        errorExists = true;
                    }
                    try {
                        user.getAddressString();
                    } catch (Exception ignored) {
                        errorExists = true;
                    }
                    try {
                        user.getDoBString();
                    } catch (Exception ignored) {
                        errorExists = true;
                    }
                    try {
                        user.getMobile();
                    } catch (Exception ignored) {
                        errorExists = true;
                    }
                    try {
                        user.getPoB();
                    } catch (Exception ignored) {
                        errorExists = true;
                    }
                    try {
                        user.getMajor();
                    } catch (Exception ignored) {
                        errorExists = true;
                    }
                    try {
                        user.getRank();
                    } catch (Exception ignored) {
                        errorExists = true;
                    }
                    try {
                        user.getJoined();
                    } catch (Exception ignored) {
                        errorExists = true;
                    }
                    if (errorExists) {
                        Toast.makeText(requireContext(), R.string.error_fill_all_fields, Toast.LENGTH_LONG).show();
                    } else {
                        //TODO show progress bar, disable all other fields (greyisch color over the whole screen
                        // or something like that
                        progressBar.setVisibility(View.VISIBLE);
                        LinearLayout layout = view.findViewById(R.id.profile_layout);
                        layout.setBackgroundResource(R.color.darkGray);
                        layout.setAlpha(0.5f);
                        view.findViewById(R.id.profile_name_layout).setOnClickListener(null);
                        view.findViewById(R.id.profile_address_layout).setOnClickListener(null);
                        view.findViewById(R.id.profile_dob_layout).setOnClickListener(null);
                        view.findViewById(R.id.profile_mobile_layout).setOnClickListener(null);
                        view.findViewById(R.id.profile_pob_layout).setOnClickListener(null);
                        view.findViewById(R.id.profile_major_layout).setOnClickListener(null);
                        view.findViewById(R.id.profile_rank_layout).setOnClickListener(null);
                        view.findViewById(R.id.profile_joined_layout).setOnClickListener(null);
                        view.findViewById(R.id.login_profile_image_layout).setOnClickListener(null);
                        view.findViewById(R.id.login_register).setOnClickListener(null);
                        uploadData();
                    }
                });
        // [END register button]

        return view;
    }

    /**
     * Upload the data, starting with the image. Put the <tt>image_path</tt> in {@link #user}
     * and Auth.<tt>profilePicture</tt>. Than register the user and send a verification to users {@link Person#getMail() email}. Get
     * the uid and set it via {@link Person#setUid(String)}. <br>
     * After that upload the data to firestore.
     *
     * @see #profileData()
     * @since 1.8
     */
    private void uploadData() {
        stepNumber++;
        // [START Upload Image]
        if (stepNumber == 0) {
            if (imageGotSelected.get()) {
                String imagePath = (user.getFirst_Name()).replace(" ", "_");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                Firebase.IMAGES.child(imagePath).putBytes(data)
                        .addOnFailureListener(e -> {
                            //Upload failed
                            Firebase.CRASHLYTICS.log("ProfileFragment.onStop: upload of user data failed");
                            Firebase.CRASHLYTICS.recordException(e);
                            Log.e(TAG, "stop: upload failed", e);
                        }).addOnCompleteListener(task -> {
                    //Set uri to User and update Auth user
                    Firebase.IMAGES.child(imagePath).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                this.image_uri = uri;
                                user.setPicture_path(imagePath);
                                uploadData();
                            });
                });
            } else {
                uploadData();
            }
        }
        // [END Upload image]

        // [START Create user in Firebase Auth]
        if (stepNumber == 1) {
            //Create user in Firebase Auth and set uid to user.setUid()
            Firebase.AUTHENTICATION.createUserWithEmailAndPassword(user.getMail(), userPW)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Firebase.USER = Firebase.AUTHENTICATION.getCurrentUser();
                            user.setUid(Firebase.USER.getUid());
                            UserProfileChangeRequest.Builder profileUpdates =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(user.getFullName());
                            if (image_uri.toString().length() != 0) {
                                profileUpdates.setPhotoUri(image_uri);
                            }
                            Firebase.USER.updateProfile(profileUpdates.build())
                                    .addOnSuccessListener(unused ->
                                            Log.d(TAG, "onSuccess: auth profile updated.")
                                    );
                            uploadData();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        // [END Create user in Firebase Auth]

        // [START Upload user data]
        if (stepNumber == 2) {
            Firebase.FIRESTORE.collection("Person")
                    .add(user)
                    .addOnSuccessListener(unused -> {
                        uploadData();
                        Log.d(TAG, "onSuccess: user registered.");
                    });
        }
        // [END Upload user data]

        // [START Send mail for confirmation]
        if (stepNumber == 3) {
            Firebase.USER.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: email send.");
                        }
                        uploadData();
                    });
        }
        // [END Send mail for confirmation]

        // [START Load the new user data into the app]
        if (stepNumber == 4) {
            Firebase.FIRESTORE.collection("Person")
                    .whereEqualTo("uid", Firebase.USER.getUid())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            try {
                                User userdata = new User();
                                Person p = d.toObject(Person.class);
                                userdata.setId(d.getId());
                                userdata.setUid(Firebase.USER.getUid());
                                userdata.setImage(Firebase.USER.getPhotoUrl());
                                userdata.setEmail(Firebase.USER.getEmail());
                                userdata.setData(p);
                                Firebase.ANALYTICS.setUserProperty("user_rank", p.getRank());
                                App.setUser(userdata);
                                uploadData();
                            } catch (Exception e) {
                                Log.e(TAG, "uploadUserData: loading the userdata did not work", e);
                                Firebase.CRASHLYTICS.log(TAG + "uploadUserData: loading the userdata did not work");
                                Firebase.CRASHLYTICS.recordException(e);
                            }
                        }
                    });
        }
        // [END Load the new user data into the app]
        updateProgressbar();
    }

    private void updateProgressbar() {
        downloadProgress += (100f / 5);
        progressBar.setProgress((int) downloadProgress);
        Log.d(TAG, String.valueOf(downloadProgress));
        if (downloadProgress == 100) {
            progressBar.setVisibility(View.GONE);
            Snackbar.make(MainActivity.parentLayout, R.string.register_complete, Snackbar.LENGTH_LONG).show();
            LoginDialog.customDialogListener.dismissDialog();
        }
    }


    /**
     * Download the ranks from the database. After that format them
     * into an ArrayList and display the dialog for the user to select one.
     */
    @SuppressWarnings("unchecked")
    private void downloadRank() {
        Firebase.FIRESTORE
                .collection("Kind")
                .document("rank")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> dataSet = documentSnapshot.getData();
                        if (dataSet != null) {
                            int size = dataSet.size();
                            for (int i = 0; i < size; i++) {
                                try {
                                    Map<String, Object> data = (Map<String, Object>) dataSet.get(String.valueOf(i));
                                    Rank r = new Rank();
                                    r.setId(i);
                                    r.setFirst_fraternity((boolean) data.get(Rank.FIRST_FRATERNITY));
                                    r.setFull_member((boolean) data.get(Rank.FULL_MEMBER));
                                    r.setIn_loco((boolean) data.get(Rank.IN_LOCO));
                                    r.setPrice_semester((Map<String, Object>) data.get(Rank.PRICE));
                                    r.setRank_name((String) data.get(Rank.NAME));
                                    ranks.add(r);
                                } catch (Exception e) {
                                    Log.d(TAG, "Something went wrong", e);
                                }
                            }

                            rankDialog(ranks)
                                    .show();
                        } else {
                            Log.d(TAG, "2 something went wrong while downloading the ranks");
                        }
                    } else {
                        Log.d(TAG, "1 something went wrong while downloading the ranks");
                    }
                });
    }

    /**
     * Display the ranks a user can choose from
     *
     * @return The finished Builder.
     */
    @NotNull
    private AlertDialog.Builder rankDialog(@NotNull ArrayList<Rank> ranks) {
        String[] options = new String[ranks.size()];
        for (int i = 0; i < ranks.size(); i++) {
            options[i] = ranks.get(i).getRank_name();
        }
        AlertDialog.Builder rankPicker = new AlertDialog.Builder(inflater.getContext());
        rankPicker.setTitle(R.string.profile_choose_rank)
                .setCancelable(false)
                .setNegativeButton(R.string.abort, (dialog, which) -> {
                    rank.setText(R.string.profile_choose_rank);
                    try {
                        settingsLayout.setVisibility(View.GONE);
                    } catch (Exception ignored) {
                    }
                    this.ranks.clear();
                    dialog.dismiss();
                })
                .setItems(options, (dialog, which) -> {
                    try {
                        settingsLayout.setVisibility(View.VISIBLE);
                    } catch (Exception ignored) {
                    }
                    rank.setText(options[which]);
                    Log.i(TAG, which + " is the selected value");
                    user.setRank(options[which]);
                    user.setRankSetting(ranks.get(which));
                    this.ranks.clear();
                });
        return rankPicker;
    }
}
