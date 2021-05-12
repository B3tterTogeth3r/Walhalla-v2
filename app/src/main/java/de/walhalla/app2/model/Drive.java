package de.walhalla.app2.model;

import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.walhalla.app2.utils.Variables;

@IgnoreExtraProperties
public class Drive implements Cloneable {
    private int id;
    private float distance;
    private String date, destination, addition;

    public Drive() {
        this.id = 0;
        this.date = "";
        this.distance = 0;
        this.destination = "";
        this.addition = "";
    }

    public Drive(int id, String date, float distance, String destination, String add) {
        this.id = id;
        this.date = date;
        this.distance = distance;
        this.destination = destination;
        this.addition = add;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getAddition() {
        return addition;
    }

    public void setAddition(String addition) {
        this.addition = addition;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Serializable getDate_Output() {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Variables.LOCALE);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm", Variables.LOCALE);
        Date datum;
        String datumNeu;
        try {
            datum = formatter2.parse(date);
            assert datum != null;
            datumNeu = format.format(datum);
            return datumNeu;
        } catch (Exception e) {
            return date;
        }
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
