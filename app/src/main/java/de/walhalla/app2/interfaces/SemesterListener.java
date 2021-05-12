package de.walhalla.app2.interfaces;

public interface SemesterListener {
    void displayChangeDone();
    default void onAppStart(){}
    default void joinedSelectionDone(){}
}
