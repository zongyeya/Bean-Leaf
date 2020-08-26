package com.syp.test;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.syp.MainActivity;
import com.syp.model.Cafe;
import com.syp.model.Item;
import com.syp.model.Order;
import com.syp.model.Singleton;
import com.syp.model.User;

import java.util.HashMap;
import java.util.Map;

public class InsertTestUserData {

    public static DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child("whiteBoxTestUser");
    public static DatabaseReference cafeRef = FirebaseDatabase.getInstance().getReference().child("cafes").child("whiteBoxTestCafe");
    public static DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("items").child("whiteBoxTestCafe");
    public static DatabaseReference currentOrderRef = userRef.child("currentOrder");


    public static void setUpUser(MainActivity mainActivity){

        //Add man
//        removeUser();
        User whiteTestUser = new User();
        whiteTestUser.setId("whiteBoxTestUser");
        whiteTestUser.setEmail("whiteBoxTestUser@gmail.com");
        Order TestOrder1 = new Order();
        TestOrder1.setCafe("TestCafe");
        TestOrder1.setDistance(1000);
        TestOrder1.setId("TestOrder1");
        TestOrder1.setTimestamp(System.currentTimeMillis());

        //test case for items within order
        Map<String, Item> testMapOfItems = new HashMap<>();
        Item testOrder1Item1 = new Item();
        testOrder1Item1.setCaffeine(10);
        testOrder1Item1.setCount(1);
        testOrder1Item1.setId("testOrder1Item1");
        testOrder1Item1.setName("Chang's signature juice");
        testOrder1Item1.setPrice(20);
        Item testOrder1Item2 = new Item();
        testOrder1Item2.setCaffeine(10);
        testOrder1Item2.setCount(1);
        testOrder1Item2.setId("testOrder1Item2");
        testOrder1Item2.setName("Ethan's soup");
        testOrder1Item2.setPrice(10);
        testMapOfItems.put(testOrder1Item1.getId(), testOrder1Item1);
        testMapOfItems.put(testOrder1Item2.getId(), testOrder1Item2);

        TestOrder1.setItems(testMapOfItems);
        Map<String, Order> testMapOfOrders = new HashMap<>();
        testMapOfOrders.put(TestOrder1.getId(), TestOrder1);


        // Added User as owner of Cafe
        Cafe whiteTestCafe = new Cafe();
        whiteTestCafe.setId("whiteBoxTestCafe");
        whiteTestCafe.setName("TestCafe");
        whiteTestCafe.setAddress("935 W 30th Street");
        whiteTestCafe.setHours("M-S 9am-11pm");
        whiteTestCafe.setTotalSales(1000);
        whiteTestCafe.setLatitude(37.400503);
        whiteTestCafe.setLongitude(-122.113522);

        // Insert Test Menu Items
        Map<String, Item> testMapOfItemsTwo = new HashMap<>();
        Item testOrder1Item1Cafe = new Item();
        testOrder1Item1Cafe.setCaffeine(10);
        testOrder1Item1Cafe.setCount(1);
        testOrder1Item1Cafe.setId("testOrder1Item1Cafe");
        testOrder1Item1Cafe.setName("CoffeeACafe");
        testOrder1Item1Cafe.setPrice(20);

        testMapOfItems.put(testOrder1Item1Cafe.getId(), testOrder1Item1Cafe);
        whiteTestCafe.setItems(testMapOfItems);
        whiteTestUser.getCafes().put(whiteTestCafe.getId(), whiteTestCafe);


        whiteTestUser.setOrders(testMapOfOrders);
        userRef.setValue(whiteTestUser);

        Singleton.get(mainActivity).setUserId(whiteTestUser.getId());
        Singleton.get(mainActivity).setCurrentCafeId(whiteTestCafe.getId());
        Singleton.get(mainActivity).setCurrentItemId(testOrder1Item1Cafe.getId());
    }

    public static void setUpCafe(MainActivity mainActivity){

        //Add man
//        removeCafe();
        Cafe whiteTestCafe = new Cafe();
        whiteTestCafe.setId("whiteBoxTestCafe");
        whiteTestCafe.setName("TestCafe");
        whiteTestCafe.setAddress("935 W 30th Street");
        whiteTestCafe.setHours("M-S 9am-11pm");
        whiteTestCafe.setTotalSales(1000);
        whiteTestCafe.setLatitude(37.400503);
        whiteTestCafe.setLongitude(-122.113522);

        // Insert Test Menu Items
        Map<String, Item> testMapOfItems = new HashMap<>();
        Item testOrder1Item1 = new Item();
        testOrder1Item1.setCaffeine(10);
        testOrder1Item1.setCount(1);
        testOrder1Item1.setId("testOrder1Item1");
        testOrder1Item1.setName("CoffeeA");
        testOrder1Item1.setPrice(20);
        Item testOrder1Item2 = new Item();
        testOrder1Item2.setCaffeine(20);
        testOrder1Item2.setCount(1);
        testOrder1Item2.setId("testOrder1Item2");
        testOrder1Item2.setName("CoffeeB");
        testOrder1Item2.setPrice(20);
        Item testOrder1Item3 = new Item();
        testOrder1Item3.setCaffeine(30);
        testOrder1Item3.setCount(1);
        testOrder1Item3.setId("testOrder1Item3");
        testOrder1Item3.setName("CoffeeC");
        testOrder1Item3.setPrice(30);
        testMapOfItems.put(testOrder1Item1.getId(), testOrder1Item1);
        testMapOfItems.put(testOrder1Item2.getId(), testOrder1Item2);
        testMapOfItems.put(testOrder1Item3.getId(), testOrder1Item3);

        whiteTestCafe.setItems(testMapOfItems);
        cafeRef.setValue(whiteTestCafe);

        Singleton.get(mainActivity).setCurrentCafeId(whiteTestCafe.getId());
        Singleton.get(mainActivity).setCurrentItemId(testOrder1Item1.getId());
    }

    public static void setUpCurrentOrder(){
        Map<String, Item> testMapOfItems = new HashMap<>();
        Item testOrder1Item1 = new Item();
        testOrder1Item1.setCaffeine(10);
        testOrder1Item1.setCount(1);
        testOrder1Item1.setId("testOrder1Item1");
        testOrder1Item1.setName("Chang's signature juice");
        testOrder1Item1.setPrice(20);
        Item testOrder1Item2 = new Item();
        testOrder1Item2.setCaffeine(10);
        testOrder1Item2.setCount(1);
        testOrder1Item2.setId("testOrder1Item2");
        testOrder1Item2.setName("Ethan's soup");
        testOrder1Item2.setPrice(10);
        testMapOfItems.put(testOrder1Item1.getId(), testOrder1Item1);
        testMapOfItems.put(testOrder1Item2.getId(), testOrder1Item2);
        currentOrderRef.child("items").setValue(testMapOfItems);
    }

    public static void removeUser(){
        userRef.removeValue();
    }

    public static void removeCafe(){
        cafeRef.removeValue();
    }

    public static void removeCurrentOrder(){
        currentOrderRef.removeValue();
    }
}
