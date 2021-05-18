package de.walhalla.app2;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app2.model.Semester;

@SuppressLint("StaticFieldLeak")
public class App extends Application {
    //private static final String TAG = "App";
    private static Context context;
    private static Semester currentSemester, chosenSemester;

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

}