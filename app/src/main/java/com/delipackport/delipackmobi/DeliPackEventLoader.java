package com.delipackport.delipackmobi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.delipackport.delipackmobi.CustomerContract.CustomerContract;
import com.delipackport.delipackmobi.CustomerContract.UpdateDownloadText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.cookie.Cookie;

public class DeliPackEventLoader extends AppCompatActivity implements UpdateDownloadText {

    private Button endridersearch;
    private CustomerContract customerContract;
    public static Activity searchRiderActivity;
    private String customerID, rider_id;
    private TextView downloadtext;
    String rID = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delipack_event_loader);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout(displayMetrics.widthPixels,displayMetrics.heightPixels);

        endridersearch = findViewById(R.id.endridersearch);
        downloadtext = findViewById(R.id.downloadtext);
        customerContract = new CustomerContract(this);
        SearchRiderFragment.delipackEventloader = this;
        for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
            if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject customerJSON = new JSONObject(cookie.getValue());
                    customerID = customerJSON.getString("customer_id");

                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            } else if (cookie.getName().equals("company_details")){
                try{
                    JSONObject jsonObj = new JSONObject(cookie.getValue());
                    rider_id = jsonObj.getString("company_rider_id");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }

        Intent endIntent = getIntent();
        if(endIntent != null){
            rID = endIntent.getStringExtra("rider_id");
            System.out.println("In create rID " + rID);

        } else {
            System.out.println("Bad Intent..");
        }

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("CustomerRiderRequest")
                .child(customerID);

                databaseReference.child("unaccepted").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if (dataSnapshot.exists()){
                             if (dataSnapshot.getValue().equals("true")){
                                 databaseReference.removeValue();
                                 finish();
                             }
                         }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        endridersearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CustomerRiderRequest").child(customerID);
                DatabaseReference riderfoundforcustomer = FirebaseDatabase.getInstance().getReference().child("RiderFoundForCustomer");

                System.out.println("End Rider search ");
                databaseReference.child("riderid").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            System.out.println("End Rider search " + dataSnapshot.getValue());
                            rID = dataSnapshot.getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (rider_id != null){
                    System.out.println("In rider_id");
                    riderfoundforcustomer.child(rider_id).child("assigned").setValue("not assigned");
                    databaseReference.removeValue();

                } else {
                    if(rID != null){
                        System.out.println("In rID " + rID);
                        riderfoundforcustomer.child(rID).child("assigned").setValue("not assigned");
                        databaseReference.removeValue();

                    }

                }

                SearchRiderFragment.picklat = "";
                SearchRiderFragment.picklong = "";
                SearchRiderFragment.proximity = 0.1;
//                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest").child(customerID);
        databaseReference.child("rideraccepted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.getValue().equals("true")){
                        return;
                    }
                } else {
                    databaseReference.removeValue();
                    finish();
//                    SearchRiderFragment.geoQuery.removeAllListeners();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("On destrouyeeed called");
    }


    @Override
    public void UpdateSearchText(String message) {
        downloadtext.setText(message);
    }
}
