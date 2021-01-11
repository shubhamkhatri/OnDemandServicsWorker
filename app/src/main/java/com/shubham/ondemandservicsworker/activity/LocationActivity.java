package com.shubham.ondemandservicsworker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.shubham.ondemandservicsworker.R;
import com.shubham.ondemandservicsworker.utils.LoginPreferences;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LocationActivity extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    private Button next;
    private String Latitude = "", Longitude = "";
    private String FirstName, LastName, Email, CountryCode, PhoneNo, Age, Gender, Address, City, FinalMob,codeSentToUser;
    private FirebaseAuth firebaseAuth;
    private AlertDialog alertDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private EditText otp;
    private LoginPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getSupportActionBar().hide();
        //getActionBar().hide();


        Intent intent = getIntent();
        FirstName = intent.getStringExtra("First Name");
        LastName= intent.getStringExtra("Last Name");
        Email= intent.getStringExtra("Email");
        CountryCode= intent.getStringExtra("Country Code");
        PhoneNo= intent.getStringExtra("Phone Number");
        Age= intent.getStringExtra("Age");
        Gender= intent.getStringExtra("Gender");
        Address= intent.getStringExtra("Address");
        City= intent.getStringExtra("City");
        FinalMob= intent.getStringExtra("Final Mobile");

        next = (Button) findViewById(R.id.location_next_button);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();
        loginPreferences = new LoginPreferences(this);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_google_map);

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(LocationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkClient();
        } else {
            ActivityCompat.requestPermissions(LocationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {

                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            googleMap.setMyLocationEnabled(true);

                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                            MarkerOptions options = new MarkerOptions().position(latLng);

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                            googleMap.addMarker(options);

                            Latitude = String.valueOf(latLng.latitude);
                            Longitude = String.valueOf(latLng.longitude);

                            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    options.position(latLng);

                                    googleMap.clear();

                                    googleMap.addMarker(options);

                                    Latitude = String.valueOf(latLng.latitude);
                                    Longitude = String.valueOf(latLng.longitude);
                                }
                            });

                            next.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sendVerificationCode(FinalMob);
                                   progressDialog.show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void sendVerificationCode(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider
            .OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otp.setText(code);
                verifyOTPcode(code);
            }
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSentToUser = s;
            progressDialog.dismiss();
            setAlert();

            //OTP that is sent to the user
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(LocationActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(LocationActivity.this, "Too many Request", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void setAlert() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(LocationActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.otp_dialog, null);
        otp = (EditText) mView.findViewById(R.id.txt_input);
        Button btn_cancel = (Button) mView.findViewById(R.id.btn_cancel);
        Button btn_okay = (Button) mView.findViewById(R.id.btn_okay);
        alert.setView(mView);
        alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otp.getText().toString().trim();
                if (code.length() != 6) {
                    otp.setError(getString(R.string.error));
                    otp.requestFocus();
                    return;
                } else {
                    progressDialog.setMessage("SignUp in progress");
                    progressDialog.show();
                    verifyOTPcode(code);
                }
            }
        });
        alertDialog.show();
    }

    private void verifyOTPcode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSentToUser, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(LocationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dataUpdate();
                            alertDialog.dismiss();
                        } else {
                            //verification unsuccessful.. display an error message
                            progressDialog.dismiss();
                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Toast.makeText(LocationActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void dataUpdate() {

        ArrayList<String> timeSlot = new ArrayList<>();
        timeSlot.add("");

        Map<String, Object> user = new LinkedHashMap<>();

        user.put("First Name", FirstName);
        user.put("Last Name", LastName);
        user.put("Email", Email);
        user.put("Country Code", CountryCode);
        user.put("Phone", PhoneNo);
        user.put("Age", Age);
        user.put("Gender", Gender);
        user.put("Address", Address);
        user.put("City", City);
        user.put("Services Done", "0");
        user.put("Wallet", "0");
        user.put("Latitude", Latitude);
        user.put("Longitude", Longitude);
        user.put("Time Slot", timeSlot);
        user.put("Rating", "3");
        user.put("Total Rating", "1");


        db.collection("workers").document(PhoneNo).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loginPreferences.setLaunch(1);
                        Intent intent = new Intent(LocationActivity.this, TabLayoutActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LocationActivity.this, "Error#333", Toast.LENGTH_SHORT).show();
                        Log.d("TAG Database Error", e.toString());
                        firebaseAuth.signOut();
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkClient();
            }
        }
    }

    public void checkClient() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        progressDialog.dismiss();
                        getCurrentLocation();
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(LocationActivity.this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }
}