package com.syp.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Singleton {

    public final static float dailyCaffeineLimit = 400;
    public final static String firebaseUserTag = "users";
    public final static String firebaseCafeTag = "cafes";
    public final static String firebaseOrderTag = "orders";
    public final static String firebaseItemsTag = "items";
    public final static String firebaseItemCountTag = "count";
    public final static String firebaseCurrentOrderTag = "currentOrder";
    public final static String firebaseCafeHoursTag = "hours";

    private static Singleton singleton;

    private Context context;
    private DatabaseReference database;
    private FirebaseStorage storage;
    private FirebaseUser firebaseUser;
    private boolean isLoggedIn = false;

    private HashMap<String, Cafe> cafes;
    private String currentUserId;
    private String currentCafeId;
    private String currentItemId;
    private String currentOrderId;
    private long startTime;
    private long endTime;

    // --------------------
    // Singleton functions
    // --------------------
    private Singleton(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        cafes = new HashMap<>();
        startTime = 0;
        endTime = 0;
    }

    public static Singleton get(Context context) {
        if (singleton == null) {
            singleton = new Singleton(context);
        }
        return singleton;
    }

    public DatabaseReference getDatabase() {
        return database;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public void insertCafes() {
        String key = database.child("cafes").push().getKey();
        Cafe cafe = new Cafe();
        cafe.setId(key);
        cafe.setName("Dong Cha");
        cafe.setAddress("935 W 30th St");
        cafe.setLatitude(37.400403);
        cafe.setLongitude(-122.113402);

        Toast.makeText(context, "Cafe 1 added", Toast.LENGTH_LONG).show();
        database.child("cafes").child(key).setValue(cafe.toMap());

        key = database.child("cafes").push().getKey();
        cafe = new Cafe();
        cafe.setId(key);
        cafe.setName("Pot of Chang");
        cafe.setAddress("935 W 30th St");
        cafe.setLatitude(37.400503);
        cafe.setLongitude(-122.113522);

        Toast.makeText(context, "Cafe 2 added", Toast.LENGTH_LONG).show();
        database.child("cafes").child(key).setValue(cafe.toMap());
    }

    public void insertItems() {
        String key = database.child("cafes").child(currentCafeId).child("items").push().getKey();
        Item item = new Item();
        item.setId(key);
        item.setName("The TEA");
        item.setPrice(11.5);
        item.setCaffeine(30);

        Toast.makeText(context, "Item 1 added", Toast.LENGTH_LONG).show();
        database.child("cafes").child(currentCafeId).child("items").child(key).setValue(item.toMap());

        key = database.child("cafes").child(currentCafeId).child("items").push().getKey();
        item = new Item();
        item.setId(key);
        item.setName("The Juice");
        item.setPrice(10);

        Toast.makeText(context, "Item 2 added", Toast.LENGTH_LONG).show();
        database.child("cafes").child(currentCafeId).child("items").child(key).setValue(item.toMap());
    }

    public void setFirebaseUser(FirebaseUser u) {
        this.firebaseUser = u;
    }

     public void uploadFile(Uri uri, String extension) {
     StorageReference fileReference =
     storage.getReference().child(System.currentTimeMillis() + "." + extension);
     fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
             String uploadId = database.child("Images").push().getKey();
             database.child("Images").child(uploadId).setValue(fileReference.getDownloadUrl());
             }
             }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {

             }
             }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

             }
         });
     }

    // --------------------
    // Current items / shop indexes
    // --------------------

    public String getUserId(){
        return currentUserId;
    }

    public void setUserId(String userID){
        this.currentUserId = userID;
    }

    public void setCurrentItemId(String id) {
        this.currentItemId = id;
    }

    public void setCurrentCafeId(String cafeId) {
        this.currentCafeId = cafeId;
    }

    public String getCurrentCafeId() {
        return currentCafeId;
    }

    public String getCurrentItemId() { return currentItemId; }

    public void setCurrentOrderId(String id) {
        this.currentOrderId = id;
    }
    public String getCurrentOrderId() {
        return this.currentOrderId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;

        DatabaseReference ref = database.getDatabase().getReference().child("user").child(currentUserId)
                .child("currentOrder");

        if(startTime == 0) {
            ref.child("travelTime").setValue(0);
            ref.child("distance").setValue(0);
            return;
        }
        else{
            long timeSpent = endTime - startTime;
            ref.child("travelTime").setValue(timeSpent);
        }
    }

    // --------------------
    // Adding / Removing / Editing objects
    // --------------------

    public void addCafeIfNotExist(String id, Cafe cafe) {
        if (!cafes.containsKey(id)) {
            Log.d("ADDING", id);
            cafes.put(id, cafe);
        }
    }

    // --------------------
    // Set if user logged info
    // --------------------
    public void setLoggedIn() {
        isLoggedIn = true;
    }

    // --------------------
    // Get if user logged in
    // --------------------
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}