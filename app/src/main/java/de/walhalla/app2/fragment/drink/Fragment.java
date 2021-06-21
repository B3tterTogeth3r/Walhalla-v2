package de.walhalla.app2.fragment.drink;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.dialog.search.SearchModel;
import de.walhalla.app2.dialog.search.SearchPersonResult;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.interfaces.Kinds;
import de.walhalla.app2.model.Drink;
import de.walhalla.app2.model.DrinkKind;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.utils.Variables;
import de.walhalla.app2.utils.goHome;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;

import static de.walhalla.app2.interfaces.Kinds.DRINK_FINE;
import static de.walhalla.app2.interfaces.Kinds.DRINK_INVOICE;
import static de.walhalla.app2.interfaces.Kinds.DRINK_PAYMENT;

public class Fragment extends CustomFragment {
    public static final ArrayList<DrinkKind> kindsList = new ArrayList<>();
    private static final String TAG = "drink.Fragment";
    private static final ArrayList<SearchModel> personList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static ArrayList<Person> persons = new ArrayList<>();
    private final ArrayList<Drink> drinkList = new ArrayList<>();
    private LinearLayout layout;
    private LayoutInflater inflater;

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        // [START Listening to the current drink prices]
        registration.add(
                Firebase.FIRESTORE
                        .collection("Kind")
                        .document("Drink")
                        .addSnapshotListener((value, error) -> {
                            if (error != null) {
                                Log.e(TAG, "onEvent: loading drinks did not work", error);
                                Firebase.CRASHLYTICS.log(TAG + "onEvent: loading drinks did not work");
                                Firebase.CRASHLYTICS.recordException(error);
                                return;
                            }
                            if (value != null) {
                                //Format value into objects of DrinkKind
                                try {
                                    //Find all the kinds
                                    Map<String, Object> values = value.getData();
                                    assert values != null;
                                    Set<String> keys = values.keySet();
                                    kindsList.clear();
                                    for (String key : keys) {
                                        try {
                                            Map<String, Object> kind = (Map<String, Object>) values.get(key);
                                            DrinkKind dk = new DrinkKind();
                                            dk.setAvailable(kind.get(DrinkKind.AVAILABLE).toString().equals("true"));
                                            dk.setName(kind.get(DrinkKind.NAME).toString());
                                            dk.setPriceBuy((Float.parseFloat(kind.get(DrinkKind.PRICE_BUY).toString())));
                                            dk.setPriceSell((Map<String, Float>) kind.get(DrinkKind.PRICE_SELL));
                                            kindsList.add(dk);
                                        } catch (Exception e) {
                                            Log.e(TAG, "onEvent: parsing did not work", e);
                                        }
                                    }
                                    Log.d(TAG, "onEvent: kindsList.size(): " + kindsList.size());
                                } catch (Exception e) {
                                    Log.e(TAG, "onEvent: loading drinks did not work", e);
                                    Firebase.CRASHLYTICS.log(TAG + "onEvent: loading drinks did not work");
                                    Firebase.CRASHLYTICS.recordException(e);
                                }

                            }
                        })
        );
        // [END Listening to the current drink prices]

        // [START Listening to all the drinks the signed in user drank in the current semester]
        registration.add(
                Firebase.FIRESTORE
                        .collection("Semester")
                        .document(String.valueOf(App.getCurrentSemester().getID()))
                        .collection("Drink")
                        .whereEqualTo("uid", App.getUser().getUid())
                        .addSnapshotListener(((value, error) -> {
                            if (error != null) {
                                Log.e(TAG, "onEvent: loading user drinks did not work", error);
                                Firebase.CRASHLYTICS.log(TAG + "onEvent: loading user drinks did not work");
                                Firebase.CRASHLYTICS.recordException(error);
                                return;
                            }
                            if (value != null && !value.isEmpty()) {
                                //Format value into objects of Drink
                                try {
                                    //Find all the drinks of the user
                                    drinkList.clear();
                                    for (DocumentSnapshot dS : value.getDocuments()) {
                                        try {
                                            Drink drink = dS.toObject(Drink.class);
                                            drinkList.add(drink);
                                        } catch (Exception e) {
                                            Log.e(TAG, "onEvent: loading user drinks did not work", e);
                                            Firebase.CRASHLYTICS.log(TAG + "onEvent: loading user drinks did not work");
                                            Firebase.CRASHLYTICS.recordException(e);
                                        }
                                    }
                                    //Order list by date
                                    drinkList.sort((o1, o2) -> Integer.compare(o1.getDate().compareTo(o2.getDate()), o2.getDate().compareTo(o1.getDate())));
                                    Log.d(TAG, "start: drinkList.size(): " + drinkList.size());
                                } catch (Exception e) {
                                    Log.e(TAG, "onEvent: loading user drinks did not work", e);
                                    Firebase.CRASHLYTICS.log(TAG + "onEvent: loading user drinks did not work");
                                    Firebase.CRASHLYTICS.recordException(e);
                                }
                                fillTable();
                            }
                        }))
        );
        // [END Listening to all the drinks the signed in user drank in the current semester]
    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {
        Fragment.context = getContext();
        this.layout = view.findViewById(R.id.fragment_container);
        this.inflater = inflater;
    }

