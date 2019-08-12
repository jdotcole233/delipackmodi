package com.delipackport.delipackmobi.CustomerContract;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.delipackport.delipackmobi.Model.CustomerHistoryModel;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.delipackport.delipackmobi.CustomerContract.CustomerContract.CUSTOMERTRANSACTIONHISTORY_URL;

public class ManageHistoryClass implements Runnable {

    private Context context;
    private String customerID;
    private List<CustomerHistoryModel> customerHistoryModel;

    public ManageHistoryClass(Context context, String customerID){
         this.context = context;
         this.customerID = customerID;
         customerHistoryModel = new ArrayList<>();
    }



    @Override
    public void run() {
        SyncHttpClient asyncHttpClient = new SyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("customer_id", customerID);

        asyncHttpClient.post(CUSTOMERTRANSACTIONHISTORY_URL, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (response != null){
                    Log.i("DeliPackMessage", "in Objects " + response.toString());
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (response != null){
                    for (int i = 0; i < response.length(); i++){
                        try {
//                            Log.i("DeliPackMessage",response.getJSONObject(i).toString());
                            JSONObject transactionObject = new JSONObject(response.getJSONObject(i).toString());
                            customerHistoryModel.add(new CustomerHistoryModel(transactionObject.getString("companies_id"), transactionObject.getString("company_name"),transactionObject.getString("source"),
                                    transactionObject.getString("destination"), transactionObject.getString("delivery_status")
                                    , transactionObject.getString("payment_type"), transactionObject.getString("total_charge")
                                    , transactionObject.getString("first_name") + " " + transactionObject.getString("last_name")
                                    , transactionObject.getString("registered_number"), transactionObject.getString("created_at"), transactionObject.getString("transaction_number"), transactionObject.getString("company_logo_path")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent historyDataIntent = new Intent("historyDataIntent");
                    historyDataIntent.putExtra("historydata", (Serializable) customerHistoryModel);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(historyDataIntent);
                    System.out.println("Broadcast message sent");
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null){
                    Log.i("DeliPackMessage", "Error in object " + errorResponse.toString());
                }
            }



            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("DeliPackMessage", "Error in Array " + errorResponse.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }


}
