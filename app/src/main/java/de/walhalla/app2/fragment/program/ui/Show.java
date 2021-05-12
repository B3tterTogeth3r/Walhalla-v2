package de.walhalla.app2.fragment.program.ui;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import de.walhalla.app2.App;
import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.fragment.program.Fragment;
import de.walhalla.app2.fragment.program.Group;
import de.walhalla.app2.interfaces.EventChangeNotifier;
import de.walhalla.app2.model.Event;
import de.walhalla.app2.utils.Variables;

@SuppressLint("StaticFieldLeak")
public class Show extends Fragment implements EventChangeNotifier, Firebase.Event {
    private static final String TAG = "program.ui.Show";
    private static final ArrayList<Event> arrayList = new ArrayList<>();
    public static EventChangeNotifier listener;
    //private static ArrayAdapter<Event> adapter;
    private static boolean internal = false;
    private static boolean draft = false;
    private static LayoutInflater inflater;
    private static LinearLayout layout;
    private ListenerRegistration registration;
    private QuerySnapshot queryDocumentSnapshots;

    public Show() {
        listener = this;
        oneSemester(App.getChosenSemester().getID());
    }

    @NotNull
    public static LinearLayout load(boolean internal, boolean draft) {
        Show.inflater = LayoutInflater.from(f.getContext());
        Show.internal = internal;
        Show.draft = draft;
        Show.layout = new LinearLayout(f.getContext());

        return layout;
    }

    public static void setBooleans(boolean internal, boolean draft) {
        Show.internal = internal;
        Show.draft = draft;
        listener.eventChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        oneSemester(App.getChosenSemester().getID());
    }

    @Override
    public void onStop() {
        try {
            registration.remove();
        } catch (Exception ignored) {
        }
        super.onStop();
    }

    @Override
    public void eventChanged() {
        oneSemester(App.getChosenSemester().getID());
    }