    @Override
    public void viewCreated() {
        fillTable();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void toolbarContent() {
        toolbar.setTitle(R.string.menu_drinks);
        toolbar.inflateMenu(R.menu.drink);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_send_reminder:
                    Drawable beer = DrawableCompat.wrap(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_announcement_24)));
                    DrawableCompat.setTint(beer, Color.BLACK);
                    AlertDialog.Builder reminderBuilder = new AlertDialog.Builder(getContext());
                    reminderBuilder.setIcon(beer)
                            .setTitle(R.string.send_reminder)
                            .setMessage(R.string.reminder_message)
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                //TODO Send message via cloud functions
                                Toast.makeText(
                                        requireContext(), R.string.error_dev, Toast.LENGTH_LONG)
                                        .show();
                            })
                            .setNeutralButton(R.string.no, null);
                    AlertDialog reminderDialog = reminderBuilder.create();
                    reminderDialog.show();
                    break;
                case R.id.menu_drink_invoice:
                    Log.d(TAG, "toolbarContent: add new invoice");
                    /* Shows search dialog for a person who drank something. After a person
                     * got selected, a new dialog with all the kinds of drinks are shown with two
                     * buttons left and right like | ++ + | amount | - -- | to add them to the
                     * persons drink bill. also the cost of these drinks will be reduced from the
                     * persons balance.
                     */
                    loadSearchDialog(DRINK_INVOICE);
                    break;
                case R.id.menu_payment:
                    Log.d(TAG, "toolbarContent: a member payed something");
                    /* Shows a search dialog for a person who payed something. After a person got
                     * selected, a new dialog with the persons name and an input field for numbers
                     * and a description will be displayed. The amount will be added to the balance
                     * of the selected person and added as a new entry to the fraternity account.
                     */
                    loadSearchDialog(DRINK_PAYMENT);
                    break;
                case R.id.menu_drink_fine:
                    /* Shows a search dialog for a person who got a fine. After a person got
                     * selected, a new dialog with the persons name and an number input field will
                     * be displayed. The amount will be reduced from the persons balance.
                     */
                    loadSearchDialog(DRINK_FINE);
                    break;
                default:
                    break;
            }
            return false;
        });
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
        toolbarContent();
    }

    @Override
    public void stop() {

    }

    @SuppressLint("InflateParams")
    private void fillTable() {
        layout.removeAllViewsInLayout();
        HorizontalScrollView hsc = (HorizontalScrollView) inflater.inflate(R.layout.layout_custom_table, null);
        TableLayout tableLayout = hsc.findViewById(R.id.table);

        // [START Add title Row]
        TableRow row = (TableRow) inflater.inflate(R.layout.layout_custom_table_row, null);
        TextView date = (TextView) inflater.inflate(R.layout.layout_custom_subtitle2, null);
        TextView amount = (TextView) inflater.inflate(R.layout.layout_custom_subtitle2, null);
        TextView value = (TextView) inflater.inflate(R.layout.layout_custom_subtitle2, null);
        TextView name = (TextView) inflater.inflate(R.layout.layout_custom_subtitle2, null);

        date.setText(R.string.date);
        amount.setText(R.string.amount);
        value.setText(R.string.price);
        name.setText(R.string.name);

        row.addView(date);
        row.addView(amount);
        row.addView(value);
        row.addView(name);
        tableLayout.addView(row);
        // [END Add title Row]

        for (Drink drink : drinkList) {
            row = (TableRow) inflater.inflate(R.layout.layout_custom_table_row, null);
            date = (TextView) inflater.inflate(R.layout.layout_custom_text, null);
            amount = (TextView) inflater.inflate(R.layout.layout_custom_text, null);
            value = (TextView) inflater.inflate(R.layout.layout_custom_text, null);
            name = (TextView) inflater.inflate(R.layout.layout_custom_text, null);

            // Format date
            Calendar c = Calendar.getInstance();
            c.setTime(drink.getDate().toDate());
            String helper = c.get(Calendar.DAY_OF_MONTH) + ". " +
                    Variables.MONTHS[c.get(Calendar.MONTH)] + " " +
                    c.get(Calendar.YEAR);
            date.setText(helper);

            amount.setText(String.valueOf(drink.getAmount()));
            String price = "€ " + (drink.getAmount() * drink.getPrice());
            value.setText(price);
            name.setText(drink.getKind());

            row.addView(date);
            row.addView(amount);
            row.addView(value);
            row.addView(name);
            tableLayout.addView(row);
        }
        layout.addView(hsc);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void loadSearchDialog(final String kind) {
        final String title = getString(R.string.menu_beer_invoice);
        final String hint = getString(R.string.search);
        Firebase.FIRESTORE
                .collection("Person")
                .get()
                .addOnCompleteListener(task -> {
                    personList.clear();
                    persons.clear();
                    //Log exception and send it to Crashlytics
                    if (task.getException() != null) {
                        Log.e(TAG, "onComplete: downloading persons did not work",
                                task.getException());
                        Firebase.CRASHLYTICS
                                .log("Downloading persons for search dialog did not work");
                        Firebase.CRASHLYTICS
                                .recordException(task.getException());
                        Firebase.CRASHLYTICS
                                .sendUnsentReports();
                    }
                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot ds : task.getResult()) {
                            Person p = ds.toObject(Person.class);
                            if (p != null) {
                                String rank = p.getRank();
                                if (rank.equals(Kinds.RANK_ACTIVE) | rank.equals(Kinds.RANK_AH)) {
                                    p.setId(ds.getId());
                                    persons.add(p);
                                }
                            } else {
                                Log.e(TAG, "onEvent: unable to format person");
                                Firebase.CRASHLYTICS.log("drink: onStart: onEvent: Person empty");
                                Firebase.CRASHLYTICS.sendUnsentReports();
                            }
                        }
                        //Order list by last name
                        persons.sort((o1, o2) -> o1.getLast_Name().compareTo(o2.getLast_Name()));
                        for (Person p : persons) {
                            personList.add(new SearchModel(p.getFullName()));
                        }
                        new SimpleSearchDialogCompat(getActivity(), title, hint, null,
                                personList, new SearchPersonResult(kind)).show();
                    }
                })
        ;
    }
}