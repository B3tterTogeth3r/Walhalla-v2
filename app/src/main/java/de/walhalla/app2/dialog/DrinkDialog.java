package de.walhalla.app2.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.interfaces.Kinds;
import de.walhalla.app2.model.Accounting;
import de.walhalla.app2.model.Person;

/**
 * @author B3tterTogeth3r
 * @version 1.0
 * @see AlertDialog.Builder
 * @see DialogInterface.OnClickListener
 * @since 2.5
 */
public class DrinkDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {
    private static final String TAG = "InvoiceDialog";
    private final Person person;
    private final String kind;
    private final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
    private final EditText number, desc;

    @SuppressLint("InflateParams")
    public DrinkDialog(Context context, @NotNull Person person, String kind) {
        super(context);
        this.kind = kind;
        this.person = person;

        setCancelable(false);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.item_drink_kind_input, null);
        number = layout.findViewById(R.id.drink_kind_counter);
        desc = layout.findViewById(R.id.drink_kind_description);

        String title = "";
        if (kind.equals(Kinds.DRINK_PAYMENT)) {
            Drawable beer = DrawableCompat.wrap(Objects.requireNonNull(AppCompatResources.getDrawable(getContext(), R.drawable.ic_baseline_payments_24)));
            DrawableCompat.setTint(beer, Color.BLACK);
            setIcon(beer);
            title = getContext().getString(R.string.drink_payment_from) + " " + person.getFullName();
            desc.setVisibility(View.VISIBLE);
        } else if (kind.equals(Kinds.DRINK_FINE)) {
            Drawable beer = DrawableCompat.wrap(Objects.requireNonNull(AppCompatResources.getDrawable(getContext(), R.drawable.ic_fine)));
            DrawableCompat.setTint(beer, Color.BLACK);
            setIcon(beer);
            title = getContext().getString(R.string.drink_fine_for) + " " + person.getFullName();
        }
        setTitle(title);
        setView(layout);

        setNeutralButton(R.string.abort, this);
        setPositiveButton(R.string.send, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case -1: // Positive Button
                float amount = Float.parseFloat(number.getText().toString());
                Log.d(TAG, "onClick: input number " + amount);
                if (kind.equals(Kinds.DRINK_PAYMENT)) {
                    // Add to personal balance
                    person.personPaid(amount);
                    Firebase.FIRESTORE
                            .collection("Person")
                            .document(person.getId())
                            .update("balance", person.getBalance())
                            .addOnSuccessListener(unused -> Log.d(TAG, "onClick: update successful"));

                    // Add to semester account(ing)
                    String description;
                    if (desc.getText().toString().length() < 4) {
                        description = "Einzahlung";
                    } else {
                        description = desc.getText().toString();
                    }
                    Accounting acc = new Accounting(new Timestamp(new Date()), 0f, amount,
                            description, person.getFullName());
                    String semester = remoteConfig.getString("current_semester_id");
                    Firebase.FIRESTORE
                            .collection("Semester")
                            .document(semester)
                            .collection("Account")
                            .add(acc)
                            .addOnSuccessListener(unused -> Log.d(TAG, "onClick: upload successful"));
                } else if (kind.equals(Kinds.DRINK_FINE)) {
                    // Add to personal balance
                    person.addToBalance(amount);
                    Firebase.FIRESTORE
                            .collection("Person")
                            .document(person.getId())
                            .update("balance", person.getBalance())
                            .addOnSuccessListener(unused -> Log.d(TAG, "onClick: update successful"));
                }
                dialog.dismiss();
                break;
            case -3: // Neutral Button
                Snackbar.make(MainActivity.parentLayout, R.string.error_abort, Snackbar.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: ");
            case -2: // Negative Button
            default: // Something else
                dialog.dismiss();
                break;
        }
    }
}
