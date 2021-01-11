package com.shubham.ondemandservicsworker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shubham.ondemandservicsworker.R;

public class BookingInfoActivity extends AppCompatActivity {

    private TextView name, phoneNumber, carCompany, carNumber, date, time, payment, wash, clean, vacuum, extra, total, report, address;
    private ImageView statusImage, carImage;
    private RatingBar ratings;
    private String id, worker, user;
    private String Payment, Status, CarCompany, CarName, CarNumber, CarType, Date, Time, WashPrice, CleanPrice, VacuumPrice, ExtraPrice, TotalPrice;
    private String Name, Rating;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private Button complete, navigate;
    private String Latitude, Longitude;
    private String destination = "",Wallet,ServicesWorker,ServicesUser,dateTime;
    private int walletMoney,servicesWorker,servicesUser;
    private ProgressDialog progressDialog2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_info);
        setDefault();
        progressDialog = new ProgressDialog(BookingInfoActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        progressDialog2 = new ProgressDialog(BookingInfoActivity.this);
        progressDialog2.setMessage("Updating...");
        progressDialog2.setCancelable(false);

        Intent intent = getIntent();
        id = intent.getStringExtra("Id");

        setText();
    }

    private void setText() {
        db.collection("bookings").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        worker = documentSnapshot.getString("Worker");
                        user = documentSnapshot.getString("User");
                        Status = documentSnapshot.getString("Status");
                        CarCompany = documentSnapshot.getString("Car Company");
                        CarName = documentSnapshot.getString("Car Name");
                        CarNumber = documentSnapshot.getString("Car Number");
                        CarType = documentSnapshot.getString("Car Type");
                        Date = documentSnapshot.getString("Date");
                        Time = documentSnapshot.getString("Time");
                        Payment = documentSnapshot.getString("Payment");
                        WashPrice = documentSnapshot.getString("Wash Price");
                        CleanPrice = documentSnapshot.getString("Clean Price");
                        VacuumPrice = documentSnapshot.getString("Vacuum Price");
                        ExtraPrice = documentSnapshot.getString("Extra Price");
                        TotalPrice = documentSnapshot.getString("Total Price");
                        Rating = documentSnapshot.getString("Rating");
                        Latitude = documentSnapshot.getString("Latitude");
                        Longitude = documentSnapshot.getString("Longitude");

                        destination = Latitude + ", " + Longitude;
                        dateTime=Date+" "+Time;

                        phoneNumber.setText(user);

                        if (!Rating.equals("NA")) {
                            ratings.setRating(Float.parseFloat(Rating));
                        }

                        carCompany.setText(CarCompany + " " + CarName);
                        carNumber.setText(CarNumber);
                        date.setText(Date);
                        time.setText(Time);
                        payment.setText("Payment: " + Payment);

                        if (CarType.equals("Mini")) {
                            carImage.setImageResource(R.drawable.car_mini);
                        } else if (CarType.equals("Sedan")) {
                            carImage.setImageResource(R.drawable.car_sedan);
                        } else if (CarType.equals("Suv")) {
                            carImage.setImageResource(R.drawable.car_suv);
                        } else {
                            carImage.setImageResource(R.drawable.car_premium);
                        }

                        wash.setText(WashPrice);
                        clean.setText(CleanPrice);
                        vacuum.setText(VacuumPrice);
                        extra.setText(ExtraPrice);
                        total.setText(TotalPrice);

                        getWorkerInfo(worker);

                        setNavigateButton();
                        setCompleteButton();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BookingInfoActivity.this, "Error650", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void getWorkerInfo(String worker) {
        db.collection("workers").document(worker).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Wallet=documentSnapshot.getString("Wallet");
                        ServicesWorker=documentSnapshot.getString("Services Done");
                        walletMoney=Integer.parseInt(Wallet);
                        servicesWorker=Integer.parseInt(ServicesWorker);
                      getUserInfo(user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BookingInfoActivity.this, "Error650", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void getUserInfo(String user) {
        db.collection("users").document(user).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String Fname = documentSnapshot.getString("First Name");
                        String Lname = documentSnapshot.getString("Last Name");
                        String Address = documentSnapshot.getString("Address");
                        ServicesUser=documentSnapshot.getString("Services Done");
                        servicesUser=Integer.parseInt(ServicesUser);
                        Name = Fname + " " + Lname;
                        name.setText(Name);
                        address.setText(Address);
                        setReport();

                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BookingInfoActivity.this, "Error650", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void setCompleteButton() {
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog2.show();

                if(Payment.equals("Online")){
                    walletMoney=walletMoney+Integer.parseInt(TotalPrice);
                }

                servicesUser=servicesUser+1;
                servicesWorker=servicesWorker+1;

                db.collection("bookings").document(id).update("Status","Complete")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.collection("workers").document(worker).update("Time Slot", FieldValue.arrayRemove(dateTime)
                                        ,"Wallet",String.valueOf(walletMoney),"Services Done",String.valueOf(servicesWorker))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                db.collection("users").document(user).update("Services Done",String.valueOf(servicesUser))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(BookingInfoActivity.this, "Booking Completed Successfully", Toast.LENGTH_SHORT).show();
                                                                progressDialog2.dismiss();
                                                                finish();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(BookingInfoActivity.this, "Error642", Toast.LENGTH_SHORT).show();
                                                        progressDialog2.dismiss();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(BookingInfoActivity.this, "Error641", Toast.LENGTH_SHORT).show();
                                        progressDialog2.dismiss();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BookingInfoActivity.this, "Error640", Toast.LENGTH_SHORT).show();
                        progressDialog2.dismiss();
                    }
                });
            }
        });
    }

    private void setReport() {
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = "User Name: " + Name + "\nBooking Id: " + id + "\n\nIssue Details:";
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_TEXT, s);
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"abc@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Issue Report By " + user);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void setDefault() {
        name = (TextView) findViewById(R.id.booking_info_name_tv);
        phoneNumber = (TextView) findViewById(R.id.booking_info_phone_tv);
        carCompany = (TextView) findViewById(R.id.booking_info_car_name_tv);
        carNumber = (TextView) findViewById(R.id.booking_info_car_number_tv);
        date = (TextView) findViewById(R.id.booking_info_date_tv);
        time = (TextView) findViewById(R.id.booking_info_time_tv);
        payment = (TextView) findViewById(R.id.booking_info_payment_tv);
        address = (TextView) findViewById(R.id.booking_info_address_tv);
        wash = (TextView) findViewById(R.id.booking_info_wash_price_tv);
        clean = (TextView) findViewById(R.id.booking_info_clean_price_tv);
        vacuum = (TextView) findViewById(R.id.booking_info_vacuum_price_tv);
        extra = (TextView) findViewById(R.id.booking_info_extra_price_tv);
        total = (TextView) findViewById(R.id.booking_info_total_prize_tv);
        report = (TextView) findViewById(R.id.booking_info_report_tv);
        carImage = (ImageView) findViewById(R.id.booking_info_car_image);
        statusImage = (ImageView) findViewById(R.id.booking_info_status_image);
        ratings = (RatingBar) findViewById(R.id.booking_info_ratingBar);
        complete = (Button) findViewById(R.id.booking_info_complete_button);
        navigate = (Button) findViewById(R.id.booking_info_navigate_button);
    }

    private void setNavigateButton() {
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMapNavigation();
            }
        });
    }

    private void setMapNavigation() {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


}