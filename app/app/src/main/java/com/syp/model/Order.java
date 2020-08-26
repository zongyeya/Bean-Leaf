package com.syp.model;

// Firebase imports
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

// Data structure imports
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @IgnoreExtraProperties (Firebase Tag)
// Serializable (Firebase Processing)
@IgnoreExtraProperties
public class Order implements Serializable {

    // -----------------
    // Member Variables
    // -----------------
    private String id;
    private long timestamp; // Millisecond timestamp
    private String user;
    private String cafe;
    private String cafeName;
    private Map<String, Item> items;
    private String travelTime;
    private double distance;

    // ----------------------------------------------
    // Public Default Constructor (Firebase Required)
    // ----------------------------------------------
    public Order(){
        items = new HashMap<>();
    }

    // ----------------------------------------------------
    // Getters for all Variable Members (Firebase Required)
    // ----------------------------------------------------
    public String getId() {
        return id;
    }
    public String getUser() {
        return user;
    }
    public String getCafe() {
        return cafe;
    }
    public String getCafeName() { return cafeName; };
    public Map<String, Item> getItems() {
        return items;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public String getTravelTime() {
        return travelTime;
    }
    public double getDistance() {
        return distance;
    }

    // ---------------------------------
    // Setters for all Variable Members
    // ---------------------------------
    public void setId(String id) {
        this.id = id;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public void setCafe(String owner) {
        this.cafe = owner;
    }
    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }
    public void setItems(Map<String, Item> items) { this.items = items; }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }

    // ---------------------------
    // Return Items as List<Item>
    // ---------------------------
    @Exclude
    public List<Item> getItemsAsList() {
        List<Item> itemsList = new ArrayList<>();

        for(String key : items.keySet())
            itemsList.add(items.get(key));

        return itemsList;
    }

    // -------------------------------------
    // Return Timestamp (ms) as Date Object
    // -------------------------------------
    @Exclude
    public Date getTimestampAsDate(){
        return new Date(timestamp);
    }

    // ----------------------------
    // Get total caffeine of order
    // ----------------------------
    public double getTotalCaffeine() {
        double total = 0;
        for(Item i: getItemsAsList()){
            total += i.getCaffeine() * i.getCount();
        }
        return total;
    }

    // ----------------------------
    // Get total $ spent of order
    // ----------------------------
    public double getTotalSpent(){
        double total = 0;
        for(Item i: getItemsAsList()){
            total += i.getPrice() * i.getCount();
        }
        return total;
    }

    // Calculate order before tax
//    public double calculate_order_total_before_tax(){
//        double order_total = 0;
//        for (Item i : items) {
//            order_total += i.getPrice();
//        }
//        return order_total;
//    }
//
//    public double calculate_total_discount(){
//        double discount_total = 0;
//        for (String i : items.values()) {
//            discount_total += get_tax_percentage() * i.getPrice();
//        }
//        return discount_total;
//    }

//    // Calculate order after tax
//    public double calculate_order_total_after_tax() {
//        return (calculate_order_total_before_tax() * (get_tax_percentage() + 1));
//    }
//
//    // Get tax percentage
//    // Should call api with location of cafe and get percentage as decimal double
//    public double get_tax_percentage(){
//        return 0;
//    }

}
