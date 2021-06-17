package de.walhalla.app2.fragment.drink;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Drink;
import de.walhalla.app2.model.DrinkKind;
import de.walhalla.app2.utils.goHome;

public class Fragment extends CustomFragment {
    private static final String TAG = "drink.Fragment";
    private final ArrayList<DrinkKind> kindsList = new ArrayList<>();
    private final ArrayList<Drink> drinkList = new ArrayList<>();

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

                            }
                        }))
        );
        // [END Listening to all the drinks the signed in user drank in the current semester]
    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {
        LinearLayout layout = view.findViewById(R.id.fragment_container);
        layout.removeAllViewsInLayout();

    }

    @Override
    public void viewCreated() {

    }

    @Override
    public void toolbarContent() {
        toolbar.setTitle(R.string.menu_beer);
    }

    @Override
    public void authChange() {
        //Go to home
        try {
            new goHome(getParentFragmentManager());
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
}