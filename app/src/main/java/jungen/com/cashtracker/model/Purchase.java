package jungen.com.cashtracker.model;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Consumer;


/**
 *
 * Created by Joshua Jungen on 27.04.2017.
 */
public class Purchase implements Serializable {

    private String category;
    private String subcategory;
    private Date date;
    private double price;

    public Purchase(){

    }

    public Purchase(String category, String subcategory, Date date, double price) {
        this.category = category;
        this.subcategory = subcategory;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
