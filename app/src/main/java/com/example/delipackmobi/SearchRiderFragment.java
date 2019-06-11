package com.example.delipackmobi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
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

import java.util.ArrayList;
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

    private final String LOG_MSG = "DeliPackMessage";
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
    public static  Double proximity;
    Boolean riderFound;
    String riderID;
    public static Activity delipackEventloader;
    private AsyncHttpClient getCompanyInformation;
    private float [] distdiff;
    private String deliveryLocatioName, pickupLocationName;
    private Boolean isDismissed;
    private List<String> searchingString;
    public static GeoQuery geoQuery;
    Intent sendRiderID;



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

                Log.i("DeliPackMessage", "Before if statement" + response.toString());
                if(response.length() != 0){
                    Log.i("DeliPackMessage", "In if statement ");
                    new CustomerContract(getActivity())
                            .setBasicCookies("company_details", response.toString(),2, "/");
                    return;
                } else {
                    Log.i("DeliPackMessage", response.toString());
                    return;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                System.out.println(errorResponse);
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println(throwable.getMessage());
                return;
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
        if(mapView != null){ mapView.onResume();}

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(mapView != null){ mapView.onResume();}


        if (savedInstanceState != null){
//            pickUpFromLatLng = new LatLng(savedInstanceState.getDouble("pickuplat"), savedInstanceState.getDouble("pickuplong"));
//            deliverToLatLng = new LatLng(savedInstanceState.getDouble("delivertolat"), savedInstanceState.getDouble("delivertolong"));
            System.out.println("Map in Check back");
            Log.i(LOG_MSG, "Map in check back");
        }else {
            Log.i(LOG_MSG, " Else part of on create called");
//            System.out.println("Else part on on Create ");
        }


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


        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide);
        animationout = AnimationUtils.loadAnimation(getContext(), R.anim.slideout);
        animationmove = AnimationUtils.loadAnimation(getContext(), R.anim.move);
        DeliPackEventLoader.searchRiderActivity = getActivity();
        TripCompletedRatingMessage.tripCompletedSearchActivity = getActivity();
        RateCompanyRider.searchriderfragment = getActivity();

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
        distdiff = new float[1];
        isDismissed = false;

        if (savedInstanceState != null){
            Log.i(LOG_MSG, "Map in Check on activity created " + savedInstanceState);
            System.out.println("Map in Check back on activity created" + savedInstanceState);
        }else {
            Log.i(LOG_MSG, "Else part of on Activity create "+ savedInstanceState);
            System.out.println("Else part on on Activity Create " + savedInstanceState);
        }



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










