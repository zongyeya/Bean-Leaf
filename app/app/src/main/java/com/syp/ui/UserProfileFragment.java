// Package
package com.syp.ui;

// Fragment imports
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// View Imports
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

// Firebase imports (Firebase required)
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// Package imports
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Cafe;
import com.syp.model.Order;
import com.syp.model.Singleton;
import com.syp.model.User;

// ----------------------------------------------------------
// Fragment for page showing user orders, profile, and shops
// ----------------------------------------------------------
public class UserProfileFragment extends Fragment {

    // Activity and Inflater Variables
    private MainActivity mainActivity;
    private LayoutInflater layoutInflater;

    // View variables
    private View v;
    private Button addShop;
    public FloatingActionButton done;
    public FloatingActionButton edit;
    private TextView userProfileUserDisplayName;
    private EditText editUserProfileUserDisplayName;
    private TextView userProfileUserEmail;
    private EditText editUserProfileUserEmail;
    private TextView userProfileUserGender;
    private EditText editUserProfileUserGender;
    private RecyclerView userProfileUserCafes;
    private RecyclerView userProfileUserOrders;

    // ---------------------------------------
    // On Create (Fragment Override Required)
    // ---------------------------------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate view tih user fragment xml
        v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Assign each view to variables
        mainActivity = (MainActivity) getActivity();
        layoutInflater = inflater;

        // User Info Section
        userProfileUserDisplayName = v.findViewById(R.id.userProfileUserDisplayName);
        editUserProfileUserDisplayName = v.findViewById(R.id.editUserProfileUserDisplayName);
        userProfileUserEmail = v.findViewById(R.id.userProfileUserEmail);
        editUserProfileUserEmail = v.findViewById(R.id.editUserProfileUserEmail);
        userProfileUserGender = v.findViewById(R.id.userProfileUserGender);
        editUserProfileUserGender = v.findViewById(R.id.editUserProfileUserGender);
        fetchUserInfo();

        // Recycle View, LayoutManager, & Init for User Shops
        userProfileUserCafes = v.findViewById(R.id.userProfileUserCafes);
        userProfileUserCafes.setLayoutManager(new LinearLayoutManager(mainActivity));
        fetchUserCafes();

        // Add Shop Button
        addShop = v.findViewById(R.id.userProfileAddShop);
        setAddShopOnClickListener();

        // Link Change Cafe Image Button & On Click Listener
        edit = v.findViewById(R.id.editMerchantCafeEdit);
        setEditOnClickListener();

        // Link Change Cafe Image Button & On Click Listener
        done = v.findViewById(R.id.editMerchantCafeDone);
        setDoneOnClickListener();
        done.hide();

        // Recycle View, Layout Manager, & Init for User Orders
        userProfileUserOrders = v.findViewById(R.id.userProfileUserOrders);
        userProfileUserOrders.setLayoutManager(new LinearLayoutManager(mainActivity));
        fetchUserOrders();

