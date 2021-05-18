package de.walhalla.app2.dialog.login;

import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class TextWatcher extends Load implements android.text.TextWatcher {
    private static final String TAG = "TextWatcher";
    private final View view;
    private final int imageInt;
    private final boolean[] check = new boolean[]{false, false};

    /**
     * The string has to fulfill some rules. Only of all of them are
     * true, the user can click on the next button.
     * The rules are:
     * <ol>
     *     <li>The input value has to pass the {@link #checkPw(String) checkPw}
     *     function</li>
     *     <li>The input value has to be at least 8 digits long
     *     <ul>
     *         <li>Both are for the {@link Load#setPassword() first password}.
     *         If they are not true, an error symbol will appear behind
     *         the EditText. That {@link LoginDialog#error symbol} is clickable to display its error
     *         message.</li>
     *     </ul>
     *     </li>
     *     <li>If the first and second password match:
     *     <ul>
     *         <li>Display the {@link LoginDialog#allGood icon} to represent no errors were found</li>
     *         <li>Disable the OnClickListener and the SimpleToolTip</li>
     *         <li>Send the confirmation to check for the other field</li>
     *         <li>Save the password</li>
     *     </ul>
     *     </li>
     *     <li>While the to passwords don't match
     *     <ul>
     *         <li>Set the confirmation listener to false</li>
     *         <li>display an error symbol at the end of the TV</li>
     *         <li>make the symbol clickable to display the error message</li></ul></li></ol>
     * only if all 4 points are true the user can proceed with the registration.
     *
     * @param view     the View in which the TextWatcher has to act
     * @param imageInt the Number of the image the watcher has to change
     */
    public TextWatcher(View view, int imageInt) {
        this.view = view;
        this.imageInt = imageInt;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (imageInt == 0) {
            ImageView control_top = view.findViewById(R.id.login_sign_up_control_top);
            control_top.setVisibility(View.VISIBLE);
            boolean checkString = checkPw(s.toString());
            if (8 <= s.length() && checkString) {
                control_top.setImageDrawable(allGood);
                control_top.setOnClickListener(null);
                pwGood(0, true);
            } else {
                pwGood(0, false);
                control_top.setImageDrawable(error);
                String message = "";
                if (!checkString) {
                    message = App.getContext().getString(R.string.error_login_password_insecure);
                }
                if (s.length() < 8) {
                    if (message.length() != 0) {
                        message = message + "\n ";
                    }
                    message = message + App.getContext().getString(R.string.error_login_password_length);
                }
                String finalMessage = message;
                control_top.setOnClickListener(v ->
                        new SimpleTooltip.Builder(App.getContext())
                                .anchorView(v)
                                .text(finalMessage)
                                .gravity(Gravity.TOP)
                                .animated(false)
                                .build()
                                .show());
            }
        } else if (imageInt == 1) {
            ImageView control_bottom = view.findViewById(R.id.login_sign_up_control_bottom);
            control_bottom.setVisibility(View.VISIBLE);
            EditText pw1 = view.findViewById(R.id.login_sign_up_password);
            EditText pw2 = view.findViewById(R.id.login_sign_up_password_control);

            if (pw1.getText().toString().equals(pw2.getText().toString())) {
                control_bottom.setImageDrawable(allGood);
                control_bottom.setOnClickListener(null);
                pwGood(1, true);
            } else {
                pwGood(1, false);
                control_bottom.setImageDrawable(error);
                control_bottom.setOnClickListener(v ->
                        new SimpleTooltip.Builder(App.getContext())
                                .anchorView(v)
                                .text(R.string.error_login_password_not_same)
                                .gravity(Gravity.TOP)
                                .animated(true)
                                .build()
                                .show());
            }
        }
    }

    /**
     * Check the password for an upper case letter, a lower case letter and a number.
     *
     * @param str The input value.
     * @return False if not all three criteria are met
     */
    private boolean checkPw(@NotNull String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (Character.isDigit(ch)) {
                numberFlag = true;
            } else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if (numberFlag && capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }

    private void pwGood(int number, boolean done) {
        check[number] = done;
        //view.findViewById(R.id.login_next).setClickable(check[0] && check[1]);
    }
}