//
//        if (savedInstanceState != null){
//            System.out.println("saved instant state not null");
//            pickUpFromLatLng = new LatLng(savedInstanceState.getDouble("pickuplat"), savedInstanceState.getDouble("pickuplong"));
//            deliverToLatLng = new LatLng(savedInstanceState.getDouble("delivertolat"), savedInstanceState.getDouble("delivertolong"));
//            riderID = savedInstanceState.getString("riderID");
//            System.out.println("In Saved state instance");
//        } else {
//            System.out.println("In Saved instant state out");
//        }



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


                if (pickUpDeliveryModel != null) {
                    if (pickUpDeliveryModel.getFromInformation().size() == 0) {
                        new DeliPackAlert(getActivity(), "Location Fields", "Fill out pick up locations").showDeliPackAlert();
                        return;
                    } else if (pickUpDeliveryModel.getDeliveryInformation().size() == 0) {
                        new DeliPackAlert(getActivity(), "Location Fields", "Delivery information cannot be empty").showDeliPackAlert();
                        return;
                    } else {



                        if (customerContract.getPersistentCookieStore().getCookies().contains("searchdata")){
                            autocompleteFragment.setText(searchingString.get(4));
                            autocompleteFragment1.setText(searchingString.get(5));
                            Location.distanceBetween(Double.parseDouble(searchingString.get(0)),Double.parseDouble(searchingString.get(1)), Double.parseDouble(searchingString.get(2)),Double.parseDouble(searchingString.get(3)), distdiff);
                            getSearchDetails(searchingString.get(4), searchingString.get(5), getPriceAndCommission((double)Math.round(distdiff[0]/1000), 0.05,4.0)[0],
                                    getPriceAndCommission((double)Math.round(distdiff[0]/1000), 0.05, 4.0)[1]);

                        } else {
                            System.out.println(pickUpDeliveryModel.getFromInformation().size());
                            Location.distanceBetween(pickUpFromLatLng.latitude,pickUpFromLatLng.longitude, deliverToLatLng.latitude,deliverToLatLng.longitude, distdiff);
                            getSearchDetails(pickUpDeliveryModel.getFromInformation().get("pickup"), pickUpDeliveryModel.getDeliveryInformation().get("delivery"), getPriceAndCommission((double)Math.round(distdiff[0]/1000),0.05,4.0)[0], getPriceAndCommission((double)Math.round(distdiff[0]/1000),0.05,4.0)[1]);
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
                    map.addMarker(new MarkerOptions().position(pickUpFromLatLng).title("Pick up").icon(BitmapDescriptorFactory.fromResource(R.drawable.fromdot)));


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
                        PolylineOptions polylineOptions = new PolylineOptions().add(pickUpFromLatLng)
                                .add(deliverToLatLng).color(R.drawable.distance_color_display);
                        map.addPolyline(polylineOptions);
                        map.addMarker(new MarkerOptions()
                                .position(deliverToLatLng)
                                .title("Deliver to: " + place.getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.todot))

                        );
                        map.moveCamera(CameraUpdateFactory.newLatLng(deliverToLatLng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(11));
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


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("CustomerRiderRequest")
                .child(customer_id).child("delivered");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if (dataSnapshot.getValue().equals("true")){
                                    Intent rateIntent = new Intent(getActivity(), RateCompanyRider.class);
                                    startActivity(rateIntent);
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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.fromdot)));

            PolylineOptions polylineOptions = new PolylineOptions().add(pickupLocation)
                    .add(deliveryLocation).color(R.drawable.action_item_background_selector);
            map.addPolyline(polylineOptions);
            map.addMarker(new MarkerOptions()
                    .position(deliveryLocation)
                    .title(deliveryLabel)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.todot)));

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
        if (!customer_id.isEmpty()){
            DatabaseReference riderresponse = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest").child(customer_id).child("rideraccepted");
            riderresponse.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){

                        if (dataSnapshot.getValue().equals("true")){

//                            searchriderwelcomecard.setVisibility(View.INVISIBLE);
//                            searchRiderCardView.setVisibility(View.INVISIBLE);

                            if(isDismissed == true){
                                SearchRiderFragment.delipackEventloader.finish();
                                isDismissed = false;
                            }
                            readCompanyInformation(riderID);
                            sendRiderID = new Intent(getActivity(), SearchResult.class);
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

        if(!picklat.isEmpty() && !picklong.isEmpty()){

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
                        System.out.println("found rider");

                        DatabaseReference databaserider = database.getReference().child("RiderFoundForCustomer").child(riderID);
                        databaserider.child("customer_id").setValue(customer_id);
                        databaserider.child("assigned").setValue("true");
                        Map<String, Object> dellocation = new HashMap<>();


                        if (customerContract.getPersistentCookieStore().getCookies().contains("searchdata")){
                            dellocation.put("latitude", searchingString.get(2));
                            dellocation.put("longitude", searchingString.get(3));
                            dellocation.put("deliverylocationname", deliveryLocatioName);
                            dellocation.put("pickuplocationname", pickupLocationName);
                            dellocation.put("customerPhoneNumber", customer_phone_number);
                            dellocation.put("customerName",  customer_first_name + " " + customer_last_name);
                            databasecustomer.child("deliverlatlong").updateChildren(dellocation);

                        } else {
                            dellocation.put("latitude", deliverToLatLng.latitude);
                            dellocation.put("longitude", deliverToLatLng.longitude);
                            dellocation.put("deliverylocationname", deliveryLocatioName);
                            dellocation.put("pickuplocationname", pickupLocationName);
                            dellocation.put("customerPhoneNumber", customer_phone_number);
                            dellocation.put("customerName",  customer_first_name + " " + customer_last_name);
                            databasecustomer.child("deliverlatlong").updateChildren(dellocation);
                        }





                        isDismissed = true;
                        System.out.println("key has entered searching for rider");
                        proximity = 0.1;
                        return;

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
                    System.out.println("On key existed while searching for rider");
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    System.out.println("On key moved while searching for rider");
                }

                @Override
                public void onGeoQueryReady() {
                    if(!riderFound){
                        proximity += 0.1;
                        System.out.println("searching " + proximity );
                        if (proximity >= 20){
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

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
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

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onResume();
        Log.i(LOG_MSG, "On pause called");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();



        System.out.println("On destroyed called");
        Log.i(LOG_MSG, "On destroyed called");
        if(pickUpFromLatLng != null && deliverToLatLng != null){
            SharedPreferences.Editor saveSearchData = getActivity().getSharedPreferences("searchingdata", Context.MODE_PRIVATE).edit();
            saveSearchData.putString("pickuplat", Double.toString(pickUpFromLatLng.latitude));
            saveSearchData.putString("pickuplong", Double.toString(pickUpFromLatLng.longitude));
            saveSearchData.putString("delivertolat", Double.toString(deliverToLatLng.latitude));
            saveSearchData.putString("delivertolong", Double.toString(deliverToLatLng.longitude));
            saveSearchData.putString("pickupname", pickupLocationName);
            saveSearchData.putString("deliveryname", deliveryLocatioName);
            saveSearchData.apply();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("On destroyed view called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LOG_MSG, "On detached called");
    }
}
