package com.example.delipackmobi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.example.delipackmobi.Model.CustomerLocalPushNotification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static com.example.delipackmobi.CustomerContract.CustomerContract.RATING_URL;

public class RateCompanyRider extends AppCompatActivity {

    private Button rateNextButton;
    private ImageView closerating;
    private RatingBar riderRated;
    private float ratevalue;
    private CustomerContract customerContract;
    private String customerID, companyID, companyRiderID, transaction_id, deliveryMessage, companyName, pickUpLocation, deliveryLocation;
    private String [] deliveryMessages;
    private AsyncHttpClient asyncHttpClient;
    private RequestParams requestParams;
    public static Activity searchriderfragment;
    private CustomerLocalPushNotification customerLocalPushNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_company_rider);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout((int)(displayMetrics.widthPixels * 0.9), (int)(displayMetrics.heightPixels * 0.3));
        asyncHttpClient = new AsyncHttpClient();
        customerContract = new CustomerContract(this);
        customerLocalPushNotification = new CustomerLocalPushNotification(this);


        rateNextButton = findViewById(R.id.ratingnextButton);
        riderRated = findViewById(R.id.raterider);
        closerating = findViewById(R.id.cancelrating);
        ratevalue = 0.0f;

        System.out.println("Customer cookies " + customerContract.getPersistentCookieStore().getCookies());

        Log.i("DeliPackMessage", customerContract.getPersistentCookieStore().toString());

        for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
            if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject ratingcustomer = new JSONObject(cookie.getValue());
                    customerID = ratingcustomer.getString("customer_id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (cookie.getName().equals("company_details")){
                try {
                    JSONObject companyrating = new JSONObject(cookie.getValue());
                    companyID = companyrating.getString("companies_id");
                    companyRiderID = companyrating.getString("company_rider_id");
                    System.out.println("company id " + companyID + " company rider id " + companyRiderID);
                    companyName = companyrating.getString("company_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(cookie.getName().equals("searchdata")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    pickUpLocation = jsonObject.getString("pickup");
                    deliveryLocation = jsonObject.getString("delivery");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (cookie.getName().equals("transaction_id")){
                try{
                    JSONObject transactionJSON = new JSONObject(cookie.getValue());
                    transaction_id = transactionJSON.getString("transaction_id");
                    Log.i("DeliPackT", transaction_id);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }

        deliveryMessages = new String[5];
        deliveryMessages[0] = companyName + " has completed your errand";
        deliveryMessages[1] = "from: " + pickUpLocation;
        deliveryMessages[2] = "to: " + deliveryLocation;
        deliveryMessages[3] = "Kindly rate us to better improve our services ";
        deliveryMessages[4] = "Thank you!";

        deliveryMessage = companyName + " has completed your errand";

        customerLocalPushNotification.publishNotification("Delivered", deliveryMessages, deliveryMessage);


        riderRated.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratevalue = ratingBar.getRating();
                Log.i("DeliPackMessage", ratingBar.getRating() + " " + rating + " from user " + fromUser);
            }
        });



        closerating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Cookie cookie : customerContract.getPersistentCookieStore().getCookies()){
                    if (cookie.getName().equals("company_details")){

                        CardView cardView = searchriderfragment.findViewById(R.id.searchridercardview);
                        CardView welcomecardview = searchriderfragment.findViewById(R.id.cardsearchwelcome);
                        ImageButton imageButton = Homedashboard_user.homeactivity.findViewById(R.id.showmoretripinprogress);

                        imageButton.setVisibility(View.INVISIBLE);
                        welcomecardview.setVisibility(View.VISIBLE);
                        cardView.setVisibility(View.VISIBLE);

                        customerContract.deleteCookie(cookie);
                        finish();
                    } else if (cookie.getName().equals("search_data")){
                        customerContract.deleteCookie(cookie);
                    }
                }
            }
        });


        rateNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestParams = new RequestParams();
                requestParams.put("rate_value", Float.toString(ratevalue));
                requestParams.put("company_riderscompany_rider_id", companyRiderID);
                requestParams.put("customerscustomer_id", customerID);
                requestParams.put("company_id", companyID);
                requestParams.put("transactions_id", transaction_id);

                asyncHttpClient.post(RATING_URL, requestParams, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);


                        if (response.length() != 0){
                            Intent thankyouIntent = new Intent(RateCompanyRider.this, TripCompletedRatingMessage.class);
                            startActivity(thankyouIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.i("DeliPackMessage", errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.i("DeliPackMessage", responseString);
                    }
                });
            }
        });
    }


    public void clearCustomerRequest(String customerID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest")
                .child(customerID);
        databaseReference.removeValue();
    }

}
