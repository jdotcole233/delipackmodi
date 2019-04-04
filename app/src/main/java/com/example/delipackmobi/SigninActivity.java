package com.example.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SigninActivity extends AppCompatActivity {

    private Button back_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);
        back_btn = findViewById(R.id.backtohomebtn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_home_intent = new Intent(SigninActivity.this, Homedashboard_user.class);
                startActivity(back_home_intent);
                finish();
            }
        });
    }
}
