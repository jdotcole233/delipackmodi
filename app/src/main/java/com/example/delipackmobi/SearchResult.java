package com.example.delipackmobi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.cookie.Cookie;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class SearchResult extends AppCompatActivity {

    private Button confirmButton;
    private ImageButton cancelButton;
    private Spinner paymentMethod;
    private String payment_selection;
    private String riderID, customerID;
    private CustomerContract customerContract;
    private TextView companyname, bikeregistration, bikername, transactionprice;
    private DatabaseReference updateriderdata;
    public  static Activity sc;
    public static int countDownTime;
    private CountDownTimer paymentCountDown;
    private DatabaseReference customerRequest, riderfoundforcustomer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_search_result);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout(displayMetrics.widthPixels, displayMetrics.heightPixels);

        confirmButton = findViewById(R.id.make_payment);
        cancelButton = findViewById(R.id.cancelsearchresult);
        paymentMethod = findViewById(R.id.payment_choice);
        customerContract = new CustomerContract(this);
        sc = this;
        countDownTime = 90000;

        companyname = findViewById(R.id.companyname);
        bikeregistration = findViewById(R.id.riderbikeregistration);
        bikername = findViewById(R.id.companyridername);
        transactionprice = findViewById(R.id.transactionprice);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.payment_option, R.layout.dropdown_selection_color);
        arrayAdapter.setDropDownViewResource(R.layout.selection_check_color);
        paymentMethod.setAdapter(arrayAdapter);

//        Intent riderIDretrieve = getIntent();
//        riderID = riderIDretrieve.getStringExtra("riderID");

        for(Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
            if(cookie.getName().equals("company_details")){
                try {
                    JSONObject displayCompanyData = new JSONObject(cookie.getValue());

                    riderID = displayCompanyData.getString("company_rider_id");

                    companyname.setText("Company: " + displayCompanyData.getString("company_name"));
                    bikeregistration.setText("Reg number:" + displayCompanyData.getString("registered_number"));
                    bikername.setText("Rider name: " + displayCompanyData.getString("first_name") + " " + displayCompanyData.getString("last_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (cookie.getName().equals("searchdata")){
                try {
                    JSONObject display_price = new JSONObject(cookie.getValue());
                    Double price_calc =  display_price.getDouble("delivery_charge") + display_price.getDouble("commission_charge");
                            transactionprice.setText(price_calc.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    customerID = jsonObject.getString("customer_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }




        paymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                payment_selection = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchResultIntent = new Intent(getApplicationContext(),PackageCancel.class);
                startActivity(searchResultIntent);
                finish();

            }
        });




        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!payment_selection.isEmpty()){
                    paymentCountDown.cancel();
                    confirmButton.setText("Confirm payment");

                    if(payment_selection.equals("Mobile Money")){
//                        startActivity(new Intent(SearchResult.this, MobileMoneyPayment.class));
//                        confirmcardview.setVisibility(View.INVISIBLE);
                        new DeliPackAlert(SearchResult.this, "Payment option", "Coming soon").showDeliPackAlert();

                    } else if (payment_selection.equals("Cash")){
//                        Toast.makeText(SearchResult.this, "Cash Selected", Toast.LENGTH_LONG).show();
//                        new DeliPackAlert(SearchResult.this, "Payment option", "Cash payment selected").showDeliPackAlert();
                        Intent sendRiderID = new Intent(SearchResult.this, CashOptionSelected.class);
                        sendRiderID.putExtra("bikerID", riderID);
                        getPaymentType(payment_selection);
                        startActivity(sendRiderID);

                    } else if (payment_selection.equals("Select payment option")){
                        new DeliPackAlert(SearchResult.this, "Payment option", "Select a payment option").showDeliPackAlert();
//                        Toast.makeText(SearchResult.this, "Select a payment option", Toast.LENGTH_LONG).show();
                        return;
                    }

                } else {
                    new DeliPackAlert(SearchResult.this, "Payment option", "Select a payment option").showDeliPackAlert();
//                    Toast.makeText(SearchResult.this, "Select a payment option", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        paymentCountDown =  new CountDownTimer(60000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                confirmButton.setText("Confirm payment in " + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                finish();
                customerRequest = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest");
                customerRequest.child(customerID).removeValue();
                riderfoundforcustomer = FirebaseDatabase.getInstance().getReference().child("RiderFoundForCustomer");
                riderfoundforcustomer.child(riderID).child("assigned").setValue("not assigned");

            }
        };
        paymentCountDown.start();


    }

    //Query database for rider information display function

    //Calculate delivery charges function



    public void getPaymentType(String paymentSelected){
        if (!paymentSelected.isEmpty()){
            Gson searchConvert = new Gson();
            HashMap<String, String> search = new HashMap<>();
            search.put("paymentType", paymentSelected);
            String convertedJSON = searchConvert.toJson(search);
            customerContract.setBasicCookies("paymentType", convertedJSON, 4, "/");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("On stop in search result called");

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("On resume in search result called");

    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("On start in search result called");
        paymentCountDown.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("On destroy in search result called");
    }
}
