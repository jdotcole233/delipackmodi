package com.example.delipackmobi;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.example.delipackmobi.Model.CustomerHistoryAdapter;
import com.example.delipackmobi.Model.CustomerHistoryModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static com.example.delipackmobi.CustomerContract.CustomerContract.CUSTOMERTRANSACTIONHISTORY_URL;


public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter customerHistoryAdapter;
    private List<CustomerHistoryModel>  historylist;
    private CustomerContract customerHistoryContract;
    private String customerID;
    private List<CustomerHistoryModel> customerHistoryModel;


    public HistoryFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        recyclerView = getActivity().findViewById(R.id.history_list_display);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        customerHistoryContract = new CustomerContract(getActivity());
        customerHistoryModel = new ArrayList<>();

        for(Cookie cookie : customerHistoryContract.getPersistentCookieStore().getCookies()){
            if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    customerID = jsonObject.getString("customer_id");
                    Log.i("DeliPackMessage", "in Customer id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        getCustomerTransactionHistory(customerID);


//        for (int i = 0; i < 15; i++){
//            CustomerHistoryModel customerHistoryModel = new CustomerHistoryModel(
//                    "Company " + (i + 1), "Pick up at " + (i + 1),
//                    "Deliver to " + (i + 1), "Price GHC 13"
//            );
//            historylist.add(customerHistoryModel);
//        }


       // if (customerHistoryModel.size() != 0){

      //  }


    }



    public void getCustomerTransactionHistory(String customerID){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("customer_id", customerID);

        asyncHttpClient.post(CUSTOMERTRANSACTIONHISTORY_URL, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (response.length() != 0){
                    Log.i("DeliPackMessage", "in Objects " + response.toString());
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (response.length() != 0){
                   for (int i = 0; i < response.length(); i++){
                       try {
                           Log.i("DeliPackMessage",response.getJSONObject(i).toString());
                           JSONObject transactionObject = new JSONObject(response.getJSONObject(i).toString());
                           customerHistoryModel.add(new CustomerHistoryModel(transactionObject.getString("company_name"),transactionObject.getString("source"),
                                   transactionObject.getString("destination"), transactionObject.getString("delivery_status")
                                   , transactionObject.getString("payment_type"), transactionObject.getString("total_charge")
                                   , transactionObject.getString("first_name") + " " + transactionObject.getString("last_name")
                                   , transactionObject.getString("registered_number"), transactionObject.getString("created_at"), transactionObject.getString("transaction_number")));
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }

                    customerHistoryAdapter = new CustomerHistoryAdapter(customerHistoryModel, getContext());
                    recyclerView.setAdapter(customerHistoryAdapter);
                    Log.i("DeliPackMessage", "In Array " + response.toString());
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse.length() != 0){
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
