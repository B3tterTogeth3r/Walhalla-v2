package de.walhalla.app2.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * the {@code Rank} class represents ranks the fraternity has. It
 * contains three booleans the user can change for him-/herself.
 * The price of each semester can only be changed in the console for now.
 *
 * @author B3tterTogeth3r
 * @version 1.3
 * @since 1.4
 */
public class Rank {
    public final static String FIRST_FRATERNITY = "first_fraternity";
    public final static String FULL_MEMBER = "full_member";
    public final static String IN_LOCO = "in_loco";
    public final static String PRICE = "price_semester";
    public final static String NAME = "rank_name";
    private int id = 0;
    private boolean first_fraternity, full_member, in_loco;
    private Map<String, Object> price_semester;
    private String rank_name;

    /**
     * Empty constructor for the download from the firebase console.
     */
    public Rank() {
    }

    /**
     * Returns the id of the class. the value could be 0
     *
     * @return the id
     * @since 1.0
     * @deprecated not needed anymore
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id to a specific value
     *
     * @param id value of the identifier
     * @since 1.0
     * @deprecated not needed anymore
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return boolean as described above
     * @since 1.1
     * If this is the first fraternity of the user, the result is true. Otherwise it is false. This is necessary for the management of the bills the fraternity gets from its umbrella corporation.
     */
    public boolean isFirst_fraternity() {
        return first_fraternity;
    }


    /**
     * @param first_fraternity boolean as described above
     * @since 1.1
     * If this is the first fraternity of the user, the result
     * is true. Otherwise it is false. This is necessary for
     * the management of the bills the fraternity gets from
     * its umbrella corporation.
     */
    public void setFirst_fraternity(boolean first_fraternity) {
        this.first_fraternity = first_fraternity;
    }

    /**
     * @return is true if user is a full member of the fraternity.
     * @since 1.0
     */
    public boolean isFull_member() {
        return full_member;
    }

    /**
     * @param full_member set user to a full member of the fraternity.
     * @since 1.0
     */
    public void setFull_member(boolean full_member) {
        this.full_member = full_member;
    }

    /**
     * @return true, if the user lives in the same city as the fraternity is located?
     * @since 1.2
     */
    public boolean isIn_loco() {
        return in_loco;
    }

    /**
     * @param in_loco set the user to live in the same city as the fraternity.
     * @since 1.2
     */
    public void setIn_loco(boolean in_loco) {
        this.in_loco = in_loco;
    }

    /**
     * @return a list with prices user has to pay to the fraternity
     * depending on is state and rank every semester or every year.
     * @since 1.2
     */
    public Map<String, Object> getPrice_semester() {
        return price_semester;
    }

    /**
     * @param price_semester a list with prices user has to pay to the fraternity
     *                       depending on is state and rank every semester or every year.
     * @since 1.2
     */
    public void setPrice_semester(Map<String, Object> price_semester) {
        this.price_semester = price_semester;
    }

    /**
     * @return the name of the rank
     * @since 1.1
     */
    public String getRank_name() {
        return rank_name;
    }

    /**
     * @param rank_name change the name of the rank
     * @since 1.1
     */
    public void setRank_name(String rank_name) {
        this.rank_name = rank_name;
    }

    /**
     * not needed anymore since 1.5 because upload
     * of custom classes to firebase is now possible
     *
     * @return mapped values of the {@link #Rank()}
     * @since 1.3
     */
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIRST_FRATERNITY, isFirst_fraternity());
        map.put(FULL_MEMBER, isFull_member());
        map.put(IN_LOCO, isIn_loco());
        map.put(PRICE, getPrice_semester());
        map.put(NAME, getRank_name());
        return map;
    }

    /**
     * Set the values of the users rank from a {@code map} object to a {@code rank} class
     *
     * @param rankSettings values of the users settings.
     * @since 1.3
     */
    public void setSettings(Map<String, Object> rankSettings) {
        try {
            setFirst_fraternity((boolean) rankSettings.get(FIRST_FRATERNITY));
        } catch (Exception ignored) {
        }
        try {
            setFull_member((boolean) rankSettings.get(FULL_MEMBER));
        } catch (Exception ignored) {
        }
        try {
            setIn_loco((boolean) rankSettings.get(IN_LOCO));
        } catch (Exception ignored) {
        }
        try {
            //noinspection unchecked
            setPrice_semester((Map<String, Object>) rankSettings.get(PRICE));
        } catch (Exception ignored) {
        }
        try {
            setRank_name((String) rankSettings.get(NAME));
        } catch (Exception ignored) {
        }
    }
}
