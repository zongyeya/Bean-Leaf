package com.syp.test;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;

//import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.syp.WhiteBoxTestingActivities.TestStatisticsFragmentActivity;
import com.google.firebase.database.ValueEventListener;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Cafe;
import com.syp.model.Item;
import com.syp.model.Order;
import com.syp.model.Singleton;
import com.syp.model.User;
import com.syp.ui.MapFragment;
import com.syp.ui.StatisticsFragment;
//import com.syp.uiTests.StatisticsTestFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class MapFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;
    private RelativeLayout rlContainer;
    private MapFragment fragment = null;


    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.putExtra("parameter", "Value");
        mActivity = mActivityTestRule.launchActivity(intent);
        rlContainer = mActivity.findViewById(R.id.emptyContainer);
        assertNotNull(rlContainer);

        fragment = new MapFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), fragment).commitAllowingStateLoss();
//        InsertTestUserData.setUpUser(mActivity);
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void testInfoBoxInvisible()
    {
        assertEquals(fragment.infoBox.getVisibility(), View.GONE);
    }

    @Test
    public void testMapNotNull()
    {
        assertNotNull(fragment.mMap);
    }

    @Test
    public void testLocation()
    {
        assertEquals(fragment.getUserLatitudeLongitude().latitude, mActivity.latitude, .500);
        assertEquals(fragment.getUserLatitudeLongitude().longitude, mActivity.longitude, .500);
    }

    @Test
    public void testMapMarkers()
    {
        DatabaseReference ref = Singleton.get(mActivity).getDatabase().child("cafes");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int sum = 0;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    sum = sum + 1;
                }
                Log.d("ASDFASDF", "" + fragment.markers.size());
                assertEquals(sum, fragment.markers.size());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

    @Test
    public void testMapMarkerAgainstCafeID(){
        DatabaseReference ref = Singleton.get(mActivity).getDatabase().child("cafes");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int index = 0;
                int sum = 0;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    index = index + 1;
                    for(Marker marker: fragment.markers){
                        if(marker.getTag().toString().equalsIgnoreCase(ds.getValue(Cafe.class).getId()))
                            sum = sum + 1;
                    }
                }
                assertEquals(sum, fragment.markers.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
