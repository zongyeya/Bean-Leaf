// Package
package com.syp.ui;

// View Imports
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.util.Log;

// Firebase GeoLocation Imports
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;

// Google GeoLocation Imports
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

// Firebase Database Imports
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

// Internal Class Imports
import com.squareup.picasso.Picasso;
import com.syp.CafeAdapter;
import com.syp.IOnLoadLocationListener;
import com.syp.MyLatLng;
import com.syp.model.Cafe;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Singleton;

// Data Structure Imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// -----------------------------------------------
// Fragment for showing map and markers for cafes
// -----------------------------------------------
public class MapFragment extends Fragment {

    public LinearLayout infoBox;
    public Button viewCafeButton;
    public TextView shopName;
    public TextView shopAddress;
    public TextView shopHours;
    public SearchView searchInput;
    public ArrayList<Cafe> searchResults;
    public ListView searchResultView;
    public CafeAdapter adapter;
    public MainActivity mainActivity;
    public GoogleMap mMap;
    public List<Marker> markers;
    public Button TESTinvisibleRedMarkerButton_PotofChange;
    public Button TESTinvisibleRedMarkerButton_PotofCha;
    public Marker potOfChang;
    public Marker potOfCha;
    public ImageView shopImage;
    public View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate View
        v = inflater.inflate(R.layout.fragment_map, container, false);

        // Get Main Activity
        mainActivity = (MainActivity) getActivity();
        markers = new ArrayList<>();

        // Assign views to variables
        infoBox = v.findViewById(R.id.cafe_infobox);
        shopName = v.findViewById(R.id.map_shopName);
        shopAddress = v.findViewById(R.id.map_shopAddress);
        shopHours = v.findViewById(R.id.map_shopTime);
        searchInput = v.findViewById(R.id.searchCafeInput);
        setOnSearchClicked();
        searchResultView = v.findViewById(R.id.searchResults);
        searchResults = new ArrayList<>();
        adapter = new CafeAdapter(mainActivity, searchResults);
        searchResultView.setAdapter(adapter);
        setListItemListener(searchResultView);
        TESTinvisibleRedMarkerButton_PotofChange = v.findViewById(R.id.TESTinvisibleRedMarker_PotOfChang);
        TESTinvisibleRedMarkerButton_PotofCha = v.findViewById(R.id.TESTinvisibleRedMarker_PotOfCha);
        TESTinvisibleRedMarkerButton_PotofCha.setVisibility(View.VISIBLE);
        TESTinvisibleRedMarkerButton_PotofChange.setVisibility(View.VISIBLE);
        addTESTInvisibleRedMarkerButtonOnClick();
//        shopImage = (ImageView) v.findViewById(R.id.map_shopImage);

        // View Cafe Button & On Click Listener
        viewCafeButton = v.findViewById(R.id.view_cafe_button);
        setViewCafeOnClickListener();

        getMap();

