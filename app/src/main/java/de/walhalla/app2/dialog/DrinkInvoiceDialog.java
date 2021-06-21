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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.fragment.drink.Fragment;
import de.walhalla.app2.model.Drink;
import de.walhalla.app2.model.DrinkKind;
import de.walhalla.app2.model.Person;

/**
 * @author B3tterTogeth3r
 * @version 1.0
 * @see android.app.AlertDialog.Builder
 * @see android.content.DialogInterface.OnClickListener
 * @since 2.5
 */
public class DrinkInvoiceDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {
    private static final String TAG = "InvoiceDialog";
    private final Person person;
    private final ArrayList<Drink> drinkList = new ArrayList<>();
    private final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    @SuppressLint("InflateParams")
    public DrinkInvoiceDialog(Context context, @NotNull Person person) {
        super(context);
        this.person = person;
        setTitle(person.getFullName());

        Drawable beer = DrawableCompat.wrap(Objects.requireNonNull(AppCompatResources.getDrawable(getContext(), R.drawable.ic_beer)));
        DrawableCompat.setTint(beer, Color.BLACK);
        setIcon(beer);

        setCancelable(false);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.layout_custom_linear_layout, null);
        layout.removeAllViewsInLayout();
        for (DrinkKind dk : Fragment.kindsList) {
            Log.d(TAG, "InvoiceDialog: kind.name: " + dk.getName());
            // add one item of "item_drink_kind", fill it with the correct data and listeners
            RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.item_drink_kind, null);
            //Get price sell
            float price = 0;
            try {
                price = Float.parseFloat(String.valueOf(dk.getPriceSell().get(person.getRank())));
            } catch (Exception e) {
                Log.e(TAG, "InvoiceDialog: finding price did not work", e);
            }
            Drink drink = new Drink(0, price, person.getUid(), dk.getName());
            Button plusOne = relativeLayout.findViewById(R.id.drink_kind_plus);
            Button plusTen = relativeLayout.findViewById(R.id.drink_kind_plus_ten);
            Button minusOne = relativeLayout.findViewById(R.id.drink_kind_minus);
            Button minusTen = relativeLayout.findViewById(R.id.drink_kind_minus_ten);
            ((TextView) relativeLayout.findViewById(R.id.drink_kind_name)).setText(dk.getName());
            TextView counter = relativeLayout.findViewById(R.id.drink_kind_counter);
            plusOne.setOnClickListener(v -> {
                drink.setAmount(drink.getAmount() + 1);
                counter.setText(String.valueOf(drink.getAmount()));
            });
            plusTen.setOnClickListener(v -> {
                drink.setAmount(drink.getAmount() + 10);
                counter.setText(String.valueOf(drink.getAmount()));
            });
            minusOne.setOnClickListener(v -> {
                if (drink.getAmount() != 0) {
                    drink.setAmount(drink.getAmount() - 1);
                }
                counter.setText(String.valueOf(drink.getAmount()));
            });
            minusTen.setOnClickListener(v -> {
                if (drink.getAmount() <= 10) {
                    drink.setAmount(0);
                } else {
                    drink.setAmount(drink.getAmount() - 10);
                }
                counter.setText(String.valueOf(drink.getAmount()));
            });
            drinkList.add(drink);
            layout.addView(relativeLayout);
        }
        setView(layout);

        setNeutralButton(R.string.abort, this);
        setPositiveButton(R.string.send, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case -1: // Positive Button
                // Upload data sets where amount > 0
                Log.d(TAG, "onClick: changing " + person.getFullName() + " balance from " + person.getBalance());
                float totalPayment = 0f;
                for (Drink d : drinkList) {
                    int amount = d.getAmount();
                    // increase total payment and upload d to firestore
                    if (amount != 0) {
                        totalPayment = totalPayment + (amount * d.getPrice());
                        String semester = remoteConfig.getString("current_semester_id");
                        Firebase.FIRESTORE
                                .collection("Semester")
                                .document(semester)
                                .collection("Drink")
                                .add(d)
                                .addOnSuccessListener(documentReference ->
                                        Log.d(TAG, "onSuccess: upload of new drink successful"));
                    }
                }
                // Update selected persons balance and update firestore
                person.addToBalance(totalPayment);
                Log.d(TAG, "to " + person.getBalance() + " with the id value of " + person.getId());
                Firebase.FIRESTORE
                        .collection("Person")
                        .document(person.getId())
                        .update("balance", person.getBalance())
                        .addOnSuccessListener(unused -> Log.d(TAG, "onClick: update successful"));
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
