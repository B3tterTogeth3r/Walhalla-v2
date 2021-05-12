package de.walhalla.app2.interfaces;

import android.graphics.drawable.Drawable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app2.model.Event;
import de.walhalla.app2.model.Semester;

public interface AddNewSemesterListener {
    void semesterDone(@NotNull Semester semester);

    void chargenDone(@NotNull ArrayList<Object> chargenList, ArrayList<Drawable> allImages);

    void philChargenDone(@NotNull ArrayList<Object> philChargenList, ArrayList<Drawable> allImages);

    default void eventsDone(@NotNull ArrayList<Event> eventsList) {
    }

    void greetingDone(@NotNull ArrayList<Object> greetingList);

    void notesDone(@NotNull ArrayList<Object> notesList);
}
