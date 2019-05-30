package com.example.delipackmobi;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PackageInProgress extends AppCompatActivity {

    private ImageView showlessormorebtn, showmorebtn;
    private Button cancelbtn;
    private Button callclientbtn;
    public static Activity Homedardboardactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_in_progress);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout((int)(displayMetrics.widthPixels * 0.95), (int)(displayMetrics.heightPixels * 0.3));


        showlessormorebtn = findViewById(R.id.minimizeprogress);
        cancelbtn = findViewById(R.id.canceltripinaction);
        callclientbtn = findViewById(R.id.callcustomer);
        showmorebtn = PackageInProgress.Homedardboardactivity.findViewById(R.id.showmoretripinprogress);


        showlessormorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showmorebtn.setVisibility(View.VISIBLE);
                finish();
            }
        });
    }
}
