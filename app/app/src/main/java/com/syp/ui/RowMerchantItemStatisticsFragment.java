// Package
package com.syp.ui;

// View & Nav Imports
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

// Package Class imports
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Item;
import com.syp.model.Singleton;

import java.util.Locale;

import static com.syp.MainActivity.mainActivity;

// ------------------------------------------------------------------------------------
// Row representing a Item with Statistics ( connected to Recycle View, Merchant Only )
// ------------------------------------------------------------------------------------
public class RowMerchantItemStatisticsFragment extends RecyclerView.ViewHolder {

    // Views
    private TextView merchantItemStatisticsRowItemName;
    private TextView merchantItemStatisticsRowItemTotal;
    private TextView merchantItemStatisticsRowItemTotalOrders;
    private ImageView merchantItemStatisticsRowItemImage;
    private Singleton singleton;

    // Item assocaited with Row
    private Item item;

    // --------------------------------------------
    // Constructor of Merchant Item Statistics Row
    // --------------------------------------------
    public RowMerchantItemStatisticsFragment(View itemView) {
        super(itemView);
        merchantItemStatisticsRowItemName = itemView.findViewById(R.id.merchantItemStatisticsRowItemName);
        merchantItemStatisticsRowItemTotal = itemView.findViewById(R.id.merchantItemStatisticsRowItemTotal);
        merchantItemStatisticsRowItemTotalOrders = itemView.findViewById(R.id.merchantItemStatisticsRowItemTotalOrders);
        merchantItemStatisticsRowItemImage = itemView.findViewById(R.id.merchantItemStatisticsRowItemImage);

        singleton = Singleton.get(mainActivity);
        setImage();
    }

    // ------------------------------------------
    // Sets row info according to item passed in
    // ------------------------------------------
    public void setCafeItemStatisticsInfo(Item item, MainActivity mainActivity, NavDirections action){
        this.item = item;
        setItemName();
        setItemTotal();
        setItemTotalOrders();
    }

    // --------------------------
    // Setters for views
    // --------------------------
    private void setItemName() {
        merchantItemStatisticsRowItemName.setText(item.getName());
    }
    private void setItemTotal() {
        merchantItemStatisticsRowItemTotal.setText(String.format(Locale.ENGLISH, "$%.2f", item.getPrice()));
    }
    private void setItemTotalOrders() {
        merchantItemStatisticsRowItemTotalOrders.setText(String.format(Locale.ENGLISH, "$%.2f", item.getCaffeine()));
    }

    private void setImage() {
//        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("cafes").child(singleton.getCurrentCafeId()).child("items").child(item.getId()).child("ItemImage").child("-LvSRStxHRW8YsdC7VC9").child("imageUrl");
//        dbref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String link = dataSnapshot.getValue(String.class);
//                System.out.println("HEREEEEE:    " + link);
//                Picasso.get().load(link).into(userItemRowItemImage);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });
        Singleton.get(mainActivity).getDatabase()
                .child("cafes")
                .child(Singleton.get(mainActivity).getCurrentCafeId())
                .child("items")
                .child(item.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("ItemImage").child("imageUrl").getValue(String.class) == null) {
                    Log.d("Image2", "null");
                    return;
                }

                Singleton.get(mainActivity).getStorage().getReference().child("uploads").child(dataSnapshot.child("ItemImage").child("imageUrl").getValue(String.class)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri == null) {
                            Log.d("Image", "null");
                            return;
                            // Got the download URL for 'users/me/profile.png'
                        }
                        String link = uri.toString();
                        Log.d("Link", link);
                        Picasso.get().load(link).into(merchantItemStatisticsRowItemImage);
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
