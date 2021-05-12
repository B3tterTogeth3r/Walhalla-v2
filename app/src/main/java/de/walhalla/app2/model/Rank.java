package de.walhalla.app2.model;

import java.util.Map;

public class Rank {
    public final static String FIRST_FRATERNITY = "first_fraternity";
    public final static String FULL_MEMBER = "full_member";
    public final static String IN_LOCO = "in_loco";
    public final static String PRICE = "price_semester";
    public final static String NAME = "rank_name";
    private int id;
    private boolean first_fraternity, full_member, in_loco;
    private Map<String, Object> price_semester;
    private String rank_name;

    public Rank() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFirst_fraternity() {
        return first_fraternity;
    }

    public void setFirst_fraternity(boolean first_fraternity) {
        this.first_fraternity = first_fraternity;
    }

    public boolean isFull_member() {
        return full_member;
    }

    public void setFull_member(boolean full_member) {
        this.full_member = full_member;
    }

    public boolean isIn_loco() {
        return in_loco;
    }

    public void setIn_loco(boolean in_loco) {
        this.in_loco = in_loco;
    }

    public Map<String, Object> getPrice_semester() {
        return price_semester;
    }

    public void setPrice_semester(Map<String, Object> price_semester) {
        this.price_semester = price_semester;
    }

    public String getRank_name() {
        return rank_name;
    }

    public void setRank_name(String rank_name) {
        this.rank_name = rank_name;
    }
}
