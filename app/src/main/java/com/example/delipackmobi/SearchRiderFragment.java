package com.example.delipackmobi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.delipackmobi.Model.PickUpDeliveryModel;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.FINGERPRINT_SERVICE;


public class SearchRiderFragment extends Fragment {


    AutocompleteSupportFragment autocompleteFragment;
    AutocompleteSupportFragment autocompleteFragment1;
    private Button rider_search_btn, confirm_button, confirm_payment, make_payment;
    private PickUpDeliveryModel pickUpDeliveryModel;
    private CardView resultcard, confirmcardview;
    private Animation animation, animationout, animationmove;
    private TextView confirm_textview;
    private Spinner paymentSpinner;
    private String payment_selection;
    private MapView mapView;
    private GoogleMap map;
    private LatLng pickUpFromLatLng;
    private LatLng deliverToLatLng;
    private ProgressBar progressBar;
    private TextView loadertext;
    private CardView searchRiderCardView;


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
        progressBar = getActivity().findViewById(R.id.searchloader);
        loadertext = getActivity().findViewById(R.id.loadingtext);
        searchRiderCardView = getActivity().findViewById(R.id.searchridercardview);

        mapView = getActivity().findViewById(R.id.customermap);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (pickUpFromLatLng != null && deliverToLatLng != null){
                    PolylineOptions polylineOptions = new PolylineOptions().add(pickUpFromLatLng)
                            .add(deliverToLatLng).color(Color.RED);
                    googleMap.addPolyline(polylineOptions);
                    googleMap.addMarker(new MarkerOptions().position(deliverToLatLng).title("YOU"));
                    googleMap.moveCamera(CameraUpdateFactory.zoomTo(11));
                }


            }
        });
        mapView.onCreate(savedInstanceState);





        rider_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pickUpDeliveryModel != null) {

                    if (pickUpDeliveryModel.getFromInformation() == null) {
                        new DeliPackAlert(getActivity(), "Location Fields", "Fill out pick up locations").showDeliPackAlert();
                        return;
                    } else if (pickUpDeliveryModel.getDeliveryInformation() == null) {
                        new DeliPackAlert(getActivity(), "Location Fields", "Delivery information cannot be empty").showDeliPackAlert();
                        return;
                    } else {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CustomerRiderRequest");
                        GeoFire geoFire = new GeoFire(databaseReference);
                        geoFire.setLocation("1", new GeoLocation(pickUpFromLatLng.latitude,pickUpFromLatLng.longitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {

                            }
                        });
                        rider_search_btn.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        loadertext.setVisibility(View.VISIBLE);
                        findClosestBiker();
                        startActivity(new Intent(getActivity(), SearchResult.class));

                    }


                    for (String a : pickUpDeliveryModel.getDeliveryInformation().values())
                        Log.i("delipack", a.toString());

                    for (String a : pickUpDeliveryModel.getFromInformation().values())
                        Log.i("delipack", a.toString());

                } else {
                    new DeliPackAlert(getActivity(), "Location Fields", "Fill out both forms to find rider").showDeliPackAlert();
                    return;
                }

            }
        });


        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setHint("Pick up location");
        autocompleteFragment.setCountry("GH");


// Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.OPENING_HOURS));
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                HashMap<String, String> place_from = new HashMap<>();

                if (place != null) {
                    place_from.put(place.getId().toString(), place.getName().toString());
                    pickUpFromLatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    pickUpDeliveryModel.setFromInformation(place_from);
                    Log.i("g", "Place: " + place.getLatLng());
                    map.addMarker(new MarkerOptions().position(pickUpFromLatLng).title("Pick up"));


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

        autocompleteFragment1.setHint("Delivery location");
        autocompleteFragment1.setCountry("GH");

// Specify the types of place data to return.
        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                HashMap<String, String> place_delivery = new HashMap<>();
                if (place != null) {
                    deliverToLatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    place_delivery.put(place.getId().toString(), place.getName().toString());
                    pickUpDeliveryModel.setDeliveryInformation(place_delivery);
                    Log.i("g", "Place: " + place);
                    if (pickUpFromLatLng != null && deliverToLatLng != null){
                        PolylineOptions polylineOptions = new PolylineOptions().add(pickUpFromLatLng)
                                .add(deliverToLatLng).color(Color.RED);
                        map.addPolyline(polylineOptions);
                        map.addMarker(new MarkerOptions()
                                .position(deliverToLatLng)
                                .title("Deliver to")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                        );
                        map.moveCamera(CameraUpdateFactory.newLatLng(deliverToLatLng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(11));
                    }

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


        getRiderResponse();


    }





    /*
    *  Customer listens for when a rider accepts their request
    *  A value in the parent json object called "CustomerResponseReceived"
    *
     */

    public void getRiderResponse(){
        DatabaseReference riderresponse = FirebaseDatabase.getInstance().getReference().child("RiderCustomerConnect").child("rider_id").child("");
        riderresponse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
//                    loadertext.setVisibility(View.INVISIBLE);
//                    progressBar.setVisibility(View.INVISIBLE);
//                    rider_search_btn.setVisibility(View.VISIBLE);
//                    searchRiderCardView.setVisibility(View.INVISIBLE);
//                    startActivity(new Intent(getActivity(), SearchResult.class));
//                    getActivity().finish();

                    /*
                    * Proceed to payment for service from here
                    * Rider id has been sent to the payment activity
                    * Using putExtra
                     */

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }







    Double proximity = 0.2;
    Boolean riderFound = false;
    String riderID;
    /*
     * System search for near by  driver using GeoFire
     * If driver is not found within 0.3 km radius, the system
     * increase search range plus 1
     * If rider is found, the riders id and saved globally
     * and the child object [riderID] in RiderCustomerConnect is
     * updated. Which the rider constantly listens on for changes.
     */

    public void findClosestBiker(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RidersLocationAvailable");
        System.out.println("FInd driver" + databaseReference);
        GeoFire geoFire = new GeoFire(databaseReference);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(pickUpFromLatLng.latitude, pickUpFromLatLng.longitude), proximity);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!riderFound){
                    riderFound = true;
                    riderID = key;

                    DatabaseReference driverfounddatabase = FirebaseDatabase.getInstance()
                            .getReference()
                            .child("RiderCustomerConnect").child(key);
                    DatabaseReference updatedriverobject = FirebaseDatabase.getInstance().getReference()
                            .child("rider").child(key);

                    driverfounddatabase.setValue("","");
//
//                    HashMap<String, Object> map = new HashMap();
//                    map.put("customer_id", 1);
//                    map.put("payment_made","");
//
//                    updatedriverobject.setValue(map);
//
//                    driverfounddatabase.updateChildren(map);
//                    Intent intent = new Intent();
//                    intent.putExtra("rider_id", key);


                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!riderFound){
                    proximity += 0.2;
                    if (proximity >= 5){
                        System.out.println("Done searching");
                        return;
                    }
                    findClosestBiker();

                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }















    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }








}
