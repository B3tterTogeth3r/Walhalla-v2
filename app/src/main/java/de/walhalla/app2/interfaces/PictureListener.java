package de.walhalla.app2.interfaces;

import android.graphics.Bitmap;

/**
 * An interface for the {@link de.walhalla.app2.utils.ImageDownload ImageDownload.class}
 * <p>has 5 methods
 * <br>{@link #downloadDone}
 * <br>{@link #setProgressBar}
 * <br>{@link #descriptionDone(String)}
 * <br>{@link #nextImage()}
 * <br>{@link #previousImage()}
 */
public interface PictureListener {
    /**
     * is triggered when the
     * {@link de.walhalla.app2.utils.ImageDownload#doInBackground(Void...) downlad} of the Task is done
     *
     * @param imageBitmap the returning bitmap of the image
     */
    void downloadDone(Bitmap imageBitmap);

    /**
     * should send the progress of the
     * {@link de.walhalla.app2.utils.ImageDownload#onProgressUpdate(Integer...) task} downloading the image
     *
     * @param progress the value of the progress
     */
    default void setProgressBar(int progress) {
    }

    /**
     * triggered on done download of the description to the previous loaded image.
     *
     * @param description the value of the description
     */
    default void descriptionDone(String description) {
    }

    default void nextImage() {
    }

    default void previousImage() {
    }
}
