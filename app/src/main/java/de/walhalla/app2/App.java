package de.walhalla.app2;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.model.Semester;

//TODO Comment top level class and its functions
@SuppressLint("StaticFieldLeak")
public class App extends Application {
    //private static final String TAG = "App";
    private static Context context;
    private static Semester currentSemester, chosenSemester;
    private static User user;

    public App() {
    }

    public static Context getContext() {
        return context;
    }

    @NotNull
    public static Semester getCurrentSemester() {
        return currentSemester;
    }

    public static void setCurrentSemester(Semester semester) {
        App.currentSemester = semester;
        setChosenSemester(currentSemester);
    }

    public static Semester getChosenSemester() {
        return chosenSemester;
    }

    public static void setChosenSemester(Semester chosenSemester) {
        App.chosenSemester = chosenSemester;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        App.user = user;
    }

}