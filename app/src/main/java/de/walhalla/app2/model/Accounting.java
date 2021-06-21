package de.walhalla.app2.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import de.walhalla.app2.utils.Variables;

/**
 * @author B3tterTogeth3r
 * @version 1.0
 * @see java.lang.Cloneable
 * @since 2.5
 */
@IgnoreExtraProperties
public class Accounting implements Cloneable {
    private String id;
    private Timestamp date;
    private float income = 0f;
    private float expense = 0f;
    private String event;
    private String purpose;
    private String add;
    private int recipe;

    /** public empty constructor
     * @see #Accounting(Timestamp, float, float, String, String) Accounting.drinks
     * @see #Accounting(Timestamp, float, float, String, String, String) Accounting.expense
     * @see #Accounting(Timestamp, float, float, String, String, String, int) Accounting.complete
     */
    public Accounting() {
    }

    /**
     * An entry formatted to put an account movement into the collection <b>with</b> a recipe
     *
     * @param date    the date of the entry
     * @param expense amount of expense taken
     * @param income  amount of income gotten
     * @param event   id of the event
     * @param purpose description of the expense/income
     * @param add     additional description, mostly used for expenses/incomes outside of events
     * @param recipe  to every expense should be a recipe with a number
     * @since 1.0
     */
    public Accounting(Timestamp date, float expense, float income, String event, String purpose, String add, int recipe) {
        this.date = date;
        this.expense = expense;
        this.income = income;
        this.event = event;
        this.purpose = purpose;
        this.add = add;
        this.recipe = recipe;
    }

    /**
     * An entry formatted to put account movements into the collection <b>without</b> a recipe
     *
     * @param date    the date of the entry
     * @param expense amount of expense taken
     * @param income  amount of income gotten
     * @param event   id of the event
     * @param purpose description of the expense/income
     * @param add     additional description, mostly used for expenses/incomes outside of events
     * @since 1.0
     */
    public Accounting(Timestamp date, float expense, float income, String event, String purpose, String add) {
        this.date = date;
        this.expense = expense;
        this.income = income;
        this.event = event;
        this.purpose = purpose;
        this.add = add;
    }

    /**
     * An entry formatted to put drinks into the account collection of firestore.
     *
     * @param date    the date of the entry
     * @param expense amount of expense taken
     * @param income  amount of income gotten
     * @param purpose description of the expense/income
     * @param add     additional description, mostly used for expenses/incomes outside of events
     * @since 1.0
     */
    public Accounting(Timestamp date, float expense, float income, String purpose, String add) {
        this.date = date;
        this.expense = expense;
        this.income = income;
        this.event = event;
        this.purpose = purpose;
        this.add = add;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    @Exclude
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
