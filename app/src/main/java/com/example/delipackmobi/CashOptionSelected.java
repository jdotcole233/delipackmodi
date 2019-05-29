package com.example.delipackmobi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CashOptionSelected extends AppCompatActivity {

    private Spinner cashpaymentoption;
    private Button cash_btn;
    private ImageButton cancelcashbtn;
    private String cashoptionselected;
    String riderIDFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_option_selected);
        cashpaymentoption = findViewById(R.id.cash_choice);
        cash_btn = findViewById(R.id.cash_payment_button);
        cancelcashbtn = findViewById(R.id.cancelcashoption);
        Intent getRiderID = getIntent();
        riderIDFound = getRiderID.getStringExtra("bikerID");


        ArrayAdapter<CharSequence> cashspinner = ArrayAdapter.createFromResource(getApplicationContext(), R.array.cash_payment_option, android.R.layout.simple_spinner_item);
        cashspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cashpaymentoption.setAdapter(cashspinner);


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
                if (cashoptionselected.equals("Pay at pick up")){
                    updatePaymentValueForRider(riderIDFound, CashOptionSelected.this, PackageInProgress.class);

                } else if (cashoptionselected.equals("Pay on delivery")){
                    updatePaymentValueForRider(riderIDFound, CashOptionSelected.this, PackageInProgress.class);

                }else {
                    return;
                }
//                new DeliPackAlert(CashOptionSelected.this, "Selection", cashoptionselected).showDeliPackAlert();
            }
        });



        cancelcashbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void updatePaymentValueForRider(String riderID, Context context, Class switchto){
        DatabaseReference confirmpayment = FirebaseDatabase.getInstance().getReference()
                .child("RiderFoundForCustomer")
                .child(riderID)
                .child("paymentapproved");
        confirmpayment.setValue("true");
        Intent changeActivitiies = new Intent(context, switchto);
        startActivity(changeActivitiies);
    }
}
