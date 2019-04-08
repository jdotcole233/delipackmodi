package com.example.delipackmobi;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.delipackmobi.Model.CustomerHistoryAdapter;
import com.example.delipackmobi.Model.CustomerHistoryModel;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter customerHistoryAdapter;
    private List<CustomerHistoryModel>  historylist;


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
}
