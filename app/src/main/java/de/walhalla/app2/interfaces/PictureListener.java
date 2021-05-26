package de.walhalla.app2.interfaces;

import android.graphics.Bitmap;

/**
 * An interface for the {@link de.walhalla.app2.utils.ImageDownload ImageDownload.class}
 * <p>has the following methods:
 * <ul>
 *     <li>{@link #downloadDone}</li>
 *     <li>{@link #setProgressBar}</li>
 *     <li>{@link #descriptionDone(String)}</li>
 *     <li>{@link #nextImage()}</li>
 *     <li>{@link #previousImage()}</li>
 * </ul>
 *
 * @author B3tterTogeth3r
 * @version 1.0
 * @since 2.0
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

    /**
     * The next image is to be loaded and shown
     */
    default void nextImage() {
    }

    /**
     * The previous image is to be loaded and shown
     */
    default void previousImage() {
    }
}
