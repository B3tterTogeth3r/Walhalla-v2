package de.walhalla.app2.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.interfaces.PictureListener;

/**
 * A custom class written by Tobias Tumbrink to download images from the
 * Firebase cloud storage with different methods and constructors.
 * <p>
 * The class is an AsyncTask so the image gets downloaded in the background
 * of the app and only after finish displayed onto the ui
 * <p>
 * I would have done the same thing an a usual Runnable or Thread, but
 * for some reason always the screen froze. I couldn't figure out, why,
 * So I hat to use an empty AsyncTask.
 */
public class ImageDownload extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = "ImageDownload";
    private final PictureListener listener;
    private final String reference;
    private final boolean addWatermark, loadDescription;
    private Bitmap imageBitmap;

    /**
     * Constructor of the {@link ImageDownload ImageDownload.class} with the most params.
     *
     * @param listener        PictureListener:
     *                        is used to return the downloaded image to the thread the listener is in
     * @param reference       String:
     *                        is the whole path to the image in the Firebase Storage bucket.
     * @param addWatermark    boolean:
     *                        should the image have a watermark? Default: false
     * @param loadDescription boolean:
     *                        if the image has a description in the Firestore Database
     *                        the name will be downloaded and displayed at bottom of the image.
     *                        if not, no name will be displayed.
     */
    public ImageDownload(PictureListener listener, String reference, boolean addWatermark, boolean loadDescription) {
        this.listener = listener;
        this.reference = reference;
        this.addWatermark = addWatermark;
        this.loadDescription = loadDescription;
    }

    /**
     * Constructor of the {@link ImageDownload ImageDownload.class} with no description
     *
     * @param listener     PictureListener:
     *                     is used to return the downloaded image to the thread the listener is in
     * @param reference    String:
     *                     is the whole path to the image in the Firebase Storage bucket.
     * @param addWatermark boolean:
     *                     should the image have a watermark? Default: false
     */
    public ImageDownload(PictureListener listener, String reference, boolean addWatermark) {
        this.listener = listener;
        this.reference = reference;
        this.addWatermark = addWatermark;
        this.loadDescription = false;
    }

    /**
     * Constructor of the {@link ImageDownload ImageDownload.class} with the watermark or description
     *
     * @param listener  PictureListener:
     *                  is used to return the downloaded image to the thread the listener is in
     * @param reference String:
     *                  is the whole path to the image in the Firebase Storage bucket.
     */
    public ImageDownload(PictureListener listener, String reference) {
        this.listener = listener;
        this.reference = reference;
        this.addWatermark = false;
        this.loadDescription = false;
    }

    /**
     * downloads the image, if the {@link #listener} is not null.
     *
     * @param voids No parameters needed; all parameters are given to the class in its constructor
     * @return VOID because of runtime errors listeners are used.
     */
    @Override
    protected Void doInBackground(Void... voids) {
        if (listener != null) {
            StorageReference image = FirebaseStorage.getInstance().getReference(reference);
            image.getBytes(Variables.ONE_MEGABYTE)
                    .addOnSuccessListener(bytes -> {
                        imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (addWatermark) {
                            imageBitmap = addWatermark(imageBitmap);
                        }
                        //send download result to the starting listener
                        listener.downloadDone(imageBitmap);
                        if (loadDescription) {
                            loadImageDescription();
                        }
                    })
                    .addOnFailureListener(e ->
                            Log.e(TAG, "image download unsuccessful", e))
                    .addOnCanceledListener(() -> Log.d(TAG, "onCanceled"));
        } else {
            Log.d(TAG, "run: no listener available");
        }
        return null;
    }

    /**
     * load the description of the image from the Firestore database.
     * the result is send to the {@link PictureListener#descriptionDone(String) descriptionDone}
     * listener of the PictureListener.
     */
    private void loadImageDescription() {
        Firebase.FIRESTORE.collection("Data")
                .whereEqualTo("name", reference)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            listener.descriptionDone((String) documentSnapshot.get("title"));
                        }
                    } else {
                        listener.descriptionDone(null);
                    }
                })
                .addOnFailureListener(e -> listener.descriptionDone(null));
    }

    /**
     * @param source send the bitmap, that should get a watermark
     * @return the image with a watermark at the top right corner of its original image
     */
    private Bitmap addWatermark(@NotNull Bitmap source) {
        float ratio = 0.3f;
        Canvas canvas;
        Paint paint;
        Bitmap bmp;
        Matrix matrix;
        RectF r;

        Bitmap watermark = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.wappen_herz);

        int width, height;
        float scale;

        width = source.getWidth();
        height = source.getHeight();

        // Create the new bitmap
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

        // Copy the original bitmap into the new one
        canvas = new Canvas(bmp);
        canvas.drawBitmap(source, 0, 0, paint);

        // Scale the watermark to be approximately to the ratio given of the source image height
        scale = ((float) height * ratio) / (float) watermark.getHeight();

        // Create the matrix
        matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Determine the post-scaled size of the watermark
        r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());
        matrix.mapRect(r);

        // Move the watermark to the top right corner
        matrix.postTranslate(width - r.width() - 5, 5);

        // Draw the watermark
        canvas.drawBitmap(watermark, matrix, paint);

        return bmp;
    }

    /**
     * set the progress of the download
     *
     * @param values value of the progress
     */
    @Override
    protected void onProgressUpdate(@NotNull Integer... values) {
        listener.setProgressBar(values[0]);
    }
}
