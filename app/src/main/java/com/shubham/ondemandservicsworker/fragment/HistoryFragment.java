package com.shubham.ondemandservicsworker.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shubham.ondemandservicsworker.R;
import com.shubham.ondemandservicsworker.activity.BookingHistoryInfoActivity;
import com.shubham.ondemandservicsworker.adapter.BookingHistoryAdapter;
import com.shubham.ondemandservicsworker.model.historyList;

import java.util.ArrayList;
import java.util.Comparator;


public class HistoryFragment extends Fragment {

    private ListView bookingList;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private String PhoneNumber;
    private ArrayList<historyList> booking = new ArrayList<historyList>();
    private TextView nothing;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        nothing=(TextView)v.findViewById(R.id.fragment_history_tv);
        progressDialog=new ProgressDialog(getActivity());

        booking.clear();
        bookingList = (ListView) v.findViewById(R.id.history_list);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        PhoneNumber = firebaseUser.getPhoneNumber();
        PhoneNumber = PhoneNumber.substring(3);

        progressDialog.show();
        populateList();

        return v;
    }

    private void populateList() {
        db.collection("bookings").whereEqualTo("Worker", PhoneNumber)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        String carModel = document.getString("Car Model");
                        String carCompany = document.getString("Car Company");
                        String carNumber = document.getString("Car Number");
                        String carType = document.getString("Car Type");
                        String wash = document.getString("Wash Price");
                        String clean = document.getString("Clean Price");
                        String vacuum = document.getString("Vacuum Price");
                        String price = document.getString("Total Price");
                        String date = document.getString("Date");
                        String time = document.getString("Time");
                        String status = document.getString("Status");

                        String Model = carCompany + " " + carModel;
                        String Service = "";
                        if (!wash.equals("0"))
                            Service = Service + "Wash ";
                        if (!vacuum.equals("0"))
                            Service = Service + "Vacuum ";
                        if (!clean.equals("0"))
                            Service = Service + "Clean ";
                        String DateTime = date + " " + time;
                        if (status.equals("Ongoing")) {
                            continue;
                        } else {
                            booking.add(new historyList(id, Model, carNumber, Service, DateTime, carType, price, status));
                        }
                    }
                    booking.sort(Comparator.comparing(o -> o.getDateTime()));

                    if(booking.size()>0)
                        nothing.setVisibility(View.GONE);

                    BookingHistoryAdapter bookingHistoryAdapter = new BookingHistoryAdapter(getActivity(), booking);
                    progressDialog.dismiss();
                    bookingList.setAdapter(bookingHistoryAdapter);
                    bookingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            historyList d = booking.get(position);
                            Intent intent = new Intent(getActivity(), BookingHistoryInfoActivity.class);
                            intent.putExtra("Id", d.getId());
                            startActivity(intent);
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Error#121", Toast.LENGTH_SHORT).show();
                    Log.d("DATA FETCH ERROR", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
