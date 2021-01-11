package com.shubham.ondemandservicsworker.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shubham.ondemandservicsworker.R;
import com.shubham.ondemandservicsworker.fragment.BookingFragment;
import com.shubham.ondemandservicsworker.fragment.ProfileFragment;
import com.shubham.ondemandservicsworker.fragment.HistoryFragment;


public class TabLayoutActivity extends AppCompatActivity {

    private final Fragment mHistoryFragment = HistoryFragment.newInstance();
    private final Fragment mBookingFragment = BookingFragment.newInstance();
    private final Fragment mProfileFragment = ProfileFragment.newInstance();


    private Fragment mActiveFragment = mBookingFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_booking:
                    mActiveFragment = mBookingFragment;
                    loadFragment(mActiveFragment);
                    return true;

                case R.id.navigation_list:
                    mActiveFragment = mHistoryFragment;
                    loadFragment(mActiveFragment);
                    return true;


                case R.id.navigation_profile:
                    mActiveFragment = mProfileFragment;
                    loadFragment(mActiveFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        loadFragment(mActiveFragment);

        // Set the onNavigationItemSelected listener
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView mBottomNavigationView = findViewById(R.id.navigation);
        if (mBottomNavigationView.getSelectedItemId() == R.id.navigation_booking)
        {
            super.onBackPressed();
            finishAffinity();
        }
        else
        {
            mBottomNavigationView.setSelectedItemId(R.id.navigation_booking);
        }
    }
}