package com.delipackport.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class EditCustomerProfile extends AppCompatActivity {

    private ImageView close_profile;
    private EditText customerfirstname, customerlastname, customeremail, customerphonnumber;
    private Button updateprofile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        close_profile = findViewById(R.id.editprofile_close);
        customerfirstname = findViewById(R.id.customerfirstname);
        customerlastname = findViewById(R.id.customerlastname);
        customeremail = findViewById(R.id.customeremail);
        customerphonnumber = findViewById(R.id.customerphonenumber);
        updateprofile = findViewById(R.id.updateprofile);

        Intent customerinformationintent = getIntent();

        customerfirstname.setText(customerinformationintent.getStringExtra("first_name"));
        customerlastname.setText(customerinformationintent.getStringExtra("last_name"));
        customeremail.setText(customerinformationintent.getStringExtra("email"));
        customerphonnumber.setText(customerinformationintent.getStringExtra("phone_number"));
        customerphonnumber.setFocusable(false);

        close_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Please try again later.";
                new DeliPackAlert(EditCustomerProfile.this, "Update profile", message).showDeliPackAlert();
                return;
            }
        });
    }
}
