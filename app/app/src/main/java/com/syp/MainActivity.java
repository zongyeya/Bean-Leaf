package com.syp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import com.google.android.material.navigation.NavigationView;

import com.syp.model.*;
import com.syp.ui.AddItemExistingFragment;
import com.syp.ui.AddItemNewFragment;
import com.syp.ui.AddShopFragment;
import com.syp.ui.CafeFragment;
import com.syp.ui.CheckoutFragment;
import com.syp.ui.EditMerchantCafeFragment;
import com.syp.ui.ItemEditFragment;
import com.syp.ui.OrderItemFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity
        implements LocationListener, OnMapReadyCallback, GeoQueryDataEventListener, IOnLoadLocationListener {

    public GoogleMap mapViewGoogleMap;
    public GoogleMap viewMerchantCafeGoogleMap;
    public GoogleMap viewCafeGoogleMap;


    public static MainActivity mainActivity;
    private AppBarConfiguration mAppBarConfiguration;
    private LocationCallback locationCallback;
    private Uri selectedImage;
    private ProgressBar mProgressBar;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private EditText mEditTextFileName;
    protected LocationManager locationManager;
    public double latitude;
    public double longitude;

    private GeofencingClient geofencingClient;
    public GeofenceBroadcastReceiver gBR;
    private PendingIntent geofencePendingIntent;
    private GeofencingRequest geofenceRequest;
    private List<Geofence> geofences;

    IntentFilter geofenceIntentFilter;

    private static int GEOFENCE_RADIUS_IN_METERS = 36;
    private static int GEOFENCE_EXPIRATION_IN_MILLISECONDS = 1000000000;

    // Cafes
    ArrayList<Cafe> cafes;
    String geofenceCafeID;
    int currentCafeIndex;
    int currentItemIndex;
    User user;
    Order currentOrder;
    Cafe newCafe;

    MarkerOptions userMarker;

    public static MainActivity getStaticMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize singleton
        Singleton.get(this);
        setTitle("Bean and Leaf");
        mainActivity = this;

        geofencingClient = LocationServices.getGeofencingClient(this);
        fetchCafes();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    userMarker = new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()));
                    userMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.animateCamera(
                            CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20));
                    mMap.addMarker(userMarker);
                }
            }

            ;
        };

        // Get current latitude and longitude
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    0);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkLocationPermission())
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        MainActivity.this.startActivityForResult(i, 10);

        // TODO: CHECK IF USER VISITED A CAFE WHILE APP WAS CLOSED

        // Populate navigation bar with maps page
        setContentView(R.layout.activity_maps);
        // Create navigation tab with drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        // Creating top level destinations in nav graph hierarchy
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.mapFragment, R.id.userFragment,
                R.id.statisticsFragment, R.id.checkoutFragment, R.id.logoutFragment).setDrawerLayout(drawer).build();
        NavController navController = findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("cafes");
    }

    private void fetchCafes() {

        // Get Database Reference to cafes
        DatabaseReference cafeRef = Singleton.get(this).getDatabase().child("cafes");

        // Add Listener when info is recieved or changed
        cafeRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                geofences = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    Cafe cafe = locationSnapshot.getValue(Cafe.class);
                    Geofence g = new Geofence.Builder().setRequestId(cafe.getId())
                            .setCircularRegion(cafe.getLatitude(), cafe.getLongitude(), GEOFENCE_RADIUS_IN_METERS)
                            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build();
                    geofences.add(g);
                }

                geofenceRequest = getGeofencingRequest();
                geofencePendingIntent = getGeofencePendingIntent();
                geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent);
                // Log.d("Finish Setting up", "ASDFASDFASDF");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void popUpEnteredCafe(String cafeID) {
        this.geofenceCafeID = cafeID;
        Intent i = new Intent(this, EnteredCafeRadius.class);
        startActivityForResult(i, 2001);
    }

    public void popUpExitedCafe(String cafeID) {
        this.geofenceCafeID = cafeID;
        Intent i = new Intent(this, EnteredCafeRadius.class);
        startActivityForResult(i, 2002);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(latitude, longitude)).zoom(12)
                .build();
