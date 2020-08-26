// Package
package com.syp.ui;

// View Imports
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Package class imports
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.DateFormats;
import com.syp.model.Order;
import com.syp.model.Singleton;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

// Data Structure Imports
import java.util.Locale;
import android.util.Log;

// -----------------------------------------------------------
// Row representing a User Order ( connected to Recycle View)
// -----------------------------------------------------------
public class RowUserOrder extends RecyclerView.ViewHolder {

    // Views
    private TextView userOrderRowCafeName;
    private TextView userOrderRowOrderDate;
    private TextView userOrderRowOrderTotal;
    private TextView userOrderRowOrderCaffeineTotal;
    private TextView userOrderRowOrderTravelTime;
    private TextView userOrderRowOrderDistance;

    // Order associated with row
    private Order order;

    // --------------------------
    // Constructor of User Order
    // --------------------------userOrderRowCafeName
    public RowUserOrder(View itemView) {
        super(itemView);
        userOrderRowCafeName = itemView.findViewById(R.id.userOrderRowCafeName);
        userOrderRowOrderDate = itemView.findViewById(R.id.userOrderRowOrderDate);
        userOrderRowOrderTotal = itemView.findViewById(R.id.userOrderRowOrderTotal);
        userOrderRowOrderCaffeineTotal = itemView.findViewById(R.id.userOrderRowOrderCaffeineTotal);
        userOrderRowOrderTravelTime = itemView.findViewById(R.id.userOrderRowOrderTravelTime);
        userOrderRowOrderDistance = itemView.findViewById(R.id.userOrderRowOrderDistance);
    }

    // ------------------------------------------------------
    // Configures cell with information from order passed in
    // ------------------------------------------------------
    public void setOrder(Order order, MainActivity mainActivity){
        this.order = order;
        this.setOrderCafeName(mainActivity);
        this.setOrderCafeDate();
        this.setOrderPrice();
        this.setOrderCaffeine();
        this.setOrderRowOrderDistance();
        this.setOrderRowOrderTravelTime();

        itemView.setOnClickListener((View v) -> {
            Singleton.get(mainActivity).setCurrentOrderId(order.getId());
            NavDirections action = UserProfileFragmentDirections.actionUserFragmentToUserOrderFragment();
            Navigation.findNavController(v).navigate(action);
        });
    }

    // -------------------------------------------
    // Setters for order informations on UI Views
    // -------------------------------------------
    private void setOrderCafeName(MainActivity mainActivity) {
        Singleton.get(mainActivity).getDatabase()
                .child("cafes")
                .child(order.getCafe())
                .child("name")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userOrderRowCafeName.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }
    private void setOrderCafeDate() {
        userOrderRowOrderDate.setText(DateFormats.getDateString(order.getTimestampAsDate()));
    }
    private void setOrderRowOrderDistance() {
        userOrderRowOrderDistance.setText(String.format(Locale.ENGLISH, "%.2f km", order.getDistance()));
    }

    private void setOrderRowOrderTravelTime() {
        String travelTime = "NA m";
        if(order.getTravelTime() != null && order.getTravelTime().trim().length() != 0)
            travelTime = order.getTravelTime().trim() + " m";
        userOrderRowOrderTravelTime.setText(travelTime);
    }

    private void setOrderPrice() {
        userOrderRowOrderTotal.setText(String.format(Locale.ENGLISH, "$%.2f", order.getTotalSpent()));
    }
    private void setOrderCaffeine() {
        userOrderRowOrderCaffeineTotal.setText(String.format(Locale.ENGLISH, "%.2f mg", order.getTotalCaffeine()));
    }

}
