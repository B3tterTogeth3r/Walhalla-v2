package de.walhalla.app2.model;

import com.google.firebase.Timestamp;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code News} class represents a message that the frat send to
 * the users. It is available in two states as a {@link #isDraft() draft}
 * or as a {@link #isInternal() internal} message. Only if both are false
 * the message is public. Every News element has a {@link #getTitle() title}
 * , a {@link #getTime() time} where it was created and a
 * {@link #getContent() content} the user can see. Some elements can have an
 * {@link #getImage() image} or/and a {@link #getLink() link} too.
 *
 * @author B3tterTogeth3r
 * @version 1.3
 * @see java.lang.Cloneable
 * @since 1.6
 */
@IgnoreExtraProperties
public class News implements Cloneable {
    public static final String CONTENT = "content";
    public static final String DRAFT = "draft";
    public static final String INTERNAL = "internal";
    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String TIME = "time";
    public static final String IMAGE = "image";

    private ArrayList<String> content;
    private boolean draft, internal;
    private Timestamp time;
    private Map<String, Object> link;
    private String title, image;
    private String id;

    /**
     * Empty constructor for the download from the firebase console.
     */
    public News() {
    }

    /**
     * Full constructor for creating a new element to upload to
     * the firebase cloud firestore database.
     *
     * @param content  The content of the message
     * @param draft    Is the message still in draft mode?
     * @param internal Is it "just" an internal message.
     * @param time     When was is created?
     * @param link     Has the message links.
     * @param title    The title of the message.
     * @param image    The image path of the message.
     */
    public News(ArrayList<String> content, boolean draft, boolean internal, Timestamp time, Map<String, Object> link, String title, String image) {
        this.content = content;
        this.draft = draft;
        this.internal = internal;
        this.time = time;
        this.link = link;
        this.title = title;
        this.image = image;
    }

    /**
     * Returns the id of the class. the value could be 0
     *
     * @return the id
     * @since 1.0
     * @deprecated not needed anymore
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id to a specific value
     *
     * @param id value of the identifier
     * @since 1.0
     * @deprecated not needed anymore
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Every paragraph of the message is a new entry in the list.
     *
     * @return list of paragraphs
     * @since 1.1
     */
    public ArrayList<String> getContent() {
        return content;
    }

    /**
     * @param content list of entries
     * @since 1.1
     * Every paragraph of the message got already its own entry
     */
    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    /**
     * @return is the message in draft mode?
     * @since 1.2
     */
    public boolean isDraft() {
        return draft;
    }

    /**
     * @param draft if false, message is published.
     * @since 1.2
     */
    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    /**
     * @return false, if message is public, true, if message is for internal purpose only
     * @since 1.2
     */
    public boolean isInternal() {
        return internal;
    }

    /**
     * set the message in internal or public mode
     *
     * @param internal true for internal, false for public
     * @since 1.2
     */
    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    /**
     * @return the time the message was created.
     * @since 1.3
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * @param time current time
     * @since 1.3
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * @return is not empty if message has one or more links
     * @since 1.3
     */
    public Map<String, Object> getLink() {
        return link;
    }

    /**
     * Every message can have links, but does not have to
     *
     * @param link list of links
     * @since 1.3
     */
    public void setLink(Map<String, Object> link) {
        this.link = link;
    }

    /**
     * @return the title of the message
     * @since 1.0
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title of the message
     * @since 1.0
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the path to the image of the message
     * @since 1.3
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the path to the image of the message
     * @since 1.3
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return a cloned instance of this class.
     * @throws CloneNotSupportedException Thrown to indicate that the <code>clone</code> method in class
     *                                    <code>Object</code> has been called to clone an object, but that
     *                                    the object's class does not implement the <code>Cloneable</code>
     *                                    interface.
     * @see java.lang.CloneNotSupportedException
     * @see java.lang.Cloneable
     * @see java.lang.Object#clone()
     * @since 1.7
     */
    @NotNull
    @Exclude
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return mapped values of the {@link #News()}
     * @since 1.1
     * @deprecated not needed anymore since 1.2 because upload
     * of custom classes to firebase is now possible
     */
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put(CONTENT, getContent());
        data.put(DRAFT, isDraft());
        data.put(INTERNAL, isInternal());
        data.put(LINK, getLink());
        data.put(TITLE, getTitle());
        data.put(TIME, getTime());
        data.put(IMAGE, getImage());

        return data;
    }
}
