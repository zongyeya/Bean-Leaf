package com.syp.model;

// Imports (Firebase Required)
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

// Imports for Data Structures
import java.util.HashMap;
import java.util.Map;

// @IgnoreExtraProperties (Firebase Tag)
// Serializable (Firebase Processing)
@IgnoreExtraProperties
public class Item implements Serializable {

    // -----------------
    // Member Variables
    // -----------------
    private String id;
    private String name;
    private double price;
    private double caffeine;
    private String image;
    private int count; // Only used to keep track of how many items ordered

    // -----------------------------------------------------
    // Constructors (Default Constructor Firebase Required)
    // -----------------------------------------------------
    public Item(){ }
    public Item(Item other) {
        this.id = other.getId();
        this.name = other.getName();
        this.price = other.getPrice();
        this.caffeine = other.getCaffeine();
        this.image = other.getImage();
        this.count = 0;
    }

    // ----------------------------------------------------
    // Getters for all Variable Members (Firebase Required)
    // ----------------------------------------------------

    public Integer getCount() {
        return count;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Double getPrice() {
        return price;
    }
    public String getImage() {
        return image;
    }
    public Double getCaffeine() {
        return caffeine;
    }

    // ---------------------------------
    // Setters for all Variable Members
    // ---------------------------------

    public void setCount(int count) {
        this.count = count;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setCaffeine(double caffeine_amt_in_mg) {
        this.caffeine = caffeine_amt_in_mg;
    }
    public void setImage(String image) {
        this.image = image;
    }

    // --------------------------------------------------
    // Converts Item to Map Object (Firebase Recommended)
    // --------------------------------------------------

    @Exclude
    Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("price", price);
        result.put("caffeine", caffeine);
        return result;
    }


}
