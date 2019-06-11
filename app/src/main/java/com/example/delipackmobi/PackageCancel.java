package com.example.delipackmobi;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.cookie.Cookie;

public class PackageCancel extends AppCompatActivity {
    private Button canceltransaction;
    private Button abortcancelingtransaction;
    private ProgressBar cancelprogress;
    private CustomerContract customerContract;
    private String customer_id, rider_id;
    private DatabaseReference customerRequest, riderfoundforcustomer;
    public static Activity searchResultactivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_cancel);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout((int)(displayMetrics.widthPixels * 0.9), (int)(displayMetrics.heightPixels * 0.4));
        canceltransaction = findViewById(R.id.yescanceltransaction);
        abortcancelingtransaction = findViewById(R.id.abortcancel);
        cancelprogress = findViewById(R.id.cancelloader);
        customerContract = new CustomerContract(this);

        for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
            if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    customer_id = jsonObject.getString("customer_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (cookie.getName().equals("company_details")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    rider_id = jsonObject.getString("company_rider_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }



        abortcancelingtransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(PackageCancel.this, SearchResult.class);
                startActivity(searchIntent);
                finish();
            }
        });

        canceltransaction.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //end eveything gracefully
                cancelprogress.setVisibility(View.VISIBLE);
                canceltransaction.setFocusable(false);
                abortcancelingtransaction.setFocusable(false);

                if (!customer_id.isEmpty() && !rider_id.isEmpty()){
                    customerRequest = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest");
                    customerRequest.child(customer_id).removeValue();
                    riderfoundforcustomer = FirebaseDatabase.getInstance().getReference().child("RiderFoundForCustomer");
                    riderfoundforcustomer.child(rider_id).child("assigned").setValue("not assigned");
                    cancelprogress.setVisibility(View.INVISIBLE);
                    canceltransaction.setFocusable(true);
                    abortcancelingtransaction.setFocusable(true);

                    finish();

                }


            }
        });
    }
}
