package com.example.delipackmobi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

public class HistoryDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_details);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int windowwidth =  (int)(displayMetrics.widthPixels * 0.9);
        int windowheight = (int)(displayMetrics.heightPixels * 0.82);

        getWindow().setLayout(windowwidth , windowheight);

    }
}
