package de.walhalla.app2.fragment.home;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.firestore.MetadataChanges;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.abstraction.CustomFragment;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.interfaces.PictureListener;
import de.walhalla.app2.model.Semester;
import de.walhalla.app2.utils.ImageDownload;

public class Fragment extends CustomFragment {
    private static final String TAG = "home.Fragment";
    private List<Object> greeting = new ArrayList<>();
    private ArrayList<Object> imageSlider = new ArrayList<>();
    private List<Object> notes = new ArrayList<>();
    private LinearLayout greetingLayout, notesLayout;
    private RelativeLayout imageLayout;
    private LayoutInflater inflater;
    private Semester semester = new Semester();
    private int imagePosition = 0;

    @Override
    public void start() {
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
                            formatImagesResult(Objects.requireNonNull(imageList));
                        } catch (Exception e) {
                            Log.e(TAG, "start: format images", e);
                        }
                    }
                })
        );
    }

    @SuppressLint("InflateParams")
    @Override
    public void createView(@NotNull View view, @NotNull LayoutInflater inflater) {
        //Log.d(TAG, "createView: running");
        LinearLayout l = view.findViewById(R.id.fragment_container);
        l.setOrientation(LinearLayout.VERTICAL);
        this.inflater = getLayoutInflater();
        ScrollView scrollView = new ScrollView(requireContext());
        LinearLayout layout = new LinearLayout(requireContext());
        greetingLayout = (LinearLayout) inflater.inflate(R.layout.layout_custom_linear_layout, null);

        imageLayout = (RelativeLayout) inflater.inflate(R.layout.image_slider, null);
        notesLayout = (LinearLayout) inflater.inflate(R.layout.layout_custom_linear_layout, null);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(greetingLayout);
        layout.addView(imageLayout);
        layout.addView(notesLayout);

        scrollView.addView(layout);
        l.addView(scrollView);
    }

    @Override
    public void viewCreated() {
        Animation anim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
        greetingLayout.setAnimation(anim);
        notesLayout.startAnimation(anim);
        loadGreeting(greetingLayout, greeting);
        loadNotes(notesLayout, notes);
        loadImageSlider(imageLayout, imageSlider);
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

    /**
     * Read the greeting and news out of the current semester and display them in their own LinearLayout
     *
     * @param semester the data of the current semester
     */
    private void formatSemesterResult(@NotNull Semester semester) {
        //Greeting
        greeting = new ArrayList<>(Arrays.asList(semester.getGreeting().toArray()));
        greetingLayout.removeAllViewsInLayout();
        greetingLayout.removeAllViews();
        loadGreeting(greetingLayout, greeting);
        //Notes
        notes = new ArrayList<>(semester.getNotes());
        loadNotes(notesLayout, notes);
    }

    /**
     * format the found data into a list
     *
     * @param imageList List of the images to display
     */
    private void formatImagesResult(@NotNull List<Object> imageList) {
        imageSlider = new ArrayList<>(imageList);
        //Log.e(TAG, "formatImagesResult: imageSlider.size(): "  + imageSlider.size());
        loadImageSlider(imageLayout, imageSlider);
    }

    /**
     * Sets the greeting of the selected semester. The first row
     * is the salutation, the last row the closing formula
     *
     * @param linearLayout The layout to display the text
     * @param text         The list with all the paragraphs of the greeting
     */
    @SuppressWarnings("unchecked")
    @SuppressLint("InflateParams")
    private void loadGreeting(LinearLayout linearLayout, List<Object> text) {
        if (text != null && !text.isEmpty()) {
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            int size = text.size() - 1;
            Map<String, String> sign = (Map<String, String>) text.get(size);
            text.remove(size);
            size--;

            TextView header = (TextView) inflater.inflate(R.layout.layout_custom_title, null);
            header.setText(R.string.greeting);
            linearLayout.addView(header);
            for (int i = 0; i < size; i++) {
                Object t = text.get(i);
                TextView tv = (TextView) inflater.inflate(R.layout.layout_custom_text, null);
                tv.setText(t.toString());
                linearLayout.addView(tv);
            }
            TextView bottom = (TextView) inflater.inflate(R.layout.layout_custom_subtitle2, null);
            bottom.setText(text.get(size).toString());
            linearLayout.addView(bottom);
            RelativeLayout signRow = (RelativeLayout) inflater.inflate(R.layout.item_greeting_bottom, null);
            TextView studentX = signRow.findViewById(R.id.greeting_x);
            TextView philX = signRow.findViewById(R.id.greeting_ahx);
            studentX.setText(sign.get("Aktivensenior"));
            philX.setText(sign.get("Philistersenior"));
            linearLayout.addView(signRow);
        }
    }

    /**
     * Sets the current notes of the Semester.
     *
     * @param notes The layout to display the text
     * @param text  The List with all notes.
     */
    private void loadNotes(@NotNull LinearLayout notes, List<Object> text) {
        if (text != null && !text.isEmpty()) {
            int size = text.size();
            notes.removeAllViewsInLayout();
            notes.setOrientation(LinearLayout.VERTICAL);
            TextView title = (TextView) inflater.inflate(R.layout.layout_custom_subtitle, null);
            title.setText(R.string.home_notes);
            notes.addView(title);
            for (int i = 0; i < size; i++) {
                try {
                    TextView content = (TextView) inflater.inflate(R.layout.layout_custom_text, null);
                    content.setText(text.get(i).toString());
                    notes.addView(content);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Sets the images into the layout
     *
     * @param imageLayout The layout to display the text
     * @param imageSlider The List with all image paths.
     */
    private void loadImageSlider(RelativeLayout imageLayout, ArrayList<Object> imageSlider) {
        if (imageSlider != null && !imageSlider.isEmpty()) {
            imagePosition = 0;
            int size = imageSlider.size();
            ImageButton left = imageLayout.findViewById(R.id.diashow_previous);
            ImageButton right = imageLayout.findViewById(R.id.diashow_next);
            final ImageView image = imageLayout.findViewById(R.id.diashow_image);
            TextView descriptionTV = imageLayout.findViewById(R.id.diashow_description);
            String imagePath = imageSlider.get(imagePosition).toString();
            new ImageDownload(new PictureListener() {
                @Override
                public void downloadDone(Bitmap imageBitmap) {
                    image.setImageBitmap(imageBitmap);
                }

                @Override
                public void descriptionDone(String description) {
                    descriptionTV.setText(description);
                }
            }, imagePath, false, true).execute();
            if (size != 1) {
                left.setVisibility(View.VISIBLE);
                right.setVisibility(View.VISIBLE);
                left.setOnClickListener(v -> {
                    if (imagePosition == 0) {
                        imagePosition = size;
                    }
                    imagePosition--;
                    String imagePath2 = imageSlider.get(imagePosition - 1).toString();
                    new ImageDownload(new PictureListener() {
                        @Override
                        public void downloadDone(Bitmap imageBitmap) {
                            image.setImageBitmap(imageBitmap);
                        }

                        @Override
                        public void descriptionDone(String description) {
                            descriptionTV.setText(description);
                        }
                    }, imagePath2, false, true).execute();
                });
                right.setOnClickListener(v -> {
                    if (imagePosition == size) {
                        imagePosition = 0;
                    }
                    imagePosition++;
                    String imagePath2 = imageSlider.get(imagePosition - 1).toString();
                    new ImageDownload(new PictureListener() {
                        @Override
                        public void downloadDone(Bitmap imageBitmap) {
                            image.setImageBitmap(imageBitmap);
                        }

                        @Override
                        public void descriptionDone(String description) {
                            descriptionTV.setText(description);
                        }
                    }, imagePath2, false, true).execute();
                });
            }
        }
    }
}