package com.shubham.ondemandservicsworker.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shubham.ondemandservicsworker.R;
import com.shubham.ondemandservicsworker.utils.LoginPreferences;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class SignUpActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, countryCode, mobileNo, age, address, city, otp;
    private RadioButton genderMale, genderFemale;
    private TextView login;
    private Button register;
    private boolean male, female;
    private String Gender = "", mobile, codeSentToUser;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_PERMISSION = 101;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LocationSettingsRequest.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        //getActionBar().hide();

        setDefault();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields!!", Toast.LENGTH_SHORT).show();
                    firstName.setError(getString(R.string.error));
                } else if (lastName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields!!", Toast.LENGTH_SHORT).show();
                    lastName.setError(getString(R.string.error));
                } else if (email.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields!!", Toast.LENGTH_SHORT).show();
                    email.setError(getString(R.string.error));
                } else if (!email.getText().toString().trim().contains("@")) {
                    Toast.makeText(SignUpActivity.this, "Please enter valid Email!!", Toast.LENGTH_SHORT).show();
                    email.setError("Invalid Email");
                } else if (countryCode.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields!!", Toast.LENGTH_SHORT).show();
                    countryCode.setError(getString(R.string.error));
                } else if (mobileNo.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields!!", Toast.LENGTH_SHORT).show();
                    mobileNo.setError(getString(R.string.error));
                } else if (mobileNo.getText().toString().trim().length() != 10) {
                    Toast.makeText(SignUpActivity.this, "Please enter a valid number!!", Toast.LENGTH_SHORT).show();
                    mobileNo.setError("Invalid Number!");
                } else if (age.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields!!", Toast.LENGTH_SHORT).show();
                    age.setError(getString(R.string.error));
                } else if (genderCheck().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please select Gender", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields!!", Toast.LENGTH_SHORT).show();
                    address.setError(getString(R.string.error));
                } else if (city.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields!!", Toast.LENGTH_SHORT).show();
                    city.setError(getString(R.string.error));
                } else {
                    checkNumber();
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

    }

    private void setDefault() {
        firstName = (EditText) findViewById(R.id.su_firstname);
        lastName = (EditText) findViewById(R.id.su_lastname);
        email = (EditText) findViewById(R.id.su_email);
        countryCode = (EditText) findViewById(R.id.su_countryCode);
        mobileNo = (EditText) findViewById(R.id.su_mobileNumber);
        age = (EditText) findViewById(R.id.su_age);
        address = (EditText) findViewById(R.id.su_address);
        city = (EditText) findViewById(R.id.su_city);
        genderMale = (RadioButton) findViewById(R.id.su_male);
        genderFemale = (RadioButton) findViewById(R.id.su_female);
        login = (TextView) findViewById(R.id.su_login_text);
        register = (Button) findViewById(R.id.su_registerBtn);
    }

    public String genderCheck() {
        String g = "";
        male = genderMale.isChecked();
        female = genderFemale.isChecked();
        if (male)
            g = "Male";
        if (female)
            g = "Female";

        return g;
    }

    private void checkNumber() {
        String e = mobileNo.getText().toString().trim();
        db.collection("workers").document(e).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                mobileNo.setError("Already Registered!!");
                                Toast.makeText(SignUpActivity.this, "User already exists with this Number \n Please Login", Toast.LENGTH_SHORT).show();
                            } else {
                                if (ContextCompat.checkSelfPermission(
                                        SignUpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(SignUpActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                                } else {
                                    if (isLocationEnabled(SignUpActivity.this))
                                        setWarning();
                                    else
                                        setLocation();
                                }
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Error650", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setWarning() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Warning!!");
        builder.setIcon(R.drawable.warning);
        builder.setMessage("The process will fetch your location that will be used to show you to the customers on the map." +
                "\nKindly set the pin at your store or the place where you want to show yourself on map.\n" +
                "You can change your location later from edit profile.\n\nWould you like to Continue?").setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        mobile = countryCode.getText().toString().trim() +
                                mobileNo.getText().toString().trim();
                        Intent intent = new Intent(SignUpActivity.this, LocationActivity.class);
                        intent.putExtra("First Name", firstName.getText().toString().trim());
                        intent.putExtra("Last Name", lastName.getText().toString().trim());
                        intent.putExtra("Email", email.getText().toString().trim());
                        intent.putExtra("Country Code", countryCode.getText().toString().trim());
                        intent.putExtra("Phone Number", mobileNo.getText().toString().trim());
                        intent.putExtra("Age", age.getText().toString().trim());
                        intent.putExtra("Gender", genderCheck());
                        intent.putExtra("Address", address.getText().toString().trim());
                        intent.putExtra("City", city.getText().toString().trim());
                        intent.putExtra("Final Mobile", mobile);
                        startActivity(intent);


                    }
                }).setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled(SignUpActivity.this))
                    setWarning();
                else
                    setLocation();
            }
        }
    }

    public void setLocation() {
        LocationRequest request = new LocationRequest()
                .setFastestInterval(1500)
                .setInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    task.getResult(ApiException.class);
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED: {
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(SignUpActivity.this, REQUEST_CODE_PERMISSION);
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            } catch (ClassCastException ex) {
                            }
                            break;
                        }
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: {
                            break;
                        }
                    }
                }
            }
        });
    }

    private boolean isLocationEnabled(Context context) {
        int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF);
        final boolean enabled = (mode != android.provider.Settings.Secure.LOCATION_MODE_OFF);
        return enabled;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                switch (resultCode) {
                    case RESULT_OK:
                        setWarning();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(SignUpActivity.this,"Location permission denied please turn on gps/location of device manually and continue...",Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }
}


