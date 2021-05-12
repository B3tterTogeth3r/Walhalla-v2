package de.walhalla.app2.dialog;

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

import de.walhalla.app2.MainActivity;
import de.walhalla.app2.R;
import de.walhalla.app2.firebase.Firebase;
import de.walhalla.app2.interfaces.SemesterListener;
import de.walhalla.app2.model.Semester;

public class ChangeSemesterDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String TAG = "ChangeSemesterDialog";
    private final SemesterListener listener;
    private NumberPicker np_right, np_center;

    public ChangeSemesterDialog(SemesterListener listener) {
        this.listener = listener;
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
        RelativeLayout numerPickers = (RelativeLayout) inflater.inflate(R.layout.dialog_item_sem_change, null);
        numerPickers.findViewById(R.id.np_left).setVisibility(View.GONE);

        np_center = numerPickers.findViewById(R.id.np_center);
        np_right = numerPickers.findViewById(R.id.np_right);
        String[] time = new String[]{getString(R.string.ws), getString(R.string.ss)};
        np_center.setDisplayedValues(time);
        np_center.setMaxValue(1);
        np_center.setMinValue(0);
        np_center.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_right.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        layout.addView(numerPickers);
        toolbar.setTitle(R.string.dialog_semester_change);
        String[] year = createYears();
        np_right.setMinValue(0);
        np_right.setMaxValue(year.length - 1);
        np_right.setValue(year.length - 1);
        np_right.setDisplayedValues(year);
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
            Log.i(TAG, "onClick: positive button: get result " + semesterID + " -> send result");
            Firebase.FIRESTORE.collection("Semester")
                    .document(String.valueOf(semesterID))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            try {
                                Semester s = documentSnapshot.toObject(Semester.class);
                                listener.selectorDone(s);
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
