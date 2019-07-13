package com.delipackport.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_dash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent splashintent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(splashintent);
                finish();

            }
        }, 3000);
    }
}
