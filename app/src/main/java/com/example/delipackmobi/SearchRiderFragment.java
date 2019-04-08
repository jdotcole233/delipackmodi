package com.example.delipackmobi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.delipackmobi.Model.PickUpDeliveryModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class SearchRiderFragment extends Fragment {



    AutocompleteSupportFragment autocompleteFragment;
    AutocompleteSupportFragment autocompleteFragment1;
    private Button rider_search_btn;
    private PickUpDeliveryModel pickUpDeliveryModel;
    private CardView resultcard;

    public SearchRiderFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_rider, container, false);




    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        rider_search_btn = getActivity().findViewById(R.id.rider_search);
        pickUpDeliveryModel = new PickUpDeliveryModel();
        resultcard = getActivity().findViewById(R.id.search_result_cardview);




        rider_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pickUpDeliveryModel != null){

                    if (pickUpDeliveryModel.getFromInformation() == null){
                        Toast.makeText(getActivity(), "You need to fill a Pick up location", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (pickUpDeliveryModel.getDeliveryInformation() == null){
                        Toast.makeText(getContext(), "Delivery information cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(getActivity(), "Everything is good", Toast.LENGTH_SHORT).show();
                        resultcard.setVisibility(View.VISIBLE);
                    }


                    for(String a: pickUpDeliveryModel.getDeliveryInformation().values())
                        Log.i("delipack",  a.toString());

                    for(String a: pickUpDeliveryModel.getFromInformation().values())
                        Log.i("delipack",  a.toString());

                } else {
                    Toast.makeText(getActivity(), "Fill out both forms to find rider", Toast.LENGTH_SHORT).show();

                    return;
                }

            }
        });














        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

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
               getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment1);

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



    }


}
