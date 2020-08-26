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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

// Package class imports
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Item;
import com.syp.model.Singleton;

// ------------------------------------------------------------------------------------
// Row representing a Item with Statistics ( connected to Recycle View )
// ------------------------------------------------------------------------------------
public class RowUserItemFragment extends RecyclerView.ViewHolder {

    // Views
    private TextView userItemRowItemName;
    private TextView userItemRowItemPrice;
    private TextView userItemRowItemCaffeine;
    private ImageView userItemRowItemImage;
    private View userItemRow;   // View for On Click

    private MainActivity mainActivity;
    private Singleton singleton;
    private StorageReference storageRef;

    // Item associated with row
    private Item item;

    // -----------------------------
    // Constructor of User Item Row
    // -----------------------------
    public RowUserItemFragment(View itemView) {
        super(itemView);
        singleton = Singleton.get(mainActivity);


        // Find Views
        userItemRowItemName = itemView.findViewById(R.id.userItemRowItemName);
        userItemRowItemPrice = itemView.findViewById(R.id.userItemRowItemPrice);
        userItemRowItemCaffeine = itemView.findViewById(R.id.userItemRowItemCaffeine);
        userItemRowItemImage = itemView.findViewById(R.id.userItemRowItemImage);



        userItemRow = itemView.findViewById(R.id.userItemRow);
    }

    public void setUserItemInfo(Item item, MainActivity mainActivity, NavDirections action){
        // Set Item
        this.item = item;

        // Setters for UI
        setItemName();
        setItemPrice();
        setItemCaffeine();
        setImage();

        // If Action is required set On Click for Row
        if(action != null)
            setItemImageOnClickListener(mainActivity, action);
    }

    private void setItemName() {
        userItemRowItemName.setText(item.getName());
        Log.d("ITEM_LOG1", item.getName());
    }
    private void setItemPrice() {
        userItemRowItemPrice.setText( "$" + item.getPrice());
    }
    private void setItemCaffeine() { userItemRowItemCaffeine.setText(item.getCaffeine() + " mg of caffeine"); }
    private void setItemImageOnClickListener(MainActivity mainActivity, NavDirections action){
        userItemRow.setOnClickListener((View v) -> {
            Singleton.get(mainActivity).setCurrentItemId(item.getId());
            Navigation.findNavController(v).navigate(action);
        });
    }

    private void setImage() {

//        if (Singleton.get(mainActivity).getCurrentCafeId() == null) {
//            return;
//        }
        Log.d("itemimage", item.getImage());
        Singleton.get(mainActivity).getStorage().getReference()
                .child("uploads")
                .child(item.getImage())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri == null){
                    Log.d("Image", "null");
                    return;
                    // Got the download URL for 'users/me/profile.png'
                }
                String link = uri.toString();
                Log.d("Link", link);
                Picasso.get().load(link).into(userItemRowItemImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



//        Singleton.get(mainActivity).getDatabase()
//                .child("cafes")
//                .child(Singleton.get(mainActivity)
//                .getCurrentCafeId()).child("items").child(item.getId()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.child("ItemImage").child("imageUrl").getValue(String.class) == null) {
//                    Log.d("Image2", "null");
//                    return;
//                }
//
//                Singleton.get(mainActivity).getStorage().getReference().child("uploads").child(dataSnapshot.child("ItemImage").child("imageUrl").getValue(String.class)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        if(uri == null){
//                            Log.d("Image", "null");
//                            return;
//                            // Got the download URL for 'users/me/profile.png'
//                        }
//                        String link = uri.toString();
//                        Log.d("Link", link);
//                        Picasso.get().load(link).into(userItemRowItemImage);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle any errors
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    }
}
