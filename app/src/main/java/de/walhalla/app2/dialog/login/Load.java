package de.walhalla.app2.dialog.login;

import android.app.DatePickerDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import de.walhalla.app2.App;
import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.utils.Variables;

public class Load extends LoginDialog {
    private static final String TAG = "Load";
    LayoutInflater inflater;
    ViewGroup root;

    public Load() {
    }

    /**
     * Load the different states of the login process. one
     * of the functions has to be chosen or the dialog is
     * getting dismissed.
     */
    public Load(@NotNull LayoutInflater inflater, @NotNull ViewGroup root) {
        this.inflater = inflater;
        this.root = root;
    }

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
     * one input for the users password below. If the auth is true, the user
     * gets sign in and the dialog dismissed. If the auth is false, the user
     * gets a message.
     *
     * @return The screen to sign in
     */
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
                                Log.d(TAG, "signIn: successful");
                                Firebase.USER = Firebase.AUTHENTICATION.getCurrentUser();
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

        return view;
    }

    /**
     * This function create a view with the round shield at the top and
     * two inputs for passwords below. only if the password fulfills the required security
     * measurements and both fields are the same, the user can resume its registration.
     *
     * @return The screen set a password for sign up
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
                    setState(CONTACT_INFORMATION);
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
     *
     * @return The screen to set some data to contact the user
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
                user.setDoB(dobTime.get());

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
     *
     * @return The screen for the user to set some data the fraternity needs
     */
    public LinearLayout setFratData() {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_item_set_frat_data, root, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Button next = view.findViewById(R.id.login_next);
        next.setOnClickListener(v -> {
            // if mail found send
            //setState(SIGN_IN);
            // if mail not found send
            setState(CONTROL_DATA);
        });
        Button goBack = view.findViewById(R.id.login_previous);
        goBack.setOnClickListener(v -> setState(CONTACT_INFORMATION));
        return view;
    }

    /**
     * This function create a view.
     *
     * @return The screen for the user to sign up
     */
    public LinearLayout controlData() {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_item_login, root, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Button next = view.findViewById(R.id.login_next);
        EditText email = view.findViewById(R.id.login_email);
        next.setOnClickListener(v -> {
            Log.d(TAG, "load: clicked with email: " + email.getText().toString());
            // if mail found send
            //setState(SIGN_IN);
            // if mail not found send
            setState(SET_PASSWORD);
        });
        return view;
    }
}
