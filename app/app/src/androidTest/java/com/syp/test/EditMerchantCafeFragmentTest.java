package com.syp.test;

import android.content.Intent;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Cafe;
import com.syp.ui.EditMerchantCafeFragment;
import com.syp.ui.StatisticsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EditMerchantCafeFragmentTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;
    private RelativeLayout rlContainer;
    private EditMerchantCafeFragment fragment = null;


    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.putExtra("parameter", "Value");
        mActivity = mActivityTestRule.launchActivity(intent);
        rlContainer = mActivity.findViewById(R.id.emptyContainer);
        assertNotNull(rlContainer);

        fragment = new EditMerchantCafeFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), fragment).commitAllowingStateLoss();
        InsertTestUserData.setUpUser(mActivity);
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void testCafeName()
    {
        assertEquals("TestCafe", fragment.shopName.getText().toString());
    }

    @Test
    public void testCafeAddress()
    {
        assertEquals("935 W 30th Street", fragment.shopAddress.getText().toString());
    }

    @Test
    public void testCafeHours()
    {
        assertEquals("M-S 9am-11pm", fragment.shopHours.getText().toString());
    }

    @Test
    public void testCafeEditButton()
    {
        assertNotNull(fragment.edit);
    }

    @Test
    public void testCafeDoneButton()
    {
        assertNotNull(fragment.done);
    }

    @Test
    public void testSetCafeImage()
    {
        assertNotNull(fragment.setCafeImageButton);
    }

    @Test
    public void testMerchantCafeItems()
    {
        assertNotNull(fragment.merchantCafeItems);
    }
}
