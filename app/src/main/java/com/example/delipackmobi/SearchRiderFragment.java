package com.example.delipackmobi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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

import com.example.delipackmobi.CustomerContract.CustomerContract;
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
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.FINGERPRINT_SERVICE;
import static com.example.delipackmobi.CustomerContract.CustomerContract.GETCOMPANYDATA_URL;


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
    private CardView searchRiderCardView, searchriderwelcomecard;
    private TextView welcomeText;
    private CustomerContract customerContract;
    private String customer_first_name, customer_last_name, customer_phone_number, customer_id;
    public static String  picklat, picklong;
    public static  Double proximity = 0.1;
    Boolean riderFound = false;
    String riderID;
    public static Activity delipackEventloader;
    private AsyncHttpClient getCompanyInformation;
    private float [] distdiff;
    private String deliveryLocatioName, pickupLocationName;



    public void readCompanyInformation(String riderID){
        AsyncHttpClient getCompanyInformation = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("rider_id", riderID);

        System.out.println("read company information entered");

        getCompanyInformation.post(GETCOMPANYDATA_URL, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String responsefromserver = response.toString();


                if(!responsefromserver.isEmpty()){
                    new CustomerContract(getActivity())
                            .setBasicCookies("company_details", response.toString(),2, "/");
                } else {
                    return;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                System.out.println(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println(throwable.getMessage());
            }
        });

    }

    public Double[] getPriceAndCommission(Double distance,Double baseprice, Double basedistance){
         Double [] pricelist = new Double[2];
        Double distancediff = 0.0;
        Double initialprice = 0.0;
        Double commissionprice = 0.0;

//         if(!distance){
             if (distance >= basedistance){
                 distancediff = distance/basedistance;
             } else {
                 distancediff = basedistance/distance;
             }
             initialprice = distancediff * baseprice;
             commissionprice = initialprice * 0.5;
//         }
         pricelist[0] = initialprice;
         pricelist[1] = commissionprice;

        return pricelist;
    }



    public SearchRiderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            pickUpFromLatLng = new LatLng(savedInstanceState.getDouble("pickuplat"), savedInstanceState.getDouble("pickuplong"));
            deliverToLatLng = new LatLng(savedInstanceState.getDouble("delivertolat"), savedInstanceState.getDouble("delivertolong"));
        }


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
        DeliPackEventLoader.searchRiderActivity = getActivity();


        rider_search_btn = getActivity().findViewById(R.id.rider_search);
        pickUpDeliveryModel = new PickUpDeliveryModel();
        resultcard = getActivity().findViewById(R.id.search_result_cardview);
        progressBar = getActivity().findViewById(R.id.searchloader);
        loadertext = getActivity().findViewById(R.id.loadingtext);
        searchRiderCardView = getActivity().findViewById(R.id.searchridercardview);
        searchriderwelcomecard = getActivity().findViewById(R.id.cardsearchwelcome);
        welcomeText = getActivity().findViewById(R.id.welcomemessage);
        customerContract = new CustomerContract(getActivity());
        distdiff = new float[1];



        if (savedInstanceState != null){
            System.out.println("saved instant state not null");
            pickUpFromLatLng = new LatLng(savedInstanceState.getDouble("pickuplat"), savedInstanceState.getDouble("pickuplong"));
            deliverToLatLng = new LatLng(savedInstanceState.getDouble("delivertolat"), savedInstanceState.getDouble("delivertolong"));
        }



        System.out.println("Customer cookies " + customerContract.getPersistentCookieStore().getCookies());
        for(Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
              if(cookie.getName().equals("customerInfomation")){
                  try{
                      JSONObject customerJSON = new JSONObject(cookie.getValue());
                      customer_first_name = customerJSON.getString("first_name");
                      customer_last_name = customerJSON.getString("last_name");
                      customer_phone_number = customerJSON.getString("phone_number");
                      customer_id = customerJSON.getString("customer_id");
                      welcomeText.setText("Nice to see you! " + customerJSON.getString("first_name"));

                  }catch (Exception e){
                      System.out.println(e.getMessage());
                  }
              }
        }


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
//                        System.out.println("Delivery model " + pickUpDeliveryModel.getFromInformation().toString());
//                        Double [] pricerange = new Double[2];
//                                getPriceAndCommission((double)Math.round(distdiff[0]/1000),5.0,4.0);
                        Location.distanceBetween(pickUpFromLatLng.latitude,pickUpFromLatLng.longitude, deliverToLatLng.latitude,deliverToLatLng.longitude, distdiff);
                        getSearchDetails(pickUpDeliveryModel.getFromInformation().get("pickup"), pickUpDeliveryModel.getDeliveryInformation().get("delivery"), getPriceAndCommission((double)Math.round(distdiff[0]/1000),5.0,4.0)[0], getPriceAndCommission((double)Math.round(distdiff[0]/1000),5.0,4.0)[1]);
                        //insert actual distance and base price

                        System.out.println("distance difference " + Math.round(distdiff[0]/1000));
                        proximity = 0.1;
                        if(proximity <= 10){
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CustomerRiderRequest");
                            GeoFire geoFire = new GeoFire(databaseReference);
                            geoFire.setLocation(customer_id, new GeoLocation(pickUpFromLatLng.latitude,pickUpFromLatLng.longitude), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {

                                }
                            });
