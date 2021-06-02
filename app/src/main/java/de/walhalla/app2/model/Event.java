package de.walhalla.app2.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.walhalla.app2.interfaces.Kinds;

/**
 * The {@code Event} class represents an event the fraternity has.
 * Like {@link News news} is it available in two states and only if both
 * the {@link #isDraft() draft} and the {@link #isInternal() internal}
 * state are false, the user can see the event. Every {@code Event} needs
 * at least the following objects filled, otherwise the event will not be
 * displayed (sorted by alphabet):
 * <ul><li>{@link #getBudget() budget}</li>
 * <li>{@link #getCollar() collar}</li>
 * <li>{@link #isDraft() draft}</li>
 * <li>{@link #getEnd() end}</li>
 * <li>{@link #isInternal() internal}</li>
 * <li>{@link #getLocation_coordinates() location_coordinates}</li>
 * <li>{@link #getLocation_name() location_name}</li>
 * <li>{@link #isMeeting() meeting}</li>
 * <li>{@link #getPunctuality() punctuality}</li>
 * <li>{@link #getStart() start}</li>
 * <li>{@link #getTitle() title}</li>
 * </ul>
 * Additionaly there are more values, that can, but must not be filled:
 * <ul>
 * <li>{@link #getAddition() addition}</li>
 * <li>{@link #getDescription() description}</li>
 * <li>{@link #getHelp() help}</li>
 * </ul>
 *
 * @author B3tterTogeth3r
 * @version 3.8
 * @see java.lang.Cloneable
 * @since 1.0
 */
@IgnoreExtraProperties
public class Event implements Cloneable {
    public static final String END = "end";
    public static final String START = "start";

    private Map<String, Object> addition = null;
    private Map<String, Object> budget = new HashMap<>();
    private Map<String, Object> help = null; //Object are arrays with the name of the task the person is helping
    @Exclude
    private String id = "";
    private String collar = "";
    private String description = "";
    private String location_name = "";
    private String punctuality = "";
    private String title = "";
    private Timestamp start = new Timestamp(Calendar.getInstance().getTime());
    private Timestamp end = new Timestamp(Calendar.getInstance().getTime());
    private GeoPoint location_coordinates = new GeoPoint(49.784389, 9.924648);
    private boolean meeting = false;
    private boolean draft = false;
    private boolean internal = false;

    /**
     * Empty constructor for the download from firestore. Here it
     * needs some data to prevent a crash if they are not filled online.
     */
    public Event() {
        budget.put("after", 0);
        budget.put("before", 0);
        budget.put("current", 0);
    }

    /**
     * @return the identifier of the event.
     * @since 1.0
     * @deprecated not needed anymore because of a new firebase version.
     */
    @Exclude
    public String getId() {
        return id;
    }


    /**
     * @param id the identifier of the event.
     * @since 1.0
     * @deprecated not needed anymore because of a new firebase version.
     */
    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return true, if event is in draft mode
     * @since 2.0
     */
    public boolean isDraft() {
        return draft;
    }

    /**
     * @param draft true, if element should be in draft mode, false
     *              for public/internal access
     * @since 2.0
     */
    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    /**
     * @return true, if event is not for the public
     * @since 2.1
     */
    public boolean isInternal() {
        return internal;
    }

    /**
     * @param internal true, if event is not for the public
     * @since 2.2
     */
    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    /**
     * @return additional information to the event
     * @since 2.3
     */
    @Nullable
    public Map<String, Object> getAddition() {
        return addition;
    }

    /**
     * @param addition set additional information for the event
     * @since 2.3
     */
    public void setAddition(Map<String, Object> addition) {
        this.addition = addition;
    }

    /**
     * @return the budget of the event
     * @since 2.4
     */
    public Map<String, Object> getBudget() {
        return budget;
    }

    /**
     * @param budget set or update the budget of the event
     * @since 2.4
     */
    public void setBudget(Map<String, Object> budget) {
        this.budget = budget;
    }

