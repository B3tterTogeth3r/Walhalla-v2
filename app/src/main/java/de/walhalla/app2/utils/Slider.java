package de.walhalla.app2.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.walhalla.app2.R;
import de.walhalla.app2.interfaces.PictureListener;

/**
 * This class is to display an image at the given point in the ui.
 * The RelativeLayout <u>imageLayout</u> has to be the layout <i>image_slider</i>.
 * if the list has more than one item:
 * <ul>
 *     <li>the image changes by clicking on the ImageButtons left or right</li>
 *     <li><b>TODO</b> By swiping right or left the image changes <br></li>
 *     <li><b>TODO</b> The layout should change the image on its own</li>
 * </ul>
 */
public class Slider {
    private static final String TAG = "Slider";
    private static int imagePosition = 0;

    /**
     * Sets the images into the layout
     *
     * @param imageLayout The layout to display the text
     * @param imageSlider The List with all image paths.
     */
    public static void load(RelativeLayout imageLayout, ArrayList<Object> imageSlider) {
        if (imageSlider != null && !imageSlider.isEmpty()) {
            try {
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
                }, imagePath, true).execute();
                if (size != 1) {
                    left.setVisibility(View.VISIBLE);
                    right.setVisibility(View.VISIBLE);
                    left.setOnClickListener(v -> {
                        if (imagePosition <= 0) {
                            imagePosition = size;
                        }
                        imagePosition--;
                        String imagePath2 = imageSlider.get(imagePosition).toString();
                        new ImageDownload(new PictureListener() {
                            @Override
                            public void downloadDone(Bitmap imageBitmap) {
                                image.setImageBitmap(imageBitmap);
                            }

                            @Override
                            public void descriptionDone(String description) {
                                descriptionTV.setText(description);
                            }
                        }, imagePath2, true).execute();
                    });
                    right.setOnClickListener(v -> {
                        if (imagePosition == size - 1) {
                            imagePosition = -1;
                        }
                        imagePosition++;
                        String imagePath2 = imageSlider.get(imagePosition).toString();
                        new ImageDownload(new PictureListener() {
                            @Override
                            public void downloadDone(Bitmap imageBitmap) {
                                image.setImageBitmap(imageBitmap);
                            }

                            @Override
                            public void descriptionDone(String description) {
                                descriptionTV.setText(description);
                            }
                        }, imagePath2, true).execute();
                    });
                    //TODO Swipe gestures
                    //TODO Automatic image change
                }
            } catch (Exception e) {
                Log.e(TAG, "load: wrong layout used in creation", e);
            }
        }

    }
}
