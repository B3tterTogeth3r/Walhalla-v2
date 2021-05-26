package de.walhalla.app2.interfaces;

/**
 * The {@code EventChangeNotifier} triggers a listener, if an event in the database changed.
 *
 * @author B3tterTogeth3r
 * @version 1.0
 * @since 2.0
 */
public interface EventChangeNotifier {
    /**
     * Triggered by the realtime listener after an event changed
     */
    void eventChanged();
}
