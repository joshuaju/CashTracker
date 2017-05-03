package jungen.com.cashtracker.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import jungen.com.cashtracker.misc.DateFormatHelper;


/**
 * Created by Joshua Jungen on 27.04.2017.
 */
public class Purchase implements Serializable {

    private String category;
    private String subcategory;
    private long time;
    private double price;

    public Purchase() {

    }

    public Purchase(String category, String subcategory, long time, double price) {
        this.category = category;
        this.subcategory = subcategory;
        this.time = time;
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Exclude
    public String getPriceAsString(){
        return "" + getPrice();
    }

    @Exclude
    public String getTimeAsString(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTime());
        return DateFormatHelper.format(cal);
    }
}
