package com.syp.test;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;


//import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.syp.R;
import com.syp.model.Cafe;
import com.syp.model.Item;
import com.syp.model.User;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class CafeModelTest {

    Cafe whiteBoxTestCafe = null;

    @Before
    public void setUp() throws Exception {
        whiteBoxTestCafe = new Cafe();

    }

    @Test
    public void cafeNameTest()
    {
        whiteBoxTestCafe.setName("Ethan's Tea");
        assertEquals("Ethan's Tea", whiteBoxTestCafe.getName());
    }

    @Test
    public void cafeSetIdTest()
    {
        whiteBoxTestCafe.setId("White Box Cafe");
        assertEquals("White Box Cafe", whiteBoxTestCafe.getId());
    }

    @Test
    public void cafeAddressTest()
    {
        whiteBoxTestCafe.setAddress("1015 W 34th");
        assertEquals("1015 W 34th" , whiteBoxTestCafe.getAddress());
    }

    @Test
    public void cafeHoursTest()
    {
        whiteBoxTestCafe.setHours("M - F 9:00 - 21:00");
        assertEquals("M - F 9:00 - 21:00", whiteBoxTestCafe.getHours());
    }

    @Test
    public void cafeTotalSalesTest()
    {
        whiteBoxTestCafe.setTotalSales(200.2);
        assertEquals(200.2 , whiteBoxTestCafe.getTotalSales(),0.0);
    }

    @Test
    public void longitudeTest()
    {
        whiteBoxTestCafe.setLongitude(12.0);
        assertEquals(12.0, whiteBoxTestCafe.getLongitude(),0.0);
    }

    @Test
    public void latitudeTest()
    {
        whiteBoxTestCafe.setLatitude(12.0);
        assertEquals(12.0, whiteBoxTestCafe.getLatitude(),0.0);
    }

    @Test
    public void imageTest()
    {
        whiteBoxTestCafe.setImage("image.url");
        assertEquals("image.url", whiteBoxTestCafe.getImage());
    }



    @After
    public void tearDown() throws Exception {
        whiteBoxTestCafe = null;
    }
}