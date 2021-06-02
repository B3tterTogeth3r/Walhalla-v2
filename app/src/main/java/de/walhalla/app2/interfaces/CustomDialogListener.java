package de.walhalla.app2.interfaces;

/**
 * If a dialog has to be dismissed by a subclass, use this listener.
 *
 * @author B3tterTogeth3r
 * @version 1.0
 * @see android.app.DialogFragment DialogFragment
 * @since 2.2
 */
public interface CustomDialogListener {
    /**
     * Notify dialog about subclass dismiss request
     */
    void dismissDialog();

    /**
     * Start an intent from a subclass
     */
    void startIntent();
}
