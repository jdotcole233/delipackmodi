package com.example.delipackmobi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.cookie.Cookie;

public class PackageInProgress extends AppCompatActivity {

    private ImageView showlessormorebtn, showmorebtn;
    private Button cancelbtn;
    private Button callclientbtn;
    public static Activity Homedardboardactivity;
    private CustomerContract customerContract;
    private TextView companyname, regnumber, pickuplocation, deliverylocation;
    private String riderID, customerID, riderPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_in_progress);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout((int)(displayMetrics.widthPixels * 0.9), (int)(displayMetrics.heightPixels * 0.38));


        showlessormorebtn = findViewById(R.id.minimizeprogress);
        cancelbtn = findViewById(R.id.canceltripinaction);
        callclientbtn = findViewById(R.id.callcustomer);
        showmorebtn = PackageInProgress.Homedardboardactivity.findViewById(R.id.showmoretripinprogress);
        customerContract = new CustomerContract(this);
        companyname = findViewById(R.id.inprogresscompanyname);
        regnumber = findViewById(R.id.inprogressbikeregnumber);
        pickuplocation = findViewById(R.id.inprogresspickup);
        deliverylocation = findViewById(R.id.inprogressdelivery);

        for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
            if(cookie.getName().equals("searchdata")){
                try {
                    JSONObject riderInprogress = new JSONObject(cookie.getValue());
                    pickuplocation.setText("Pick up:" + riderInprogress.getString("pickup"));
                    deliverylocation.setText("Deliver:" + riderInprogress.getString("delivery"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (cookie.getName().equals("company_details")){
                try {
                    JSONObject riderInprogressInfor = new JSONObject(cookie.getValue());
                    regnumber.setText("Bike No: " + riderInprogressInfor.getString("registered_number"));
                    companyname.setText("Company: " + riderInprogressInfor.getString("company_name"));
                    riderID = riderInprogressInfor.getString("company_rider_id");
                    riderPhoneNumber = riderInprogressInfor.getString("work_phone");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject cancelInprogress = new JSONObject(cookie.getValue());
                    customerID = cancelInprogress.getString("customer_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("CustomerRiderRequest")
                .child(customerID).child("delivered");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (dataSnapshot.getValue().equals("true")){
                       finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelIntent = new Intent(PackageInProgress.this, TripInSessionCancel.class);
                cancelIntent.putExtra("customerID", customerID);
                cancelIntent.putExtra("riderID", riderID);
                startActivity(cancelIntent);


            }
        });

        callclientbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callClientButton = new Intent(Intent.ACTION_DIAL);
                callClientButton.setData(Uri.parse("tel:"+riderPhoneNumber));
                startActivity(callClientButton);
            }
        });


        showlessormorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showmorebtn.setVisibility(View.VISIBLE);
                finish();
            }
        });
    }
}
