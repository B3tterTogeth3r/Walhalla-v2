package de.walhalla.app2.model;

/**
 * The {@code SocialMedia} class represents the items displayed
 * in the tooltip popup menu from the bottom right corner of the
 * bottom nav toolbar.
 * <p>
 * The data like the links and names are saved in google firebase
 * cloud firstore database and have a realtime listener for changes.
 * <p>
 * The images are yet to be saved local, I couldn't find a more data
 * saving way, but that makes it more difficult to add and/or change
 * them in the future.
 *
 * @author B3tterTogeth3r
 * @version 1.0
 * @since 2.0
 */
@SuppressWarnings("unused")
public final class SocialMedia {
    private String icon, link, name;

    /**
     * Empty constructor for the download from the firebase console.
     */
    public SocialMedia() {
    }

    /**
     * @return the yet unused icon value, that is empty in the firestore database
     * @since 1.0
     * @deprecated not yet implemented correctly.
     */
    public final String getIcon() {
        return icon;
    }

    /**
     * @return the link to the social media site
     * @since 1.0
     */
    public final String getLink() {
        return link;
    }

    /**
     * @return the name of the link
     * @since 1.0
     */
    public final String getName() {
        return name;
    }
}
