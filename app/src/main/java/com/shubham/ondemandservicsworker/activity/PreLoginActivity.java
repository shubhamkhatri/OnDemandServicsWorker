package com.shubham.ondemandservicsworker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.shubham.ondemandservicsworker.R;
import com.shubham.ondemandservicsworker.utils.LoginPreferences;


public class PreLoginActivity extends AppCompatActivity {

    private Button register,login;
    private LoginPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login);

        loginPreferences = new LoginPreferences(this);
        if (loginPreferences.UserLaunch() == 1) {
            Intent intent = new Intent(PreLoginActivity.this, TabLayoutActivity.class);
            //intent.putExtra("fragment id", 0);
            startActivity(intent);
            finish();
        }

        register=(Button)findViewById(R.id.activity_pre_login_register_button);
        login=(Button)findViewById(R.id.activity_pre_login_sign_in_button);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PreLoginActivity.this,SignUpActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PreLoginActivity.this,LoginActivity.class));
            }
        });
    }
}