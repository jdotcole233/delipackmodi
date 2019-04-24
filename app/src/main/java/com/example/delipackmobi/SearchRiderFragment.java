package com.example.delipackmobi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.delipackmobi.Model.PickUpDeliveryModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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


public class SearchRiderFragment extends Fragment implements OnMapReadyCallback {


    AutocompleteSupportFragment autocompleteFragment;
    AutocompleteSupportFragment autocompleteFragment1;
    private Button rider_search_btn, confirm_button, confirm_payment, make_payment;
    private PickUpDeliveryModel pickUpDeliveryModel;
    private CardView resultcard, confirmcardview;
    private Animation animation, animationout, animationmove;
    private TextView confirm_textview;
    private Spinner paymentSpinner;
    private String payment_selection;
    //    private Animation animationout;
    private MapView mapView;
    private GoogleMap map;

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


        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide);
        animationout = AnimationUtils.loadAnimation(getContext(), R.anim.slideout);
        animationmove = AnimationUtils.loadAnimation(getContext(), R.anim.move);


        rider_search_btn = getActivity().findViewById(R.id.rider_search);
        pickUpDeliveryModel = new PickUpDeliveryModel();
        resultcard = getActivity().findViewById(R.id.search_result_cardview);

        MapsInitializer.initialize(getActivity());

        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity())) {

            case ConnectionResult.SUCCESS:
                Log.i("delipack", "Success");
                mapView = getActivity().findViewById(R.id.customermap);
                mapView.onCreate(savedInstanceState);
                if (mapView == null) {
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.customermap);
                    supportMapFragment.getMapAsync(this);
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Log.i("delipack", "Missing");
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Log.i("delipack", "Updated");
                break;
            default:
                Log.i("delipack", "Default");


        }


//        mapView = getActivity().findViewById(R.id.customermap);
//        confirm_button = getActivity().findViewById(R.id.confirm_btn);
//        confirmcardview = getActivity().findViewById(R.id.confirm_cardview);
//        confirm_payment = getActivity().findViewById(R.id.confirm_payment);
//        confirm_textview = getActivity().findViewById(R.id.confirmation_text);
//        make_payment = getActivity().findViewById(R.id.make_payment);
//        paymentSpinner = getActivity().findViewById(R.id.payment_choice);

//        SupportMapFragment supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.customermap);
//        supportMapFragment.getMapAsync(this);


        rider_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pickUpDeliveryModel != null) {

                    if (pickUpDeliveryModel.getFromInformation() == null) {
//                        Toast.makeText(getActivity(), "You need to fill a Pick up location", Toast.LENGTH_SHORT).show();
                        new DeliPackAlert(getActivity(), "Location Fields", "Fill out pick up locations").showDeliPackAlert();
                        return;
                    } else if (pickUpDeliveryModel.getDeliveryInformation() == null) {
//                        Toast.makeText(getContext(), "Delivery information cannot be empty", Toast.LENGTH_SHORT).show();
                        new DeliPackAlert(getActivity(), "Location Fields", "Delivery information cannot be empty").showDeliPackAlert();
                        return;
                    } else {
                        Toast.makeText(getActivity(), "Everything is good", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), SearchResult.class));
//                        resultcard.setVisibility(View.VISIBLE);
//                        resultcard.setAnimation(animation);
                    }


                    for (String a : pickUpDeliveryModel.getDeliveryInformation().values())
                        Log.i("delipack", a.toString());

                    for (String a : pickUpDeliveryModel.getFromInformation().values())
                        Log.i("delipack", a.toString());

                } else {
//                    Toast.makeText(getActivity(), "Fill out both forms to find rider", Toast.LENGTH_SHORT).show();
                    new DeliPackAlert(getActivity(), "Location Fields", "Fill out both forms to find rider").showDeliPackAlert();
                    return;
                }

            }
        });


//        confirm_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //perform some checks
//                resultcard.setVisibility(View.INVISIBLE);
////                resultcard.setAnimation(animationout);
//
////                confirmcardview.setAnimation(animation);
//                confirmcardview.setVisibility(View.VISIBLE);
//            }
//        });

//        confirm_payment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                confirmcardview.setY(700.0f);
//
//                ViewGroup.LayoutParams layoutParams = confirmcardview.getLayoutParams();
//                layoutParams.height = confirmcardview.getHeight() + 310;
////                confirmcardview.setAnimation(animationmove);
////                System.out.println(confirmcardview.getHeight());
//                confirmcardview.setLayoutParams(layoutParams);
//                confirm_textview.setText("Choose payment method");
//                confirm_payment.setVisibility(View.INVISIBLE);
//                paymentSpinner.setVisibility(View.VISIBLE);
//                make_payment.setVisibility(View.VISIBLE);
//
//
//            }
//        });

//        make_payment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!payment_selection.isEmpty()){
//                    if(payment_selection.equals("Mobile Money")){
//                            startActivity(new Intent(getActivity(), MobileMoneyPayment.class));
//                            confirmcardview.setVisibility(View.INVISIBLE);
//                    } else if (payment_selection.equals("Cash")){
//                        Toast.makeText(getActivity(), "Cash Selected", Toast.LENGTH_LONG).show();
//                    } else if (payment_selection.equals("Select payment option")){
//                        Toast.makeText(getActivity(), "Select a payment option", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//
//                } else {
//                    Toast.makeText(getActivity(), "Select a payment option", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//        });


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
                if (place != null) {
                    place_from.put(place.getId().toString(), place.getName().toString());
                    pickUpDeliveryModel.setFromInformation(place_from);
                    Log.i("g", "Place: " + place.getName() + ", " + place.getId());

                } else {
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
                if (place != null) {
                    place_delivery.put(place.getId().toString(), place.getName().toString());
                    pickUpDeliveryModel.setDeliveryInformation(place_delivery);
                    Log.i("g", "Place: " + place.getName() + ", " + place.getId());

                } else {
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


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        LatLng sydney = new LatLng(5.6037, 0.1870);
        map.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
//        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        googleMap.setMyLocationEnabled(true);
//        googleMap.setTrafficEnabled(true);
//        googleMap.setIndoorEnabled(true);
//        googleMap.setBuildingsEnabled(true);
//        googleMap.getUiSettings().setZoomControlsEnabled(true);

        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
