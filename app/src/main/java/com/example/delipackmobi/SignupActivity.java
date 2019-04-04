package com.example.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thrivecom.ringcaptcha.RingcaptchaApplication;
import com.thrivecom.ringcaptcha.RingcaptchaApplicationHandler;
import com.thrivecom.ringcaptcha.RingcaptchaVerification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class SignupActivity extends AppCompatActivity {


    private Button back_btn;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private  Button registerButton;
    private final static String POSTURL = "http://192.168.100.7:8000/registercutomer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);


        back_btn = findViewById(R.id.registerbackbtn);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email_user);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.registerationbutton);



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty() ||
                        confirmPassword.getText().toString().isEmpty()){
                    //output error message

                    Log.i("delipack", "All fields are mandatory");
                    return;

                } else {

//                    if(phoneNumber.getText().length() < 10 || phoneNumber.getText().length() > 10){
//                            Log.i("delipack", "phone number must be 10 ");
//                            return;
//                    }


                    if (password.getText().length() < 8){
                        //output error message
                        Log.i("delipack", "password must be more than 8 characters");
                        return;
                    } else {

                        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                            //output error message

                            Log.i("delipack", "password has be the same");
                            return;

                        } else {

                            //pass  validation

                            Log.i("delipack", "Everything is passed");


                            RingcaptchaApplication.onboard(getApplicationContext(), "5ygi4e1y1o8esa6i3uqe", "ni4ozypimupy6osi1y3a", new RingcaptchaApplicationHandler() {
                                @Override
                                public void onSuccess(RingcaptchaVerification ringObj) {
                                    Log.i("delipack", ringObj.getPhoneNumber().toString());
                                    AsyncHttpClient register_user = new AsyncHttpClient();
                                    RequestParams requestParams = new RequestParams();
                                    requestParams.add("first_name", firstName.getText().toString());
                                    requestParams.add("last_name", lastName.getText().toString());
                                    requestParams.add("email", email.getText().toString());
                                    requestParams.add("phone_number",  ringObj.getPhoneNumber().toString());
                                    requestParams.add("password", password.getText().toString());

                                    register_user.post(POSTURL, requestParams, new JsonHttpResponseHandler(){
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            // If the response is JSONObject instead of expected JSONArray
                                            Log.i("delipack", response.toString());
                                            Intent registerconfirmed = new Intent(SignupActivity.this, Homedashboard_user.class);
                                            startActivity(registerconfirmed);
                                            finish();
                                        }

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                                            super.onSuccess(statusCode, headers, responseString);
                                            Log.i("delipack", "in Stiring response " + responseString);
                                        }

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                                            super.onSuccess(statusCode, headers, response);
                                            Log.i("delipack", "in Array response" +  response);
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                                            super.onFailure(statusCode, headers, throwable, errorResponse);

                                            Log.i("delipack", errorResponse + "status code in object response");

                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                                            super.onFailure(statusCode, headers, responseString, throwable);

                                            Log.i("delipack", statusCode + "status in throwable");

                                        }

                                    });
                                }

                                @Override
                                public void onCancel() {
                                    Log.i("delipack", "canceled");
                                }
                            });


                        }
                    }
                }

            }
        });


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