//                        rider_search_btn.setVisibility(View.INVISIBLE);
//                        progressBar.setVisibility(View.VISIBLE);
//                        loadertext.setVisibility(View.VISIBLE);
                            startActivity(new Intent(getActivity(), DeliPackEventLoader.class));
                            findClosestBiker();
                        } else {
                            return;
                        }
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
                    map.clear();
                    autocompleteFragment1.setHint("Delivery location");
                    place_from.put(place.getId().toString(), place.getName().toString());
                    pickUpFromLatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    pickUpDeliveryModel.setFromInformation("pickup", place.getName().toString());

                    pickupLocationName = place.getName();
                    Log.i("g", "Place: " + place);
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
                    pickUpDeliveryModel.setDeliveryInformation("delivery",place.getName().toString());
                    deliveryLocatioName = place.getName();

                    Log.i("g", "Place: " + place);
                    if (pickUpFromLatLng != null && deliverToLatLng != null){
                        PolylineOptions polylineOptions = new PolylineOptions().add(pickUpFromLatLng)
                                .add(deliverToLatLng).color(Color.RED);
                        map.addPolyline(polylineOptions);
                        map.addMarker(new MarkerOptions()
                                .position(deliverToLatLng)
                                .title("Deliver to: " + place.getName())
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
        if (!customer_id.isEmpty()){
            DatabaseReference riderresponse = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest").child(customer_id).child("rideraccepted");
            riderresponse.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Log.i("delipacksnapshot", dataSnapshot.toString());

                        if (dataSnapshot.getValue().equals("true")){
//                        loadertext.setVisibility(View.INVISIBLE);
//                        progressBar.setVisibility(View.INVISIBLE);
//                        rider_search_btn.setVisibility(View.VISIBLE);
//                        searchRiderCardView.setVisibility(View.INVISIBLE);
//                        rider_search_btn.setVisibility(View.VISIBLE);
                            searchriderwelcomecard.setVisibility(View.INVISIBLE);
                            searchRiderCardView.setVisibility(View.INVISIBLE);

//                        startActivity(new Intent(getActivity(), SearchResult.class));
//                        if(!riderID.isEmpty()){
//                            if (SearchRiderFragment.delipackEventloader.)
//                            System.out.println("Activity name :" + SearchRiderFragment.delipackEventloader.getParent().getComponentName());
                            SearchRiderFragment.delipackEventloader.finish();
                            readCompanyInformation(riderID);
                            Intent sendRiderID = new Intent(getActivity(), SearchResult.class);
                            sendRiderID.putExtra("riderID", riderID);
                            startActivity(sendRiderID);
//                        }
                        } else if (dataSnapshot.getValue().equals("paid")) {
                            searchriderwelcomecard.setVisibility(View.INVISIBLE);
                            searchRiderCardView.setVisibility(View.INVISIBLE);
                            transactionInProgress();

                        }

                        /*
                         * Proceed to payment for service from here
                         * Rider id has been sent to the payment activity
                         * Using putExtra
                         */

                    } else {
                        System.out.println("Data changed else part " + dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }




    public void transactionInProgress(){
        Intent transactInProg = new Intent(getActivity(), PackageInProgress.class);
        startActivity(transactInProg);

        map.addMarker(new MarkerOptions().position(pickUpFromLatLng).title("Pick up"));



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





    /*
     * System search for near by  driver using GeoFire
     * If driver is not found within 0.2 km radius, the system
     * increase search range plus 1
     * If rider is found, the riders id and saved globally
     * and the child object [riderID] in RiderCustomerConnect is
     * updated. Which the rider constantly listens on for changes.
     */

    public void findClosestBiker(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RiderLocationAvailable");
        System.out.println("Find driver" + databaseReference);
        GeoFire geoFire = new GeoFire(databaseReference);

        picklat = Double.toString(pickUpFromLatLng.latitude);
        picklong = Double.toString(pickUpFromLatLng.longitude);

        if(!picklat.isEmpty() && !picklong.isEmpty()){

            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(pickUpFromLatLng.latitude, pickUpFromLatLng.longitude), proximity);
            geoQuery.removeAllListeners();
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    if (!riderFound){
                        riderFound = true;
                        riderID = key;

                        // Create a structure for driver found for customer
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databasecustomer = database.getReference().child("CustomerRiderRequest").child(customer_id); //set first child value to customer id
                        databasecustomer.child("rideraccepted").setValue("");
                        System.out.println("found rider");

                        DatabaseReference databaserider = database.getReference().child("RiderFoundForCustomer").child(riderID);
                        databaserider.child("customer_id").setValue(customer_id);
                        databaserider.child("assigned").setValue("true");


                        Map<String, Object> dellocation = new HashMap<>();
                        dellocation.put("latitude", Double.toString(deliverToLatLng.latitude));
                        dellocation.put("longitude", Double.toString(deliverToLatLng.longitude));
                        dellocation.put("deliverylocationname", deliveryLocatioName);
                        dellocation.put("pickuplocationname", pickupLocationName);
                        dellocation.put("customerPhoneNumber", customer_phone_number);
                        dellocation.put("customerName",  customer_first_name + " " + customer_last_name);
                        databasecustomer.child("deliverlatlong").updateChildren(dellocation);


//                        pickUpDeliveryModel.resetFromInformation();
//                        pickUpDeliveryModel.resetsetDeliveryInformation();

//                    progressBar.setVisibility(View.INVISIBLE);
//                    loadertext.setText("Rider should accept");
//                    rider_search_btn.setVisibility(View.VISIBLE);
//                    searchRiderCardView.setVisibility(View.INVISIBLE);

//                    startActivity(new Intent(getActivity(), SearchResult.class));




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
                        proximity += 0.1;
                        System.out.println("searching " + proximity );
                        if (proximity >= 10){
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CustomerRiderRequest").child(customer_id);
                            databaseReference.removeValue();
                            String message = "Rider not found\nYou can try again later";
                            SearchRiderFragment.delipackEventloader.finish();
                            new DeliPackAlert(getActivity(), "Search complete", message).showDeliPackAlert();
                            return;
                        }
                        findClosestBiker();

                    }
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });
        } else {
            System.out.println("Trip cancelled");
            return;
        }
    }



    public void getSearchDetails(String pickuplocation, String deliverylocation, Double charges, Double commission){
        if (!pickuplocation.isEmpty() && !deliverylocation.isEmpty()){
            Gson searchConvert = new Gson();
            HashMap<String, String> search = new HashMap<>();
            search.put("pickup", pickuplocation);
            search.put("delivery", deliverylocation);
            search.put("delivery_charge", Double.toString(charges));
            search.put("commission_charge", Double.toString(commission));
            String convertedJSON = searchConvert.toJson(search);
            customerContract.setBasicCookies("searchdata", convertedJSON, 3, "/");
        }
    }












    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if(pickUpFromLatLng.latitude)
        if (!pickUpFromLatLng.toString().isEmpty() && !deliverToLatLng.toString().isEmpty()){
            outState.putDouble("pickuplat", pickUpFromLatLng.latitude);
            outState.putDouble("pickuplong", pickUpFromLatLng.longitude);

            outState.putDouble("delivertolat", deliverToLatLng.latitude);
            outState.putDouble("delivertolong", deliverToLatLng.longitude);
        } else{
            System.out.println("Saving instant state false called ");
        }

    }
}
