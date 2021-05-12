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

    public Event() {
        budget.put("after", 0);
        budget.put("before", 0);
        budget.put("current", 0);
    }

    public Event(Map<String, Object> addition, @NotNull Map<String, Object> budget, Map<String, Object> help, String id, String collar, String description, String location_name, String punctuality, String title, Timestamp start, Timestamp end, GeoPoint location_coordinates, boolean meeting, boolean draft, boolean internal) {
        budget.put("after", 0);
        budget.put("before", 0);
        budget.put("current", 0);

        this.addition = addition;
        this.budget = budget;
        this.help = help;
        this.id = id;
        this.collar = collar;
        this.description = description;
        this.location_name = location_name;
        this.punctuality = punctuality;
        this.title = title;
        this.start = start;
        this.end = end;
        this.location_coordinates = location_coordinates;
        this.meeting = meeting;
        this.draft = draft;
        this.internal = internal;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    @Nullable
    public Map<String, Object> getAddition() {
        return addition;
    }

    public void setAddition(Map<String, Object> addition) {
        this.addition = addition;
    }

    public Map<String, Object> getBudget() {
        return budget;
    }

    public void setBudget(Map<String, Object> budget) {
        this.budget = budget;
    }

    @Nullable
    public Map<String, Object> getHelp() {
        return help;
    }

    public void setHelp(Map<String, Object> help) {
        this.help = help;
    }

    public String getCollar() {
        return collar;
    }

    public void setCollar(String collar) {
        this.collar = collar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getPunctuality() {
        return punctuality;
    }

    public void setPunctuality(String punctuality) {
        this.punctuality = punctuality;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public GeoPoint getLocation_coordinates() {
        return location_coordinates;
    }

    public void setLocation_coordinates(GeoPoint location_coordinates) {
        this.location_coordinates = location_coordinates;
    }

    public boolean isMeeting() {
        return meeting;
    }

    public void setMeeting(boolean meeting) {
        this.meeting = meeting;
    }

    @Exclude
    public int getDayOfYearStart() {
        Calendar start = Calendar.getInstance();
        start.setTime(getStart().toDate());
        return start.get(Calendar.DAY_OF_YEAR);
    }

    @Exclude
    public int getMonth() {
        Calendar start = Calendar.getInstance();
        start.setTime(getEnd().toDate());
        return start.get(Calendar.MONTH);
    }

    @Exclude
    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

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