//        mapViewGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("OnActivityResult", "Success");

        // Image Code Range
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImage = data.getData();

            ImageView imageView;
            if (requestCode == AddItemNewFragment.RESULT_LOAD_IMAGE_NEW) {
                Log.d("test", "11111111111111111111111111");
                imageView = (ImageView) findViewById(R.id.additemnew_image);
                selectedImage = data.getData();
                Picasso.get().load(selectedImage).into(imageView);
                uploadFile(requestCode);
            } else if (requestCode == AddItemExistingFragment.RESULT_LOAD_IMAGE_EXISTING) {
                Log.d("test", "22222222222222222222222");
                imageView = (ImageView) findViewById(R.id.additemexisting_image);
                selectedImage = data.getData();
                Picasso.get().load(selectedImage).into(imageView);
                uploadFile(requestCode);
            } else if (requestCode == AddShopFragment.RESULT_LOAD_IMAGE_REG) {
                Log.d("test", "33333333333333333333");
                imageView = (ImageView) findViewById(R.id.addshop_reg_image);
                selectedImage = data.getData();
                Picasso.get().load(selectedImage).into(imageView);
                uploadFile(requestCode);
            } else if (requestCode == AddShopFragment.RESULT_LOAD_IMAGE_SHOP) {
                Log.d("test", "55555555555555555555555");
                imageView = (ImageView) findViewById(R.id.addshop_cafe_image);
                selectedImage = data.getData();
                Picasso.get().load(selectedImage).into(imageView);
                uploadFile(requestCode);
            } else if (requestCode == EditMerchantCafeFragment.RESULT_LOAD_IMAGE_CHANGESHOP) {
                Log.d("test", "4444444444444444444");
                imageView = (ImageView) findViewById(R.id.editMerchantCafeCafeImage);
                selectedImage = data.getData();
                Picasso.get().load(selectedImage).into(imageView);
                uploadFile(requestCode);
            }
            else if (requestCode == ItemEditFragment.RESULT_LOAD_IMAGE) {
                Log.d("test", "777777777777777777");
                imageView = (ImageView) findViewById(R.id.orderItemItemImage);
                selectedImage = data.getData();
                Picasso.get().load(selectedImage).into(imageView);
                uploadFile(requestCode);
            } else {
                Log.d("test", "ELSEEEEEEE");
                imageView = (ImageView) findViewById(R.id.addshop_cafe_image);
                selectedImage = data.getData();
                Picasso.get().load(selectedImage).into(imageView);
                uploadFile(requestCode);
            }

            // selectedImage = data.getData();
            // Picasso.with(this).load(selectedImage).into()
            // UPLOAD TO DATABASE
        }

        if (requestCode == 10 && resultCode == RESULT_OK && null != data) {

            // gBR = new GeofenceBroadcastReceiver();
            // geofenceIntentFilter = new
            // IntentFilter("com.example.geofence.ACTION_RECEIVE");
            // registerReceiver(gBR, geofenceIntentFilter);
            Log.d("Geofence", "Registered");

            String email = data.getStringExtra("email");
            String displayName = data.getStringExtra("displayName");
            // Toast.makeText(this, email, Toast.LENGTH_LONG).show();
            // Check if user exists
            Query query = Singleton.get(this).getDatabase().child("users").orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getChildren() != null
                            && dataSnapshot.getChildren().iterator().hasNext()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String id = child.getKey();
                            Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_LONG).show();
                            Singleton.get(MainActivity.this).setUserId(id);
                            Singleton.get(MainActivity.this).setLoggedIn();
                            TextView profileName = findViewById(R.id.navigationProfileName);
                            TextView merchant = findViewById(R.id.textViewMerchant);
                            DatabaseReference p = Singleton.get(mainActivity).getDatabase()
                                    .child(Singleton.firebaseUserTag)
                                    .child(Singleton.get(mainActivity).getUserId())
                                    .child("displayName");
                            p.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String pname = dataSnapshot.getValue(String.class);
                                    profileName.setText(pname);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            DatabaseReference m = Singleton.get(mainActivity).getDatabase()
                                    .child(Singleton.firebaseUserTag)
                                    .child(Singleton.get(mainActivity).getUserId())
                                    .child("merchant");
                            m.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Boolean merchantBoolean = dataSnapshot.getValue(Boolean.class);
                                    if (merchantBoolean) {
                                        merchant.setText("Merchant");
                                    } else {
                                        merchant.setText("User");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    } else {
                        // Toast.makeText(MainActivity.this, "New user created with email " + email,
                        // Toast.LENGTH_LONG).show();
                        DatabaseReference userRef = Singleton.get(MainActivity.this).getDatabase().child("users");
                        String id = userRef.push().getKey();
                        User user = new User();
                        user.setEmail(email);
                        user.setDisplayName(displayName != null ? displayName : "");
                        user.setGender(" ");
                        TextView merchant = findViewById(R.id.textViewMerchant);
                        merchant.setText("User");
                        user.setMerchant(false);
                        userRef.child(id).setValue(user);
                        Singleton.get(MainActivity.this).setUserId(id);
                        Singleton.get(MainActivity.this).setLoggedIn();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (requestCode == 2001) {
            Log.d("Request Code : ", "2001");
            String action = data.getStringExtra("action");
            Log.d("ACTION", action);
            if (action.equalsIgnoreCase("viewCafeMenu")) {
                Singleton.get(mainActivity).setCurrentCafeId(geofenceCafeID);
                Navigation.findNavController(mainActivity, R.id.nav_host_fragment).navigate(R.id.cafeFragment);
            }
        }
        if (requestCode == 2002) {
            Log.d("Request Code: ", "2002");
            String action = data.getStringExtra("action");

            if (action.equalsIgnoreCase("viewCafeMenu")) {
                Singleton.get(mainActivity).setCurrentCafeId(geofenceCafeID);
                Navigation.findNavController(mainActivity, R.id.nav_host_fragment).navigate(R.id.cafeFragment);
            }
            if (action.equalsIgnoreCase("viewCheckout")) {
                Singleton.get(mainActivity).setCurrentCafeId(geofenceCafeID);
                Navigation.findNavController(mainActivity, R.id.nav_host_fragment).navigate(R.id.checkoutFragment);
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile(int requestCode) {
        if (selectedImage != null) {
            String currImage = System.currentTimeMillis() + "." + getFileExtension(selectedImage);
            StorageReference fileReference = mStorageRef.child(currImage);

            mUploadTask = fileReference.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload("", taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            // Upload upload = new Upload("testimage",
                            // taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//                            mDatabaseRef.child(Singleton.get(mainActivity).getCurrentCafeId()).child("images");

                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                            if (requestCode == AddItemNewFragment.RESULT_LOAD_IMAGE_NEW) {
                                // NEED TO PUSH CAFE TO DATABASE FIRST
                                dbRef = Singleton.get(mainActivity).getDatabase()
                                        .child("users")
                                        .child(Singleton.get(mainActivity).getUserId())
                                        .child("currentItem");
                                dbRef.setValue(currImage);
                            }
                            else if (requestCode == AddItemExistingFragment.RESULT_LOAD_IMAGE_EXISTING) {
                                dbRef = Singleton.get(mainActivity).getDatabase()
                                        .child("users")
                                        .child(Singleton.get(mainActivity).getUserId())
                                        .child("currentItem");
                                dbRef.setValue(currImage);

                            }
                            else if (requestCode == AddShopFragment.RESULT_LOAD_IMAGE_REG) {
                                // NEED TO PUSH CAFE TO DATABASE FIRST
//                                dbRef = Singleton.get(mainActivity).getDatabase().child("cafes").child(Singleton.get(mainActivity).getCurrentCafeId()).child("RegistrationImage");
                            }
                            else if (requestCode == AddShopFragment.RESULT_LOAD_IMAGE_SHOP) {
                                // NEED TO PUSH CAFE TO DATABASE FIRST
//                                dbRef = Singleton.get(mainActivity).getDatabase().child("cafes").child(Singleton.get(mainActivity).getCurrentCafeId()).child("CafeImage");
                            }
                            else if (requestCode == EditMerchantCafeFragment.RESULT_LOAD_IMAGE_CHANGESHOP) {
                                dbRef = Singleton.get(mainActivity).getDatabase().child("cafes").child(Singleton.get(mainActivity).getCurrentCafeId()).child("CafeImage");
                                dbRef.child("imageUrl").setValue(currImage);
                                return;
                            }
                            else if (requestCode == ItemEditFragment.RESULT_LOAD_IMAGE) {
                                dbRef = Singleton.get(mainActivity).getDatabase().child("cafes").child(Singleton.get(mainActivity).getCurrentCafeId()).child("items").child(Singleton.get(mainActivity).getCurrentItemId()).child("image");
                                dbRef.setValue(currImage);

                            }
                            else {
                                //TEMP
                            }

//                            String uploadId = dbRef.push().getKey();
//                            dbRef.child("ItemImage").setValue(upload);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // double progress = (100.0 * taskSnapshot.getBytesTransferred() /
                            // taskSnapshot.getTotalByteCount());
                            // mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    // GEOFENCING FUNCTIONS

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    private DatabaseReference myLocationRef;
    private GeoFire geoFire;
    private List<LatLng> cafeLocations;
    private IOnLoadLocationListener listener;

    private DatabaseReference myCafe;
    private Location lastLocation;
    private GeoQuery geoQuery;

    private Boolean isInCafe = false;

    private void initArea() {
        myCafe = FirebaseDatabase.getInstance().getReference("cafes");

        listener = this;
        // myCafe.addListenerForSingleValueEvent(new ValueEventListener() {
        // @Override
        // public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        // List<MyLatLng> latLngList = new ArrayList<>();
        // for (DataSnapshot locationSnapShot : dataSnapshot.getChildren()) {
        // MyLatLng latLng = locationSnapShot.getValue(LatLng(class));
        // latLngList.add(latLng);
        // }
        // listener.onLoadLocationSuccess(latLngList);
        // }
        // @Override
        // public void onCancelled(@NonNull DatabaseError databaseError) {
        // listener.onLoadLocationFailed(databaseError.getMessage());
        // }
        // });
        myCafe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // update cafeLocations list
                HashMap<String, MyLatLng> latLngList = new HashMap<String, MyLatLng>();
                // List<MyLatLng> latLngList = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    MyLatLng latLng = locationSnapshot.getValue(MyLatLng.class);
                    latLngList.put(locationSnapshot.getKey(), latLng);
                }

                listener.onLoadLocationSuccess(latLngList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addUserMarker() {
        geoFire.setLocation("You", new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (currentUser != null)
                            currentUser.remove();
                        currentUser = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                                .title("You")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUser.getPosition(), 12.0f));
                    }
                });
    }

    private void settingGeoFire() {
        myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(myLocationRef);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                if (mMap != null) {
                    lastLocation = locationResult.getLastLocation();
                    addUserMarker();
                }
                ;
            }

            ;
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (fusedLocationProviderClient != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
        addCircleArea();
    }

    private void addCircleArea() {
        if (geoQuery != null) {
            geoQuery.removeGeoQueryEventListener(this);
            geoQuery.removeAllListeners();
        }
        for (LatLng latLng : cafeLocations) {
            mMap.addCircle(new CircleOptions().center(latLng).radius(500).strokeColor(Color.BLUE).fillColor(0x220000FF)
                    .strokeWidth(5.0f));

            // Creates GeoQuery when user is in cafe location
            geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.1f);
            geoQuery.addGeoQueryDataEventListener(MainActivity.this);
        }
    }

    @Override
    public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
        sendNotification("USER", dataSnapshot.getKey() + "%s entered the cafe.");
    }

    @Override
    public void onDataExited(DataSnapshot dataSnapshot) {
        sendNotification("USER", dataSnapshot.getKey() + "%s left the cafe.");
    }

    @Override
    public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
        sendNotification("USER", dataSnapshot.getKey() + "%s moved within the cafe.");
    }

    @Override
    public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
        sendNotification("USER", dataSnapshot.getKey() + "%s changed the cafe.");
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
    }

    public void sendNotification(String title, String content) {

        String NOTIFICATION_CHANNEL_ID = "cafe_multiple_location";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "My Notification", NotificationManager.IMPORTANCE_DEFAULT);

            // Configuration
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[] { 0, 1000, 500, 1000 });
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title).setContentText(content).setAutoCancel(false).setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        Notification notification = builder.build();
        notificationManager.notify(new Random().nextInt(), notification);
    }

    @Override
    public void onLoadLocationSuccess(HashMap<String, MyLatLng> latLngs) {
        cafeLocations = new ArrayList<>();
        for (Map.Entry<String, MyLatLng> myLatLng : latLngs.entrySet()) {
            LatLng convert = new LatLng(myLatLng.getValue().getLatitude(), myLatLng.getValue().getLongitude());
            cafeLocations.add(convert);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be
        // used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);

        // clear map and add again
        if (mMap != null) {
            mMap.clear();
            // Add user Marker
            addUserMarker();

            // Add circle
            addCircleArea();
        }
    }

    @Override
    public void onLoadLocationFailure(String message) {
    }

    //
    // private void startLocationUpdates() {
    // fusedLocationProviderClient.requestLocationUpdates(locationRequest,
    // locationCallback,
    // Looper.getMainLooper());
    // }

}
