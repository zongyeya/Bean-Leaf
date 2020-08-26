package com.syp.ui;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Cafe;
import com.syp.model.Singleton;

public class CafeViewHolder extends RecyclerView.ViewHolder {

    Cafe cafe;
    private View cafeRow;
    private TextView cafeName;

    public CafeViewHolder(View cafeView) {
        super(cafeView);
        cafeName = cafeView.findViewById(R.id.cellShopName);
        cafeRow = cafeView.findViewById(R.id.cafeRowButton);
    }

    public void setCafeInfo(Cafe cafe, MainActivity mainActivity, NavDirections action){
        this.cafe = cafe;
        this.setCafeName();

        if(action == null)
            return;

        setViewCafeOnClickListener(mainActivity, action);
    }
    private void setCafeName() {
        cafeName.setText(cafe.getName());
    }

    private void setViewCafeOnClickListener(MainActivity mainActivity, NavDirections action){
        cafeRow.setOnClickListener((View v) -> {
            Singleton.get(mainActivity).setCurrentCafeId(cafe.getId());
            Navigation.findNavController(v).navigate(action);
        });
    }
}
