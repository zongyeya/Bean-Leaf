package com.syp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Cafe;
import com.syp.model.Item;
import com.syp.model.Singleton;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddShopFragment extends Fragment {

    public static int RESULT_LOAD_IMAGE_SHOP = 3;
    public static int RESULT_LOAD_IMAGE_REG = 4;

    private Cafe newCafe;
    private MainActivity mainActivity;
    private EditText shopName;
    private EditText shopAddress;
    private EditText shopHours;
    private Button addItem;
    private Button addCafeImage;
    private Button addRegistrationForm;
    private Button registerShop;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Item, RowUserItemFragment> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        View v = inflater.inflate(R.layout.fragment_add_shop, container, false);

        shopName = v.findViewById(R.id.addshop_shop_name);
        shopAddress = v.findViewById(R.id.addshop_shop_address);
        shopHours = v.findViewById(R.id.addshop_shop_hours);
        addItem = v.findViewById(R.id.addshop_add_item);
        addCafeImage = v.findViewById(R.id.addshop_add_cafe_images);
        addRegistrationForm = v.findViewById(R.id.addshop_add_registration_form_image);
        registerShop = v.findViewById(R.id.addshop_register_cafe);

        //Set up recycler view
        recyclerView = v.findViewById(R.id.addShop_currentItems);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        String userID = Singleton.get(mainActivity).getUserId();

        // Cafe
        DatabaseReference ref = Singleton.get(mainActivity).getDatabase().child("users").child(userID)
                .child("currentCafe");

        // Value Listener
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    return;
                }
                Cafe cafe = dataSnapshot.getValue(Cafe.class);
                shopName.setText(cafe.getName());
                shopAddress.setText(cafe.getAddress());
                shopHours.setText(cafe.getHours());
                Log.d("ASDF", shopName.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Query
        Query query = Singleton.get(mainActivity).getDatabase().child("users").child(userID)
                .child("currentCafe")
                .child("items");

        // Firebase Options
        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(query, new SnapshotParser<Item>() {
                            @NonNull
                            @Override
                            public Item parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Item item = snapshot.getValue(Item.class);
                                Log.d("ASDF", item.getName());
                                return item;
                            }
                        })
                        .build();

        //Firebase Recycler View
        adapter = new FirebaseRecyclerAdapter<Item, RowUserItemFragment>(options) {
            @NonNull
            @Override
            public RowUserItemFragment onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_cafe_menu_item, parent, false);

                return new RowUserItemFragment(view);
            }
            @NonNull
            @Override
            protected void onBindViewHolder(RowUserItemFragment holder, final int position, Item item) {
                holder.setUserItemInfo(item, mainActivity, AddShopFragmentDirections.actionAddShopFragmentToAddItemNewFragment());
            }
        };

        // specify an adapter
        recyclerView.setAdapter(adapter);
        adapter.startListening();


        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                DatabaseReference ref = Singleton.get(mainActivity).getDatabase()
//                        .child("users").child(userID)
//                        .child("currentCafe");
//
//                newCafe = new Cafe();
//                boolean failed = false;
//                newCafe.setName(shopName.getText().toString());
//                double latitude = 34.02686, longitude = -118.280857;
//
//                try {
//                    Log.d("Address", shopAddress.getText().toString().trim());
//                    Geocoder geocoder = new Geocoder(mainActivity, Locale.getDefault());
//                    List<Address> addresses = geocoder.getFromLocationName(shopAddress.getText().toString().trim(), 1);
//                    Address address = addresses.get(0);
//                    Log.d("address", address.toString());
//                    longitude = address.getLongitude();
//                    latitude = address.getLatitude();
//                } catch (IOException io){
//                    Log.d("io", io.getMessage());
//                    shopAddress.setHint("Invalid Address");
//                    shopAddress.setText("");
//                    failed = true;
//                }
//
//                newCafe.setLatitude(latitude);
//                newCafe.setLongitude(longitude);
//                newCafe.setAddress(shopAddress.getText().toString());
//                newCafe.setHours(shopHours.getText().toString());
//                ref.setValue(newCafe);

                NavDirections action = AddShopFragmentDirections.actionAddShopFragmentToAddItemNewFragment();
                Navigation.findNavController(v).navigate(action);
            }
        });

        addCafeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(RESULT_LOAD_IMAGE_SHOP);

