//package com.syp;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.fragment.app.FragmentActivity;
//
//import android.Manifest;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.location.Location;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Looper;
//import android.widget.Toast;
//
//import com.firebase.geofire.GeoFire;
//import com.firebase.geofire.GeoLocation;
//import com.firebase.geofire.GeoQuery;
//import com.firebase.geofire.GeoQueryDataEventListener;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionDeniedResponse;
//import com.karumi.dexter.listener.PermissionGrantedResponse;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.single.PermissionListener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GeoQueryDataEventListener, IOnLoadLocationListener {
//
//    private GoogleMap mMap;
//    private LocationRequest locationRequest;
//    private LocationCallback locationCallback;
//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private Marker currentUser;
//    private DatabaseReference myLocationRef;
//    private GeoFire geoFire;
//    private List<LatLng> cafeLocations;
//    private IOnLoadLocationListener listener;
//
//    private DatabaseReference myCafe;
//    private Location lastLocation;
//    private GeoQuery geoQuery;
//
//    private Boolean isInCafe = false;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_geofencing);
//
//        Dexter.withActivity(this)
//                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        buildLocationRequest();
//                        buildLocationCallback();
//                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GeofencingActivity.this);
//
//                        initArea();
//                        settingGeoFire();
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response) {
//                        Toast.makeText(GeofencingActivity.this, "You must enable permission", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
//                    }
//                }).check();
//    }
//
//    private void initArea() {
//        myCafe = FirebaseDatabase.getInstance()
//                .getReference("cafes");
//
//        listener = this;
////        myCafe.addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                        List<MyLatLng> latLngList = new ArrayList<>();
////                        for (DataSnapshot locationSnapShot : dataSnapshot.getChildren()) {
////                            MyLatLng latLng = locationSnapShot.getValue(LatLng(class));
////                            latLngList.add(latLng);
////                        }
////                        listener.onLoadLocationSuccess(latLngList);
////                    }
////                    @Override
////                    public void onCancelled(@NonNull DatabaseError databaseError) {
////                        listener.onLoadLocationFailed(databaseError.getMessage());
////                    }
////                });
//        myCafe.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //update cafeLocations list
//                HashMap<String, MyLatLng> latLngList = new HashMap<String, MyLatLng>();
////                List<MyLatLng> latLngList = new ArrayList<>();
//                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
//                    MyLatLng latLng = locationSnapshot.getValue(MyLatLng.class);
//                    latLngList.put(locationSnapshot.getKey(), latLng);
//                }
//
//                listener.onLoadLocationSuccess(latLngList);
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void addUserMarker() {
//        geoFire.setLocation("You", new GeoLocation(lastLocation.getLatitude(),
//                lastLocation.getLongitude()), new GeoFire.CompletionListener() {
//            @Override
//            public void onComplete(String key, DatabaseError error) {
//                if (currentUser != null) currentUser.remove();
//                currentUser = mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(lastLocation.getLatitude(),
//                                lastLocation.getLongitude()))
//                        .title("You"));
//
//                mMap.animateCamera(CameraUpdateFactory
//                        .newLatLngZoom(currentUser.getPosition(), 12.0f));
//            }
//        });
//    }
//
//    private void settingGeoFire() {
//        myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
//        geoFire = new GeoFire(myLocationRef);
//    }
//
//    private void buildLocationCallback() {
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(final LocationResult locationResult) {
//                if (mMap != null) {
//                    lastLocation = locationResult.getLastLocation();
//                    addUserMarker();
//                };
//            };
//        };
//    }
//
//    private void buildLocationRequest() {
//        locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(3000);
//        locationRequest.setSmallestDisplacement(10f);
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//
//        if (fusedLocationProviderClient != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//            }
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//        }
//        addCircleArea();
//    }
//
//    private void addCircleArea() {
//        if (geoQuery != null) {
//            geoQuery.removeGeoQueryEventListener(this);
//            geoQuery.removeAllListeners();
//        }
//        for (LatLng latLng : cafeLocations) {
//            mMap.addCircle(new CircleOptions().center(latLng)
//                    .radius(500)
//                    .strokeColor(Color.BLUE)
//                    .fillColor(0x220000FF)
//                    .strokeWidth(5.0f)
//            );
//
//            //Creates GeoQuery when user is in cafe location
//            geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.1f);
//            geoQuery.addGeoQueryDataEventListener(GeofencingActivity.this);
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//        super.onStop();
//    }
//
//    @Override
//    public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
//        sendNotification("USER", dataSnapshot.getKey() + "%s entered the cafe.");
//    }
//
//    @Override
//    public void onDataExited(DataSnapshot dataSnapshot) {
//        sendNotification("USER", dataSnapshot.getKey() + "%s left the cafe.");
//    }
//
//    @Override
//    public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
//        sendNotification("USER", dataSnapshot.getKey() + "%s moved within the cafe.");
//    }
//
//    @Override
//    public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
//        sendNotification("USER", dataSnapshot.getKey() + "%s changed the cafe.");
//    }
//
//    @Override
//    public void onGeoQueryReady() {
//
//    }
//
//    @Override
//    public void onGeoQueryError(DatabaseError error) {
//        Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
//    }
//
//    public void sendNotification(String title, String content) {
//
//        Toast.makeText(this, ""+content, Toast.LENGTH_SHORT).show();
//
//        String NOTIFICATION_CHANNEL_ID = "cafe_multiple_location";
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            // Configuration
//            notificationChannel.setDescription("Channel description");
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
//            notificationChannel.enableVibration(true);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        builder.setContentTitle(title)
//                .setContentText(content)
//                .setAutoCancel(false)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//
//        Notification notification = builder.build();
//        notificationManager.notify(new Random().nextInt(), notification);
//    }
//
//    @Override
//    public void onLoadLocationSuccess(HashMap<String, MyLatLng> latLngs) {
//        cafeLocations = new ArrayList<>();
//        for (Map.Entry<String, MyLatLng> myLatLng : latLngs.entrySet()) {
//            LatLng convert = new LatLng(myLatLng.getValue().getLatitude(), myLatLng.getValue().getLongitude());
//            cafeLocations.add(convert);
//        }
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(GeofencingActivity.this);
//        //clear map and add again
//        if (mMap != null) {
//            mMap.clear();
//            //Add user Marker
//            addUserMarker();
//
//            //Add circle
//            addCircleArea();
//        }
//    }
//
//    @Override
//    public void onLoadLocationFailure(String message) {
//        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
//    }
//}
