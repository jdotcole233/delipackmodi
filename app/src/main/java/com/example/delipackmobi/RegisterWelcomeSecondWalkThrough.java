package com.example.delipackmobi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterWelcomeSecondWalkThrough extends AppCompatActivity {
    private Button welcomesecondbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_welcome_second_walk_through);

        welcomesecondbtn = findViewById(R.id.welcometrackbtn);

        welcomesecondbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trackIntent = new Intent(RegisterWelcomeSecondWalkThrough.this, RegisterWelcomeFinalWalkthrough.class);
                startActivity(trackIntent);
                finish();
            }
        });
    }
}
