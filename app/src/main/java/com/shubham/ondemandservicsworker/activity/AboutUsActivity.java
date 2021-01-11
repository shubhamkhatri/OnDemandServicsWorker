package com.shubham.ondemandservicsworker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shubham.ondemandservicsworker.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        getSupportActionBar().hide();
        //getActionBar().hide();
    }
}