package com.example.delipackmobi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.example.delipackmobi.CustomerContract.HistoryServiceClass;
import com.example.delipackmobi.CustomerContract.ManageNetworkConnectionClass;
import com.example.delipackmobi.CustomerContract.NetworkAllowanceCheck;
import com.example.delipackmobi.CustomerContract.UpdateHistory;
import com.example.delipackmobi.Model.CustomerHistoryAdapter;
import com.example.delipackmobi.Model.CustomerHistoryModel;
import com.example.delipackmobi.Model.PickUpDeliveryModel;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;


public class Homedashboard_user extends AppCompatActivity {


    SearchRiderFragment searchRiderFragment;
    ProfileFragment profileFragment;
    HistoryFragment historyFragment;
    private ImageButton showmorebtn;
    private BottomNavigationView  navigation;
    Fragment mContent;
    public static Activity homeactivity;
    private Intent historyIntent, networkIntentService;
    private CustomerContract customerContract;
    private String customerID;
    private List<CustomerHistoryModel> customerHistoryModel;
    UpdateHistory updateHistory, profileCount;
    ManageNetworkConnectionClass manageNetworkConnectionClass;
    NetworkAllowanceCheck networkAllowanceCheck;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homedashboard_user);

        manageNetworkConnectionClass = new ManageNetworkConnectionClass(this);
        if(!Places.isInitialized()){
            Places.initialize(this, "AIzaSyDKBYaQubmWi0ockGK4hmMAPG_RcKcZ7mk");
        }



        PackageInProgress.Homedardboardactivity = this;

        searchRiderFragment = new SearchRiderFragment();
        profileFragment = new ProfileFragment();
        historyFragment = new HistoryFragment();
        networkAllowanceCheck = new NetworkAllowanceCheck(this);
        showmorebtn = findViewById(R.id.showmoretripinprogress);
        showmorebtn.setVisibility(View.INVISIBLE);
        homeactivity = this;
        customerContract = new CustomerContract(this);

        for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
            if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    customerID = jsonObject.getString("customer_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        if (manageNetworkConnectionClass.checkConnectivity()){
           aaa();
//        getSupportFragmentManager().beginTransaction().replace()
        } else {
            Intent nointernet = new Intent(this, NetworkConnectionView.class);
            startActivity(nointernet);
        }

    }


    public void aaa(){


        updateHistory = (UpdateHistory) historyFragment;
        profileCount = profileFragment;

        historyIntent = new Intent(this, HistoryServiceClass.class);
        historyIntent.putExtra("customerID", customerID);
        startService(historyIntent);



        LocalBroadcastManager.getInstance(this).registerReceiver(historyBroadCast, new IntentFilter("historyDataIntent"));
        LocalBroadcastManager.getInstance(this).registerReceiver(networkBroadCast, new IntentFilter("networkConnectionCheck"));



        showmorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showinprogressactivity = new Intent(Homedashboard_user.this, PackageInProgress.class);
                startActivity(showinprogressactivity);
                showmorebtn.setVisibility(View.INVISIBLE);

            }
        });

        Log.i("DelicPackMessage", "Parent create called");

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchFragments(searchRiderFragment);
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_history:
                    switchFragments(historyFragment);
                    return true;
                case R.id.navigation_search:
                    switchFragments(searchRiderFragment);
                    return true;
                case R.id.navigation_profile:
                    switchFragments(profileFragment);
                    return true;
                case R.id.logout_user:
                    Toast.makeText(Homedashboard_user.this, "You logged out", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return true;
        }
    };


    public void switchFragments(Fragment fragment){
        if (!manageNetworkConnectionClass.checkConnectivity()){
            Intent networkConnection = new Intent(Homedashboard_user.this, NetworkConnectionView.class);
            startActivity(networkConnection);
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, fragment);
        fragmentTransaction.commit();
        System.out.println("In fragement " + fragment);
    }




    private BroadcastReceiver historyBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Broadcast recieved in HomedashBoard");
            customerHistoryModel = (List<CustomerHistoryModel>) intent.getSerializableExtra("historydata");
            System.out.println("Broadcast history " + customerHistoryModel.size());
            updateHistory.updateHistoryUI(customerHistoryModel);
            profileCount.updateHistoryUI(customerHistoryModel);
        }
    };


    private  BroadcastReceiver networkBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Network broadcast received");
            String networkstatus = intent.getStringExtra("connectionstatus");

            if (networkstatus.equals("connected")){
                Toast.makeText(Homedashboard_user.this, "Internet connection available", Toast.LENGTH_LONG).show();
            } else if (networkstatus.equals("not connected")) {
                Toast.makeText(Homedashboard_user.this, "Internet connection not available", Toast.LENGTH_LONG).show();
            }
        }
    };



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        getSupportFragmentManager().putFragment(outState, SearchRiderFragment.class.getName(), searchRiderFragment);
//        getSupportFragmentManager().putFragment(outState, SearchRiderFragment.class.getName(), mContent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (manageNetworkConnectionClass.checkConnectivity()){
            aaa();
        }else {
            Intent nointernet = new Intent(this, NetworkConnectionView.class);
            startActivity(nointernet);
            networkAllowanceCheck.enable(this);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("on pause called in main");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manageNetworkConnectionClass.checkConnectivity()){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(historyBroadCast);
            stopService(historyIntent);
        }

        System.out.println("Main on destroy");
    }
}
