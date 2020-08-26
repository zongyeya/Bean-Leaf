package com.syp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.syp.R;
import com.syp.model.Singleton;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class CheckoutViewHolder extends RecyclerView.ViewHolder {
    // Your holder should contain a member variable
    // for any view that will be set as you render a row
    private TextView itemTitleTv;
    private TextView itemSubTitleTv;
    private Button removeButton;

    public CheckoutViewHolder(View itemView) {
            super(itemView);
            itemTitleTv = itemView.findViewById(R.id.itemTitle);
            itemSubTitleTv = itemView.findViewById(R.id.itemSubTitle);
            removeButton = itemView.findViewById(R.id.btnRemoveOne);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavDirections action = CheckoutFragmentDirections.actionCheckoutFragmentToMapFragment();
                    Navigation.findNavController(v).navigate(action);
                }
            });
    }

    public void setTitle(String title) {
        itemTitleTv.setText(title);
    }

    public void setPrice(String price) {
        itemSubTitleTv.setText(price);
    }

    public Button getRemoveBtn() { return removeButton; }
}
