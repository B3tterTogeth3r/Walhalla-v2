package de.walhalla.app2.fragment.program;

import android.util.Log;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.walhalla.app2.App;
import de.walhalla.app2.model.Event;
import de.walhalla.app2.utils.Variables;

public class Group {
    private final static String TAG = "groupByMonths";

    @NotNull
    public static ArrayList<Map<String, Object>> byMonths(@NotNull ArrayList<Event> eventList) {
        ArrayList<Map<String, Object>> groupedList = new ArrayList<>();
        Log.e(TAG, "byMonths: eventList.size = " + eventList.size());
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(App.getChosenSemester().getBegin());
        end.setTime(App.getChosenSemester().getEnd());
        //Find months from start to end including this two
        while (start.before(end)) {
            int monthNo = start.get(Calendar.MONTH);
            ArrayList<Event> events = new ArrayList<>();
            for (Event e : eventList) {
                if (e.getMonth() == monthNo) {
                    events.add(e);
                    try {
                        events.sort((o1, o2) -> Integer.compare(o1.getStart().compareTo(o2.getStart()), o2.getStart().compareTo(o1.getStart())));
                    } catch (Exception ignored) {
                    }
                }
            }
            //Group events
            Map<String, Object> month = new HashMap<>(byDate(events));
            month.put("title", Variables.MONTHS[start.get(Calendar.MONTH)]);
            groupedList.add(month);
            start.add(Calendar.MONTH, 1);
        }
        return groupedList;
    }

    @NotNull
    @Contract(pure = true)
    private static Map<String, Object> byDate(@NotNull ArrayList<Event> events) {
        Map<String, Object> result = new HashMap<>();
        int size = events.size();
        for (int i = 0; i < size; i++) {
            int current = i + 1;
            if (current < events.size() && events.get(i).getDayOfYearStart() == events.get(current).getDayOfYearStart()) {
                //TODO Add multi day events as a possible "head event"
                Map<String, Object> more = new HashMap<>();
                more.put(String.valueOf(i), events.get(i));
                while (current < events.size() && events.get(i).getDayOfYearStart() == events.get(current).getDayOfYearStart()) {
                    more.put(String.valueOf(current), events.get(current));
                    current++;
                }
                result.put(String.valueOf(i), more);
                i = current - 1;
            } else {
                result.put(String.valueOf(i), events.get(i));
            }
        }
        return result;
    }
}
