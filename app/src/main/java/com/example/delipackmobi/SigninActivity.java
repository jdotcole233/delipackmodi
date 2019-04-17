package com.example.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;

public class SigninActivity extends AppCompatActivity {

    private Button back_btn, customerSignin;
    private EditText customerPhoneNumber, customerPassword;
    private AsyncHttpClient asyncHttpClient;
    private DeliPackAlert deliPackAlert;
    private static final String LOGIN_URL = "";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);
        back_btn = findViewById(R.id.backtohomebtn);
        customerSignin = findViewById(R.id.customerSigninButton);
        customerPhoneNumber = findViewById(R.id.customerPhoneNumber);
        customerPassword = findViewById(R.id.customerPassword);



        customerSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customerphone = customerPhoneNumber.getText().toString();
                String customerpassword = customerPassword.getText().toString();

                if (customerphone.isEmpty() || customerpassword.isEmpty()){
                    String message = "Ensure all fields are field before clicking the sign in button";
                    new DeliPackAlert(SigninActivity.this, "Mandatory login fields", message).showDeliPackAlert();
                    return;
                } else if (customerphone.length() < 10){
                    String message = "Invalid phone number detected. Make sure your phone number is 10 digits";
                    new DeliPackAlert(SigninActivity.this, "Wrong phone number", message).showDeliPackAlert();
                    return;
                }
            }
        });








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
