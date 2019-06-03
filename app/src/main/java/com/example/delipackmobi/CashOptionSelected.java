package com.example.delipackmobi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static com.example.delipackmobi.CustomerContract.CustomerContract.UPDATEDTRANSACTION_URL;

public class CashOptionSelected extends AppCompatActivity {

    private Spinner cashpaymentoption;
    private Button cash_btn;
    private ImageButton cancelcashbtn;
    private String cashoptionselected;
    private String riderIDFound, customerID;
    private CustomerContract customerContract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_option_selected);
        cashpaymentoption = findViewById(R.id.cash_choice);
        cash_btn = findViewById(R.id.cash_payment_button);
        cancelcashbtn = findViewById(R.id.cancelcashoption);
        Intent getRiderID = getIntent();
        riderIDFound = getRiderID.getStringExtra("bikerID");
        customerContract = new CustomerContract(this);


        ArrayAdapter<CharSequence> cashspinner = ArrayAdapter.createFromResource(getApplicationContext(), R.array.cash_payment_option, android.R.layout.simple_spinner_item);
        cashspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cashpaymentoption.setAdapter(cashspinner);

        for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
            if(cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject customerIDinfo = new JSONObject(cookie.getValue());
                    customerID = customerIDinfo.getString("customer_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        cashpaymentoption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cashoptionselected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        cash_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cashoptionselected.equals("Pay at pick up")){
                    updatePaymentValueForRider(riderIDFound, CashOptionSelected.this, PackageInProgress.class);
                    DatabaseReference updateAccepted = FirebaseDatabase.getInstance().getReference()
                            .child("CustomerRiderRequest").child(customerID);
                    updateAccepted.child("rideraccepted").setValue("paid");

                    for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
                        if(cookie.getName().equals("paymentType")){
                            try {
                                JSONObject jsonObject = new JSONObject(cookie.getValue());
                                updateAccepted.child("deliverlatlong").child("paymentType").setValue(jsonObject.getString("paymentType"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Log.i("DeliPackMessage", "Something went wrong ");
                        }
                    }


                } else if (cashoptionselected.equals("Pay on delivery")){
                    updatePaymentValueForRider(riderIDFound, CashOptionSelected.this, PackageInProgress.class);
                    DatabaseReference updateAccepted = FirebaseDatabase.getInstance().getReference()
                            .child("CustomerRiderRequest").child(customerID).child("rideraccepted");
                    updateAccepted.setValue("paid");

                }else {
                    return;
                }
//                new DeliPackAlert(CashOptionSelected.this, "Selection", cashoptionselected).showDeliPackAlert();
            }
        });



        cancelcashbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void updatePaymentValueForRider(String riderID, final Context context, final Class switchto){
        DatabaseReference confirmpayment = FirebaseDatabase.getInstance().getReference()
                .child("RiderFoundForCustomer")
                .child(riderID)
                .child("paymentapproved");
        confirmpayment.setValue("true");

        AsyncHttpClient updateTrasaction = new AsyncHttpClient();
        RequestParams transactionParameters = new RequestParams();

        for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
            if(cookie.getName().equals("company_details")){
                try {
                    JSONObject transaction = new JSONObject(cookie.getValue());
                    transactionParameters.put("company_riderscompany_rider_id",transaction.getString("company_rider_id"));
                    transactionParameters.put("companiescompanies_id",transaction.getString("companies_id"));
                    transactionParameters.put("motor_bikesbike_id",transaction.getString("bike_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject transactionCustomer = new JSONObject(cookie.getValue());
                    transactionParameters.put("customerscustomer_id",transactionCustomer.getString("customer_id"));
                    transactionParameters.put("delivery_status","ACTIVE");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (cookie.getName().equals("searchdata")){
                try {
                    JSONObject transactionSearch = new JSONObject(cookie.getValue());
                    transactionParameters.put("destination", transactionSearch.getString("delivery"));
                    transactionParameters.put("source",transactionSearch.getString("pickup"));
                    transactionParameters.put("delivery_charge",  transactionSearch.getDouble("delivery_charge"));
                    transactionParameters.put("commission_charge", transactionSearch.getDouble("commission_charge"));
//                    transactionParameters.put("ETA","22:33");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (cookie.getName().equals("paymentType")){
                try {
                    JSONObject transactionSearch = new JSONObject(cookie.getValue());
                    transactionParameters.put("payment_type", transactionSearch.getString("paymentType"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        updateTrasaction.post(UPDATEDTRANSACTION_URL, transactionParameters, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String transactionResponse = response.toString();
                if (!transactionResponse.isEmpty()){
                    try {
                        if (response.getString("success").equals("Done")){
                            Intent changeActivitiies = new Intent(context, switchto);
                            startActivity(changeActivitiies);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                System.out.println(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println(responseString);
            }
        });
    }
}
