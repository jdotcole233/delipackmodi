package com.example.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class SearchResult extends AppCompatActivity {

    private Button confirmButton;
    private ImageButton cancelButton;
    private Spinner paymentMethod;
    private String payment_selection;


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

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.payment_option, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethod.setAdapter(arrayAdapter);

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
                finish();
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!payment_selection.isEmpty()){
                    if(payment_selection.equals("Mobile Money")){
                        startActivity(new Intent(SearchResult.this, MobileMoneyPayment.class));
//                        confirmcardview.setVisibility(View.INVISIBLE);
                    } else if (payment_selection.equals("Cash")){
                        Toast.makeText(SearchResult.this, "Cash Selected", Toast.LENGTH_LONG).show();
                    } else if (payment_selection.equals("Select payment option")){
                        Toast.makeText(SearchResult.this, "Select a payment option", Toast.LENGTH_LONG).show();
                        return;
                    }

                } else {
                    Toast.makeText(SearchResult.this, "Select a payment option", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });


    }
}