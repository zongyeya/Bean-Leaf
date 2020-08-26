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
public class Cafe implements Serializable {

    // -----------------
    // Member Variables
    // -----------------
    private String id;
    private Map<String, Item> items;  // (Firebase Friendly Structure)
    private String name;
    private String address;
    private String hours;
    private double totalSales;
    private double longitude;
    private double latitude;
    private String image;

    // ----------------------------------------------
    // Public Default Constructor (Firebase Required)
    // ----------------------------------------------
    public Cafe() {}

    // ----------------------------------------------------
    // Getters for all Variable Members (Firebase Required)
    // ----------------------------------------------------

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getHours() {
        return hours;
    }
    public double getTotalSales() {
        return totalSales;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public String getImage() { return image; }

    // ---------------------------------
    // Setters for all Variable Members
    // ---------------------------------

    public void setId(String id) {
        this.id = id;
    }
    public Map<String, Item> getItems() {
        return items;
    }
    public void setItems(Map<String, Item> items) {
        this.items = items;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setHours(String hours) {
        this.hours = hours;
    }
    public void setTotalSales(double totalSales) { this.totalSales = totalSales; }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setImage(String image) {
        this.image = image;
    }

    // --------------------------------------------------
    // Converts Cafe to Map Object (Firebase Recommended)
    // --------------------------------------------------

    @Exclude
    Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("address", address);
        result.put("name", name);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        return result;
    }

}
