package de.walhalla.app2.fragment.balance;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.utils.Variables;
import de.walhalla.app2.utils.goHome;

public class Fragment extends CustomFragment {
    private static final String TAG = "balance.Fragment";
    private Person person;
    private RelativeLayout layout;
    private LayoutInflater inflater;
    private TextView amount;

    @Override
    public void start() {
        registration.add(
                Firebase.FIRESTORE
                        .collection("Person")
                        .document(App.getUser().getId())
                        .addSnapshotListener((value, error) -> {
                            if (error != null) {
                                // Go back to home
                                authChange();
                            }
                            if (value != null && value.exists()) {
                                try {
                                    person = value.toObject(Person.class);
                                    updateAmount();
                                } catch (Exception e) {
                                    // Go back to home
                                    authChange();
                                }
                            } else {
                                // Go back to home
                                authChange();
                            }
                        })
        );
    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {
        LinearLayout linearLayout = view.findViewById(R.id.fragment_container);
        ScrollView sc = new ScrollView(getContext());
        layout = new RelativeLayout(getContext());
        sc.removeAllViews();
        linearLayout.removeAllViews();
        layout.removeAllViews();
        sc.addView(layout);
        linearLayout.addView(sc);
        this.inflater = inflater;
    }

    @Override
    public void viewCreated() {
        // [START Creating the title textview]
        // filling it with content
        TextView title = (TextView) inflater.inflate(R.layout.layout_custom_title, null);
        title.setId(R.id.txt_title);
        title.setText(R.string.balance_title);
        layout.addView(title);
        // [END Creating the title textview]

        // [START Creating amount textview]
        // Filling it with content, changing the design and moving it below the title
        amount = (TextView) inflater.inflate(R.layout.layout_custom_text, null);
        amount.setId(R.id.txt_amount);
        amount.setTextSize(TypedValue.COMPLEX_UNIT_PT, 30f);
        amount.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        amount.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        RelativeLayout.LayoutParams amountParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        amountParams.addRule(RelativeLayout.BELOW, title.getId());
        amountParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        amountParams.setMargins(20, 20, 20, 20);

        layout.addView(amount, amountParams);
        // [END Creating amount textview]

        // [START Creating a description textview]
        // to describe what the amount means and moving it below the amount
        TextView description = (TextView) inflater.inflate(R.layout.layout_custom_text, null);
        description.setId(R.id.txt_description);
        description.setText(R.string.balance_description);

        RelativeLayout.LayoutParams descParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        descParams.addRule(RelativeLayout.BELOW, amount.getId());

        layout.addView(description, descParams);
        // [END Creating a description textview]
    }

    @Override
    public void toolbarContent() {

    }

    @Override
    public void authChange() {
        //Go to home
        try {
            if (Firebase.AUTHENTICATION.getCurrentUser() == null) {
                new goHome(getParentFragmentManager());
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }

    @Override
    public void displayChange() {

    }

    @Override
    public void stop() {

    }

    /**
     * Setting the new amount inside the text view {@link #amount}
     */
    private void updateAmount() {
        try {
            String string = "€ " + String.format(Variables.LOCALE, "%.2f", person.getBalance());
            amount.setText(string);
        } catch (Exception e) {
            Log.e(TAG, "updateAmount: formatting did not work", e);
        }
    }
}