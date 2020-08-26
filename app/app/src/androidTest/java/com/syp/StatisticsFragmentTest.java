package com.syp.test;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

//import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.syp.WhiteBoxTestingActivities.TestStatisticsFragmentActivity;
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Item;
import com.syp.model.Order;
import com.syp.model.Singleton;
import com.syp.model.User;
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


public class StatisticsFragmentTest {

    @Rule
    public ActivityTestRule <MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;
    private RelativeLayout rlContainer;
    private StatisticsFragment fragment = null;


    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.putExtra("parameter", "Value");
        mActivity = mActivityTestRule.launchActivity(intent);
        rlContainer = mActivity.findViewById(R.id.emptyContainer);
        assertNotNull(rlContainer);

        fragment = new StatisticsFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), fragment).commitAllowingStateLoss();
        InsertTestUserData.setUpUser(mActivity);
        getInstrumentation().waitForIdleSync();

    }

    @Test
    public void testDailyTotalOutput()
    {
        Log.d("AssTag", fragment.dailyTotal.getText().toString());
        assertEquals("$30.00", fragment.dailyTotal.getText().toString());
    }

    @Test
    public void testWeeklyTotalOutput()
    {
        assertEquals("$30.00", fragment.weeklyTotal.getText().toString());
    }

    @Test
    public void testMonthlyTotalOutput()
    {
        assertEquals("$30.00", fragment.monthlyTotal.getText().toString());
    }

    @Test
    public void testTotalCaffeineOutput()
    {
        assertEquals("20.00 mg",fragment.totalCaffeine.getText().toString());
    }

    @Test
    public void caffeinePieChartLaunch()
    {
        View view = fragment.getView().findViewById(R.id.pieChartCaffeine);
        assertNotNull(view);
    }

    @Test
    public void dailySpendingBarChartLaunch()
    {
        View view = fragment.getView().findViewById(R.id.barChartPriceDaily);
        assertNotNull(view);
    }

    @Test
    public void weeklySpendingBarChartLaunch()
    {
        View view = fragment.getView().findViewById(R.id.barChartPriceWeekly);
        assertNotNull(view);
    }

    @Test
    public void monthlySpendingBarChartLaunch()
    {
        View view = fragment.getView().findViewById(R.id.barChartPriceMonthly);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        InsertTestUserData.removeUser();
        mActivity = null;
        mActivityTestRule = null;
    }
}