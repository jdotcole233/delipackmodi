package com.delipackport.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.delipackport.delipackmobi.CustomerContract.CustomerContract;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thrivecom.ringcaptcha.RingcaptchaApplication;
import com.thrivecom.ringcaptcha.RingcaptchaApplicationHandler;
import com.thrivecom.ringcaptcha.RingcaptchaVerification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SignupActivity extends AppCompatActivity {


    private Button back_btn;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private  Button registerButton;
    private final static String POSTURL = "http://delipackport.com/api/registercutomer";
    private DeliPackAlert deliPackAlert;
    private CustomerContract customerContract;

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
        customerContract = new CustomerContract(this);



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty() ||
                        confirmPassword.getText().toString().isEmpty()){
                    new DeliPackAlert(SignupActivity.this, "Mandatory fields", "All fields must be field before you can be registered").showDeliPackAlert();
                    return;

                } else {

//                    if(phoneNumber.getText().length() < 10 || phoneNumber.getText().length() > 10){
//                            Log.i("delipack", "phone number must be 10 ");
//                            return;
//                    }


                    if (password.getText().length() < 8){
                        //output error message
                        String message = "password must be more than 8 characters";
                        new DeliPackAlert(SignupActivity.this, "Password count", message).showDeliPackAlert();
                        return;
                    } else {

                        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                            //output error message
                            String message = "It appears your choosen passwords do not match, check and try again!!! ";
                            new DeliPackAlert(SignupActivity.this, "Password mismatch", message).showDeliPackAlert();
                            return;

                        } else {

                            //pass  validation

//                            Log.i("delipack", "Everything is passed");


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

                                            if (response != null){
                                                try {
                                                    String responded = response.getString("success_cue");
                                                    if (responded.equals("Success")){
                                                        Log.i("delipack", response.toString());
                                                        customerContract.setBasicCookies("customerInfomation", response.toString(),1,"/");
                                                        finish();
                                                        Intent registerconfirmed = new Intent(SignupActivity.this, RegisteredWelcomeWalkThrough.class);
                                                        startActivity(registerconfirmed);
                                                    } else if (responded.equals("Failed")){
                                                        new DeliPackAlert(SignupActivity.this, "Duplicate Phone Number", "Please use a different valid phone number or contact support@delivpack.com for help").showDeliPackAlert();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

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
                                            new DeliPackAlert(SignupActivity.this, "Network failure", "Something went wrong with the internet, please try again").showDeliPackAlert();
                                            Log.i("delipack", errorResponse + "status code in object response");

                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                                            super.onFailure(statusCode, headers, responseString, throwable);
                                            new DeliPackAlert(SignupActivity.this, "Network failure", "Something went wrong with the internet, please try again").showDeliPackAlert();
                                            Log.i("delipack", statusCode + " status in throwable" + responseString);

                                        }

                                    });
                                }



                                @Override
                                public void onCancel() {
                                    new DeliPackAlert(SignupActivity.this, "Verification canceled", "You cancelled verfication process").showDeliPackAlert();

//                                    Log.i("delipack", "canceled");
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
