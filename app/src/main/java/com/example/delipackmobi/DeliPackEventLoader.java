package com.example.delipackmobi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.example.delipackmobi.CustomerContract.UpdateDownloadText;
import com.example.delipackmobi.CustomerContract.UpdateHistory;
import com.example.delipackmobi.Model.CustomerHistoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;

public class DeliPackEventLoader extends AppCompatActivity implements UpdateDownloadText {

    private Button endridersearch;
    private CustomerContract customerContract;
    public static Activity searchRiderActivity;
    private String customerID;
    private TextView downloadtext;

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
            }
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
                databaseReference.removeValue();
                SearchRiderFragment.picklat = "";
                SearchRiderFragment.picklong = "";
                SearchRiderFragment.proximity = 0.1;
                finish();
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
                    SearchRiderFragment.geoQuery.removeAllListeners();
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
