package de.walhalla.app2.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Person} class represents persons with their full
 * data needed for the app to work properly. The class has an empty
 * constructor and one with all necessary fields for he app to work.
 * <p>
 * The class {@code Person} includes methods for examining
 * individual objects.
 *
 * @author B3tterTogeth3r
 * @version 2.0
 * @see java.lang.Cloneable
 * @since 1.0
 */
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
    private Map<String, Object> rankSettings = new HashMap<>();

    /**
     * creating a new person with no values.
     *
     * @see Person Class description
     * @see #Person(String, String, String, String, String, String, String, String, String, Map, Map, int, Timestamp, float, String, Map) Complete Constructor
     */
    public Person() {
    }

    /**
     * Creating a new Person with all values set by the user/this constructor
     *
     * @param doB          Date of birth
     * @param poB          Place of birth
     * @param first_Name   First names
     * @param last_Name    Sir name
     * @param mail         e-mail address
     * @param mobile       mobile or landline number
     * @param rank         name of the rank
     * @param uid          uid of the firebase auth
     * @param address      address with {@link #ADDRESS_NUMBER} {@link #ADDRESS_STREET} {@link #ADDRESS_ZIP_CODE} {@link #ADDRESS_CITY}
     * @param address_2    second address with {@link #ADDRESS_NUMBER} {@link #ADDRESS_STREET} {@link #ADDRESS_ZIP_CODE} {@link #ADDRESS_CITY}
     * @param joined       the id of the joined semester
     * @param balance      the value of the person
     * @param picture_path string to the cloud storage bucket with the image inside
     * @param major        the major or the occupation title
     * @param id           id of the user
     * @param rankSettings the customised settings by the user
     */
    public Person(String id, String poB, String first_Name, String last_Name, String mail, String mobile, String rank, String uid, String major, Map<String, Object> address, Map<String, Object> address_2, int joined, Timestamp doB, float balance, String picture_path, Map<String, Object> rankSettings) {
        this.id = id;
        PoB = poB;
        this.first_Name = first_Name;
        this.last_Name = last_Name;
        this.mail = mail;
        this.mobile = mobile;
        this.rank = rank;
        this.uid = uid;
        this.major = major;
        this.address = address;
        this.address_2 = address_2;
        this.joined = joined;
        DoB = doB;
        this.balance = balance;
        this.picture_path = picture_path;
        this.rankSettings = rankSettings;
    }

    /**
     * @return value of users id
     * @since 1.0
     */
    public String getId() {
        return id;
    }

    /**
     * @param id value of users id
     * @since 1.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Timestamp of the date of birth
     * @since 1.0
     */
    public Timestamp getDoB() {
        return DoB;
    }

    /**
     * The birthday of the user.
     *
     * @param doB Timestamp of the date of birth
     * @since 1.0
     */
    public void setDoB(Timestamp doB) {
        this.DoB = doB;
    }

    /**
     * Get the users place of birth or origin.
     *
     * @return where the user is from
     * @since 1.7
     */
    public String getPoB() {
        return PoB;
    }

    /**
     * Set the users place of birth or to the city the user grew up in.
     *
     * @param poB Place of birth or origin
     * @since 1.7
     */
    public void setPoB(String poB) {
        this.PoB = poB;
    }

    /**
     * Get the users first name
     *
     * @return value of user first name
     * @throws UnsupportedOperationException if value <tt>null</tt> or empty
     * @since 1.0
     */
    public String getFirst_Name() throws UnsupportedOperationException {
        if (first_Name == null || first_Name.isEmpty()) {
            throw new UnsupportedOperationException("User has no first name");
        }
        return first_Name;
    }

    /**
     * Set the users first name
     *
     * @param first_Name value of users first name
     * @since 1.0
     */
    public void setFirst_Name(String first_Name) {
        this.first_Name = first_Name;
    }

    /**
     * Get the users surname
     *
     * @return value of {@link #last_Name}
     * @throws UnsupportedOperationException if value <tt>null</tt> or empty
     * @since 1.0
     */
    public String getLast_Name() throws UnsupportedOperationException {
        if (last_Name == null || last_Name.isEmpty()) {
            throw new UnsupportedOperationException("User has no surname");
        }
        return last_Name;
    }

    /**
     * Set the users surname
     *
     * @param last_Name value of surname
     */
    public void setLast_Name(String last_Name) {
        this.last_Name = last_Name;
    }

    /**
     * @return the email address of the user
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail the email address of the user
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * @return mobile number of user
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile mobile or landline number of the user
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return value of rank
     * @throws UnsupportedOperationException if {@code rank} is null or it is empty
     * @since 1.0
     */
    public String getRank() throws UnsupportedOperationException {
        if (rank == null || rank.isEmpty()) {
            throw new UnsupportedOperationException("Person.rank has not been set yet");
        }
        return rank;
    }

    /**
     * @param rank name of the rank
     * @since 1.0
     */
    public void setRank(String rank) {
        this.rank = rank;
    }

    /**
     * @return value of uid
     * @throws UnsupportedOperationException if {@code uid} is null or it is empty
     * @since 2.0
     */
    public String getUid() throws UnsupportedOperationException {
        if (uid == null || uid.isEmpty()) {
            throw new UnsupportedOperationException("Person.uid has not been set yet.");
        }
        return uid;
    }

    /**
     * the uid of the firebase authentication code
     *
     * @param uid string value
     * @since 2.0
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return map of address
     * @throws UnsupportedOperationException if {@code address} is empty
     * @since 2.0
     */
    public Map<String, Object> getAddress() throws UnsupportedOperationException {
        if (address.isEmpty()) {
            throw new UnsupportedOperationException("Person.address has not yet been set.");
        }
        return address;
    }

    /**
     * @param address the map of the address
     * @throws UnsupportedOperationException if one of the following is not true:
     *                                       <ul>
     *                                           <li>value is <tt>null</tt></li>
     *                                           <li>not contains {@link #ADDRESS_STREET}</li>
     *                                           <li>not contains {@link #ADDRESS_NUMBER}</li>
     *                                           <li>not contains {@link #ADDRESS_ZIP_CODE}</li>
     *                                           <li>not contains {@link #ADDRESS_CITY}</li>
     *                                       </ul>
     * @since 2.0
     */
    public void setAddress(Map<String, Object> address) throws UnsupportedOperationException {
        if (address != null
                && !address.containsKey(ADDRESS_STREET)
                && !address.containsKey(ADDRESS_NUMBER)
                && !address.containsKey(ADDRESS_ZIP_CODE)
                && !address.containsKey(ADDRESS_CITY)) {
            throw new UnsupportedOperationException("Address: Not all fields all are filled");
        }
        this.address = address;
    }

    /**
     * @return map of address_2
     * @throws UnsupportedOperationException if {@code address_2} is empty
     * @since 2.0
     */
    public Map<String, Object> getAddress_2() throws UnsupportedOperationException {
        if (address_2.isEmpty()) {
            throw new UnsupportedOperationException("Person.address_2 is not set");
        }
        return address_2;
    }

    /**
     * @param address_2 the map of the address
     * @throws UnsupportedOperationException if one of the following is not true:
     *                                       <ul>
     *                                           <li>value is <tt>null</tt></li>
     *                                           <li>not contains {@link #ADDRESS_STREET}</li>
     *                                           <li>not contains {@link #ADDRESS_NUMBER}</li>
     *                                           <li>not contains {@link #ADDRESS_ZIP_CODE}</li>
     *                                           <li>not contains {@link #ADDRESS_CITY}</li>
     *                                       </ul>
     * @since 2.0
     */
    public void setAddress_2(Map<String, Object> address_2) throws UnsupportedOperationException {
        if (address_2 != null && !address_2.containsKey(ADDRESS_STREET)
                && !address_2.containsKey(ADDRESS_NUMBER)
                && !address_2.containsKey(ADDRESS_ZIP_CODE)
                && !address_2.containsKey(ADDRESS_CITY)) {
            throw new UnsupportedOperationException("Address_2: Not all fields all are filled");
        }
        this.address_2 = address_2;
    }

    /**
     * @return number of joined semester
     * @throws IndexOutOfBoundsException if {@code joined} is 0 or smaler
     * @since 1.1
     */
    public int getJoined() throws IndexOutOfBoundsException {
        if (joined >= 0) {
            throw new IndexOutOfBoundsException("User has no joined semester");
        }
        return joined;
    }

    /**
     * @param joined the number of the semester
     * @throws IndexOutOfBoundsException if {@code joined} is smaller than 2
     * @since 1.0
     */
    public void setJoined(int joined) throws IndexOutOfBoundsException {
        if (joined > 2) {
            throw new IndexOutOfBoundsException("This semester does not exist");
        }
        this.joined = joined;
    }

    /**
     * @return value of the persons balance
     * @since 1.0
     */
    public float getBalance() {
        return balance;
    }

    /**
     * @param balance value of the persons account balance
     * @since 1.1
     */
    public void setBalance(float balance) {
        this.balance = balance;
    }

    /**
     * @param value value the person spend from its account
     * @since 2.0
     */
    @Exclude
    public void addToBalance(float value) {
        balance -= value;
    }

    /**
     * @param amount value the person paid into its account
     * @since 2.0
     */
    @Exclude
    public void personPaid(float amount) {
        balance += amount;
    }

    /**
     * @return path of the picture
     * @since 1.3
     */
    public String getPicture_path() {
        return picture_path;
    }

    /**
     * @param picture_path set a new image for that person
     * @since 1.3
     */
    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }

    /**
     * @return value of the major subject of the person
     * @since 1.4
     */
    public String getMajor() {
        return major;
    }

    /**
     * @param major name of the major subject
     * @since 1.4
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * this method is just for the upload to the firebase console.
     *
     * @return map of the settings the user chose for itself.
     * @since 2.0
     */
    public Map<String, Object> getRankSettings() {
        return rankSettings;
    }

    /**
     * this method is just for the download to the firebase console.
     *
     * @param rankSettings map of the settings the user chose for itself.
     * @since 2.0
     */
    public void setRankSettings(Map<String, Object> rankSettings) {
        this.rankSettings = rankSettings;
    }

    /**
     * @return the Settings the user set in its own class.
     * @see de.walhalla.app2.model.Rank
     * @see #getRankSettings()
     * @since 2.0
     */
    @Exclude
    public Rank getRankSetting() {
        Rank rank = new Rank();
        rank.setSettings(rankSettings);
        return rank;
    }

    /**
     * @param rankSettings The settings, the user set
     */
    @Exclude
    public void setRankSetting(@NotNull Rank rankSettings) {
        this.rankSettings = rankSettings.toMap();
    }

    /**
     * @return value of {@code first_Name} and {@code last_Name} combined with a space
     * @since 1.1
     */
    @NotNull
    @Exclude
    public String getFullName() {
        return getFirst_Name() + " " + getLast_Name();
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
    @Exclude
    @NotNull
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return mapped values of the {@link #Person()}
     * @deprecated not needed anymore since 2.0 because upload
     * of custom classes to firebase is now possible
     */
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

    /**
     * formatted into a string with a line break. Example:
     * <blockquote><pre>
     *     Mergentheimer Straße 32
     *     97082 Würzburg
     * </pre></blockquote>
     *
     * @return value of the address as a String
     */
    @NotNull
    @Exclude
    public String getAddressString() {
        return address.get(Person.ADDRESS_STREET).toString() + " " +
                address.get(Person.ADDRESS_NUMBER).toString() + "\n" +
                address.get(Person.ADDRESS_ZIP_CODE).toString() + " " +
                address.get(Person.ADDRESS_CITY).toString();
    }


    /**
     * formatted into a string in german format. Example:
     * <blockquote><pre>
     *     11.09.1864
     * </pre></blockquote>
     *
     * @return value of the date
     */
    @NotNull
    @Exclude
    public String getDoBString() {
        Date date = getDoB().toDate();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH) + "." + c.get(Calendar.MONTH) + "." + c.get(Calendar.YEAR);
    }
}