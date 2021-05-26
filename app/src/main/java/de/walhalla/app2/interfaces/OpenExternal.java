package de.walhalla.app2.interfaces;

import org.jetbrains.annotations.NotNull;

/**
 * The {@code OpenExternal} interface is to communicate with
 * the {@code MainActivity} to open this links
 *
 * @author B3tterTogeth3r
 * @version 1.0
 * @since 2.0
 */
public interface OpenExternal {
    /**
     * @param url link the browser should open
     */
    void browser(@NotNull String url);

    /**
     * open default e mail app on the phone
     */
    void email();
}
