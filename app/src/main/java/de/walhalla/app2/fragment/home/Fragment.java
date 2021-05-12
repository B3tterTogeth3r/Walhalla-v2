package de.walhalla.app2.fragment.home;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.firestore.MetadataChanges;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Semester;

public class Fragment extends CustomFragment {
    private static final String TAG = "home.Fragment";
    private final ArrayList<Object> greeting = new ArrayList<>();
    private final ArrayList<Object> imageSlider = new ArrayList<>();
    private final ArrayList<Object> notes = new ArrayList<>();
    private LinearLayout layout;
    private LayoutInflater inflater;
    private Semester semester = new Semester();

    @Override
    public void start() {
        Log.e(TAG, "start: is running");
        registration.add(Firebase.FIRESTORE
                .collection("Semester")
                .document(String.valueOf(App.getCurrentSemester().getID()))
                .addSnapshotListener(MetadataChanges.INCLUDE, ((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "start: semester listener failed", error);
                        return;
                    }
                    if (value != null && value.exists()) {
                        try {
                            semester = value.toObject(Semester.class);
                            formatSemesterResult(Objects.requireNonNull(semester));
                        } catch (Exception e) {
                            Log.e(TAG, "start: format to semester did not work", e);
                        }
                    }
                }))
        );
        registration.add(Firebase.FIRESTORE
                .collection("Diashow")
                .document("home")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "start: images listener failed", error);
                    }
                    if (value != null && value.exists()) {
                        try {
                            @SuppressWarnings("unchecked")
                            List<Object> imageList = (List<Object>) value.get("picture_names");
                            formatImagesResult(imageList);
                        } catch (Exception e) {
                            Log.e(TAG, "start: format images", e);
                        }
                    }
                })
        );
    }

    private void formatSemesterResult(@NotNull Semester semester) {
        //Greeting
        greeting.addAll(Collections.singletonList(semester.getGreeting()));
        //Notes
        notes.addAll(semester.getNotes());
    }

    private void formatImagesResult(@Nullable List<Object> imageList) {
        imageSlider.add(imageList);
    }

    @SuppressLint("InflateParams")
    @Override
    public void createView(@NotNull View view, @NotNull LayoutInflater inflater) {
        Log.e(TAG, "createView: running");
        LinearLayout l = view.findViewById(R.id.fragment_container);
        l.setOrientation(LinearLayout.VERTICAL);
        this.inflater = getLayoutInflater();
        ScrollView scrollView = new ScrollView(requireContext());
        layout = (LinearLayout) inflater.inflate(R.layout.layout_custom_linear_layout, null);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(loadGreeting(semester.getGreeting()));

        scrollView.addView(layout);
        l.addView(scrollView);
    }

    /**
     * Sets the greeting of the selected semester. The first row
     * is the salutation, the last row the closing formula
     *
     * @param text The list with all the paragraphs of the greeting
     */
    private LinearLayout loadGreeting(ArrayList<Object> text) {
        LinearLayout greeting = (LinearLayout) inflater.inflate(R.layout.layout_custom_linear_layout, null);
        Log.d(TAG, "loadGreeting: " + text.size());
        if (text != null && !text.isEmpty()) {
            Log.d(TAG, "loadGreeting: " + text.size());
            greeting.setOrientation(LinearLayout.VERTICAL);
            greeting.setAnimation((Animation) requireContext().getResources().getAnimation(R.anim.fade_in));
            TextView header = (TextView) inflater.inflate(R.layout.layout_custom_title, null);
            header.setText(R.string.greeting);
            greeting.addView(header);
            for (Object t : text) {
                TextView tv = (TextView) inflater.inflate(R.layout.layout_custom_text, null);
                tv.setText(t.toString());
                greeting.addView(tv);
            }
        }
        return greeting;
    }

    @Override
    public void viewCreated() {
        Log.d(TAG, "viewCreated: greeting.size(): " + semester.getGreeting().size());
    }

    @Override
    public void toolbarContent() {
        toolbar.setTitle("Walhalla");
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
}