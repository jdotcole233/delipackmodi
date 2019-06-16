package com.example.delipackmobi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.example.delipackmobi.CustomerContract.ManageNetworkConnectionClass;
import com.example.delipackmobi.CustomerContract.NetworkAllowanceCheck;
import com.example.delipackmobi.Model.PickUpDeliveryModel;
import com.example.delipackmobi.Model.ReadRiderInformationBackAsynTask;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static com.example.delipackmobi.CustomerContract.CustomerContract.GETCOMPANYDATA_URL;


public class SearchRiderFragment extends Fragment {

    private final String LOG_MSG = "DeliPackMessage";
    private  AutocompleteSupportFragment autocompleteFragment, autocompleteFragment1;
    private Button rider_search_btn, confirm_button, confirm_payment, make_payment;
    private PickUpDeliveryModel pickUpDeliveryModel;
    private CardView resultcard, confirmcardview, searchRiderCardView, searchriderwelcomecard;
    private TextView confirm_textview, loadertext, welcomeText;
    private Spinner paymentSpinner;
    private MapView mapView;
    private GoogleMap map;
    private LatLng pickUpFromLatLng, deliverToLatLng;
    private ProgressBar progressBar;
    private CustomerContract customerContract;
    private String customer_first_name, customer_last_name, customer_phone_number, customer_id, payment_selection;
    public  static String picklat, picklong, riderID;
    public static  Double proximity;
    private String rider_id_found, deliveryLocatioName, pickupLocationName;
    public static Activity delipackEventloader;
    private AsyncHttpClient getCompanyInformation;
    private float [] distdiff;
    private Boolean isDismissed, riderFound;
    private List<String> searchingString;
    public static GeoQuery geoQuery;
    Intent sendRiderID;
    private NetworkAllowanceCheck networkAllowanceCheck;
    private ManageNetworkConnectionClass manageNetworkConnectionClass;
    PolylineOptions polylineOptions;
    ReadRiderInformationBackAsynTask readRiderInformationBackAsynTask;
    Intent transactInProg;
    Boolean isComing = false;











