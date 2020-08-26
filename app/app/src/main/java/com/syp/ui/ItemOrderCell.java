package com.syp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.syp.R;

import org.w3c.dom.Text;

public class ItemOrderCell extends RecyclerView.ViewHolder {

    private TextView orderItemName;
    private TextView orderItemCount;

    public ItemOrderCell(View cell) {
        super(cell);
        orderItemName = cell.findViewById(R.id.orderItemName);
        orderItemCount = cell.findViewById(R.id.orderItemCount);
    }

    public void setOrderItemCount(String orderItemCount) {
        this.orderItemCount.setText(orderItemCount);
    }
    public void setOrderItemName(String orderItemName){
        this.orderItemName.setText("x"+orderItemName);
    }
}
