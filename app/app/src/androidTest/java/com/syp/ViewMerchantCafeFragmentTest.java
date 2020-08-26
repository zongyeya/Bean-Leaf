package com.syp.test;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Item;
import com.syp.ui.StatisticsFragment;
import com.syp.ui.ViewMerchantCafeFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ViewMerchantCafeFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;
    private RelativeLayout rlContainer;
    private ViewMerchantCafeFragment fragment = null;


    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.putExtra("parameter", "Value");
        mActivity = mActivityTestRule.launchActivity(intent);
        rlContainer = mActivity.findViewById(R.id.emptyContainer);
        assertNotNull(rlContainer);

        fragment = new ViewMerchantCafeFragment();
        InsertTestUserData.setUpCafe(mActivity);
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void testCafeName()
    {
        assertEquals("TestCafe", fragment.viewMerchantCafeCafeName.getText().toString());
    }

    @Test
    public void testCafeAddress()
    {
        assertEquals("935 W 30th Street", fragment.viewMerchantCafeCafeAddress.getText().toString());
    }

    @Test
    public void testCafeHours()
    {
        assertEquals("M-S 9am-11pm", fragment.viewMerchantCafeCafeHours.getText().toString());
    }

    @Test
    public void testCafeTotal()
    {
        assertEquals("1000.0", Double.toString(fragment.cafe.getTotalSales()));
    }

    @Test
    public void testCafeLatitude()
    {
        assertEquals("37.400503", Double.toString(fragment.cafe.getLatitude()));
    }

    @Test
    public void testCafeLongitude()
    {
        assertEquals("-122.113522", Double.toString(fragment.cafe.getLongitude()));
    }

    @Test
    public void testCafeItemStatistics()
    {
        View view = fragment.getView().findViewById(R.id.viewMerchantCafeCafeItemStatistics);
        assertNotNull(view);
    }

    @Test
    public void testCafeItemStatistics3()
    {
        assertNotNull("3", Integer.toString(fragment.cafe.getItems().size()));
    }

    @Test
    public void testCafeItemStatisticsInsert1()
    {
        Item testOrder1Item4 = new Item();
        testOrder1Item4.setCaffeine(30);
        testOrder1Item4.setCount(1);
        testOrder1Item4.setId("testOrder1Item3");
        testOrder1Item4.setName("CoffeeC");
        testOrder1Item4.setPrice(30);
        fragment.cafe.getItems().put(testOrder1Item4.getId(), testOrder1Item4);
        assertNotNull("4", Integer.toString(fragment.cafe.getItems().size()));
    }

    @Test
    public void testCafeItemStatisticsRemove1()
    {
        fragment.cafe.getItems().remove("testOrder1Item3");
        assertNotNull("2", Integer.toString(fragment.cafe.getItems().size()));
    }

    @Test
    public void testCafeMap()
    {
        assertNotNull(fragment.viewMerchantCafeCafeMap);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
        mActivityTestRule = null;
    }
}