    public SearchRiderFragment() {
        if(mapView != null){ mapView.onResume();}
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mapView != null){ mapView.onResume();}
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mapView != null){ mapView.onResume();}
        return inflater.inflate(R.layout.fragment_search_rider, container, false);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mapView != null){ mapView.onResume();}

        DeliPackEventLoader.searchRiderActivity = getActivity();
        TripCompletedRatingMessage.tripCompletedSearchActivity = getActivity();
        RateCompanyRider.searchriderfragment = getActivity();
        networkAllowanceCheck = new NetworkAllowanceCheck(getActivity());
        manageNetworkConnectionClass = new ManageNetworkConnectionClass(getActivity());

        riderFound = false;
        proximity = 0.1;


        rider_search_btn = getActivity().findViewById(R.id.rider_search);
        pickUpDeliveryModel = new PickUpDeliveryModel();
        resultcard = getActivity().findViewById(R.id.search_result_cardview);
        progressBar = getActivity().findViewById(R.id.searchloader);
        loadertext = getActivity().findViewById(R.id.loadingtext);
        searchRiderCardView = getActivity().findViewById(R.id.searchridercardview);
        searchriderwelcomecard = getActivity().findViewById(R.id.cardsearchwelcome);
        welcomeText = getActivity().findViewById(R.id.welcomemessage);
        customerContract = new CustomerContract(getActivity());
        readRiderInformationBackAsynTask = new ReadRiderInformationBackAsynTask(getActivity());
        distdiff = new float[1];
        isDismissed = false;
        getCompanyInformation = new AsyncHttpClient();


        if (pickUpDeliveryModel.getFromInformation().size() == 0 && pickUpDeliveryModel.getDeliveryInformation().size() == 0){
            rider_search_btn.setEnabled(false);
        }


        retrieveSearchInformationLocally();




        getrideridfromlocaldeviceforplotting();


        mapView = getActivity().findViewById(R.id.customermap);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                if ( !searchingString.isEmpty()){
                    System.out.println("preference back " + searchingString.get(0));
                    plotLocationsOnMap(new LatLng(Double.parseDouble(searchingString.get(0)), Double.parseDouble(searchingString.get(1))), new LatLng(Double.parseDouble(searchingString.get(2)), Double.parseDouble(searchingString.get(3))), "", "");

                } else if (pickUpFromLatLng != null && deliverToLatLng != null){
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

                if (!manageNetworkConnectionClass.checkConnectivity()){
                    Intent networkIntent = new Intent(getActivity(), NetworkConnectionView.class);
                    startActivity(networkIntent);
                    return;
                }

                if (pickUpDeliveryModel != null) {
                    if (pickUpDeliveryModel.getFromInformation().size() == 0) {
                        new DeliPackAlert(getActivity(), "Location Fields", "Fill out pick up locations").showDeliPackAlert();
                        return;
                    } else if (pickUpDeliveryModel.getDeliveryInformation().size() == 0) {
                        new DeliPackAlert(getActivity(), "Location Fields", "Delivery information cannot be empty").showDeliPackAlert();
                        return;
                    } else {

                        isComing = false;

                        if (customerContract.getPersistentCookieStore().getCookies().contains("searchdata")){
                            autocompleteFragment.setText(searchingString.get(4));
                            autocompleteFragment1.setText(searchingString.get(5));
                            Location.distanceBetween(Double.parseDouble(searchingString.get(0)),Double.parseDouble(searchingString.get(1)), Double.parseDouble(searchingString.get(2)),Double.parseDouble(searchingString.get(3)), distdiff);
                            getSearchDetails(searchingString.get(4), searchingString.get(5), getPriceAndCommission((double)Math.round(distdiff[0]/1000), 5.0,4.0)[0],
                                    getPriceAndCommission((double)Math.round(distdiff[0]/1000), 5.0, 4.0)[1]);

                        } else {
                            System.out.println(pickUpDeliveryModel.getFromInformation().size());
                            Location.distanceBetween(pickUpFromLatLng.latitude,pickUpFromLatLng.longitude, deliverToLatLng.latitude,deliverToLatLng.longitude, distdiff);
                            getSearchDetails(pickUpDeliveryModel.getFromInformation().get("pickup"), pickUpDeliveryModel.getDeliveryInformation().get("delivery"), getPriceAndCommission((double)Math.round(distdiff[0]/1000),5.0,4.0)[0], getPriceAndCommission((double)Math.round(distdiff[0]/1000),5.0,4.0)[1]);
                        }



                        proximity = 0.1;
                        if(proximity <= 10 && searchingString.size() == 0){
                            System.out.println("In if statement when size == 0");

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CustomerRiderRequest");
                            GeoFire geoFire = new GeoFire(databaseReference);
                            geoFire.setLocation(customer_id, new GeoLocation(pickUpFromLatLng.latitude,pickUpFromLatLng.longitude), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    startActivity(new Intent(getActivity(), DeliPackEventLoader.class));
                                }
                            });
                            findClosestBiker();


                        } else if (proximity <= 10 && searchingString.size() != 0){
                            System.out.println("In if statement when size != 0 " + "searching string " + searchingString.toString());
                            riderFound = false;
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CustomerRiderRequest");
                            GeoFire geoFire = new GeoFire(databaseReference);
                            LatLng searchLatLng = new LatLng(Double.parseDouble(searchingString.get(0)), Double.parseDouble(searchingString.get(1)));
                            geoFire.setLocation(customer_id, new GeoLocation(searchLatLng.latitude, searchLatLng.longitude), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    System.out.println("Error from searching " + error);
                                    findClosestBiker();
                                    startActivity(new Intent(getActivity(), DeliPackEventLoader.class));

                                }
                            });

                        }
                        else {
                            System.out.println("Size of proximity " + proximity + " " + "Searching string " + searchingString.size());
                            return;
                        }
                        System.out.println("In something " + isComing);
                    }



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
                    map.addMarker(new MarkerOptions().position(pickUpFromLatLng).title("Pick up").icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_home_address)));


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
                if (place != null) {

                    deliverToLatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    pickUpDeliveryModel.setDeliveryInformation("delivery",place.getName().toString());
                    deliveryLocatioName = place.getName();

                    Log.i("g", "Place: " + place);
                    if (pickUpFromLatLng != null && deliverToLatLng != null){
//                        PolylineOptions polylineOptions = new PolylineOptions().add(pickUpFromLatLng)
//                                .add(deliverToLatLng).color(R.drawable.distance_color_display);
//                        map.addPolyline(polylineOptions);
//                        map.addMarker(new MarkerOptions()
//                                .position(deliverToLatLng)
//                                .title("Deliver to: " + place.getName())
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_to_map_pin))
//
//                        );
                        makeDirectionRequest(pickUpFromLatLng, deliverToLatLng, R.drawable.icons_home_address, R.drawable.icons_to_map_pin);
