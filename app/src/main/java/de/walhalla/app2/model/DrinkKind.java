package de.walhalla.app2.model;

import java.util.Map;

/**
 * This class captures a kind of drink with its name, buying price, selling price per rank and
 * whether it is available or not.
 *
 * @author B3tterTogeth3r
 * @version 1.0
 * @since 2.3
 */
public class DrinkKind {
    public static final String AVAILABLE = "available";
    public static final String NAME = "name";
    public static final String PRICE_BUY = "priceBuy";
    public static final String PRICE_SELL = "priceSell";
    private boolean available;
    private String name;
    private double priceBuy;
    private Map<String, Float> priceSell;

    public DrinkKind() {
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

    public double getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(double priceBuy) {
        this.priceBuy = priceBuy;
    }

    public Map<String, Float> getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(Map<String, Float> priceSell) {
        this.priceSell = priceSell;
    }
}
