package com.example.delipackmobi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

public class DeliPackEventLoader extends AppCompatActivity {

    private Button endridersearch;
    private CustomerContract customerContract;
    public static Activity searchRiderActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delipack_event_loader);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout(displayMetrics.widthPixels,displayMetrics.heightPixels);

        endridersearch = findViewById(R.id.endridersearch);
        customerContract = new CustomerContract(this);
        SearchRiderFragment.delipackEventloader = this;
        try {
            JSONObject customerJSON = new JSONObject(customerContract.getPersistentCookieStore().getCookies().get(0).getValue());

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        endridersearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CustomerRiderRequest").child("1");
                databaseReference.removeValue();
                SearchRiderFragment.picklat = "";
                SearchRiderFragment.picklong = "";
                SearchRiderFragment.proximity = 0.1;
                finish();
            }
        });
    }
}
