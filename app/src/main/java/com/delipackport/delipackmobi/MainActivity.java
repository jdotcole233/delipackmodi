package com.delipackport.delipackmobi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.delipackport.delipackmobi.CustomerContract.CustomerContract;

import cz.msebera.android.httpclient.cookie.Cookie;

public class MainActivity extends AppCompatActivity {

    private Button signin_btn;
    private Button signup_btn;
    private CustomerContract savedCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signin_btn = findViewById(R.id.signinbtn);
        signup_btn = findViewById(R.id.signupbtn);

        savedCustomer = new CustomerContract(this);

        for (Cookie cookie: savedCustomer.getPersistentCookieStore().getCookies()){
            if (cookie.getName().equals("customerInfomation")){
                if (!cookie.getValue().isEmpty()){
                    startActivity(new Intent(MainActivity.this, Homedashboard_user.class));
                    finish();
                } else {
                    return;
                }
            }

        }


        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin_intent = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(signin_intent);
                finish();
            }
        });


        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(signup_intent);
                finish();
            }
        });
    }
}
