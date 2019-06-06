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
    private CustomerHistoryModel customerHistoryModel;


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

        historylist = new ArrayList<>();

        recyclerView = getActivity().findViewById(R.id.history_list_display);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        customerHistoryModel = new CustomerHistoryModel();

        for(Cookie cookie : customerHistoryContract.getPersistentCookieStore().getCookies()){
            if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    customerID = jsonObject.getString("customer_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        getCustomerTransactionHistory(customerID);


        for (int i = 0; i < 15; i++){
            CustomerHistoryModel customerHistoryModel = new CustomerHistoryModel(
                    "Company " + (i + 1), "Pick up at " + (i + 1),
                    "Deliver to " + (i + 1), "Price GHC 13"
            );
            historylist.add(customerHistoryModel);
        }



        customerHistoryAdapter = new CustomerHistoryAdapter(historylist, getContext());
        recyclerView.setAdapter(customerHistoryAdapter);

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
                    Log.i("DeliPackMessage", response.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