//                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, RESULT_LOAD_IMAGE_SHOP);
            }
        });

        addRegistrationForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(RESULT_LOAD_IMAGE_REG);

//                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, RESULT_LOAD_IMAGE_REG);
            }
        });

        registerShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCafe = new Cafe();
                boolean failed = false;

                if(shopName.getText().toString().trim().length() == 0){
                    shopName.setHintTextColor(Color.RED);
                    shopName.setHint("Invalid Shop Name");
                    shopName.setText("");
                    failed = true;
                }
                else{
                    newCafe.setName(shopName.getText().toString());
                }

                double latitude = 34.02686, longitude = -118.280857;

                try {
                    Log.d("Address", shopAddress.getText().toString().trim());
                    Geocoder geocoder = new Geocoder(mainActivity, Locale.getDefault());
                    Log.d("tStringAdd", shopAddress.getText().toString());

                    List<Address> addresses = geocoder.getFromLocationName(shopAddress.getText().toString().trim(), 1);
                    Address address = addresses.get(0);
                    Log.d("address", address.toString());
                    longitude = address.getLongitude();
                    latitude = address.getLatitude();
                } catch (IOException io){
                    Log.d("ADDRESS IO", io.getMessage());
                    shopAddress.setHint("Invalid Address");
                    shopAddress.setText("");
                    failed = true;
                }

                newCafe.setLatitude(latitude);
                newCafe.setLongitude(longitude);

                if(shopAddress.getText().toString().trim().length() == 0){
                    shopAddress.setHintTextColor(Color.RED);
                    shopAddress.setHint("Invalid Address");
                    shopAddress.setText("");
                    failed = true;
                }
                else{
                    newCafe.setAddress(shopAddress.getText().toString());
                }

                if(shopHours.getText().toString().trim().length() == 0) {
                    shopHours.setHintTextColor(Color.RED);
                    shopHours.setHint("Invalid Hours");
                    shopHours.setText("");
                    failed = true;
                }
                else{
                    newCafe.setHours(shopHours.getText().toString());
                }

                if(!failed){

                    DatabaseReference ref = Singleton.get(mainActivity).getDatabase().child("users").child(userID)
                            .child("currentCafe")
                            .child("items");

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        Map<String, Item> items = new HashMap<>();
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot childSnapShot: dataSnapshot.getChildren()) {
                                items.put(childSnapShot.getKey(), childSnapShot.getValue(Item.class));
                            }
                            newCafe.setItems(items);

                            DatabaseReference newCafeRef = Singleton.get(mainActivity).getDatabase().
                                    child("users").child(Singleton.get(mainActivity).getUserId())
                                    .child("cafes");

                            String id = newCafeRef.push().getKey();
                            newCafe.setId(id);
                            newCafeRef.child(id).setValue(newCafe);

                            DatabaseReference newCafeRefCafe = Singleton.get(mainActivity).getDatabase()
                                    .child("cafes");
                            newCafeRefCafe.child(id).setValue(newCafe);

                            Singleton.get(mainActivity).getDatabase().child("users").child(userID)
                                    .child("currentCafe").removeValue();
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Singleton.get(mainActivity).getDatabase()
                            .child(Singleton.firebaseUserTag)
                            .child(Singleton.get(mainActivity).getUserId())
                            .child("merchant").setValue(true);
                    NavDirections action = AddShopFragmentDirections.actionAddShopFragmentToUserFragment();
                    Navigation.findNavController(v).navigate(action);
                }



            }
        });

        return v;
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(intent, requestCode);
    }
}
