package com.delipackport.delipackmobi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisteredWelcomeWalkThrough extends AppCompatActivity {

    private Button welcomenextbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_welcome_walk_through);
        welcomenextbtn = findViewById(R.id.welcomebtn);

        welcomenextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextWelcomeIntent = new Intent(RegisteredWelcomeWalkThrough.this, RegisterWelcomeSecondWalkThrough.class);
                startActivity(nextWelcomeIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
