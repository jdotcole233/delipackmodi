package com.delipackport.delipackmobi;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.delipackport.delipackmobi.CustomerContract.CustomerContract;
import com.delipackport.delipackmobi.Model.CustomerLocalPushNotification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static com.delipackport.delipackmobi.CustomerContract.CustomerContract.CUSTOMERERRANDSESSIONCANCEL_URL;

public class TripInSessionCancel extends AppCompatActivity {

    private Button cancelButton, donotcancelButton;
    private AsyncHttpClient asyncHttpClient;
    private String transaction_id, customer_id, rider_id;
    private CustomerContract customerContract;
    private ProgressBar cancelSession;
    private CustomerLocalPushNotification customerLocalPushNotification;
    private DatabaseReference customerRequest, riderfoundforcustomer;
    public static Activity packageinprogresstripsessioncancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_in_session_cancel);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout((int)(displayMetrics.widthPixels * 0.9), (int)(displayMetrics.heightPixels * 0.4));

        cancelButton = findViewById(R.id.yesbtn);
        donotcancelButton = findViewById(R.id.nobtn);
        asyncHttpClient = new AsyncHttpClient();
        customerContract = new CustomerContract(this);
        cancelSession = findViewById(R.id.cancelsessionloader);
        customerLocalPushNotification = new CustomerLocalPushNotification(this);

        for(Cookie cookie : customerContract.getPersistentCookieStore().getCookies()){
            if (cookie.getName().equals("transaction_id")){
                try{
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    transaction_id = jsonObject.getString("transaction_id");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            } else if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    customer_id = jsonObject.getString("customer_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (cookie.getName().equals("company_details")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    rider_id = jsonObject.getString("company_rider_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }




        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                cancelSession.setVisibility(View.VISIBLE);
                requestParams.put("transactions_id", transaction_id);

                asyncHttpClient.post(CUSTOMERERRANDSESSIONCANCEL_URL, requestParams, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        customerLocalPushNotification.publishNotification("Errand Cancelled", null, "Your errand has been cancelled successfully");
                        cancelSession.setVisibility(View.INVISIBLE);
                        if(customer_id != null && rider_id != null){
                            if (!customer_id.isEmpty() && !rider_id.isEmpty()){
                                customerRequest = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest");
                                customerRequest.child(customer_id).removeValue();
                                riderfoundforcustomer = FirebaseDatabase.getInstance().getReference().child("RiderFoundForCustomer");
                                riderfoundforcustomer.child(rider_id).child("assigned").setValue("not assigned");
                                cancelButton.setFocusable(true);
                                donotcancelButton.setFocusable(true);
                                packageinprogresstripsessioncancel.finish();
                                finish();

                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        System.out.println("cancelling errand session json object error" + errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        System.out.println("canceling errand session failure throwable"  + responseString);
                    }
                });
            }
        });


        donotcancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