    /**
     * @return helpers for that event
     * @since 2.5 in this version
     */
    @Nullable
    public Map<String, Object> getHelp() {
        return help;
    }

    /**
     * @param help set helpers of that event
     * @since 2.5 in this version
     */
    public void setHelp(Map<String, Object> help) {
        this.help = help;
    }

    /**
     * @return strictly {@code io}, {@code o} or {@code ho}
     * @since 1.0
     */
    public String getCollar() {
        return collar;
    }

    /**
     * Only values possible are: io, o, ho
     *
     * @param collar set the needed collar for that event
     * @since 1.0
     */
    public void setCollar(@Kinds.Collar String collar) {
        this.collar = collar;
    }

    /**
     * @return Description of the event. Can be an empty String
     * @since 1.0
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description for the event. Cannot be <tt>null</tt>,
     * but can be an empty {@code String}
     *
     * @param description value of the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the name of the location
     */
    public String getLocation_name() {
        return location_name;
    }

    /**
     * @param location_name set the name of the location
     */
    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    /**
     * @return value of punctuality
     * @since 1.0
     */
    public String getPunctuality() {
        return punctuality;
    }

    /**
     * strictly CT or ST if time is to be displayed. otherwise can be TIME_WHOLE_DAY, TIME_LATER, TIME_INFO, TIME_TITLE
     *
     * @param punctuality value to be set
     * @since 1.0
     */
    public void setPunctuality(@Kinds.Punctuality String punctuality) {
        this.punctuality = punctuality;
    }

    /**
     * @return The title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title of the event
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the starting time of the event
     */
    public Timestamp getStart() {
        return start;
    }

    /**
     * @param start the starting time of the event
     */
    public void setStart(Timestamp start) {
        this.start = start;
    }

    /**
     * @return the ending time of the event
     */
    public Timestamp getEnd() {
        return end;
    }

    /**
     * @param end the ending time of the event
     */
    public void setEnd(Timestamp end) {
        this.end = end;
    }

    /**
     * returns the coordinates of the location to display in
     * the GoogleMaps API
     *
     * @return the coordinates
     */
    public GeoPoint getLocation_coordinates() {
        return location_coordinates;
    }

    /**
     * The location coordinates read out of the pin in the GoogleMaps API
     *
     * @param location_coordinates the coordinates
     */
    public void setLocation_coordinates(GeoPoint location_coordinates) {
        this.location_coordinates = location_coordinates;
    }

    /**
     * @return true, if event is a meeting ("Konvent")
     */
    public boolean isMeeting() {
        return meeting;
    }

    /**
     * @param meeting true, if event is a meeting ("Konvent")
     */
    public void setMeeting(boolean meeting) {
        this.meeting = meeting;
    }


    /**
     * @return the day of the event start
     */
    @Exclude
    public int getDayOfYearStart() {
        Calendar start = Calendar.getInstance();
        start.setTime(getStart().toDate());
        return start.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * @return the month the event ends
     */
    @Exclude
    public int getMonth() {
        Calendar start = Calendar.getInstance();
        start.setTime(getEnd().toDate());
        return start.get(Calendar.MONTH);
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
     * @since 2.7
     */
    @Exclude
    @NotNull
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    /**
     * @return mapped values of the {@link #Event()}
     * @since 3.0
     * @deprecated not needed anymore since 1.2 because upload
     * of custom classes to firebase is now possible
     */
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("addition", getAddition());
        map.put("budget", getBudget());
        map.put("collar", getCollar());
        map.put("description", getDescription());
        map.put("end", getEnd());
        map.put("help", getHelp());
        map.put("location_coordinates", getLocation_coordinates());
        map.put("location_name", getLocation_name());
        map.put("meeting", isMeeting());
        map.put("punctuality", getPunctuality());
        map.put("start", getStart());
        map.put("title", getTitle());
        map.put("draft", isDraft());
        map.put("internal", isInternal());

        return map;
    }
}
