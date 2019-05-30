package com.example.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.delipackmobi.CustomerContract.CustomerContract.LOGIN_URL;

public class SigninActivity extends AppCompatActivity {

    private Button back_btn, customerSignin;
    private EditText customerPhoneNumber, customerPassword;
    private AsyncHttpClient asyncHttpClient;
    private DeliPackAlert deliPackAlert;
    private View viewloader;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private CustomerContract savedContract;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);
        back_btn = findViewById(R.id.backtohomebtn);
        customerSignin = findViewById(R.id.customerSigninButton);
        customerPhoneNumber = findViewById(R.id.customerPhoneNumber);
        customerPassword = findViewById(R.id.customerPassword);
        asyncHttpClient = new AsyncHttpClient();
        viewloader = getLayoutInflater().inflate(R.layout.delipack_event_loader, null);
        relativeLayout = viewloader.findViewById(R.id.loaderlayout);
        progressBar = findViewById(R.id.loginloader);
        progressBar.setVisibility(View.INVISIBLE);
        savedContract = new CustomerContract(this);


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
                } else {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("phone_number", customerphone);
                    requestParams.add("password", customerpassword);
//                    System.out.println("Loading");
//                    startActivity(new Intent(SigninActivity.this, DeliPackEventLoader.class));
                    deactivateWidgets();
                    asyncHttpClient.post(LOGIN_URL, requestParams, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
//                            viewloader.setVisibility(View.INVISIBLE);
//                            progressBar.setVisibility(View.INVISIBLE);
                            activateWidgets();
                            try {
                                String response_validate = response.getString("success_cue");
                                if (response_validate.contains("Success")){
//                                    new DeliPackAlert(SigninActivity.this, "Success", "Successful").showDeliPackAlert();
                                    savedContract.setBasicCookies("customerInfomation", response.toString(),1,"/");
                                    startActivity(new Intent(SigninActivity.this, Homedashboard_user.class));
                                    finish();

                                } else if (response_validate.contains("Deactivated")){
                                    String message = "Sorry!! This account has been deactivated. Contact us at 'https://delivpack.com' to resolve any issues";
                                    new DeliPackAlert(SigninActivity.this, "Deactivated",  message).showDeliPackAlert();

                                } else if (response_validate.contains("Failed")){
                                    String message = "Attempt to login failed, check credentials and try again";
                                    new DeliPackAlert(SigninActivity.this, "Login Failure", message).showDeliPackAlert();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            System.out.println(errorResponse);
//                            progressBar.setVisibility(View.INVISIBLE);
                            activateWidgets();

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            System.out.println(responseString);
//                            progressBar.setVisibility(View.INVISIBLE);
                            activateWidgets();
                        }

                    });



                }
            }
        });








        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_home_intent = new Intent(SigninActivity.this, MainActivity.class);
                startActivity(back_home_intent);
                finish();
            }
        });
    }


    private void deactivateWidgets(){
        progressBar.setVisibility(View.VISIBLE);
        customerSignin.setClickable(false);
        back_btn.setClickable(false);
        customerPhoneNumber.setEnabled(false);
        customerPassword.setEnabled(false);
    }

    private  void activateWidgets(){
        progressBar.setVisibility(View.INVISIBLE);
        customerSignin.setClickable(true);
        back_btn.setClickable(true);
        customerPhoneNumber.setEnabled(true);
        customerPassword.setEnabled(true);
    }

}
