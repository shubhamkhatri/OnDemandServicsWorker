package com.shubham.ondemandservicsworker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shubham.ondemandservicsworker.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class BookingHistoryInfoActivity extends AppCompatActivity {

    private TextView name, phoneNumber, carCompany, carNumber, date, time, payment, wash, clean, vacuum, extra, total, report;
    private ImageView statusImage, carImage;
    private RatingBar ratings;
    private String id, worker, user;
    private String Payment, Status, CarCompany, CarName, CarNumber, CarType, Date, Time, WashPrice, CleanPrice, VacuumPrice, ExtraPrice, TotalPrice;
    private String Name, Rating;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history_info);

        setDefault();
        progressDialog = new ProgressDialog(BookingHistoryInfoActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

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
                        CarName = documentSnapshot.getString("Car Model");
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

                        phoneNumber.setText(user);

                        if (Status.equals("Complete")) {
                            statusImage.setImageResource(R.drawable.complete);
                        } else {
                            statusImage.setImageResource(R.drawable.cancel);
                        }

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

                        getUserInfo(user);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BookingHistoryInfoActivity.this, "Error650", Toast.LENGTH_SHORT).show();
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
                        Name = Fname + " " + Lname;
                        name.setText(Name);
                        setReport();

                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BookingHistoryInfoActivity.this, "Error650", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
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
        name = (TextView) findViewById(R.id.booking_history_info_name_tv);
        phoneNumber = (TextView) findViewById(R.id.booking_history_info_phone_tv);
        carCompany = (TextView) findViewById(R.id.booking_history_info_car_name_tv);
        carNumber = (TextView) findViewById(R.id.booking_history_info_car_number_tv);
        date = (TextView) findViewById(R.id.booking_history_info_date_tv);
        time = (TextView) findViewById(R.id.booking_history_info_time_tv);
        payment = (TextView) findViewById(R.id.booking_history_info_payment_tv);
        wash = (TextView) findViewById(R.id.booking_history_info_wash_price_tv);
        clean = (TextView) findViewById(R.id.booking_history_info_clean_price_tv);
        vacuum = (TextView) findViewById(R.id.booking_history_info_vacuum_price_tv);
        extra = (TextView) findViewById(R.id.booking_history_info_extra_price_tv);
        total = (TextView) findViewById(R.id.booking_history_info_total_prize_tv);
        report = (TextView) findViewById(R.id.booking_history_info_report_tv);
        carImage = (ImageView) findViewById(R.id.booking_history_info_car_image);
        statusImage = (ImageView) findViewById(R.id.booking_history_info_status_image);
        ratings = (RatingBar) findViewById(R.id.booking_history_info_ratingBar);

    }
}