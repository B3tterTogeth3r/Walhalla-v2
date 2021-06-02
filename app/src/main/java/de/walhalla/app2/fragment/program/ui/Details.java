package de.walhalla.app2.fragment.program.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import de.walhalla.app2.App;
import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.model.Event;
import de.walhalla.app2.utils.Variables;

import static de.walhalla.app2.firebase.Firebase.ANALYTICS;

@SuppressLint({"StaticFieldLeak", "NonConstantResourceId"})
public class Details extends DialogFragment implements OnMapReadyCallback {
    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    private static final String TAG = "Details of Program-Event";
    private static final Bundle analyticsData = new Bundle();
    public static Dialog DIALOG;
    private static Details detailsDialog;
    private static Date start_date;
    private final Event event;
    private Toolbar toolbar;

    public Details(Event event) {
        this.event = event;
    }

    public static void display(FragmentManager fragmentManager, Event event) {
        Details.detailsDialog = new Details(event);
        detailsDialog.show(fragmentManager, TAG);
    }

    @Override
    public void onStop() {
        super.onStop();
        //Set beginning values for the analyticsData
        Date end_date = Calendar.getInstance(Variables.LOCALE).getTime();
        analyticsData.putString(Variables.Analytics.EVENT_ID, event.getId());
        analyticsData.putString(FirebaseAnalytics.Param.START_DATE, start_date.toString());
        analyticsData.putString(FirebaseAnalytics.Param.END_DATE, end_date.toString());
        int duration = (int) (end_date.getTime() - start_date.getTime() / 1000);
        analyticsData.putString(Variables.Analytics.DURATION, duration + " seconds");
        ANALYTICS.logEvent(Variables.Analytics.EVENT_DETAILS, analyticsData);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
        super.onCreateView(inflater, containter, savedInstanceState);
        View view = inflater.inflate(R.layout.program_details, containter, false);

        toolbar = view.findViewById(R.id.program_details_close);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout linearLayout = view.findViewById(R.id.program_details_layout);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(R.string.program_details);
        /*if (User.isLogIn() && User.hasCharge()) {
            toolbar.inflateMenu(R.menu.program_dialog_charge);
        } else {*/
        toolbar.inflateMenu(R.menu.program_dialog_default);
        //}

        toolbar.setOnMenuItemClickListener(item -> {
            try {
                switch (item.getItemId()) {
                    case R.id.action_calendar:
                        Log.i(TAG, "calendar");
                        saveToCalendar(event);
                        break;
                    /*case R.id.action_edit:
                        Log.i(TAG, "edit");
                        Edit.display(getChildFragmentManager(), event, detailsDialog);
                        break;
                    case R.id.action_delete:
                        Log.i(TAG, "delete");
                        delete(event);
                        break;*/
                    default:
                        dismiss();
                        break;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return true;
        });

        TextView title = view.findViewById(R.id.program_details_title);
        TextView time = view.findViewById(R.id.program_details_time);
        TextView description = view.findViewById(R.id.program_details_description);

        FrameLayout frameLayout = new FrameLayout(requireContext());
        frameLayout.setId(R.id.maps_fragment);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) scale * 400);
        linearLayout.addView(frameLayout, params);

        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(frameLayout.getId(), supportMapFragment).commit();
        try {
            supportMapFragment.getMapAsync(this);
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            Toast.makeText(getContext(), "MapAPI wollte nicht so", Toast.LENGTH_SHORT).show();
        }

        title.setText(event.getTitle());
        description.setText(event.getDescription());
        Calendar c = Calendar.getInstance();
        c.setTime(event.getStart().toDate());
        float hourFl = c.get(Calendar.HOUR_OF_DAY), minuteFl = c.get(Calendar.MINUTE);
        String minute, hour;
        if (c.get(Calendar.HOUR_OF_DAY) < 10) {
            hour = "0" + String.format(Variables.LOCALE, "%.0f", hourFl);
        } else {
            hour = String.format(Variables.LOCALE, "%.0f", hourFl);
        }
        if (c.get(Calendar.MINUTE) < 10) {
            minute = "0" + String.format(Variables.LOCALE, "%.0f", minuteFl);
        } else {
            minute = String.format(Variables.LOCALE, "%.0f", minuteFl);
        }
        String helper = c.get(Calendar.DAY_OF_MONTH) + ". " +
                Variables.MONTHS[c.get(Calendar.MONTH)] + " " +
                c.get(Calendar.YEAR) + " " + getString(R.string.program_time_at)
                + " " + hour + ":" + minute +
                " " + getResources().getString(R.string.clock);
        time.setText(helper);

        //TODO Show Helper with there task at the bottom
    }

