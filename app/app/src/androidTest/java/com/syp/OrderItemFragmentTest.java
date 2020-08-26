package com.syp.test;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.syp.MainActivity;
import com.syp.R;
import com.syp.ui.OrderItemFragment;
import com.syp.ui.StatisticsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrderItemFragmentTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;
    private RelativeLayout rlContainer;
    private OrderItemFragment fragment = null;


    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.putExtra("parameter", "Value");
        mActivity = mActivityTestRule.launchActivity(intent);
        rlContainer = mActivity.findViewById(R.id.emptyContainer);
        assertNotNull(rlContainer);

        fragment = new OrderItemFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), fragment).commitAllowingStateLoss();
        InsertTestUserData.setUpCafe(mActivity);
        getInstrumentation().waitForIdleSync();

    }

    @Test
    public void testItemTitle()
    {
        assertEquals("CoffeeA", fragment.orderItemItemTitle.getText().toString());
    }

    @Test
    public void testItemPrice()
    {
        assertEquals("$20.0", fragment.orderItemItemPrice.getText().toString());
    }

    @Test
    public void testItemCaffeine()
    {
        assertEquals("10.0 mg of caffeine", fragment.orderItemItemCaffeine.getText().toString());
    }

    @Test
    public void testItemToCartButton()
    {
        assertNotNull(fragment.orderItemAddToCart);
    }

    @Test
    public void testCafeItemStatistics()
    {
        assertNotNull(fragment.orderItemCountStepper);
    }
}
