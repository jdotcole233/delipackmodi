package com.delipackport.delipackmobi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delipackport.delipackmobi.CustomerContract.CustomerContract;
import com.delipackport.delipackmobi.CustomerContract.UpdateHistory;
import com.delipackport.delipackmobi.Model.CustomerHistoryAdapter;
import com.delipackport.delipackmobi.Model.CustomerHistoryModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;


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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        if (getActivity() != null ){
            recyclerView = getActivity().findViewById(R.id.history_list_display);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(linearLayoutManager);
        }

        customerHistoryContract = new CustomerContract(getActivity());

        if (customerHistoryModel != null){
            if (customerHistoryModel.size() != 0){
                customerHistoryAdapter = new CustomerHistoryAdapter(customerHistoryModel, getContext());
                recyclerView.setAdapter(customerHistoryAdapter);
            } else {
                new DeliPackAlert(getActivity(), "Empty History", "No history recorded at this moment").showDeliPackAlert();
                System.out.println("Nothing showing in history");
            }
        } else {
            new DeliPackAlert(getActivity(), "Empty History", "No history recorded at this moment").showDeliPackAlert();
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
