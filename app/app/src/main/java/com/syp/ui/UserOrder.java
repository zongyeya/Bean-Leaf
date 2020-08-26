package com.syp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import com.syp.R;
import com.syp.model.Item;
import com.syp.model.Singleton;
import com.syp.MainActivity;

public class UserOrder extends Fragment {
    private MainActivity mainActivity;
    private LayoutInflater layoutInflater;
    private View v;

    private RecyclerView orderItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate view tih user fragment xml
        v = inflater.inflate(R.layout.user_order, container, false);

        // Assign each view to variables
        mainActivity = (MainActivity) getActivity();
        layoutInflater = inflater;

        // Recycle View, LayoutManager, & Init for User Shops
        orderItems = v.findViewById(R.id.user_order_recycler_view);
        orderItems.setLayoutManager(new LinearLayoutManager(mainActivity));
        fetchUserOrders();

        return v;
    }


    private void fetchUserOrders(){

        // Get query from databse
        Query userItemsQueries = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseUserTag)
                .child(Singleton.get(mainActivity).getUserId())
                .child(Singleton.firebaseOrderTag)
                .child(Singleton.get(mainActivity).getCurrentOrderId())
                .child(Singleton.firebaseItemsTag);

        // Firebase Option Builder to convert Data Snapshot to Cafe class
        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(userItemsQueries, Item.class)
                .build();

        // Firebase Create Cafe View Holder
        FirebaseRecyclerAdapter userOrderAdapter = new FirebaseRecyclerAdapter<Item, ItemInfo>(options) {
            @NonNull
            @Override
            public ItemInfo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ItemInfo(layoutInflater.inflate(R.layout.user_order_row, parent, false));
            }
            @Override
            protected void onBindViewHolder(@NonNull ItemInfo holder, final int position, @NonNull Item item) {
                holder.setItemInfo(item, mainActivity, UserProfileFragmentDirections.actionUserFragmentToUserOrderFragment());
            }
        };

        // Set Adapter and start listening
        orderItems.setAdapter(userOrderAdapter);
        userOrderAdapter.startListening();

    }
}
