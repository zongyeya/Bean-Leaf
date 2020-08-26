// Package
package com.syp.ui;

// View & Navigation Imports
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Firebase Imports
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// Package Class Imports
import com.squareup.picasso.Picasso;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Cafe;
import com.syp.model.Item;
import com.syp.model.Singleton;

// ---------------------------------------
// On Create (Fragment Override Required)
// ---------------------------------------
public class EditMerchantCafeFragment extends Fragment {

    public static int RESULT_LOAD_IMAGE_CHANGESHOP = 5;

    // Main Activity
    private MainActivity mainActivity;
    private LayoutInflater layoutInflater;

    // Views
    public TextView shopName;
    public TextView shopAddress;
    public TextView shopHours;
    public EditText shopHoursEdit;
    public Button addItem;
    public FloatingActionButton done;
    public FloatingActionButton edit;
    public RecyclerView merchantCafeItems;
    public ImageView image;
    public Button setCafeImageButton;
    public Cafe cafe;
    private View v;

    // ---------------------------------------
    // On Create (Fragment Override Required)
    // ---------------------------------------
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate view
        v = inflater.inflate(R.layout.fragment_merchantshop, container, false);

        // Main Activity
        mainActivity = (MainActivity) getActivity();
        layoutInflater = inflater;

        // Link views to variables
        shopName = v.findViewById(R.id.editMerchantCafeCafeName);
        shopAddress = v.findViewById(R.id.editMerchantCafeCafeAddress);
        shopHours = v.findViewById(R.id.editMerchantCafeCafeHours);
        shopHoursEdit = v.findViewById(R.id.editMerchantCafeEditCafeHours);
        image = v.findViewById(R.id.editMerchantCafeCafeImage);

        // Link Change Cafe Image Button & On Click Listener
        setCafeImageButton = v.findViewById(R.id.editMerchantCafeSetCafeImage);
        setSetCafeImageButtonOnClickListener();

        // Link Change Cafe Image Button & On Click Listener
        addItem = v.findViewById(R.id.editMerchantCafeAddItem);
        setAddItemOnClickListener();

        // Link Change Cafe Image Button & On Click Listener
        edit = v.findViewById(R.id.editMerchantCafeEdit);
        setEditOnClickListener();

        // Link Change Cafe Image Button & On Click Listener
        done = v.findViewById(R.id.editMerchantCafeDone);
        setDoneOnClickListener();
        done.hide();

        // Set up Recycle View
        merchantCafeItems = v.findViewById(R.id.editMerchantCafeCafeItems);
        merchantCafeItems.setLayoutManager(new LinearLayoutManager(mainActivity));

        // Fetch Info from
        fetchMerchantCafeInfo();
        fetchMerchantCafeItems();

        setImage();

        return v;
    }

    // -----------------------------------------------------
    // Fetch Merchant Cafe Items and load into recycle view
    // -----------------------------------------------------
    private void fetchMerchantCafeItems(){

        // Query for Merchant Cafe Items
        Query query = FirebaseDatabase.getInstance().getReference()
            .child(Singleton.firebaseUserTag)
            .child(Singleton.get(mainActivity).getUserId())
            .child(Singleton.firebaseCafeTag)
            .child(Singleton.get(mainActivity).getCurrentCafeId())
            .child(Singleton.firebaseItemsTag);

        // Firebase Options
        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
            .setQuery(query, Item.class)
            .build();


        //Firebase Recycler View
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Item, RowUserItemFragment>(options) {
            @Override
            public RowUserItemFragment onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RowUserItemFragment(layoutInflater.inflate(R.layout.fragment_cafe_menu_item, parent, false));
            }
            @Override
            protected void onBindViewHolder(RowUserItemFragment holder, final int position, Item item) {
                holder.setUserItemInfo(item, mainActivity, EditMerchantCafeFragmentDirections.actionMerchantShopFragmentToItemEditFragment());
            }
        };

        // specify an adapter
        merchantCafeItems.setAdapter(adapter);
        adapter.startListening();
    }

    // -----------------------------------------------------
    // Set Add Item On Click Listener
    // -----------------------------------------------------
    private void setAddItemOnClickListener(){
        addItem.setOnClickListener((View v)->{
            NavDirections action = EditMerchantCafeFragmentDirections.actionMerchantShopFragmentToAddItemExistingFragment();
            Navigation.findNavController(v).navigate(action);
        });
    }

    // -----------------------------------------------------
    // Set Cafe Image Button On Click Listener
    // -----------------------------------------------------
    private void setSetCafeImageButtonOnClickListener(){
        setCafeImageButton.setOnClickListener((View v) -> {
            openFileChooser();
//            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(i, RESULT_LOAD_IMAGE_CHANGESHOP);
        });
    }

    // -----------------------------------------------------
    // Set On Click Listener for Edit Button
    // -----------------------------------------------------
    private void setEditOnClickListener(){
        edit.setOnClickListener((View view) ->{
            shopHours.setVisibility(View.GONE);
            shopHoursEdit.setVisibility(View.VISIBLE);
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
            shopHours.setVisibility(View.VISIBLE);
            shopHoursEdit.setVisibility(View.GONE);
            shopHours.setText(shopHoursEdit.getText());
            edit.show();
            done.hide();

            // Set Value for User -> Cafe -> Hours
            Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseUserTag)
                .child(Singleton.get(mainActivity).getUserId())
                .child(Singleton.firebaseCafeTag)
                .child(Singleton.get(mainActivity).getCurrentCafeId())
                .child(Singleton.firebaseCafeHoursTag)
                .setValue(shopHoursEdit.getText().toString());

            // Set Value for Master Cafe List
            Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseCafeTag)
                .child(Singleton.get(mainActivity).getCurrentCafeId())
                .child(Singleton.firebaseCafeHoursTag)
                .setValue(shopHoursEdit.getText().toString());

            // Take out keyboard
            InputMethodManager imm = (InputMethodManager) done.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        });
    }

    // -----------------------------------------------------
    // Get Merchant Cafe Info from Database
    // -----------------------------------------------------
    private void fetchMerchantCafeInfo(){

        // Merchant Cafe Ref
        DatabaseReference merchantCafeRef = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseUserTag)
                .child(Singleton.get(mainActivity).getUserId())
                .child(Singleton.firebaseCafeTag)
                .child(Singleton.get(mainActivity).getCurrentCafeId());

        // Set on Click Listenner
        merchantCafeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Set Info for Cafe
                setMerchantCafeInfo(dataSnapshot.getValue(Cafe.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    // -----------------------------------------------------
    // Set Info in TextViews based off passed in cafe
    // -----------------------------------------------------
    private void setMerchantCafeInfo(Cafe cafe){
        this.cafe = cafe;
        shopName.setText(this.cafe.getName());
        shopAddress.setText(this.cafe.getAddress());
        shopHours.setText(this.cafe.getHours());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(intent, RESULT_LOAD_IMAGE_CHANGESHOP);
    }

    private void setImage() {
        Singleton.get(mainActivity).getDatabase()
                .child("cafes")
                .child(Singleton.get(mainActivity)
                        .getCurrentCafeId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("CafeImage").child("imageUrl").getValue(String.class) == null) {
                    Log.d("Image2", "null");
                    return;
                }

                Singleton.get(mainActivity).getStorage().getReference().child("uploads").child(dataSnapshot.child("CafeImage").child("imageUrl").getValue(String.class)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(uri == null){
                            Log.d("Image", "null");
                            return;
                            // Got the download URL for 'users/me/profile.png'
                        }
                        String link = uri.toString();
                        Log.d("Link", link);
                        Picasso.get().load(link).into(image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
