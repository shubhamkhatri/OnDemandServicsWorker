package com.shubham.ondemandservicsworker.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shubham.ondemandservicsworker.R;
import com.shubham.ondemandservicsworker.activity.AboutUsActivity;
import com.shubham.ondemandservicsworker.activity.ContactUsActivity;
import com.shubham.ondemandservicsworker.activity.EditProfileActivity;
import com.shubham.ondemandservicsworker.activity.PreLoginActivity;
import com.shubham.ondemandservicsworker.activity.PrivacyPolicyActivity;
import com.shubham.ondemandservicsworker.activity.WalletActivity;
import com.shubham.ondemandservicsworker.utils.LoginPreferences;


public class ProfileFragment extends Fragment {

    private CardView profile, wallet, privacy, logout, about, customer;
    private TextView amount, name, servicesDone;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String PhoneNumber;
    private ProgressDialog progressDialog;
    private LoginPreferences loginPreferences;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        setDefault(v);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.show();
        loginPreferences = new LoginPreferences(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        PhoneNumber = firebaseUser.getPhoneNumber();
        PhoneNumber = PhoneNumber.substring(3);
        setText();
        setOnClick();

        return v;
    }

    private void setOnClick() {
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });


        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ContactUsActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                loginPreferences.setLaunch(0);
                getActivity().finish();
                startActivity(new Intent(getActivity(), PreLoginActivity.class));
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WalletActivity.class));
            }
        });

    }

    private void setText() {
        db.collection("workers").document(PhoneNumber).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String fName = documentSnapshot.getString("First Name");
                    String lName = documentSnapshot.getString("Last Name");
                    String matches = documentSnapshot.getString("Services Done");
                    String cash=documentSnapshot.getString("Wallet");

                    String namee = fName + " " + lName;
                    name.setText(namee);
                    servicesDone.setText(matches);
                    amount.setText(cash);
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void setDefault(View view) {

        servicesDone = (TextView) view.findViewById(R.id.pr_services);
        amount = (TextView) view.findViewById(R.id.pr_wallet);
        name = (TextView) view.findViewById(R.id.pr_name);
        profile = (CardView) view.findViewById(R.id.pr_profileCard);
        wallet = (CardView) view.findViewById(R.id.pr_myWalletCard);
        privacy = (CardView) view.findViewById(R.id.pr_impPrivacy);
        logout = (CardView) view.findViewById(R.id.pr_logOutCard);
        about = (CardView) view.findViewById(R.id.pr_aboutUsCard);
        customer = (CardView) view.findViewById(R.id.pr_customerSupportCard);

    }


}