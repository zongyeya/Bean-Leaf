package com.syp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Item;
import com.syp.model.Singleton;

public class ItemEditFragment extends Fragment {

    public static int RESULT_LOAD_IMAGE = 7;
    public TextView itemName;
    public TextView itemPrice;
    public TextView itemCaffeine;
    public EditText itemNameEdit;
    public EditText itemPriceEdit;
    public EditText itemCaffeineEdit;
    public FloatingActionButton edit;
    public FloatingActionButton done;
    public MainActivity mainActivity;
    public Button changeImage;
    public ImageView image;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_itemedit, container, false);
        mainActivity = (MainActivity) getActivity();
        Item i = new Item();

        itemName = v.findViewById(R.id.merchantitem_title);
        itemPrice = v.findViewById(R.id.merchantitem_price);
        itemCaffeine = v.findViewById(R.id.merchantitem_caffeine);
        itemNameEdit = v.findViewById(R.id.merchantitem_title_edit);
        itemPriceEdit = v.findViewById(R.id.merchantitem_price_edit);
        itemCaffeineEdit = v.findViewById(R.id.merchantitem_caffeine_edit);
        image = v.findViewById(R.id.orderItemItemImage);

        changeImage = v.findViewById(R.id.edititem_addimage);
        edit = v.findViewById(R.id.btnEditItem);
        done = v.findViewById(R.id.btnDoneItem);
        done.hide();

        itemName.setText(i.getName());
        itemPrice.setText(String.valueOf(i.getPrice()));
        itemCaffeine.setText(String.valueOf(i.getCaffeine()));
        itemNameEdit.setText(i.getName());
        itemPriceEdit.setText(String.valueOf(i.getPrice()));
        itemCaffeineEdit.setText(String.valueOf(i.getCaffeine()));
//        image.setImageURI(i.getImage());

        String userID = Singleton.get(mainActivity).getUserId();
        String cafeID = Singleton.get(mainActivity).getCurrentCafeId();
        String itemID = Singleton.get(mainActivity).getCurrentItemId();

        DatabaseReference cafeRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userID)
                .child("cafes").child(cafeID)
                .child("items").child(itemID);

        cafeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Item item = dataSnapshot.getValue(Item.class);
                itemName.setText(item.getName());
                itemPrice.setText(Double.toString(item.getPrice()));
                itemCaffeine.setText(Double.toString(item.getCaffeine()));
                itemNameEdit.setText(item.getName());
                itemPriceEdit.setText(Double.toString(item.getPrice()));
                itemCaffeineEdit.setText(Double.toString(item.getCaffeine()));
                Log.d("CAFE NAME", item.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
//                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemName.setVisibility(View.GONE);
                itemPrice.setVisibility(View.GONE);
                itemCaffeine.setVisibility(View.GONE);
                itemNameEdit.setVisibility(View.VISIBLE);
                itemPriceEdit.setVisibility(View.VISIBLE);
                itemCaffeineEdit.setVisibility(View.VISIBLE);
                edit.hide();
                done.show();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean failed = false;
                DatabaseReference ref = Singleton.get(mainActivity).getDatabase()
                        .child("users").child(Singleton.get(mainActivity).getUserId())
                        .child("cafes").child(Singleton.get(mainActivity).getCurrentCafeId())
                        .child("items").child(Singleton.get(mainActivity).getCurrentItemId());

                DatabaseReference cafeRef = Singleton.get(mainActivity).getDatabase()
                        .child("cafes").child(Singleton.get(mainActivity).getCurrentCafeId())
                        .child("items").child(Singleton.get(mainActivity).getCurrentItemId());

                if(itemNameEdit.getText().toString().trim().length() == 0){
                    itemNameEdit.setHint("Invalid Name");
                    itemNameEdit.setHintTextColor(Color.RED);
                    itemNameEdit.setText("");
                    failed = true;
                }
                else{
                    ref.child("name").setValue(itemNameEdit.getText().toString());
                    cafeRef.child("name").setValue(itemNameEdit.getText().toString());
                    itemName.setVisibility(View.VISIBLE);
                    itemNameEdit.setVisibility(View.GONE);
                    itemName.setText(itemNameEdit.getText().toString());
                }

                try{
                    ref.child("price").setValue(Double.valueOf(itemPriceEdit.getText().toString()));
                    cafeRef.child("price").setValue(Double.valueOf(itemPriceEdit.getText().toString()));
                    itemPrice.setVisibility(View.VISIBLE);
                    itemPriceEdit.setVisibility(View.GONE);
                    itemPrice.setText(itemPriceEdit.getText().toString());
                }catch(NumberFormatException nfe){
                    itemPriceEdit.setText("");
                    itemPriceEdit.setHint("Invalid Number");
                    itemPriceEdit.setHintTextColor(Color.RED);
                    failed = true;
                }

                try{
                    ref.child("caffeine").setValue(Double.valueOf(itemCaffeineEdit.getText().toString()));
                    cafeRef.child("caffeine").setValue(Double.valueOf(itemCaffeineEdit.getText().toString()));
                    itemCaffeine.setVisibility(View.VISIBLE);
                    itemCaffeineEdit.setVisibility(View.GONE);
                }catch(NumberFormatException nfe){
                    itemCaffeineEdit.setText("");
                    itemCaffeineEdit.setHint("Invalid Number");
                    itemCaffeineEdit.setHintTextColor(Color.RED);
                    failed = true;
                }

                if(!failed){
                    // Visibility
                    edit.show();
                    done.hide();
                }

                InputMethodManager imm = (InputMethodManager) done.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

        return v;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }
}
