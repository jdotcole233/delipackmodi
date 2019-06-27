package com.example.delipackmobi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.example.delipackmobi.Model.ConfirmCashTransaction;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static com.example.delipackmobi.CustomerContract.CustomerContract.UPDATEDTRANSACTION_URL;

public class CashOptionSelected extends AppCompatActivity {

    private Spinner cashpaymentoption;
    private Button cash_btn;
    private ImageButton cancelcashbtn;
    private String cashoptionselected;
    private String riderIDFound, customerID;
    private CustomerContract customerContract;
    private CountDownTimer countDownTimer;
    private DatabaseReference customerRequest, riderfoundforcustomer;
    public static Activity cashoptionselectedActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_option_selected);
        cashpaymentoption = findViewById(R.id.cash_choice);
        cash_btn = findViewById(R.id.cash_payment_button);
        cancelcashbtn = findViewById(R.id.cancelcashoption);
        Intent getRiderID = getIntent();
        riderIDFound = getRiderID.getStringExtra("bikerID");
        customerContract = new CustomerContract(this);
        cashoptionselectedActivity = this;


        ArrayAdapter<CharSequence> cashspinner = ArrayAdapter.createFromResource(getApplicationContext(), R.array.cash_payment_option, R.layout.dropdown_selection_color);
        cashspinner.setDropDownViewResource(R.layout.selection_check_color);
        cashpaymentoption.setAdapter(cashspinner);

        System.out.println("Customer cookies " + customerContract.getPersistentCookieStore().getCookies());


        for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
            if(cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject customerIDinfo = new JSONObject(cookie.getValue());
                    customerID = customerIDinfo.getString("customer_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (cookie.getName().equals("company_details")){
                try {
                    JSONObject riderJSONinfo = new JSONObject(cookie.getValue());
                    riderIDFound = riderJSONinfo.getString("company_rider_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }




        cashpaymentoption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cashoptionselected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        cash_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                cash_btn.setText("Confirm pick up");
                if (cashoptionselected.equals("Pay at pick up")){
                    SearchResult.sc.finish();
                    if(riderIDFound != null){
                        Intent transactionLoader = new Intent(CashOptionSelected.this, TransactionLoader.class);
                        transactionLoader.putExtra("riderID", riderIDFound);
                        transactionLoader.putExtra("customerID", customerID);
                        transactionLoader.putExtra("cashoptionSelected", cashoptionselected);
                        startActivity(transactionLoader);
                    }
                } else if (cashoptionselected.equals("Pay on delivery")){
                    SearchResult.sc.finish();
                    if(riderIDFound != null){
                        Intent transactionLoader = new Intent(CashOptionSelected.this, TransactionLoader.class);
                        transactionLoader.putExtra("riderID", riderIDFound);
                        transactionLoader.putExtra("customerID", customerID);
                        transactionLoader.putExtra("cashoptionSelected", cashoptionselected);
                        startActivity(transactionLoader);

                    }
                }else {
                    return;
                }
            }
        });

        cancelcashbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                cash_btn.setText("Confirm pick up in " + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                SearchResult.sc.finish();
                finish();
                customerRequest = FirebaseDatabase.getInstance().getReference().child("CustomerRiderRequest");
                customerRequest.child(customerID).removeValue();
                riderfoundforcustomer = FirebaseDatabase.getInstance().getReference().child("RiderFoundForCustomer");
                riderfoundforcustomer.child(riderIDFound).child("assigned").setValue("not assigned");

            }
        };

        countDownTimer.start();
    }


    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("On stop cash option in  called");
        countDownTimer.cancel();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("On destroy cash option in  called");

    }



}
