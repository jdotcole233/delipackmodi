package com.example.delipackmobi;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
    AutocompleteSupportFragment autocompleteFragment;
    AutocompleteSupportFragment autocompleteFragment1;
    private Button rider_search_btn;
    private PickUpDeliveryModel pickUpDeliveryModel;
    private CardView resultcard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homedashboard_user);

        if(!Places.isInitialized()){
            Places.initialize(this, "AIzaSyDKBYaQubmWi0ockGK4hmMAPG_RcKcZ7mk");
        }

        searchRiderFragment = new SearchRiderFragment();
        profileFragment = new ProfileFragment();
        historyFragment = new HistoryFragment();
        rider_search_btn = findViewById(R.id.rider_search);
        pickUpDeliveryModel = new PickUpDeliveryModel();
        resultcard = findViewById(R.id.search_result_cardview);




        rider_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pickUpDeliveryModel != null){

                    if (pickUpDeliveryModel.getFromInformation() == null){
                            Toast.makeText(Homedashboard_user.this, "You need to fill a Pick up location", Toast.LENGTH_SHORT).show();
                            return;
                    } else if (pickUpDeliveryModel.getDeliveryInformation() == null){
                            Toast.makeText(Homedashboard_user.this, "Delivery information cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                    } else {
                        Toast.makeText(Homedashboard_user.this, "Everything is good", Toast.LENGTH_SHORT).show();
                        resultcard.setVisibility(View.VISIBLE);
                    }


                    for(String a: pickUpDeliveryModel.getDeliveryInformation().values())
                         Log.i("delipack",  a.toString());

                    for(String a: pickUpDeliveryModel.getFromInformation().values())
                        Log.i("delipack",  a.toString());

                } else {
                    Toast.makeText(Homedashboard_user.this, "Fill out both forms to find rider", Toast.LENGTH_SHORT).show();

                    return;
                }

            }
        });














        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.OPENING_HOURS));
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                HashMap<String, String> place_from = new HashMap<>();
                if (place != null){
                    place_from.put(place.getId().toString(),place.getName().toString());
                    pickUpDeliveryModel.setFromInformation(place_from);
                    Log.i("g", "Place: " + place.getName() + ", " + place.getId());

                } else{
                    place_from = null;
                }
                // TODO: Get info about the selected place.
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("g", "An error occurred: " + status);
            }
        });


// Initialize the AutocompleteSupportFragment.
        autocompleteFragment1 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment1);

// Specify the types of place data to return.
        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                HashMap<String, String> place_delivery = new HashMap<>();
                if (place != null){
                    place_delivery.put(place.getId().toString(),place.getName().toString());
                    pickUpDeliveryModel.setDeliveryInformation(place_delivery);
                    Log.i("g", "Place: " + place.getName() + ", " + place.getId());

                } else{
                    place_delivery = null;
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("g", "An error occurred: " + status);
            }
        });



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
            return false;
        }
    };


    public void switchFragments(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, fragment);
        fragmentTransaction.commit();
    }

}
