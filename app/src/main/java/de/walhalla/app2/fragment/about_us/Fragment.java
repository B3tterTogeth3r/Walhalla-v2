package de.walhalla.app2.fragment.about_us;

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
 * This fragment displays in some short paragraphs the principals of the fraternity and who they are looking for
 */
public class Fragment extends CustomFragment {
    private static final String TAG = "about_us.Fragment";
    private final ArrayList<Map<String, Object>> download = new ArrayList<>();
    private LinearLayout layout;

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        registration.add(Firebase.FIRESTORE
                .collection("Sites")
                .document("About_us")
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
    public void createView(@NotNull View view, @NonNull @NotNull LayoutInflater inflater) {
        LinearLayout l = view.findViewById(R.id.fragment_container);
        l.setOrientation(LinearLayout.VERTICAL);
        ScrollView scrollView = new ScrollView(requireContext());
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
        toolbar.setTitle(R.string.menu_about_us);
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
                for (int i = download.size() - 1; 0 <= i; i--) {
                    Map<String, Object> one = download.get(i);
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