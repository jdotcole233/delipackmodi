package com.delipackport.delipackmobi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterWelcomeFinalWalkthrough extends AppCompatActivity {

    private Button getstartedbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_welcome_final_walkthrough);
        getstartedbtn = findViewById(R.id.welcomestartedbtn);

        getstartedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent finalIntent = new Intent(RegisterWelcomeFinalWalkthrough.this, Homedashboard_user.class);
                startActivity(finalIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
