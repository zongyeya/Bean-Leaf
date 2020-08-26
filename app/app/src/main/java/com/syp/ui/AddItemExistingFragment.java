package com.syp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Item;
import com.syp.model.Singleton;

public class AddItemExistingFragment extends Fragment {

    public static int RESULT_LOAD_IMAGE_EXISTING = 1;
    private MainActivity mainActivity;
    private EditText itemName;
    private EditText itemPrice;
    private EditText itemCaffeine;
    private Button addImage;
    private Button addItem;
    private ImageView image;
    private View greyView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
  View v = inflater.inflate(R.layout.fragment_add_item_existing, container, false);

        mainActivity = (MainActivity) getActivity();
        String userID = Singleton.get(mainActivity).getUserId();

        itemName = v.findViewById(R.id.additemexisting_item_name);
        itemPrice = v.findViewById(R.id.additemexisting_item_price);
        itemCaffeine = v.findViewById(R.id.additemexisting_item_caffeine);
        addImage = v.findViewById(R.id.additemexisting_add_image);
        addItem = v.findViewById(R.id.additemexisting_add_item);
        image =  v.findViewById(R.id.additemexisting_image);
        progressBar = v.findViewById(R.id.addItemExistingProgress);
        greyView = v.findViewById(R.id.newGreyProgress);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                progressBar.setVisibility(View.VISIBLE);
                greyView.setBackgroundColor(Color.DKGRAY);
                DatabaseReference imageRef = Singleton.get(mainActivity).getDatabase()
                        .child("users")
                        .child(Singleton.get(mainActivity).getUserId())
                        .child("currentItem");
                imageRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(String.class) == null)
                            return;
                        progressBar.setVisibility(View.INVISIBLE);
                        greyView.setBackgroundColor(Color.TRANSPARENT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, RESULT_LOAD_IMAGE_EXISTING);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item i = new Item();
                boolean failed = false;

                if(itemName.getText().toString().trim().length()==0){
                    failed = true;
                    itemName.setHintTextColor(Color.RED);
                    itemName.setHint("Invalid Name");
                }
                else{
                    i.setName(itemName.getText().toString());
                    itemName.setText(i.getName());
                }

                try{
                    i.setPrice(Double.parseDouble(itemPrice.getText().toString()));
                    itemPrice.setText(Double.toString(i.getPrice()));
                } catch (NumberFormatException nfe){
                    itemPrice.setText("");
                    itemPrice.setHint("Invalid Number");
                    itemPrice.setHintTextColor(Color.RED);
                    failed = true;
                }

                try{
                    i.setCaffeine(Double.parseDouble(itemCaffeine.getText().toString()));
                    itemCaffeine.setText(Double.toString(i.getCaffeine()));
                } catch (NumberFormatException nfe){
                    itemCaffeine.setText("");
                    itemCaffeine.setHint("Invalid Number");
                    itemCaffeine.setHintTextColor(Color.RED);
                    failed = true;
                }

                if(!failed){
                    fetchCurrentItemImage(i);

                    NavDirections action = AddItemExistingFragmentDirections.actionAddItemExistingFragmentToMerchantShopFragment();
                    Navigation.findNavController(v).navigate(action);
                }

            }
        });

        return v;
    }

    private void fetchCurrentItemImage(Item i){
        DatabaseReference imageRef = Singleton.get(mainActivity).getDatabase()
                .child("users")
                .child(Singleton.get(mainActivity).getUserId())
                .child("currentItem");

        imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                i.setImage(dataSnapshot.getValue(String.class));
                DatabaseReference userCafeRef = Singleton.get(mainActivity).getDatabase().child("users")
                        .child(Singleton.get(mainActivity).getUserId())
                        .child("cafes")
                        .child(Singleton.get(mainActivity).getCurrentCafeId())
                        .child("items");

                DatabaseReference cafeRef = Singleton.get(mainActivity).getDatabase()
                        .child("cafes")
                        .child(Singleton.get(mainActivity).getCurrentCafeId())
                        .child("items");

                String id = userCafeRef.push().getKey();
                i.setId(id);
                userCafeRef.child(id).setValue(i);
                cafeRef.child(id).setValue(i);
                imageRef.removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(intent, RESULT_LOAD_IMAGE_EXISTING);
    }
}