//                        map.moveCamera(CameraUpdateFactory.newLatLng(deliverToLatLng));
//                        map.animateCamera(CameraUpdateFactory.zoomTo(11));
                        rider_search_btn.setEnabled(true);
                    }

                } else {
//                    place_delivery = null;
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("g", "An error occurred: " + status);
            }
        });



        getRiderResponse();
        if (riderID != null){
            getRiderCordinatesToPlot(customer_id);

        }
//        openSearchResult();



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("CustomerRiderRequest")
                .child(customer_id).child("delivered");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if (dataSnapshot.getValue().equals("true")){
                                    if (getActivity() != null){
                                        Intent rateIntent = new Intent(getActivity(), RateCompanyRider.class);
                                        startActivity(rateIntent);
                                    }
                                }
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }




    public void plotLocationsOnMap(LatLng pickupLocation, LatLng deliveryLocation, String pickUpLabel, String deliveryLabel){

        if(pickupLocation != null && deliveryLocation != null){
            map.addMarker(new MarkerOptions()
                    .position(pickupLocation)
                    .title(pickUpLabel)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_to_map_pin)));

            PolylineOptions polylineOptions = new PolylineOptions().add(pickupLocation)
                    .add(deliveryLocation).color(R.drawable.action_item_background_selector);
            map.addPolyline(polylineOptions);
            map.addMarker(new MarkerOptions()
                    .position(deliveryLocation)
                    .title(deliveryLabel)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_home_address)));

            map.moveCamera(CameraUpdateFactory.newLatLng(deliveryLocation));
            map.animateCamera(CameraUpdateFactory.zoomTo(11));
        }

    }


    public void plotRiderCustomerLocationsOnMap(LatLng pickupLocation, LatLng deliveryLocation, String pickUpLabel, String deliveryLabel){

        if(pickupLocation != null && deliveryLocation != null){
            map.addMarker(new MarkerOptions()
                    .position(pickupLocation)
                    .title(pickUpLabel)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_scooter)));

            PolylineOptions polylineOptions = new PolylineOptions().add(pickupLocation)
                    .add(deliveryLocation).color(R.drawable.action_item_background_selector);
            map.addPolyline(polylineOptions);
            map.addMarker(new MarkerOptions()
                    .position(deliveryLocation)
                    .title(deliveryLabel)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_home_address)));

            map.moveCamera(CameraUpdateFactory.newLatLng(deliveryLocation));
            map.animateCamera(CameraUpdateFactory.zoomTo(11));
        }

    }



    /*
    *  Customer listens for when a rider accepts their request
    *  A value in the parent json object called "CustomerResponseReceived"
    *
     */

    public void getRiderResponse(){
        if (customer_id != null){
            DatabaseReference riderresponse = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest").child(customer_id).child("rideraccepted");
            riderresponse.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){

                        if (dataSnapshot.getValue().equals("true")){

//                            searchriderwelcomecard.setVisibility(View.INVISIBLE);
//                            searchRiderCardView.setVisibility(View.INVISIBLE);

                            if(isDismissed == true){
                                SearchRiderFragment.delipackEventloader.finish();
                                isDismissed = false;
                            }

                            try{
                                if (readRiderInformationBackAsynTask.getStatus() != AsyncTask.Status.RUNNING){
                                    readRiderInformationBackAsynTask.cancel(true);
                                    readRiderInformationBackAsynTask = new ReadRiderInformationBackAsynTask(getActivity());
                                    readRiderInformationBackAsynTask.execute(riderID);
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }


//                            readCompanyInformation(riderID);

//                            sendRiderID.putExtra("riderID", riderID);
//                        }
                        } else if (dataSnapshot.getValue().equals("paid")) {
                            searchriderwelcomecard.setVisibility(View.INVISIBLE);
                            searchRiderCardView.setVisibility(View.INVISIBLE);
                            if (getActivity() != null){
                                try {

                                    transactInProg = new Intent(getActivity(), PackageInProgress.class);
                                    startActivity(transactInProg);

                                } catch (Exception e) {
                                    startActivity(transactInProg);
                                    e.printStackTrace();
                                }
                            }


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



    public String openSearchResult(){
        DatabaseReference riderdatabaseresponse = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest").child(customer_id).child("riderID");
        riderdatabaseresponse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("DeliPackMessage", "In rider database response");

                if (dataSnapshot.exists()){
                    rider_id_found  = dataSnapshot.getValue().toString();
                    Log.i("DeliPackMessage", "In rider database response " + rider_id_found);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return rider_id_found;
    }



    public void transactionInProgress(){
        Intent transactInProg = new Intent(getActivity(), PackageInProgress.class);
        startActivity(transactInProg);

//        map.addMarker(new MarkerOptions().position(pickUpFromLatLng).title("Pick up"));
//
//
//
//        PolylineOptions polylineOptions = new PolylineOptions().add(pickUpFromLatLng)
//                .add(deliverToLatLng).color(Color.RED);
//        map.addPolyline(polylineOptions);
//        map.addMarker(new MarkerOptions()
//                .position(deliverToLatLng)
//                .title("Deliver to")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//
//        );
//        map.moveCamera(CameraUpdateFactory.newLatLng(deliverToLatLng));
//        map.animateCamera(CameraUpdateFactory.zoomTo(11));


    }


    boolean isSearching = false;


    /*
     * System search for near by  driver using GeoFire
     * If driver is not found within 0.2 km radius, the system
     * increase search range plus 1
     * If rider is found, the riders id and saved globally
     * and the child object [riderID] in RiderCustomerConnect is
     * updated. Which the rider constantly listens on for changes.
     */

    public void findClosestBiker(){
        System.out.println("find rider called");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RiderLocationAvailable");
        GeoFire geoFire = new GeoFire(databaseReference);

        if(searchingString.size() == 0){
            picklat = Double.toString(pickUpFromLatLng.latitude);
            picklong = Double.toString(pickUpFromLatLng.longitude);
        } else {
            picklat = searchingString.get(0);
            picklong = searchingString.get(1);
        }

        System.out.println("lat is " + picklat + " lng is " + picklong);
        Log.i("DeliPackMessage","lat is " + picklat + " lng is " + picklong + riderFound);


        if(!picklat.isEmpty() && !picklong.isEmpty()){
            System.out.println("lat is " + picklat + " lng is " + picklong + " In if statement");
            Log.i("DeliPackMessage","lat is " + picklat + " lng is " + picklong + " In if statement " + riderFound );

            geoQuery = geoFire.queryAtLocation(new GeoLocation(Double.parseDouble(picklat), Double.parseDouble(picklong)), proximity);
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
                        databasecustomer.child("riderid").setValue(key);
                        System.out.println("found rider");

                        DatabaseReference databaserider = database.getReference().child("RiderFoundForCustomer").child(riderID);
                        databaserider.child("customer_id").setValue(customer_id);
                        databaserider.child("assigned").setValue("true");


                        savedeliveryinformationafterriderisfound(databasecustomer);




                        isDismissed = true;
                        System.out.println("key has entered searching for rider");
                        proximity = 0.1;

//                        pickUpDeliveryModel.resetFromInformation();
//                        pickUpDeliveryModel.resetsetDeliveryInformation();

//                    progressBar.setVisibility(View.INVISIBLE);
//                    loadertext.setText("Rider should accept");
//                    rider_search_btn.setVisibility(View.VISIBLE);
//                    searchRiderCardView.setVisibility(View.INVISIBLE);


                    }
                }

                @Override
                public void onKeyExited(String key) {
                    Log.i("DeliPackMessage","On key existed while searching for rider");
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    Log.i("DeliPackMessage","On key moved while searching for rider");

                    System.out.println("On key moved while searching for rider");
                }

                @Override
                public void onGeoQueryReady() {
                    if(!riderFound){
                        proximity += 0.1;
                        System.out.println("searching " + proximity );
                        if (proximity >= 20){
                            geoQuery.removeAllListeners();
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
                    System.out.println("Some went wrong while searching for rider");
                }
            });
        } else {
            System.out.println("Trip cancelled");
            return;
        }

        return;
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
    public void onStart() {
        super.onStart();
        mapView.onResume();
        retrieveSearchInformationLocally();
        getrideridfromlocaldeviceforplotting();
        Log.i(LOG_MSG, "On start called in search fragment " + riderID);
        if (riderID != null){
            getRiderCordinatesToPlot(customer_id);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        retrieveSearchInformationLocally();
        getrideridfromlocaldeviceforplotting();
        Log.i(LOG_MSG, "On resume called in search fragment " + riderID);
        if (riderID != null){
            getRiderCordinatesToPlot(customer_id);
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onResume();
        Log.i(LOG_MSG, "On pause called in search fragment");
        savedSearchInformationLocally();
        isComing = false;


    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("On destroyed view called");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("On destroyed called");
        Log.i(LOG_MSG, "On destroyed called");
        savedSearchInformationLocally();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LOG_MSG, "On detached called");
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.i(LOG_MSG, "On saved instant state called ");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(mapView != null){ mapView.onResume();}
        Log.i(LOG_MSG, "On Attach Called");
//        getRiderResponse();

    }



    public void retrieveSearchInformationLocally(){
        final SharedPreferences preferences = getActivity().getSharedPreferences("searchingdata", Context.MODE_PRIVATE);
        searchingString = new ArrayList<>();
        if(preferences.contains("pickuplat")){
            searchingString.add(preferences.getString("pickuplat", null));
            searchingString.add(preferences.getString("pickuplong", null));
            searchingString.add(preferences.getString("delivertolat", null));
            searchingString.add(preferences.getString("delivertolong", null));
            searchingString.add(preferences.getString("pickupname", null));
            searchingString.add(preferences.getString("deliveryname", null));
        }
        Log.i("DeliPackMessage", "Retrieveing data from shared preference");
    }

    public void savedSearchInformationLocally(){
        if(pickUpFromLatLng != null && deliverToLatLng != null){
            SharedPreferences.Editor saveSearchData = getActivity().getSharedPreferences("searchingdata", Context.MODE_PRIVATE).edit();
            saveSearchData.putString("pickuplat", Double.toString(pickUpFromLatLng.latitude));
            saveSearchData.putString("pickuplong", Double.toString(pickUpFromLatLng.longitude));
            saveSearchData.putString("delivertolat", Double.toString(deliverToLatLng.latitude));
            saveSearchData.putString("delivertolong", Double.toString(deliverToLatLng.longitude));
            saveSearchData.putString("pickupname", pickupLocationName);
            saveSearchData.putString("deliveryname", deliveryLocatioName);
            saveSearchData.apply();
            Log.i("DeliPackMessage", "Saving data to shared preference");

        }
    }



    public void savedeliveryinformationafterriderisfound(DatabaseReference databasecustomer){
        Map<String, Object> dellocation = new HashMap<>();
        if (customerContract.getPersistentCookieStore().getCookies().contains("searchdata")){
            dellocation.put("latitude", searchingString.get(2));
            dellocation.put("longitude", searchingString.get(3));
            dellocation.put("deliverylocationname", deliveryLocatioName);
            dellocation.put("pickuplocationname", pickupLocationName);
            dellocation.put("customerPhoneNumber", customer_phone_number);
            dellocation.put("customerName",  customer_first_name + " " + customer_last_name);
            dellocation.put("ridercookieid", riderID);
            databasecustomer.child("deliverlatlong").updateChildren(dellocation);


        } else {
            dellocation.put("latitude", deliverToLatLng.latitude);
            dellocation.put("longitude", deliverToLatLng.longitude);
            dellocation.put("deliverylocationname", deliveryLocatioName);
            dellocation.put("pickuplocationname", pickupLocationName);
            dellocation.put("customerPhoneNumber", customer_phone_number);
            dellocation.put("customerName",  customer_first_name + " " + customer_last_name);
            dellocation.put("ridercookieid", riderID);
            databasecustomer.child("deliverlatlong").updateChildren(dellocation);
        }


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
        commissionprice = initialprice * 0.05;
//         }
        pricelist[0] = initialprice;
        pricelist[1] = commissionprice;

        return pricelist;
    }


    public void getRiderCordinatesToPlot(String customerID){
        System.out.println("In rider cordinates " + customerID + " " + riderID);
        final DatabaseReference riderdatabasereference = FirebaseDatabase.getInstance().getReference().child("RiderFoundForCustomer");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest").child(customerID);
        databaseReference.child("PickUpBegin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            System.out.println("In pick up Data exists");
                            if (dataSnapshot.getValue().equals("true")){
                                databaseReference.child("riderid").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            if (dataSnapshot.getValue() != null){
                                                riderdatabasereference.child(dataSnapshot.getValue().toString()).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.exists()){

//                                                            if(riderID == null){
//                                                               SharedPreferences sharedPreferences = getActivity().getSharedPreferences("searchdata", Context.MODE_PRIVATE);
//                                                               riderID = sharedPreferences.getString("ridercookieid", null);
//                                                                System.out.println("rider id is null so cookie is " + riderID);
//                                                            }

                                                            List<Object> cord =  (List<Object>) dataSnapshot.child("available").child(riderID).child("l").getValue();
                                                            if (cord != null && !isComing){
                                                                if (searchingString.get(0) != null){
                                                                    LatLng riderPosition = new LatLng(Double.parseDouble(cord.get(0).toString()), Double.parseDouble(cord.get(1).toString()));
                                                                    LatLng customerPickupPosition = new LatLng(Double.parseDouble(searchingString.get(0)), Double.parseDouble(searchingString.get(1)));
                                                                    map.clear();
                                                                    makeDirectionRequest(riderPosition, customerPickupPosition, R.drawable.icons_scooter, R.drawable.icons_to_map_pin);
                                                                    System.out.println("Rider map coordinate " + cord.toString() + " original " + dataSnapshot.child("available").child(riderID).child("l").getValue());
                                                                    return;
                                                                }

                                                            }
                                                            System.out.println("Rider map coordinate " + dataSnapshot.child("available").child(riderID).child("l").getValue());

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else if (dataSnapshot.getValue().equals("deliveryBegan")){
                                isComing = true;
                                riderdatabasereference.child(riderID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            List<Object> cord =  (List<Object>) dataSnapshot.child("available").child(riderID).child("l").getValue();
                                            if (cord != null && isComing){
                                                LatLng riderPosition = new LatLng(Double.parseDouble(cord.get(0).toString()), Double.parseDouble(cord.get(1).toString()));
                                                LatLng customerDelivertoPosition = new LatLng(Double.parseDouble(searchingString.get(2)), Double.parseDouble(searchingString.get(3)));
                                                map.clear();
                                                makeDirectionRequest(riderPosition, customerDelivertoPosition, R.drawable.icons_scooter, R.drawable.icons_home_address);
//                                                plotRiderCustomerLocationsOnMap(riderPosition, customerDelivertoPosition, "", "");
                                                System.out.println("Rider map coordinate " + cord.toString() + " original " + dataSnapshot.child("available").child(riderID).child("l").getValue());
                                                return;
                                            }
                                            System.out.println("Rider map coordinate " + dataSnapshot.child("available").child(riderID).child("l").getValue());

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void getrideridfromlocaldeviceforplotting() {

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


    }



    public String parseDirectionsURL(LatLng pickupDirection, LatLng deliverDirection){
        String origin = "origin=" + pickupDirection.latitude + "," + pickupDirection.longitude;
        String destination = "destination=" + deliverDirection.latitude + "," + deliverDirection.longitude;
        String parameter = origin + "&" + destination;
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameter + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    public void makeDirectionRequest(final LatLng pickupDirection, final LatLng deliverDirection, final int pickupresID, final int deliverresID){

        try{
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            String networkURL = "";
            if (getActivity() != null){
                networkURL = parseDirectionsURL(pickupDirection, deliverDirection);
            }

            Log.i("DeliPackMessage", networkURL);
            asyncHttpClient.get(networkURL, new JsonHttpResponseHandler(){


                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if (response.length() != 0){
                        Log.i("DeliPackMessage", response.toString());
                        map.addMarker(new MarkerOptions().position(pickupDirection).icon(BitmapDescriptorFactory.fromResource(pickupresID)));
                        map.addMarker(new MarkerOptions().position(deliverDirection).icon(BitmapDescriptorFactory.fromResource(deliverresID)));


                        try {
//                         if(response.has("overview_polyline")){
                            JSONObject object =  new JSONObject(response.getJSONArray("routes").get(0).toString());
//                               Log.i("DeliPack", "Decoded json object "  + object.getJSONObject("overview_polyline").getString("points"));

                            String points_link = object.getJSONObject("overview_polyline").getString("points");
                            List<LatLng> latLngs = decodePoly(points_link);
                            Log.i("DeliPackMessage", "Decoded " + latLngs.toString());
                            polylineOptions = new PolylineOptions().addAll(latLngs).color(Color.parseColor("#1565c0"));
                            map.addPolyline(polylineOptions);

//                         }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                    map.addPolyline(polylineOptions);
                        map.moveCamera(CameraUpdateFactory.newLatLng(deliverToLatLng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(11));
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.i("DeliPackMessage", errorResponse + "");
                    return;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.i("DeliPackMessage", responseString);
                    return;

                }
            });

        } catch (Exception e){
            return;
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
