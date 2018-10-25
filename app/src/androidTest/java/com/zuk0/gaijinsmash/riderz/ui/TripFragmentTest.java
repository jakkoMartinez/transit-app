package com.zuk0.gaijinsmash.riderz.ui;


import android.support.v4.app.FragmentManager;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.zuk0.gaijinsmash.riderz.R;
import com.zuk0.gaijinsmash.riderz.ui.activity.main.MainActivity;
import com.zuk0.gaijinsmash.riderz.ui.fragment.trip.TripFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TripFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init() {
        Fragment frag = new TripFragment();
        FragmentManager fm = mActivityRule.getActivity().getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragmentContent, frag).commit();
    }

    @Test
    public void TestTripFragment() {
        onView(withId(R.id.trip_container)).check(matches((isDisplayed())));
    }

    // Use case: User selects a station and then hits spacebar, then pushes button -

    // Use case: User uses keyboard and pushes characters/symbols that don't equal to a known Station.




}