        return v;
    }

    public void getMap(){
        // Create Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((GoogleMap map) -> {

            // Set map & settings
            mainActivity.mapViewGoogleMap = map;
            mMap = map;
            setMapSettings();

            fetchCafes();
            setAllMarkerOnClickListeners();
//            setOnSearchInputChanged();
        });
    }

    private void setMapSettings(){

        // Clear Previous markers
        mMap.clear();

        // Map Type & Zoom
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setMapCamera();
        mMap.setMyLocationEnabled(true);
    }

    private void setMapCamera(){

        // Set max min
        mMap.setMinZoomPreference(8.0f);
        mMap.setMaxZoomPreference(20.0f);

        // Camera settings & start spot
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(34.022404 ,-118.285109)).zoom(12)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
    }

   public LatLng getUserLatitudeLongitude(){
        return new LatLng(mainActivity.latitude, mainActivity.longitude);
    }

    private void setViewCafeOnClickListener(){
        viewCafeButton.setOnClickListener((View v) ->
            Navigation.findNavController(v).navigate(MapFragmentDirections.actionMapFragmentToCafeFragment())
        );
    }

    private void fetchCafes(){

        // Get Database Reference to cafes
        DatabaseReference cafeRef = Singleton.get(mainActivity).getDatabase()
                .child("cafes");

        // Add Listener when info is recieved or changed
        cafeRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, MyLatLng> latLngList = new HashMap<>();
                ArrayList<Cafe> cafes = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {


                    MyLatLng latLng = locationSnapshot.getValue(MyLatLng.class);
                    Cafe cafe = locationSnapshot.getValue(Cafe.class);
                    cafes.add(cafe);

                    latLngList.put(locationSnapshot.getKey(), latLng);

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(cafe.getLatitude(), cafe.getLongitude())).title(cafe.getName()));
                    marker.setTag(cafe.getId());
                    marker.showInfoWindow();
                    markers.add(marker);

                }
                setOnSearchInputChanged(cafes);
                //listener.onLoadLocationSuccess(latLngList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    private void setAllMarkerOnClickListeners(){

        // Set onClickListener for each marker
        mMap.setOnMarkerClickListener((Marker marker)->{
            Singleton.get(mainActivity).setCurrentCafeId((String) marker.getTag());
            showInfoBox();
            return false;
        });
    }

    private ArrayList<Cafe> filterCafes(ArrayList<Cafe> allCafes, CharSequence query) {
        ArrayList<Cafe> cafes = new ArrayList<>();
        for (int i = 0; i < allCafes.size(); i++) {
            if (allCafes.get(i).getName().toLowerCase().contains(query.toString().toLowerCase())) {
                cafes.add(allCafes.get(i));
            }
        }
        return cafes;
    }

    private void setOnSearchClicked() {
        searchInput.setOnSearchClickListener((View v) -> {
            searchInput.setBackgroundColor(Color.WHITE);
        });
        searchInput.setOnCloseListener(() -> {
            searchInput.setBackgroundColor(0x00000000);
            hideKeyboard(mainActivity);
            return false;
        });
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setOnSearchInputChanged(ArrayList<Cafe> cafes) {
        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResults = filterCafes(cafes, newText);
                adapter.clear();
                int bgColour = 0x00000000;
                int visible = View.GONE;
                if (newText.length() != 0) {
                    adapter.addAll(searchResults);
                    bgColour = Color.WHITE;
                    visible = View.VISIBLE;
                }
                searchResultView.setVisibility(visible);
                searchResultView.setBackgroundColor(bgColour);
                return false;
            }
        });
    }

    private void setListItemListener(ListView lv) {
        lv.setOnItemClickListener((AdapterView<?> adapter, View v, int position,
                                    long arg3) ->
            {
                hideKeyboard(mainActivity);
                Cafe cafe = (Cafe) adapter.getItemAtPosition(position);
                Singleton.get(mainActivity).setCurrentCafeId(cafe.getId());
                NavDirections action = MapFragmentDirections.actionMapFragmentToCafeFragment();
                Navigation.findNavController(v).navigate(action);
            }
        );
    }

    private void addTESTInvisibleRedMarkerButtonOnClick(){
        TESTinvisibleRedMarkerButton_PotofCha.setOnClickListener((View v)->{
            Singleton.get(mainActivity).setCurrentCafeId((String) potOfCha.getTag());
            showInfoBox();
        });

        TESTinvisibleRedMarkerButton_PotofChange.setOnClickListener((View v)->{
            Singleton.get(mainActivity).setCurrentCafeId((String) potOfChang.getTag());
            showInfoBox();
        });
    }

    private void showInfoBox(){
        fetchCafeInfo();
        infoBox.setVisibility(View.VISIBLE);
    }

    private void fetchCafeInfo(){
        DatabaseReference ref = Singleton.get(mainActivity).getDatabase()
                .child("cafes").child(Singleton.get(mainActivity).getCurrentCafeId());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get cafe as snapshot
                Cafe cafe = dataSnapshot.getValue(Cafe.class);

                // Check cafe null
                if(cafe == null)
                    return;

                // Set cafe info
                setCafeInfo(cafe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void setCafeInfo(Cafe cafe){
        shopName.setText(cafe.getName());
        shopAddress.setText(cafe.getAddress());
        shopHours.setText(cafe.getHours());
    }
}
