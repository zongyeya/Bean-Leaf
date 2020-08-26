// Pacakge
package com.syp.ui;

// View & Nav Imports
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Google Map Imports
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

// Firebase Imports
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// Package Class Imports
import com.syp.model.Cafe;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Item;
import com.syp.model.Singleton;


// ----------------------------------------------------------
// Fragment for viewing specified merchant shop & statistics
// ----------------------------------------------------------
public class ViewMerchantCafeFragment extends Fragment {

    // Main Activity & Layout Inflater
    MainActivity mainActivity;
    LayoutInflater layoutInflater;

    // Views
    public TextView viewMerchantCafeCafeName;
    public TextView viewMerchantCafeCafeAddress;
    public TextView viewMerchantCafeCafeHours;
    public TextView viewMerchantCafeCafeTotal; // To Be Populated
    public RecyclerView viewMerchantCafeCafeItemStatistics;
    public GoogleMap viewMerchantCafeCafeMap;
    public ImageButton viewMerchantCafeEdit;
    private View v;

    // Cafe associated with page
    public Cafe cafe;

    // ----------------------------------------------------------
    // Fragment for page showing merchant shops & items
    // ----------------------------------------------------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate view
        v = inflater.inflate(R.layout.fragment_view_merchant_cafe, container, false);

        // Main Activity & Inflater
        mainActivity = (MainActivity) getActivity();
        layoutInflater = inflater;

        // Connect Views to variables
        viewMerchantCafeCafeName = v.findViewById(R.id.viewMerchantCafeCafeName);
        viewMerchantCafeCafeAddress = v.findViewById(R.id.viewMerchantCafeCafeAddress);
        viewMerchantCafeCafeHours = v.findViewById(R.id.viewMerchantCafeCafeHours);
        viewMerchantCafeCafeTotal = v.findViewById(R.id.viewMerchantCafeCafeTotal);

        // Edit Button & On Click
        viewMerchantCafeEdit = v.findViewById(R.id.viewMerchantCafeEdit);
        setEditCafeOnClickListener();

        //Set up recycler view
        viewMerchantCafeCafeItemStatistics = v.findViewById(R.id.viewMerchantCafeCafeItemStatistics);
        viewMerchantCafeCafeItemStatistics.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Get Info & Populate
        fetchMerchantCafeItems();
        setMap();

        return v;
    }

    // ----------------------------------------------------------
    // Set Map on Page and Populate data when map is ready
    // ----------------------------------------------------------
    private void setMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.cafeEditMap);
        mapFragment.getMapAsync((GoogleMap map)->{
            viewMerchantCafeCafeMap = map;
            fetchMerchantCafeInfo();
        });
    }

    // ----------------------------------------------------------
    // Fetch Merchant Cafe Info & Populate when info received
    // ----------------------------------------------------------
    private void fetchMerchantCafeInfo(){

        // Get cafe from firebase
        DatabaseReference merchantCafeRef = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseCafeTag)
                .child(Singleton.get(mainActivity).getCurrentCafeId());

        // Create Listener when info is recieved or changed
        merchantCafeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setCafeInfo(dataSnapshot.getValue(Cafe.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // ----------------------------------------------------------
    // Set Info for page with cafe passed in
    // ----------------------------------------------------------
    public void setCafeInfo(Cafe cafe){

        // load data into views
        this.cafe = cafe;
        viewMerchantCafeCafeName.setText(this.cafe.getName());
        viewMerchantCafeCafeAddress.setText(this.cafe.getAddress());
        viewMerchantCafeCafeHours.setText(this.cafe.getHours());
        viewMerchantCafeCafeTotal.setText(Double.toString(this.cafe.getTotalSales()));
        setMapSettings();
        setMapLocation();
    }

    // ----------------------------------------------------------
    // Set Settings for map on page
    // ----------------------------------------------------------
    private void setMapSettings(){

        // Create map settings
        viewMerchantCafeCafeMap.clear();
        viewMerchantCafeCafeMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        viewMerchantCafeCafeMap.setMinZoomPreference(6.0f);
        viewMerchantCafeCafeMap.setMaxZoomPreference(14.0f);
    }

    // ----------------------------------------------------------
    // Population location & camera for map on page
    // ----------------------------------------------------------
    private void setMapLocation(){

        Marker marker = viewMerchantCafeCafeMap.addMarker(new MarkerOptions().position(new LatLng(cafe.getLatitude(), cafe.getLongitude())));
        marker.setTag(cafe.getName());

        // Create Camera
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(this.cafe.getLatitude(), this.cafe.getLongitude()))
                .zoom(50)
                .build();

        // Move map camera to camera
        viewMerchantCafeCafeMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
    }

    // ----------------------------------------------------------
    // Add On Click to Edit Button
    // ----------------------------------------------------------
    private void setEditCafeOnClickListener(){
        viewMerchantCafeEdit.setOnClickListener((View v) -> {
            NavDirections action = ViewMerchantCafeFragmentDirections.actionViewMerchantCafeFragmentToMerchantShopFragment();
            Navigation.findNavController(v).navigate(action);
        });
    }

    // ----------------------------------------------------------
    // Fetch Merchant Cafe Items and populate when received
    // ----------------------------------------------------------
    private void fetchMerchantCafeItems(){

        // Merchant Cafe Items Query
        Query merchantCafeItemsQuery = Singleton.get(mainActivity).getDatabase()
            .child(Singleton.firebaseCafeTag)
            .child(Singleton.get(mainActivity).getCurrentCafeId())
            .child(Singleton.firebaseItemsTag);

        // Firebase Options
        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
            .setQuery(merchantCafeItemsQuery, Item.class)
            .build();

        // Firebase Recycler View
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Item, RowUserItemFragment>(options) {
            @NonNull
            @Override
            public RowUserItemFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RowUserItemFragment(layoutInflater.inflate(R.layout.fragment_cafe_menu_item, parent, false));
            }
            @NonNull
            @Override
            protected void onBindViewHolder(RowUserItemFragment holder, final int position, @NonNull Item item) {
                Log.d("ITEM_LOG", item.getName());
                holder.setUserItemInfo(item, mainActivity, null);
            }
        };

        // specify an adapter
        viewMerchantCafeCafeItemStatistics.setAdapter(adapter);
        adapter.startListening();
    }
}
