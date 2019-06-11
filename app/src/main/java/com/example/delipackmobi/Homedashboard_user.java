package com.example.delipackmobi;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Homedashboard_user extends AppCompatActivity {


    SearchRiderFragment searchRiderFragment;
    ProfileFragment profileFragment;
    HistoryFragment historyFragment;
    private ImageButton showmorebtn;
    private BottomNavigationView  navigation;
    Fragment mContent;
    public static Activity homeactivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homedashboard_user);


        if(!Places.isInitialized()){
            Places.initialize(this, "AIzaSyDKBYaQubmWi0ockGK4hmMAPG_RcKcZ7mk");
        }

        PackageInProgress.Homedardboardactivity = this;

        searchRiderFragment = new SearchRiderFragment();
        profileFragment = new ProfileFragment();
        historyFragment = new HistoryFragment();
        showmorebtn = findViewById(R.id.showmoretripinprogress);
        showmorebtn.setVisibility(View.INVISIBLE);
        homeactivity = this;

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
//        getSupportFragmentManager().beginTransaction().replace()

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
//        navigation.setItemTextColor(new ColorStateList(new int[] []{
//                new int[] { android.R.attr.state_enabled}, // enabled
//                new int[] {android.R.attr.state_enabled}, // disabled
//                new int[] {android.R.attr.state_checked}, // unchecked
//                new int[] { android.R.attr.state_pressed}
//        }, new int[]{
//                Color.parseColor("#ffad33"),
//                Color.GREEN,
//                Color.BLUE
//        }));
//
//        navigation.setItemIconTintList(new ColorStateList(new int[] []{
//                new int[] { android.R.attr.state_enabled}, // enabled
//                new int[] {android.R.attr.state_enabled}, // disabled
//                new int[] {android.R.attr.state_checked}, // unchecked
//                new int[] { android.R.attr.state_pressed}
//        }, new int[]{
//                Color.parseColor("#ffad33"),
//                Color.GREEN,
//                Color.BLUE
//        }));
        navigation.setActivated(true);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.remove(R.id.fragmentFrame);
//        fragmentTransaction.remove(searchRiderFragment);
//        fragmentTransaction.detach(searchRiderFragment);
        fragmentTransaction.replace(R.id.fragmentFrame, fragment);
        fragmentTransaction.commit();
        System.out.println("In fragement " + fragment);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        getSupportFragmentManager().putFragment(outState, SearchRiderFragment.class.getName(), searchRiderFragment);
//        getSupportFragmentManager().putFragment(outState, SearchRiderFragment.class.getName(), mContent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Main on destroy");
    }
}
