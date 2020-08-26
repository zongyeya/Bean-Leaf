package com.syp.model;

// Firebase imports
import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

// Data Structure Imports
import java.util.ArrayList;
import org.apache.commons.lang3.time.DateUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @IgnoreExtraProperties (Firebase Tag)
// Serializable (Firebase Processing)
@IgnoreExtraProperties
public class User implements Serializable {

    // -----------------
    // Member Variables
    // -----------------
    private String id;
    private String email;
    private String displayName;
    private boolean merchant;
    private String gender;
    private Map<String, Cafe> cafes;
    private Map<String, Order> orders;
    private Order currentOrder;
    private String password;

    // ----------------------------------------------
    // Public Default Constructor (Firebase Required)
    // ----------------------------------------------
    public User(){
        cafes = new HashMap<>();
        orders = new HashMap<>();
    }

    // ----------------------------------------------------
    // Getters for all Variable Members (Firebase Required)
    // ----------------------------------------------------
    public String getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getDisplayName() {
        return displayName;
    }
    public boolean getMerchant() {
        return merchant;
    }
    public String getGender() {
        return gender;
    }
    public Map<String, Cafe> getCafes(){
        return this.cafes;
    }
    public Map<String, Order> getOrders(){
        return orders;
    }
    public Order getCurrentOrder() {
        return currentOrder;
    }
    public String getPassword(){
        return password;
    }


    // ---------------------------------
    // Setters for all Variable Members
    // ---------------------------------
    public void setId(String id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public void setMerchant(boolean merchant) {
        this.merchant = merchant;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setCafes(Map<String, Cafe> cafes) {this.cafes = cafes;}
    public void setOrders(Map<String, Order> orders){
        this.orders = orders;
    }
    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }
    public void setPassword(String password){
        this.password = password;
    }


    // -----------------------------
    // Return Orders as List<Order>
    // -----------------------------
    public List<Order> getOrdersAsList(){
        ArrayList<Order> ordersList = new ArrayList<>();

        for(String key : orders.keySet()){
            ordersList.add(orders.get(key));
        }

        return ordersList;

    }

    // ---------------------------
    // Return Items as List<Item>
    // ---------------------------
    public ArrayList<Order> getTodayOrders(){
        ArrayList<Order> today = new ArrayList<Order>();

        for(Order order: this.getOrdersAsList()){
            if(DateUtils.isSameDay(new Date(order.getTimestamp()), new Date())){
                today.add(order);
            }
        }
        return today;
    }

    // -----------------------------
    // Get total caffeine for today
    // -----------------------------
    public double getTodayCaffeine(){
        double total = 0;
        ArrayList<Order> today_order = getTodayOrders();

        for(Order order: today_order){
            for(Item item: order.getItemsAsList()){
                total += item.getCaffeine()*item.getCount();
            }
        }

        return total;
    }
}