    @Override
    public void onStart() {
        super.onStart();
        DIALOG = getDialog();
        if (DIALOG != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(DIALOG.getWindow()).setLayout(width, height);
            DIALOG.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
        start_date = Calendar.getInstance(Variables.LOCALE).getTime();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        LatLng place;
        googleMap.setMaxZoomPreference(15);
        googleMap.setMinZoomPreference(15);

        if (event != null) {//TODO
            place = new LatLng(event.getLocation_coordinates().getLatitude(), event.getLocation_coordinates().getLongitude());
            googleMap.addMarker(new MarkerOptions().position(place).title(event.getLocation_name()));
        } else {
            place = new LatLng(49.784389, 9.924648);
            googleMap.addMarker(new MarkerOptions().position(place).title("adH Walhallae"));
        }
        // Add a marker and move the camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
    }

    protected void saveToCalendar(@NotNull Event toSave) {
        //TODO E: The time is mostly the current one...
        //TODO Maybe add a color so every event looks the same on every device by default
        Intent intent = new Intent(Intent.ACTION_INSERT);
        TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");

        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        Date date = toSave.getStart().toDate();
        calStart.setTime(date);
        date = toSave.getEnd().toDate();
        calEnd.setTime(date);
        calEnd.setTimeZone(tz);
        calStart.setTimeZone(tz);

        if (toSave.getPunctuality().contains("ct")) {
            //ad 15 minutes to the time
            calStart.add(Calendar.MINUTE, 15);// = startTime + 60 * 15 * 1000;
            //endTime = endTime + 60 * 15 * 1000;
        }
        LatLng latLng = new LatLng(toSave.getLocation_coordinates().getLatitude(), toSave.getLocation_coordinates().getLongitude());
        Geocoder geocoder = new Geocoder(getContext());
        String formatted_address = "";
        try {
            if (Geocoder.isPresent()) {
                List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                for (int i = 0; i < addressList.size(); i++) {
                    formatted_address = addressList.get(i).getThoroughfare() + " "
                            + addressList.get(i).getSubThoroughfare() + ", "
                            + addressList.get(i).getPostalCode() + " "
                            + addressList.get(i).getLocality();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(Calendar.getInstance().getTimeInMillis());
            System.out.println(calStart.getTimeInMillis());
            System.out.println(toSave.getStart().toDate().getTime());
            intent.setType("vnd.android.cursor.item/event")
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.ORGANIZER, Variables.Walhalla.MAIL_SENIOR)
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
                    .putExtra("dtstart", ((int) toSave.getStart().toDate().getTime()))// Only date part is considered when ALL_DAY is true
                    .putExtra(CalendarContract.Events.DTEND, toSave.getEnd().toDate().getTime()) // Only date part is considered when ALL_DAY is true
                    .putExtra(CalendarContract.Events.TITLE, toSave.getTitle())
                    .putExtra(CalendarContract.Events.DESCRIPTION, toSave.getDescription())
                    .putExtra(CalendarContract.Events.EVENT_TIMEZONE, tz.getID())
                    .putExtra(CalendarContract.Events.EVENT_COLOR, R.color.red)
                    .putExtra(CalendarContract.Events.DISPLAY_COLOR, R.color.red)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, formatted_address)
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    .putExtra(CalendarContract.Events.RRULE, "FREQ=NONE");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void delete(@NotNull final Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.program_delete_title)
                .setNegativeButton(R.string.abort, (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(getContext(), R.string.abort, Toast.LENGTH_LONG).show();
                })
                .setPositiveButton(R.string.send, (dialog, which) -> Firebase.FIRESTORE
                        .collection("Semester")
                        .document(String.valueOf(App.getChosenSemester().getID()))
                        .collection("Event")
                        .document(event.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            if (detailsDialog != null) {
                                detailsDialog.dismiss();
                            }
                            Snackbar.make(MainActivity.parentLayout, R.string.program_dialog_delete_successful, Snackbar.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            uploadError();
                            dismiss();
                            try {
                                detailsDialog.dismiss();
                            } catch (Exception exception) {
                                Log.d(TAG, "Dismissing the details dialog didn't work", exception);
                            }
                            Snackbar.make(MainActivity.parentLayout, R.string.error_delete_event, Snackbar.LENGTH_LONG).show();
                        }));
        builder.create().show();
    }

    private void uploadError() {
        Snackbar.make(requireView(), R.string.error_upload, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.close, v -> {
                })
                .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark, null))
                .show();
    }
}
