package de.walhalla.app2.model;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.walhalla.app2.utils.Variables;

@IgnoreExtraProperties
public class Accounting implements Cloneable {
    private int id;
    private String date;
    private float income;
    private float expense;
    private String event;
    private String purpose;
    private String add;
    private int recipe;

    public Accounting() {
    }

    public Accounting(int id, String date, float expense, float income, String event, String purpose, String add, int recipe) {
        this.id = id;
        this.date = date;
        this.expense = expense;
        this.income = income;
        this.event = event;
        this.purpose = purpose;
        this.add = add;
        this.recipe = recipe;
    }

    @Exclude
    public int getId() {
        return id;
    }

    @Exclude
    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public Date getDate() {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd", Variables.LOCALE);
        Date datum;
        try {
            datum = formatter2.parse(date);
            return datum;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Exclude
    public String getDateFormat() {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd", Variables.LOCALE);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy", Variables.LOCALE);
        Date datum;
        String datumNeu;
        try {
            datum = formatter2.parse(date);
            assert datum != null;
            datumNeu = format.format(datum);
            return datumNeu;
        } catch (ParseException e) {
            return date;
        }
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public String getIncomeFormat() {
        String format = String.format(Variables.LOCALE, "%.2f", getIncome());
        return "€ " + format;
    }

    public float getExpense() {
        return expense;
    }

    public void setExpense(float expense) {
        this.expense = expense;
    }

    @Exclude
    public String getExpenseFormat() {
        String format = String.format(Variables.LOCALE, "%.2f", getExpense());
        return "€ " + format;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public int getRecipe() {
        return recipe;
    }

    public void setRecipe(int recipe) {
        this.recipe = recipe;
    }

    public String getDateString() {
        return date;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("date", getDate());
        data.put("expense", getExpense());
        data.put("income", getIncome());
        data.put("event", getEvent());
        data.put("purpose", getPurpose());
        data.put("add", getAdd());
        data.put("recipe", getRecipe());

        return data;
    }
}
