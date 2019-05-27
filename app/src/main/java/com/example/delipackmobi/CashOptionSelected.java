package com.example.delipackmobi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

public class CashOptionSelected extends AppCompatActivity {

    private Spinner cashpaymentoption;
    private Button cash_btn;
    private ImageButton cancelcashbtn;
    private String cashoptionselected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_option_selected);
        cashpaymentoption = findViewById(R.id.cash_choice);
        cash_btn = findViewById(R.id.cash_payment_button);
        cancelcashbtn = findViewById(R.id.cancelcashoption);


        ArrayAdapter<CharSequence> cashspinner = ArrayAdapter.createFromResource(this, R.array.cash_payment_option, android.R.layout.simple_spinner_item);
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
                new DeliPackAlert(CashOptionSelected.this, "Selection", cashoptionselected).showDeliPackAlert();
            }
        });



        cancelcashbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
