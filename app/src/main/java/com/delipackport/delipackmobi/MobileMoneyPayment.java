package com.delipackport.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.delipackport.delipackmobi.R;

public class MobileMoneyPayment extends AppCompatActivity {

    private ImageButton cancelButton;
    private Button mobilemoneypaybtn;
    private EditText mobilemoneynumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobilemoney_payment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mobilemoneynumber = findViewById(R.id.mobilemoneynumber);
        mobilemoneypaybtn = findViewById(R.id.mobilemoneypaybtn);

        int width = (int) (displayMetrics.widthPixels);
        int height = (int) (displayMetrics.heightPixels);

        getWindow().setLayout(width, height);
        cancelButton = findViewById(R.id.cancelsearchresult);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mobilemoneypaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mobilemoneynumber.getText().toString();

                if(phoneNumber.isEmpty()){
                    String message = "Enter a valid mobile money phone number to complete transaction";
                    new DeliPackAlert(MobileMoneyPayment.this, "Phone number needed", message).showDeliPackAlert();
                    return;
                } else if(phoneNumber.length() < 10 || phoneNumber.length() > 10){
                    String message = "Enter a valid mobile money phone number to complete transaction";
                    new DeliPackAlert(MobileMoneyPayment.this, "Wrong number format", message).showDeliPackAlert();
                    return;

                } else {
                    Intent getRiderID = getIntent();
                    System.out.println(getRiderID.getStringExtra("riderID"));
                }
            }
        });



    }
}
