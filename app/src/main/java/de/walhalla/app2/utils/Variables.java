package de.walhalla.app2.utils;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.firebase.firestore.GeoPoint;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Semester;

public class Variables {
    public static final Locale LOCALE = new Locale("de", "DE");
    public static final String EDIT = "edit";
    public static final String ADD = "add";
    public static final String DELETE = "delete";
    public static final String DETAILS = "details";
    public static final String SHOW = "show";
    public static final String[] MONTHS = {App.getContext().getString(R.string.month_jan),
            App.getContext().getString(R.string.month_feb), App.getContext().getString(R.string.month_mar),
            App.getContext().getString(R.string.month_apr), App.getContext().getString(R.string.month_may),
            App.getContext().getString(R.string.month_jun), App.getContext().getString(R.string.month_jul),
            App.getContext().getString(R.string.month_aug), App.getContext().getString(R.string.month_sep),
            App.getContext().getString(R.string.month_oct), App.getContext().getString(R.string.month_nov),
            App.getContext().getString(R.string.month_dec)};
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static final long ONE_MEGABYTE = 1024 * 1024 * 2;
    public static final float SCALE = App.getContext().getResources().getDisplayMetrics().density;
    public static final String START_PAGE = "start_page";
    public static final String START_PAGE_VALUE = "home";
    public static ArrayList<Semester> SEMESTER_ARRAY_LIST;
    public static SharedPreferences SHARED_PREFERENCES;
    public static String SHARED_PREFERENCES_PATH_DEFAULT;

    public static void setAllSemesters() {
        try {
            SEMESTER_ARRAY_LIST = new ArrayList<>();
            Semester current = new Semester();
            current.setID(1);
            Calendar c = Calendar.getInstance(LOCALE);
            c.set(1864, 10, 7);
            current.setBegin(c.getTime());
            c.set(3000, 11, 31);
            current.setEnd(c.getTime());
            current.setLong("Admin mode");
            current.setShort("Admin");
            SEMESTER_ARRAY_LIST.add(current);

            current.setID(2);
            c.set(1864, 10, 7);
            current.setBegin(c.getTime());
            c.set(3000, 11, 31);
            current.setEnd(c.getTime());
            current.setLong("Admin mode");
            current.setShort("Admin");
            SEMESTER_ARRAY_LIST.add(current);
            int year = 1864;
            for (int i = 3; i < 358; i++) {
                //Add winter semester
                Semester ws = new Semester();
                ws.setID(i);
                c.set(year, 9, 0);
                ws.setBegin(c.getTime());
                String back = String.valueOf(year + 1).substring(2);
                ws.setLong(App.getContext().getString(R.string.ws) + " " + year + "/" + back);
                ws.setShort("WS" + year + "/" + back);
                year++;
                c.set(year, 2, 31, 23, 59, 59);
                ws.setEnd(c.getTime());
                setCurrent(ws);
                SEMESTER_ARRAY_LIST.add(ws);
                //Add summer semester
                i++;
                Semester ss = new Semester();
                ss.setID(i);
                c.set(year, 3, 0);
                ss.setBegin(c.getTime());
                ss.setLong(App.getContext().getString(R.string.ss) + " " + year);
                ss.setShort("SS" + year);
                c.set(year, 8, 30, 23, 59, 59);
                ss.setEnd(c.getTime());
                setCurrent(ss);
                SEMESTER_ARRAY_LIST.add(ss);
            }
            //Log.i("CreateSem", "size: " + SEMESTER_ARRAY_LIST.size());
            Firebase.initNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setCurrent(@NotNull Semester current) {
        Calendar datum = Calendar.getInstance(LOCALE);
        if (current.getBegin().before(datum.getTime()) && current.getEnd().after(datum.getTime())) {
            setDefaultGreetingAndNotes(current);
            App.setCurrentSemester(current);
        } else if (current.getBegin().equals(datum.getTime())) {
            setDefaultGreetingAndNotes(current);
            App.setCurrentSemester(current);
        } else if (current.getEnd().equals(datum.getTime())) {
            setDefaultGreetingAndNotes(current);
            App.setCurrentSemester(current);
        }
    }

    //set default greeting and notes if the current semester does not (yet) have any
    private static void setDefaultGreetingAndNotes(@NotNull Semester current) {
        Resources res = Resources.getSystem();
        try {
            //Greeting
            String[] data = res.getStringArray(R.array.greeting_default_text);
            ArrayList<Object> list = new ArrayList<>();
            list.add(App.getContext().getString(R.string.greeting_head));
            list.add(Arrays.asList(data));
            list.add(App.getContext().getString(R.string.greeting_end));
            current.setGreeting(list);
            //Notes
            list.clear();
            data = res.getStringArray(R.array.notes_default);
            list.addAll(Arrays.asList(data));
            current.setNotes(list);
        } catch (Exception ignored) {

        }
    }

    public static class Walhalla {
        public static final String NAME = "K.St.V. Walhalla im KV zu Würzburg";
        public static final String ADH_ADDRESS = "Mergentheimer Straße 32\n97082 Würzburg";
        public static final String ADH_NAME = "adH";
        public static final GeoPoint ADH_LOCATION = new GeoPoint(49.784389, 9.924648);
        public static final String MAIL_SENIOR = "senior@walhalla-wuerzburg.de";
        public static final String MAIL_CONSENIOR = "consenior@walhalla-wuerzburg.de";
        public static final String MAIL_FUXMAJOR = "fuchsmajor@walhalla-wuerzburg.de";
        public static final String MAIL_SCHRIFTFUEHRER = "scriptor@walhalla-wuerzburg.de";
        public static final String MAIL_KASSIER = "kassier@walhalla-wuerzburg.de";
        public static final String MAIL_SENIOR_PHIL = "ahx@walhalla-wuerzburg.de";
        public static final String MAIL_CONSENIOR_PHIL = "ahxx@walhalla-wuerzburg.de";
        public static final String MAIL_FUXMAJOR_PHIL = "ahxxx@walhalla-wuerzburg.de";
        public static final String MAIL_WH_PHIL = "hausverwaltung@walhalla-wuerzburg.de";
        public static final String SENIOR = "Senior";
        public static final String CONSENIOR = "Consenior";
        public static final String FUXMAJOR = "Fuxmajor";
        public static final String SCHRIFTFUEHRER = "Schriftführer";
        public static final String KASSIER = "Kassier";
        public static final String WEBSITE = "http://walhalla-wuerzburg.de";
    }

    public static class Analytics {
        public static final String DURATION = "duration";
        public static final String MENU_ITEM_NAME = "menu_item_name";
        public static final String MENU_ITEM_CLICKED = "menu_item_clicked";
        public static final String EVENT_DETAILS = "event_details";
        public static final String EVENT_ID = "event_id";
    }

    public static class Rights {
        public static final String TAG = "Rights";
        public static final String ADD = "add";
        public static final String EDIT = "edit";
        public static final String DELETE = "delete";
        public static final String WRITE = "write";

        @NotNull
        @Contract(" -> new")
        public static Set<String> charge() {
            return new HashSet<>(Arrays.asList(ADD, EDIT, DELETE));
        }

        @NotNull
        @Contract(" -> new")
        public static Set<String> admin() {
            return new HashSet<>(Arrays.asList(ADD, EDIT, DELETE, WRITE));
        }
    }
}
