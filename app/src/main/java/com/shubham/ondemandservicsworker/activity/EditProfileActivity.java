package com.shubham.ondemandservicsworker.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shubham.ondemandservicsworker.R;

import java.util.LinkedHashMap;
import java.util.Map;


public class EditProfileActivity extends AppCompatActivity {

    private EditText fname, lname, email, cCode, mnumber, age, address, city;
    private RadioButton genderMale, genderFemale;
    private boolean male, female;
    private Button save, locate;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String Phone, Gender = "";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private String Latitude = "", Longitude = "";
    private boolean LocationFlag = false;
    private ProgressDialog progressDialog;
    private LocationSettingsRequest.Builder builder;
    private static final int REQUEST_CODE_PERMISSION = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();
        //getActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Phone = firebaseUser.getPhoneNumber();
        Phone = Phone.substring(3);
        setDefault();
        setText();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fname.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    fname.setError(getString(R.string.error));
                } else if (lname.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    lname.setError(getString(R.string.error));
                } else if (email.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    email.setError(getString(R.string.error));
                } else if (age.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    cCode.setError(getString(R.string.error));
                } else if (address.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    address.setError(getString(R.string.error));
                } else if (city.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    city.setError(getString(R.string.error));
                } else {
                    progressDialog.setMessage("Updating...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    dataUpdate();
                }
            }
        });

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        EditProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditProfileActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    if (isLocationEnabled(EditProfileActivity.this))
                        setWarning();
                    else
                        setLocation();
                }
            }
        });

    }

    public void setDefault() {
        save = (Button) findViewById(R.id.ed_saveBtn);
        locate = (Button) findViewById(R.id.ed_location);
        fname = (EditText) findViewById(R.id.ed_firstname);
        lname = (EditText) findViewById(R.id.ed_lastname);
        email = (EditText) findViewById(R.id.ed_email);
        cCode = (EditText) findViewById(R.id.ed_countryCode);
        mnumber = (EditText) findViewById(R.id.ed_mobileNumber);
        age = (EditText) findViewById(R.id.ed_age);
        address = (EditText) findViewById(R.id.ed_address);
        city = (EditText) findViewById(R.id.ed_city);
        genderMale = (RadioButton) findViewById(R.id.ed_male);
        genderFemale = (RadioButton) findViewById(R.id.ed_female);

    }

    private void setText() {
        db.collection("workers").document(Phone).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String FName = documentSnapshot.getString("First Name");
                    String LName = documentSnapshot.getString("Last Name");
                    String Age = documentSnapshot.getString("Age");
                    String Emaill = documentSnapshot.getString("Email");
                    String Ccode = documentSnapshot.getString("Country Code");
                    String Mnumber = documentSnapshot.getString("Phone");
                    String Address = documentSnapshot.getString("Address");
                    String City = documentSnapshot.getString("City");
                    String Genderr = documentSnapshot.getString("Gender");

                    fname.setText(FName);
                    lname.setText(LName);
                    email.setText(Emaill);
                    cCode.setText(Ccode);
                    mnumber.setText(Mnumber);
                    age.setText(Age);
                    address.setText(Address);
                    city.setText(City);

                    if (Genderr.equals("Male")) {
                        genderMale.setChecked(true);
                    } else {
                        genderFemale.setChecked(true);
                    }

                }
            }
        });

    }

    public void dataUpdate() {
        String FirstName = fname.getText().toString().trim();
        String LastName = lname.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String CountryCode = cCode.getText().toString().trim();
        String PhoneNo = mnumber.getText().toString().trim();
        String Age = age.getText().toString().trim();
        Gender = genderCheck();
        String Address = address.getText().toString().trim();
        String City = city.getText().toString().trim();

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
        if (LocationFlag) {
            user.put("Latitude", Latitude);
            user.put("Longitude", Longitude);
        }


        db.collection("workers").document(Phone).update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent i = new Intent(EditProfileActivity.this, TabLayoutActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Error!121", Toast.LENGTH_SHORT).show();
                    }
                });
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

    public void setWarning() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Warning!!");
        builder.setIcon(R.drawable.warning);
        builder.setMessage("The process will fetch your location that will be used to show you to the customers on the map." +
                "\nKindly set the pin at your store or the place where you want to show yourself on map.\n" +
                "You can change your location later.\n\nWould you like to Continue?").setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Intent intent = new Intent(EditProfileActivity.this, LocationUpdateActivity.class);
                        startActivityForResult(intent, 1);

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
                if (isLocationEnabled(EditProfileActivity.this))
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
                                resolvableApiException.startResolutionForResult(EditProfileActivity.this, REQUEST_CODE_PERMISSION);
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
                        Toast.makeText(EditProfileActivity.this, "Location permission denied please turn on gps/location of device manually and continue...", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {

                    Latitude= data.getStringExtra("Latitude");
                    Longitude=data.getStringExtra("Longitude");

                    LocationFlag=true;
                }
                break;
        }
    }

}