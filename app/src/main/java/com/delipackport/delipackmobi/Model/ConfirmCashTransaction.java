package com.delipackport.delipackmobi.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.delipackport.delipackmobi.CashOptionSelected;
import com.delipackport.delipackmobi.CustomerContract.CustomerContract;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static com.delipackport.delipackmobi.CustomerContract.CustomerContract.UPDATEDTRANSACTION_URL;

public class ConfirmCashTransaction extends AsyncTask <String, Void, Boolean> {

    private Context context;
    private Intent transactionloader;
    private Activity transactionActivity;
    private CustomerContract customerContract;
    private Boolean isDone = false;
    private AsyncHttpClient updateTrasaction;
    private RequestParams transactionParameters;
    private DatabaseReference updateAccepted, confirmpayment;



    public ConfirmCashTransaction(Activity activity){
        transactionActivity = activity;
        this.context = activity.getApplicationContext();
        customerContract = new CustomerContract(this.context);
        updateTrasaction = new AsyncHttpClient();
        transactionParameters = new RequestParams();
    }


    @Override
    protected Boolean doInBackground(final String... strings) {
        confirmpayment = FirebaseDatabase.getInstance().getReference()
                .child("RiderFoundForCustomer")
                .child(strings[0])
                .child("paymentapproved");
        confirmpayment.setValue("true");

        updateAccepted = FirebaseDatabase.getInstance().getReference()
                .child("CustomerRiderRequest").child(strings[1]);
        updateAccepted.child("rideraccepted").setValue("paid");
        updateAccepted.child("deliverlatlong").child("paymentType").setValue(strings[2]);
        updateAccepted.child("acceptbuttonvisibility").setValue("false");

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (isCancelled()){
                    return;
                }


                for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
                    if(cookie.getName().equals("company_details")){
                        try {
                            JSONObject transaction = new JSONObject(cookie.getValue());
                            transactionParameters.put("company_riderscompany_rider_id",transaction.getString("company_rider_id"));
                            transactionParameters.put("companiescompanies_id",transaction.getString("companies_id"));
                            transactionParameters.put("motor_bikesbike_id",transaction.getString("bike_id"));
                            transactionParameters.put("company_riderscompany_rider_id", strings[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (cookie.getName().equals("customerInfomation")){
                        try {
                            JSONObject transactionCustomer = new JSONObject(cookie.getValue());
                            transactionParameters.put("customerscustomer_id",transactionCustomer.getString("customer_id"));
                            transactionParameters.put("delivery_status","DELIVERED");


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
                            transactionParameters.put("payment_type", strings[2]);
//                    transactionParameters.put("ETA","22:33");
                            Double expectedPayment = Double.parseDouble(transactionSearch.getString("delivery_charge")) + Double.parseDouble(transactionSearch.getString("commission_charge"));
                            updateAccepted.child("deliverlatlong")
                                    .child("pickuplocationname").setValue(transactionSearch.getString("pickup"));
                            updateAccepted.child("deliverlatlong")
                                    .child("totalCharge").setValue(new DecimalFormat("#.#").format(expectedPayment));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }


                updateTrasaction.post(UPDATEDTRANSACTION_URL, transactionParameters, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        if (response != null){
                            try {
                                if (response.getString("success").equals("Done")){
                                    customerContract.setBasicCookies("transaction_id",response.toString(),6,"/");
                                    isDone = true;
                                    Log.i("DeliPackMessage", "Value of is done " + isDone );

//                            Intent changeActivitiies = new Intent(context, switchto);
//                            startActivity(changeActivitiies);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                isDone = false;
                                Log.i("DeliPackMessage", "Value of is done in catch" + isDone );

                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        System.out.println(errorResponse);
                        isDone = false;
                        Log.i("DeliPackMessage", "Value of is done onfailure throwable " + isDone );

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        System.out.println(responseString);
                        isDone = false;
                        Log.i("DeliPackMessage", "Value of is done on failure responseString" + isDone );

                    }

                    @Override
                    public boolean getUseSynchronousMode() {
                        return super.getUseSynchronousMode();
                    }
                });

            }
        };

        handler.post(runnable);

        return isDone;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        transactionloader = new Intent(context, TransactionLoader.class);
//        context.startActivity(transactionloader);
        System.out.println("On Pre Execute called..");

    }




    @Override
    protected void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        System.out.println("On Post Execute called..");
        Log.i("DeliPackMessage", "Value of is done bool " + bool );
        CashOptionSelected.cashoptionselectedActivity.finish();
//        if(isDone){
                transactionActivity.finish();
                System.out.println("On Post Execute called if statement..");

//        }

    }
}
