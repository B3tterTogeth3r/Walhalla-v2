package de.walhalla.app2;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app2.model.Semester;

@SuppressLint("StaticFieldLeak")
public class App extends Application {
    //private static final String TAG = "App";
    private static Context context;
    public static boolean isInternet;
    public static String internetKind;
    private static Semester currentSemester, chosenSemester;
    private static ArrayList<String> currentChargen = new ArrayList<>();
    private static ArrayList<String> admins = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public App() {
    }

    public static void setCurrentChargen(ArrayList<String> currentChargen) {
        App.currentChargen = currentChargen;
    }

    public static ArrayList<String> getCurrentChargen() {
        return currentChargen;
    }

    public static ArrayList<String> getAdmins() {
        return admins;
    }

    public static void setAdmins(ArrayList<String> admins) {
        App.admins = admins;
    }

    public static Context getContext() {
        return context;
    }

    public static boolean getInternet() {
        return isInternet;
    }

    @NotNull
    public static Semester getCurrentSemester() {
        return currentSemester;
    }

    public static void setCurrentSemester(Semester semester) {
        App.currentSemester = semester;
        setChosenSemester(currentSemester);
    }

    public static Semester getLastSemester() {
        return new Semester();//Database.getSemesterArrayList().get(currentSemester.getID() - 1);
    }

    public static Semester getChosenSemester() {
        return chosenSemester;
    }

    public static void setChosenSemester(Semester chosenSemester) {
        App.chosenSemester = chosenSemester;
    }

}