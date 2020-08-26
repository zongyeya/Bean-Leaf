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
import com.syp.MainActivity;
import com.syp.R;
import com.syp.model.Cafe;
import com.syp.model.Item;
import com.syp.model.User;
import com.syp.ui.AddItemExistingFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class WhiteBoxAddItemExistingFragmentTest {

    @Rule
    public ActivityTestRule <MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;
    private RelativeLayout rlContainer;
    private AddItemExistingFragment fragment = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        rlContainer = (RelativeLayout) mActivity.findViewById(R.id.emptyContainer);
        assertNotNull(rlContainer);

        fragment = new AddItemExistingFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();

    }
}