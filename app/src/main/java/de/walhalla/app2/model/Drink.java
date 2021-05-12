package de.walhalla.app2.model;

import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Drink implements Cloneable {
    private boolean available = false;
    private String name;
    private float priceBuy = 0;
    private Map<String, Float> priceSell = new HashMap<>();
    public final static String AKTIVE ="Aktive";
    public final static String AH = "Alter Herr";
    public final static String FUX ="Fux";
    public final static String GAST = "Gast";

    public Drink() {
        priceSell.put(AKTIVE, 0f);
        priceSell.put(AH, 0f);
        priceSell.put(FUX, 0f);
        priceSell.put(GAST, 0f);
    }

    public Drink(boolean available, String name, float priceBuy, Map<String, Float> priceSell) {
        this.available = available;
        this.name = name;
        this.priceBuy = priceBuy;
        this.priceSell = priceSell;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(float priceBuy) {
        this.priceBuy = priceBuy;
    }

    public Map<String, Float> getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(Map<String, Float> priceSell) {
        this.priceSell = priceSell;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
