package de.walhalla.app2.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Person implements Cloneable {
    public static final String ADDRESS = "address";
    public static final String ADDRESS_2 = "address_2";
    public static final String BALANCE = "balance";
    public static final String DOB = "DoB";
    public static final String FIRST_NAME = "first_Name";
    public static final String JOINED = "joined";
    public static final String LAST_NAME = "last_Name";
    public static final String MAIL = "mail";
    public static final String MAJOR = "major";
    public static final String MOBILE = "mobile";
    public static final String PICTURE_PATH = "picture_path";
    public static final String POB = "PoB";
    public static final String RANK = "rank";
    public static final String UID = "uid";

    public static final String ADDRESS_CITY = "city";
    public static final String ADDRESS_NUMBER = "number";
    public static final String ADDRESS_STREET = "street";
    public static final String ADDRESS_ZIP_CODE = "zip-code";

    private String id;
    private String PoB;
    private String first_Name;
    private String last_Name;
    private String mail;
    private String mobile;
    private String rank;
    private String uid;
    private String major;
    private Map<String, Object> address = new HashMap<>();
    private Map<String, Object> address_2 = new HashMap<>();
    private int joined = 0;
    private Timestamp DoB;
    private float balance = 0f;
    private String picture_path;
    private List<String> rankSettings = new ArrayList<>();

    public Person() {
    }

    public Person(Timestamp DoB, String PoB, String first_Name, String last_Name, String mail, String mobile, String rank, String uid, Map<String, Object> address, Map<String, Object> address_2, int joined, float balance, String picture_path, String major) {
        this.DoB = DoB;
        this.PoB = PoB;
        this.first_Name = first_Name;
        this.last_Name = last_Name;
        this.mail = mail;
        this.mobile = mobile;
        this.rank = rank;
        this.uid = uid;
        this.address = address;
        this.address_2 = address_2;
        this.joined = joined;
        this.balance = balance;
        this.picture_path = picture_path;
        this.major = major;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getDoB() {
        return DoB;
    }

    public void setDoB(Timestamp doB) {
        this.DoB = doB;
    }

    public String getPoB() {
        return PoB;
    }

    public void setPoB(String poB) {
        this.PoB = poB;
    }

    public String getFirst_Name() {
        return first_Name;
    }

    public void setFirst_Name(String first_Name) {
        this.first_Name = first_Name;
    }

    public String getLast_Name() {
        return last_Name;
    }

    public void setLast_Name(String last_Name) {
        this.last_Name = last_Name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, Object> getAddress() {
        return address;
    }

    public void setAddress(Map<String, Object> address) {
        this.address = address;
    }

    public Map<String, Object> getAddress_2() {
        return address_2;
    }

    public void setAddress_2(Map<String, Object> address_2) {
        this.address_2 = address_2;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public List<String> getRankSettings() {
        return rankSettings;
    }

    public void setRankSettings(List<String> rankSettings) {
        this.rankSettings = rankSettings;
    }

    @Exclude
    public String getFullName() {
        return getFirst_Name() + " " + getLast_Name();
    }

    @Exclude
    @NotNull
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();

        data.put(DOB, getDoB());
        data.put(POB, getPoB());
        data.put(ADDRESS, getAddress());
        data.put(ADDRESS_2, getAddress_2());
        data.put(BALANCE, getBalance());
        data.put(FIRST_NAME, getFirst_Name());
        data.put(JOINED, getJoined());
        data.put(LAST_NAME, getLast_Name());
        data.put(MAIL, getMail());
        data.put(MOBILE, getMobile());
        data.put(PICTURE_PATH, getPicture_path());
        data.put(RANK, getRank());
        data.put(UID, getUid());
        data.put(MAJOR, getMajor());

        return data;
    }

    @Exclude
    public String getAddressString() {
        return address.get(Person.ADDRESS_STREET).toString() + " " +
                address.get(Person.ADDRESS_NUMBER).toString() + "\n" +
                address.get(Person.ADDRESS_ZIP_CODE).toString() + " " +
                address.get(Person.ADDRESS_CITY).toString();
    }

    @Exclude
    public String getDoBString() {
        Date date = getDoB().toDate();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH) + "." + c.get(Calendar.MONTH) + "." + c.get(Calendar.YEAR);
    }
}