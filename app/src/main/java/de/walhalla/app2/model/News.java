package de.walhalla.app2.model;

import com.google.firebase.Timestamp;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public News() {
    }

    public News(ArrayList<String> content, boolean draft, boolean internal, Timestamp time, Map<String, Object> link, String title, String image) {
        this.content = content;
        this.draft = draft;
        this.internal = internal;
        this.time = time;
        this.link = link;
        this.title = title;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getContent() {
        return content;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Map<String, Object> getLink() {
        return link;
    }

    public void setLink(Map<String, Object> link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

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
