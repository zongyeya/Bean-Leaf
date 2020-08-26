package com.syp.test;

import android.content.Intent;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Item;
import com.syp.ui.ItemEditFragment;
import com.syp.ui.OrderItemFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ItemEditFragmentTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;
    private RelativeLayout rlContainer;
    private ItemEditFragment fragment = null;


    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.putExtra("parameter", "Value");
        mActivity = mActivityTestRule.launchActivity(intent);
        rlContainer = mActivity.findViewById(R.id.emptyContainer);
        assertNotNull(rlContainer);

        fragment = new ItemEditFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), fragment).commitAllowingStateLoss();
        InsertTestUserData.setUpUser(mActivity);
        getInstrumentation().waitForIdleSync();

    }

    @Test
    public void testItemTitle()
    {
        assertEquals("CoffeeACafe", fragment.itemName.getText().toString());
    }

    @Test
    public void testItemPrice()
    {
        assertEquals("20.0", fragment.itemPrice.getText().toString());
    }

    @Test
    public void testItemCaffeine()
    {
        assertEquals("10.0", fragment.itemCaffeine.getText().toString());
    }

    @Test
    public void testItemTitleChange()
    {
        fragment.itemName.setText("CoffeeBCafe");
        assertEquals("CoffeeBCafe", fragment.itemName.getText().toString());
    }

    @Test
    public void testItemCaffeineChange()
    {
        fragment.itemCaffeine.setText("30");
        assertEquals("30", fragment.itemCaffeine.getText().toString());
    }

    @Test
    public void testItemEditButton()
    {
        assertNotNull(fragment.edit);
    }

    @Test
    public void testItemDoneButton()
    {
        assertNotNull(fragment.done);
    }

    @Test
    public void testChangeImageButton()
    {
        assertNotNull(fragment.changeImage);
    }
}