    @Override
    public void oneSemester(int semester_id) {
        if (registration != null) {
            registration.remove();
        }
        registration = Firebase.FIRESTORE
                .collection("Semester")
                .document(String.valueOf(semester_id))
                .collection("Event")
                .addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "listen:error", error);
                        return;
                    }
                    queryDocumentSnapshots = value;
                    formatResult();
                });
    }

    @SuppressLint("InflateParams")
    private void formatResult() {
        arrayList.clear();
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> task = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot document : task) {
                Event e = document.toObject(Event.class);
                if (e != null) {
                    e.setId(document.getId());
                    /*if (User.hasCharge() && !draft && !internal) {
                        arrayList.add(e);
                    } else */
                    if (draft) {
                        if (e.isDraft()) {
                            arrayList.add(e);
                        }
                    } else if (internal) {
                        if (e.isInternal()) {
                            arrayList.add(e);
                        }
                    } else {
                        if (!e.isInternal() && !e.isDraft()) {
                            arrayList.add(e);
                        }
                    }
                }
                try {
                    arrayList.sort((o1, o2) -> Integer.compare(o1.getStart().compareTo(o2.getStart()), o2.getStart().compareTo(o1.getStart())));
                } catch (Exception ignored) {
                }
            }
        }
        //Group ArrayList by month and day
        ArrayList<Map<String, Object>> groupByMonthsFinal = Group.byMonths(arrayList);
        Log.d(TAG, "groupByMonthsFinal.size = " + groupByMonthsFinal.size());

        //show Events grouped by day and month
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.removeAllViews();
        for (Map<String, Object> month : groupByMonthsFinal) {
            Log.d(TAG, "month:size = " + month.size());
            if (1 != month.size()) {
                String title = (String) month.get("title");
                TextView titleTV = (TextView) inflater.inflate(R.layout.layout_custom_title, null);
                titleTV.setText(title);
                layout.addView(titleTV);
                List<String> keys = new ArrayList<>(month.keySet());
                keys.remove("title");
                int keySize = keys.size();
                for (int i = 0; i < keySize; i++) {
                    try {
                        Event event = (Event) month.get(keys.get(i));
                        View view = fillEventItem(event, inflater.inflate(R.layout.item_event_default, null));
                        view.setOnClickListener(v -> Details.display(f.getParentFragmentManager(), event));
                        view.setBackgroundResource(R.drawable.border_bottom_black);
                        layout.addView(view);
                    } catch (Exception e) {
                        if (e.getClass() != ClassCastException.class) {
                            Log.d(TAG, "formatResult: design: first try: exception: ", e);
                        }
                        try {
                            Map<String, Event> eventMap = (Map<String, Event>) month.get(keys.get(i));
                            //Add layout around for the border
                            LinearLayout multiEventAtOneDay = new LinearLayout(f.getContext());
                            multiEventAtOneDay.setOrientation(LinearLayout.VERTICAL);
                            for (String string : eventMap.keySet()) {
                                // Format the events accordingly
                                Event sameDay = eventMap.get(string);
                                View view = fillEventItem(sameDay, inflater.inflate(R.layout.item_event_default, null));
                                view.setOnClickListener(v -> Details.display(f.getParentFragmentManager(), sameDay));
                                view.setBackgroundResource(R.drawable.border_bottom_gray);
                                multiEventAtOneDay.addView(view);
                            }
                            multiEventAtOneDay.setBackgroundResource(R.drawable.border_bottom_black);
                            layout.addView(multiEventAtOneDay);
                        } catch (Exception ex) {
                            Log.d(TAG, "formatResult: design: second try: parsing into that map did not work at position " + keys.get(i), ex);
                        }
                    }
                }
            }
        }
    }

    @NotNull
    @Contract("_, _ -> param2")
    private View fillEventItem(Event event, @NotNull View view) {
        RelativeLayout date = view.findViewById(R.id.item_event_date);
        TextView year = view.findViewById(R.id.item_event_year);
        TextView day = view.findViewById(R.id.item_event_day);
        TextView month = view.findViewById(R.id.item_event_month);
        TextView time = view.findViewById(R.id.item_event_time);
        TextView punkt = view.findViewById(R.id.item_event_punkt);
        TextView collar = view.findViewById(R.id.item_event_collar);
        TextView title = view.findViewById(R.id.item_event_title);
        TextView description = view.findViewById(R.id.item_event_description);

        //Switch between the kinds of events
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(event.getStart().toDate());
            if (event.getPunctuality().contains("after") || event.getPunctuality().contains("ansch")) {
                date.setVisibility(View.INVISIBLE);
                time.setText(R.string.program_later);
                collar.setText(event.getCollar());
                punkt.setVisibility(View.GONE);
                title.setText(event.getTitle());
                description.setText(event.getDescription());
            }
            //TODO Make that also possible for events at the same day depending on .getStart()
            else if (event.getPunctuality().contains("later")) {
                date.setVisibility(View.INVISIBLE);
                time.setText(getTime(calendar));
                punkt.setVisibility(View.GONE);
                collar.setVisibility(View.GONE);
                title.setText(event.getTitle());
                description.setText(event.getDescription());
            }
            //TODO Make that also possible for events at the same day depending on .getStart()
            else if (event.getPunctuality().contains("total")) {
                //what to show?
                date.setVisibility(View.VISIBLE);
                year.setText(String.valueOf(calendar.get(Calendar.YEAR)));
                day.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                month.setText(Variables.MONTHS[calendar.get(Calendar.MONTH)]);
                time.setText(getTime(calendar));
                collar.setVisibility(View.GONE);
                punkt.setVisibility(View.GONE);
                title.setText(event.getTitle());
                description.setText(event.getDescription());
            } else if (event.getPunctuality().contains("info")) {
                //TODO what to show?
                date.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                collar.setVisibility(View.GONE);
                punkt.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                String helper = event.getTitle() + " " + event.getDescription();
                description.setText(helper);
            } else {
                date.setVisibility(View.VISIBLE);
                year.setText(String.valueOf(calendar.get(Calendar.YEAR)));
                day.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                month.setText(Variables.MONTHS[calendar.get(Calendar.MONTH)]);
                punkt.setText(event.getPunctuality());
                time.setText(getTime(calendar));
                collar.setText(event.getCollar());
                title.setText(event.getTitle());
                description.setText(event.getDescription());
            }
            if (event.isDraft()) {
                view.setAlpha(0.5f);
                date.setBackgroundResource(R.color.colorPrimary);
            } else if (event.isInternal()) {
                view.setAlpha(1f);
                date.setBackgroundResource(R.color.colorAccent);
            } else {
                view.setAlpha(1f);
                date.setBackgroundResource(R.color.colorPrimary);
            }
        } catch (Exception format) {
            Log.e(TAG, "Something went wrong while formatting data", format);
        }

        Animation anim = AnimationUtils.loadAnimation(f.getContext(), R.anim.fade_in);
        try {
            view.startAnimation(anim);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @NotNull
    private String getTime(@NotNull Calendar calendar) {
        float hourFL = calendar.get(Calendar.HOUR_OF_DAY);
        float minuteFL = calendar.get(Calendar.MINUTE);
        String minute, hour;
        if (hourFL < 10) {
            hour = "0" + String.format(Variables.LOCALE, "%.0f", hourFL);
        } else {
            hour = String.format(Variables.LOCALE, "%.0f", hourFL);
        }
        if (minuteFL < 10) {
            minute = "0" + String.format(Variables.LOCALE, "%.0f", minuteFL);
        } else {
            minute = String.format(Variables.LOCALE, "%.0f", minuteFL);
        }
        return hour + "." + minute + " " + f.getResources().getString(R.string.clock);
    }
}
