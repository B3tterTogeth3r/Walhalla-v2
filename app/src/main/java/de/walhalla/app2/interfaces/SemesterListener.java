package de.walhalla.app2.interfaces;

import de.walhalla.app2.App;
import de.walhalla.app2.model.Semester;

//TODO Comment interface
public interface SemesterListener {
    void displayChangeDone();

    default void selectorDone(Semester chosenSemester) {
        App.setChosenSemester(chosenSemester);
        displayChangeDone();
    }

    default void joinedSelectionDone(Semester chosenSemester) {
    }
}
