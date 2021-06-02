package de.walhalla.app2;

import android.net.Uri;

import de.walhalla.app2.model.Person;

/**
 * This class saves the data of the currently signed in user.
 *
 * @author B3tterTogeth3r
 * @version 1.0
 * @since 2.2
 */
public class User {
    private String uid, email, id;
    private Uri image;
    private Person data;

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image_path) {
        this.image = image_path;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Person getData() {
        return data;
    }

    public void setData(Person data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