        return v;
    }
    // -----------------------------------------------------
    // Set On Click Listener for Edit Button
    // -----------------------------------------------------
    private void setEditOnClickListener(){
        edit.setOnClickListener((View view) ->{
            userProfileUserDisplayName.setVisibility(View.GONE);
            editUserProfileUserDisplayName.setText(userProfileUserDisplayName.getText());
            editUserProfileUserDisplayName.setVisibility(View.VISIBLE);
            userProfileUserEmail.setVisibility(View.GONE);
            editUserProfileUserEmail.setText(userProfileUserEmail.getText());
            editUserProfileUserEmail.setVisibility(View.VISIBLE);
            userProfileUserGender.setVisibility(View.GONE);
            editUserProfileUserGender.setText(userProfileUserGender.getText());
            editUserProfileUserGender.setVisibility(View.VISIBLE);
            edit.hide();
            done.show();
        });
    }

    // -----------------------------------------------------
    // Set On Click Listener for Done Button
    // -----------------------------------------------------
    private void setDoneOnClickListener(){
        done.setOnClickListener((View view)->{

            // Set shop hours and edit visibility

            userProfileUserDisplayName.setVisibility(View.VISIBLE);
            editUserProfileUserDisplayName.setVisibility(View.GONE);
            userProfileUserDisplayName.setText(editUserProfileUserDisplayName.getText());



            userProfileUserEmail.setVisibility(View.VISIBLE);
            editUserProfileUserEmail.setVisibility(View.GONE);
            userProfileUserEmail.setText(editUserProfileUserEmail.getText());


            userProfileUserGender.setVisibility(View.VISIBLE);
            editUserProfileUserGender.setVisibility(View.GONE);
            userProfileUserGender.setText(editUserProfileUserGender.getText());
            edit.show();
            done.hide();

            // Set Value for User -> Cafe -> Hours
            Singleton.get(mainActivity).getDatabase()
                    .child(Singleton.firebaseUserTag)
                    .child(Singleton.get(mainActivity).getUserId())
                    .child("displayName")
                    .setValue(editUserProfileUserDisplayName.getText().toString());
            Singleton.get(mainActivity).getDatabase()
                    .child(Singleton.firebaseUserTag)
                    .child(Singleton.get(mainActivity).getUserId())
                    .child("email")
                    .setValue(editUserProfileUserEmail.getText().toString());
            Singleton.get(mainActivity).getDatabase()
                    .child(Singleton.firebaseUserTag)
                    .child(Singleton.get(mainActivity).getUserId())
                    .child("gender")
                    .setValue(editUserProfileUserGender.getText().toString());

            // Take out keyboard
            InputMethodManager imm = (InputMethodManager) done.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        });
    }
    // ---------------------------------------
    // On Create (Fragment Override Required)
    // ---------------------------------------
    private void setAddShopOnClickListener(){

        // set OnClickListener for add shop button
        // ( Add Shop takes user from Profile Page -> Add Shop Page
        addShop.setOnClickListener((View v) -> {
            NavDirections action = UserProfileFragmentDirections.actionUserFragmentToAddShopFragment();
            Navigation.findNavController(v).navigate(action);
        });
    }

    // -------------------------------------------------------------
    // Set appropriate UI for user info text boxes with User object
    // -------------------------------------------------------------
    private void setUserInfo(User user){
        // Set appropriate views
        userProfileUserDisplayName.setText(user.getDisplayName());
        userProfileUserEmail.setText(user.getEmail());
        userProfileUserGender.setText(user.getGender());
    }

    // ------------------------------------------
    // Add FireBase Event Listener for User Info
    // ------------------------------------------
    private void fetchUserInfo(){

        // Get User Database Reference from firebase
        DatabaseReference userRef = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseUserTag)
                .child(Singleton.get(mainActivity).getUserId());

        // Add Event Listener for spot in database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Convert to user
                User user =  dataSnapshot.getValue(User.class);

                if(user != null)
                    setUserInfo(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // ---------------------------------------------------
    // Add FireBase Event Listener for List of User Cafes
    // ---------------------------------------------------
    private void fetchUserCafes(){

        // Get query from databse
        Query userCafeQueries = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseUserTag)
                .child(Singleton.get(mainActivity).getUserId())
                .child(Singleton.firebaseCafeTag);

        // Firebase Option Builder to convert Data Snapshot to Cafe class
        FirebaseRecyclerOptions<Cafe> options = new FirebaseRecyclerOptions.Builder<Cafe>()
            .setQuery(userCafeQueries, Cafe.class)
            .build();

        // Firebase Create Cafe View Holder
        FirebaseRecyclerAdapter userCafeAdapter = new FirebaseRecyclerAdapter<Cafe, CafeViewHolder>(options) {
            @NonNull
            @Override
            public CafeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CafeViewHolder(layoutInflater.inflate(R.layout.fragment_merchant_shop, parent, false));
            }
            @Override
            protected void onBindViewHolder(@NonNull CafeViewHolder holder, final int position, @NonNull Cafe cafe) {
                holder.setCafeInfo(cafe, mainActivity, UserProfileFragmentDirections.actionUserFragmentToViewMerchantCafeFragment());
            }
        };

        // Set Adapter and start listening
        userProfileUserCafes.setAdapter(userCafeAdapter);
        userCafeAdapter.startListening();

    }

    // ----------------------------------------------------
    // Add FireBase Event Listener for List of User Orders
    // ----------------------------------------------------
    private void fetchUserOrders(){

        // Get query from databse
        Query userOrdersQuery = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseUserTag)
                .child(Singleton.get(mainActivity).getUserId())
                .child(Singleton.firebaseOrderTag);

        // Firebase Option Builder to convert Data Snapshot to Order class
        FirebaseRecyclerOptions<Order> optionsO = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(userOrdersQuery, Order.class)
                .build();

        // Firebase Create Cafe View Holder
        FirebaseRecyclerAdapter userOrdersAdapter = new FirebaseRecyclerAdapter<Order, RowUserOrder>(optionsO) {
            @NonNull
            @Override
            public RowUserOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RowUserOrder(layoutInflater.inflate(R.layout.fragment_user_order_row, parent, false));
            }
            @Override
            protected void onBindViewHolder(RowUserOrder holder, final int position, @NonNull Order order) {
                holder.setOrder(order, mainActivity);
            }
        };

        // specify an adapter
        userProfileUserOrders.setAdapter(userOrdersAdapter);
        userOrdersAdapter.startListening();
    }
}