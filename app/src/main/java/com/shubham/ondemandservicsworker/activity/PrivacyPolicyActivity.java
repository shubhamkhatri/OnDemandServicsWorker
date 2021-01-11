package com.shubham.ondemandservicsworker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shubham.ondemandservicsworker.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        getSupportActionBar().hide();
        //getActionBar().hide();

    }
}