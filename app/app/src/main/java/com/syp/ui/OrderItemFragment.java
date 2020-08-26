// Package
package com.syp.ui;

// View & Navigation Imports
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

// Firebase Imports
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

// Package class imports
import com.squareup.picasso.Picasso;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Item;
import com.syp.model.Singleton;

import static com.syp.MainActivity.mainActivity;

// ---------------------------------------------
// Page where users can add items to their cart
// ---------------------------------------------
public class OrderItemFragment extends Fragment {

    // Main Activity
    private MainActivity mainActivity;

    // Views on page
    public TextView orderItemItemTitle;
    public TextView orderItemItemPrice;
    public TextView orderItemItemCaffeine;
    public ImageView orderItemItemImage;
    public Button orderItemAddToCart;
    public ElegantNumberButton orderItemCountStepper;

    // Item on Page
    private Item item;

    // ---------------------------------------------
    // On Create for View (Fragment required)
    // ---------------------------------------------
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate view
        View v = inflater.inflate(R.layout.fragment_orderitem, container, false);

        // Get MainActivity
        mainActivity = (MainActivity) getActivity();

        // Connect views to variables
        orderItemItemImage = v.findViewById(R.id.orderItemItemImage);
        orderItemItemTitle = v.findViewById(R.id.orderItemItemTitle);
        orderItemItemPrice = v.findViewById(R.id.orderItemItemPrice);
        orderItemItemCaffeine = v.findViewById(R.id.orderItemItemCaffeine);
        orderItemCountStepper = v.findViewById(R.id.orderItemCountStepper);

        // Add to cart button & listener
        orderItemAddToCart = v.findViewById(R.id.orderItemAddToCart);
        setAddToCartOnClickListener();

        // Populate Data
        fetchCafeItem();

        return v;
    }

    // ---------------------------------------------
    // Set On Click Listener to Add Cart Button
    // ---------------------------------------------
    private void setAddToCartOnClickListener(){

        // Add On Click Listener to add to cart button
        orderItemAddToCart.setOnClickListener((View view) -> {
            addItemCountToCurrentOrder();
            NavDirections action = OrderItemFragmentDirections.actionOrderItemFragmentToCafeFragment();
            Navigation.findNavController(view).navigate(action);
        });

    }

    // ---------------------------------------------
    // Adds current count of item to current order
    // ---------------------------------------------
    private void addItemCountToCurrentOrder(){

        // Database Reference to
        DatabaseReference orderRef = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseUserTag)
                .child(Singleton.get(mainActivity).getUserId())
                .child(Singleton.firebaseCurrentOrderTag)
                .child(Singleton.firebaseItemsTag)
                .child(this.item.getId());

        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot dataSnapshot) {

                // Get current value at orderItemCountStepper
                int stepperCount = Integer.parseInt(orderItemCountStepper.getNumber());

                // Check for zero items
                if(stepperCount <= 0)
                    return;

                // Convert to item
                Item itemSnapshot = dataSnapshot.getValue(Item.class);

                // If empty create
                if(itemSnapshot == null)
                    itemSnapshot = new Item(item);

                // Add to database
                itemSnapshot.setCount(itemSnapshot.getCount() + stepperCount);
                orderRef.setValue(itemSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // ---------------------------------------------
    // Sets views with item info
    // ---------------------------------------------
    private void setCafeItemInfo(Item item){
        this.item = new Item(item);
        orderItemItemTitle.setText(this.item.getName());
        orderItemItemPrice.setText("$" + (Double.toString(this.item.getPrice())));
        orderItemItemCaffeine.setText(((this.item.getCaffeine()) + " mg of caffeine"));
    }

    // ---------------------------------------------
    // Retrieves item info from database
    // ---------------------------------------------
    private void fetchCafeItem(){

        // Get Cafe Item from Database
        DatabaseReference cafeItemRef = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseCafeTag)
                .child(Singleton.get(mainActivity).getCurrentCafeId())
                .child(Singleton.firebaseItemsTag)
                .child(Singleton.get(mainActivity).getCurrentItemId());

        // Add listener when data is retrieved or changed
        cafeItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Convert to item
                Item item = dataSnapshot.getValue(Item.class);

                // Check if exists
                if(item == null)
                    return;

                // Set Item Cafe Info
                setCafeItemInfo(item);
                setImage();

            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void setImage() {
        Singleton.get(mainActivity).getDatabase()
                .child("cafes")
                .child(Singleton.get(mainActivity)
                        .getCurrentCafeId()).child("items").child(item.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("image").getValue(String.class) == null) {
                    Log.d("Image2", "null");
                    return;
                }

                Singleton.get(mainActivity).getStorage().getReference().child("uploads").child(dataSnapshot.child("image").getValue(String.class)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri == null) {
                            Log.d("Image", "null");
                            return;
                            // Got the download URL for 'users/me/profile.png'
                        }
                        String link = uri.toString();
                        Log.d("Link", link);
                        Picasso.get().load(link).into(orderItemItemImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
