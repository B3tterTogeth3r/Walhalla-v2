package de.walhalla.app2.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Semester} class represents all the Semesters a
 * fraternity has existed and hopefully will exist. It contains
 * the {@link #getBegin() begin} and {@link #getEnd() end} as a date,
 * the {@link #getLong() full} and {@link #getShort() short} names of it
 * as the {@link #getID() id} and, if available {@link #getNotes() notes} for the
 * user and the {@link #getGreeting() greeting} of the {@code senior} of that semester.
 *
 * @author B3tterTogeth3r
 * @version 2.0
 * @since 1.0
 */
@IgnoreExtraProperties
public class Semester {
    private int ID;
    private String Short;
    private String Long;
    private Date Begin, End;
    private ArrayList<Object> greeting = new ArrayList<>();
    private ArrayList<Object> notes = new ArrayList<>();

    /**
     * Empty constructor for the download from the firebase Cloud Firestore
     */
    public Semester() {
    }

    /**
     * Creating a new semester with the necessary data
     *
     * @param ID    identification of that semester
     * @param Short the short name
     * @param Long  the long name
     * @param Begin the beginning data
     * @param End   the end time
     */
    public Semester(int ID, String Short, String Long, Date Begin, Date End) {
        this.ID = ID;
        this.Short = Short;
        this.Long = Long;
        this.Begin = Begin;
        this.End = End;
    }

    /**
     * @return the identification of the semester.
     */
    public int getID() {
        return ID;
    }

    /**
     * @param ID sets the identification of the semester
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Gives back the short name of the semester. Example:
     * <blockquote><pre>WS84/85</pre></blockquote>
     *
     * @return The short name of the semester.
     */
    public String getShort() {
        return Short;
    }

    /**
     * Set back the short name of the semester. Example:
     * <blockquote><pre>WS84/85</pre></blockquote>
     *
     * @param aShort The short name of the semester.
     */
    public void setShort(String aShort) {
        Short = aShort;
    }

    /**
     * Gives back the long name of the semester. Example:
     * <blockquote><pre>Wintersemester 1884/85</pre></blockquote>
     *
     * @return The long name of the semester.
     */
    public String getLong() {
        return Long;
    }

    /**
     * Gives back the short name of the semester. Example:
     * <blockquote><pre>Wintersemester 1884/85</pre></blockquote>
     *
     * @param aLong The long name of the semester.
     */
    public void setLong(String aLong) {
        Long = aLong;
    }

    /**
     * Get the beginning date of the semester as accurate as possible
     *
     * @return the date of the begin
     */
    public Date getBegin() {
        return Begin;
    }

    /**
     * Set the beginning date of the semester as accurate as possible
     *
     * @param begin the date of the begin
     */
    public void setBegin(Date begin) {
        Begin = begin;
    }

    /**
     * Get the end date of the semester as accurate as possible
     *
     * @return the date of the end
     */
    public Date getEnd() {
        return End;
    }

    /**
     * Set the end date of the semester as accurate as possible
     *
     * @param end the date of the end
     */
    public void setEnd(Date end) {
        End = end;
    }

    /**
     * Every semester the elected {@code senior} should write a greeting
     * to everybody who is reading the program. It should be saved and
     * digitalised, but not all are as of now. So the {@code greeting}
     * could be an empty list. The greeting itself is saved paragraph
     * by paragraph in a list with a HashMap at the end for the senior
     * of the active and "old" members of the fraternity.
     *
     * @return the list of paragraphs
     */
    public ArrayList<Object> getGreeting() {
        return greeting;
    }

    /**
     * Every semester the elected {@code senior} should write a greeting
     * to everybody who is reading the program. It should be saved and
     * digitalised, but not all are as of now. So the {@code greeting}
     * could be an empty list. The greeting itself is saved paragraph
     * by paragraph in a list with a HashMap at the end for the senior
     * of the active and "old" members of the fraternity.
     *
     * @param greeting the list of paragraphs
     */
    public void setGreeting(ArrayList<Object> greeting) {
        this.greeting = greeting;
    }

    /**
     * Every semester the program should have some notes about regular
     * occurring events, who is when allowed to come and more. They are
     * saved in this list. If there are no notes, the list is just empty.
     *
     * @return the list of notes
     */
    public ArrayList<Object> getNotes() {
        return notes;
    }


    /**
     * Every semester the program should have some notes about regular
     * occurring events, who is when allowed to come and more. They are
     * saved in this list. If there are no notes, the list is just empty.
     *
     * @param notesList the list of notes
     */
    public void setNotes(ArrayList<Object> notesList) {
        this.notes = notesList;
    }

    /**
     * @return mapped values of the {@link #Semester()}
     * @deprecated not needed anymore since 2.0 because upload
     * of custom classes to firebase is now possible
     */
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("long", getLong());
        data.put("short", getShort());
        data.put("begin", getBegin());
        data.put("end", getEnd());

        return data;
    }
}
