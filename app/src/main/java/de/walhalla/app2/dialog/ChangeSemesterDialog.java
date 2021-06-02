package de.walhalla.app2.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

import de.walhalla.app2.App;
import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.interfaces.SemesterListener;
import de.walhalla.app2.model.Person;
import de.walhalla.app2.model.Semester;

public class ChangeSemesterDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String TAG = "ChangeSemesterDialog";
    private final SemesterListener listener;
    private final String kind;
    private NumberPicker np_right, np_center;
    private int startId = 0;

    public ChangeSemesterDialog(SemesterListener listener) {
        this.listener = listener;
        this.kind = "";
    }

    public ChangeSemesterDialog(SemesterListener listener, String kind) {
        this.listener = listener;
        this.kind = kind;
    }

    public ChangeSemesterDialog(SemesterListener listener, String kind, int startId) {
        this.listener = listener;
        this.kind = kind;
        this.startId = startId;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Activity activity = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.dialog, null);
        RelativeLayout layout = view.findViewById(R.id.dialog_layout);
        layout.removeAllViewsInLayout();
        Toolbar toolbar = view.findViewById(R.id.dialog_toolbar);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        @SuppressLint("InflateParams")
        RelativeLayout numerPickers = (RelativeLayout) inflater.inflate(R.layout.dialog_item_sem_change, null);
        numerPickers.findViewById(R.id.np_left).setVisibility(View.GONE);

        np_center = numerPickers.findViewById(R.id.np_center);
        np_right = numerPickers.findViewById(R.id.np_right);
        String[] time = new String[]{getString(R.string.ws), getString(R.string.ss)};
        np_center.setDisplayedValues(time);
        np_center.setMinValue(0);
        np_center.setMaxValue(1);
        np_center.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        layout.addView(numerPickers);
        toolbar.setTitle(R.string.dialog_semester_change);
        String[] year = createYears();
        np_right.setMinValue(0);
        np_right.setMaxValue(year.length - 1);
        np_right.setValue(year.length - 1);
        np_right.setDisplayedValues(year);
        np_right.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //Select also a previous joined semester
        if (kind.equals(Person.JOINED)) {
            Log.d(TAG, "onCreateDialog: startId == " + startId + " - kind == " + kind);
            if ((startId % 2) == 0) {
                np_center.setValue(1);
                np_right.setValue((startId / 2) - 1);
            } else {
                np_right.setDisplayedValues(createYearsWS());
                np_right.setValue(((int) (startId / 2f) - 1));
            }
            np_center.setOnValueChangedListener((picker, oldVal, newVal) -> {
                if (newVal == 0) {
                    np_right.setDisplayedValues(createYearsWS());
                } else if (newVal == 1) {
                    np_right.setDisplayedValues(createYears());
                }
            });
        } else {
            if ((App.getChosenSemester().getID() % 2) == 0) {
                np_center.setValue(1);
                np_right.setValue((App.getChosenSemester().getID() / 2) - 1);
            } else {
                np_right.setDisplayedValues(createYearsWS());
                np_right.setValue(((int) (App.getChosenSemester().getID() / 2f) - 1));
            }
            np_center.setOnValueChangedListener((picker, oldVal, newVal) -> {
                if (newVal == 0) {
                    np_right.setDisplayedValues(createYearsWS());
                } else if (newVal == 1) {
                    np_right.setDisplayedValues(createYears());
                }
            });
        }
        builder.setView(view)
                .setPositiveButton(R.string.send, this)
                .setNeutralButton(R.string.abort, this);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @NotNull
    private String[] createYears() {
        ArrayList<String> years = new ArrayList<>();
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR) + 1;
        for (int i = 1864; i < year; i++) {
            years.add(String.valueOf(i));
        }
        return years.toArray(new String[0]);
    }

    @NotNull
    private String[] createYearsWS() {
        ArrayList<String> years = new ArrayList<>();
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR) + 1;
        for (int i = 1864; i < year; i++) {
            String number = i + "/" + String.valueOf(i + 1).substring(2);
            years.add(number);
        }
        return years.toArray(new String[0]);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            dismiss();
        } else if (which == DialogInterface.BUTTON_POSITIVE) {
            float timeInt;
            if (np_center.getValue() == 1) {
                timeInt = np_center.getValue();
            } else {
                timeInt = np_center.getValue() + 1.5f;
            }
            int yearInt = np_right.getValue();
            int semesterID = (int) ((timeInt + yearInt) * 2);

            Firebase.FIRESTORE.collection("Semester")
                    .document(String.valueOf(semesterID))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            try {
                                Semester s = documentSnapshot.toObject(Semester.class);
                                s.setID(semesterID);
                                try {
                                    if (kind.equals(Person.JOINED)) {
                                        listener.joinedSelectionDone(s);
                                    } else {
                                        listener.selectorDone(s);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "onClick: if-else", e);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "onSuccess: selected semester does not exist");
                                Snackbar.make(MainActivity.parentLayout, R.string.error_semester_not_exist, Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Log.e(TAG, "onSuccess: selected semester does not exist");
                            Snackbar.make(MainActivity.parentLayout, R.string.error_semester_not_exist, Snackbar.LENGTH_LONG).show();
                        }
                    });
        } else {
            Log.d(TAG, "onClick: which: " + which);
        }
    }
}
