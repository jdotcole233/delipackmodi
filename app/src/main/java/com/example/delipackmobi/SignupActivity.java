package com.example.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SignupActivity extends AppCompatActivity {


    private Button back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);


        back_btn = findViewById(R.id.registerbackbtn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_home_intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(back_home_intent);
                finish();
            }
        });
    }
}
