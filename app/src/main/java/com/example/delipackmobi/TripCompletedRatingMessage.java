package com.example.delipackmobi;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.delipackmobi.CustomerContract.CustomerContract;

import cz.msebera.android.httpclient.cookie.Cookie;

public class TripCompletedRatingMessage extends AppCompatActivity {

    private Button donebutton;
    public static Activity tripCompletedSearchActivity;
    private CustomerContract contract;
    private Cookie deleteCookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_completed_rating_message);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout((int)(displayMetrics.widthPixels * 0.9), (int)(displayMetrics.heightPixels * 0.5));

        donebutton = findViewById(R.id.ratingdone);
        contract = new CustomerContract(this);

        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardView cardView = tripCompletedSearchActivity.findViewById(R.id.searchridercardview);
                CardView welcomecardview = tripCompletedSearchActivity.findViewById(R.id.cardsearchwelcome);
                ImageButton imageButton = tripCompletedSearchActivity.findViewById(R.id.showmoretripinprogress);
                imageButton.setVisibility(View.INVISIBLE);
                welcomecardview.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.VISIBLE);

                for (Cookie cookie : contract.getPersistentCookieStore().getCookies()){
                       if (cookie.getName().equals("searchdata")){
                           contract.getPersistentCookieStore().getCookies().remove(cookie);
                           contract.deleteCookie(cookie);
                       } else if (cookie.getName().equals("company_details")){
                           contract.deleteCookie(cookie);
                       }
                }
                finish();
            }
        });
    }
}
