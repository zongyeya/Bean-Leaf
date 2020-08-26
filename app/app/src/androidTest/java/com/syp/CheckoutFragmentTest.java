package com.syp.test;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import com.syp.MainActivity;
import com.syp.R;
import com.syp.ui.CheckoutFragment;
import com.syp.ui.OrderItemFragment;
import com.syp.ui.StatisticsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CheckoutFragmentTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;
    private RelativeLayout rlContainer;
    private CheckoutFragment fragment = null;


    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.putExtra("parameter", "Value");
        mActivity = mActivityTestRule.launchActivity(intent);
        rlContainer = mActivity.findViewById(R.id.emptyContainer);
        assertNotNull(rlContainer);

        fragment = new CheckoutFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), fragment).commitAllowingStateLoss();
        InsertTestUserData.setUpUser(mActivity);
        InsertTestUserData.setUpCafe(mActivity);
        InsertTestUserData.setUpCurrentOrder();
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void testSubTotal() {
        assertEquals(fragment.subTotalTv.getText().toString(), "$ 30.00");
    }

    @Test
    public void testTax() {
        assertEquals(fragment.taxTv.getText().toString(), "$ 2.40");
    }

    @Test
    public void testTotal() {
        assertEquals(fragment.totalTv.getText().toString(), "$ 32.40");
    }

    @Test
    public void testNumberItems() {
        assertNotNull(fragment.recyclerView);
    }
}
