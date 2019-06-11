package com.example.delipackmobi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.example.delipackmobi.CustomerContract.UpdateHistory;
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


public class HistoryFragment extends Fragment implements UpdateHistory {

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

        if (customerHistoryModel.size() != 0){
            customerHistoryAdapter = new CustomerHistoryAdapter(customerHistoryModel, getContext());
            recyclerView.setAdapter(customerHistoryAdapter);
        } else {
            System.out.println("Nothing showing in history");
        }


//        Intent hist = getActivity().getIntent();
//        customerHistoryModel = (List<CustomerHistoryModel>) hist.getSerializableExtra("hist");
//        System.out.println("History fragment " + customerHistoryModel.size());


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

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void updateHistoryUI(List<CustomerHistoryModel> customerHistoryModels) {
            if (customerHistoryModels.size() != 0){
                customerHistoryModel = new ArrayList<>();
                customerHistoryModel.addAll(customerHistoryModels);
            }
    }
}
