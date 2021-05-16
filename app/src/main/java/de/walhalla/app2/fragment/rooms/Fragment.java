package de.walhalla.app2.fragment.rooms;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.utils.Sites;

/**
 * On this fragment the rooms inside the fraternities house are a little
 * bit more described. Also a small table with distance to the biggest
 * buildings of the university and detailed images of the rooms are shown.
 * At the bottom should be buttons to contact the fraternity through online
 * services, e-mail or<i>, if available,</i> phone number.
 * <br><b><font color=#DAB020>TODO Maybe the phone number of the <i>Senior</i></font></b>
 */
public class Fragment extends CustomFragment {
    private static final String TAG = "rooms.Fragment";
    private final ArrayList<Map<String, Object>> download = new ArrayList<>();
    private LinearLayout layout;

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        registration.add(Firebase.FIRESTORE
                .collection("Sites")
                .document("rooms")
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "start: error getting about_us", error);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        this.download.clear();
                        for (Map.Entry<String, Object> line : Objects.requireNonNull(documentSnapshot.getData()).entrySet()) {
                            Log.d(TAG, "start: " + line.getKey());
                            this.download.add((Map<String, Object>) line.getValue());
                        }
                        setData();
                    }
                })
        );
    }

    @Override
    public void createView(@NonNull @NotNull View view, @NonNull @NotNull LayoutInflater inflater) {
        LinearLayout l = view.findViewById(R.id.fragment_container);
        l.setOrientation(LinearLayout.VERTICAL);
        ScrollView scrollView = new ScrollView(getContext());
        layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(layout);
        l.addView(scrollView);
    }

    @Override
    public void viewCreated() {
        setData();
    }

    @Override
    public void toolbarContent() {
        toolbar.setTitle(R.string.menu_rooms);
    }

    @Override
    public void authChange() {

    }

    @Override
    public void displayChange() {

    }

    @Override
    public void stop() {

    }

    @SuppressLint("InflateParams")
    private void setData() {
        try {
            if (getContext() != null && layout != null) {
                layout.removeAllViewsInLayout();
                for (Map<String, Object> one : download) {
                    LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_custom_linear_layout, null);
                    Sites.create(getContext(), layout, one);
                    this.layout.addView(layout);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "setData: an error occurred", e);
        }
    }
}