package com.shubham.ondemandservicsworker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shubham.ondemandservicsworker.R;
import com.shubham.ondemandservicsworker.adapter.BookingHistoryAdapter;
import com.shubham.ondemandservicsworker.model.historyList;
import com.shubham.ondemandservicsworker.model.transactionList;
import com.shubham.ondemandservicsworker.utils.LoginPreferences;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static android.R.layout.simple_list_item_2;

public class WalletActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String PhoneNumber,TotalAmount,TotalPrice;
    private ProgressDialog progressDialog;
    private Button withdraw;
    private TextView wallet;
    private ListView walletList;
    private ArrayList<transactionList> trans=new ArrayList<transactionList>();
    private AlertDialog alertDialog;
    private Calendar myCalendar;
    private TextView nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        nothing=(TextView)findViewById(R.id.wallet_tv);
        withdraw=(Button)findViewById(R.id.wallet_withdraw_button);
        wallet=(TextView)findViewById(R.id.wallet_cash);
        walletList=(ListView)findViewById(R.id.wallet_list);

        progressDialog=new ProgressDialog(WalletActivity.this);
        progressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        PhoneNumber = firebaseUser.getPhoneNumber();
        PhoneNumber = PhoneNumber.substring(3);

        setText();

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBankDetail();
            }
        });

    }

    private void setText() {
        db.collection("workers").document(PhoneNumber).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    TotalAmount=documentSnapshot.getString("Wallet");
                    wallet.setText(TotalAmount);

                    setListView();
                }
            }
        });
    }


    private void setListView() {
        db.collection("bills").whereEqualTo("Phone", PhoneNumber).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String date = document.getString("Date");
                                String Amount=document.getString("Amount");
                                trans.add(new transactionList(Amount,date));
                            }
                            if(trans.size()>0)
                                nothing.setVisibility(View.GONE);

                            ArrayAdapter adapter = new ArrayAdapter(WalletActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, trans) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                                    text1.setText("₹ "+trans.get(position).getAmount());
                                    text2.setText(trans.get(position).getDate());
                                    return view;
                                }
                            };
                            walletList.setAdapter(adapter);
                            progressDialog.dismiss();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(WalletActivity.this, "Error#121", Toast.LENGTH_SHORT).show();
                            Log.d("DATA FETCH ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void setBankDetail() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(WalletActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.bank_details_dialog, null);
        EditText name = (EditText) mView.findViewById(R.id.bank_dialog_holder_name);
        EditText account = (EditText) mView.findViewById(R.id.bank_dialog_account_number);
        EditText confirmAccount = (EditText) mView.findViewById(R.id.bank_dialog_confirm_account_number);
        EditText ifsc = (EditText) mView.findViewById(R.id.bank_dialog_ifsc);
        EditText AmountEt=(EditText)mView.findViewById(R.id.bank_dialog_amount);
        Button btn_cancel = (Button) mView.findViewById(R.id.bank_dialog_btn_cancel);
        Button btn_okay = (Button) mView.findViewById(R.id.bank_dialog_btn_okay);
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
                if (name.getText().toString().trim().isEmpty()) {
                    name.setError(getString(R.string.error));
                    Toast.makeText(WalletActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (account.getText().toString().trim().isEmpty()) {
                    account.setError(getString(R.string.error));
                    Toast.makeText(WalletActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (confirmAccount.getText().toString().trim().isEmpty()) {
                    confirmAccount.setError(getString(R.string.error));
                    Toast.makeText(WalletActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!account.getText().toString().trim().equals(confirmAccount.getText().toString().trim())) {
                    account.setError(getString(R.string.error));
                    confirmAccount.setError(getString(R.string.error));
                    Toast.makeText(WalletActivity.this, "Account number do not match", Toast.LENGTH_SHORT).show();
                } else if (ifsc.getText().toString().trim().isEmpty()) {
                    ifsc.setError(getString(R.string.error));
                    Toast.makeText(WalletActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }else if (AmountEt.getText().toString().trim().isEmpty()) {
                    AmountEt.setError(getString(R.string.error));
                    Toast.makeText(WalletActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(AmountEt.getText().toString().trim())>Integer.parseInt(TotalAmount)) {
                    AmountEt.setError(getString(R.string.error));
                    Toast.makeText(WalletActivity.this, "Amount Exceeds Wallet Amount", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.show();
                    String HolderName = name.getText().toString().trim();
                    String HolderAccount = account.getText().toString().trim();
                    String HolderIfsc = ifsc.getText().toString().trim();
                    TotalPrice=AmountEt.getText().toString().trim();

                    myCalendar=Calendar.getInstance();

                    String myFormat = "dd/MM/yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    String d=sdf.format(myCalendar.getTime());

                    Map<String, Object> bill = new LinkedHashMap<>();

                    bill.put("Account Name", HolderName);
                    bill.put("Account Number", HolderAccount);
                    bill.put("Ifsc", HolderIfsc);
                    bill.put("Amount", TotalPrice);
                    bill.put("Phone", PhoneNumber);
                    bill.put("From", "Worker");
                    bill.put("Id", "NA");
                    bill.put("Status", "Unpaid");
                    bill.put("Date",d);

                    db.collection("bills").document().set(bill)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    updateWallet();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(WalletActivity.this, "Error450", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        alertDialog.show();
    }

    private void updateWallet() {
        int finalAmount=Integer.parseInt(TotalAmount)-Integer.parseInt(TotalPrice);

        db.collection("workers").document(PhoneNumber).update("Wallet",String.valueOf(finalAmount))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        alertDialog.dismiss();
                        progressDialog.dismiss();
                        Toast.makeText(WalletActivity.this, "Bill Request Sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(WalletActivity.this, "Error650", Toast.LENGTH_SHORT).show();
            }
        });
    }


}