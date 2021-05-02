package com.delipackport.delipackmobi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class HistoryDetails extends AppCompatActivity {

    private TextView pickup, deliver, company_name, rider_name, biker_number;
    private TextView created_date, transaction_number, status, payment_type;
    private TextView charges;
    private ImageButton close_history;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_details);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

//        int windowwidth =  (int)(displayMetrics.widthPixels * 0.9);
        int windowheight = (int)(displayMetrics.heightPixels * 0.9);

        getWindow().setLayout(displayMetrics.widthPixels ,displayMetrics.heightPixels);
        pickup = findViewById(R.id.history_pickup);
        deliver = findViewById(R.id.history_deliver);
        company_name = findViewById(R.id.history_company_name);
        rider_name = findViewById(R.id.history_rider_name);
        biker_number = findViewById(R.id.history_bike_reg);
        created_date = findViewById(R.id.history_date);
        transaction_number = findViewById(R.id.history_trans_number);
        status = findViewById(R.id.history_status);
        payment_type = findViewById(R.id.history_payment_type);
        charges = findViewById(R.id.history_total_cost);
        close_history = findViewById(R.id.history_close);

        Intent historyIntent = getIntent();
        pickup.setText(historyIntent.getStringExtra("pick_up"));
        deliver.setText(historyIntent.getStringExtra("deliver_to"));
        company_name.setText(historyIntent.getStringExtra("company_name"));
        rider_name.setText(historyIntent.getStringExtra("rider_name"));
        biker_number.setText(historyIntent.getStringExtra("biker_number"));
        created_date.setText(historyIntent.getStringExtra("created_at"));
        transaction_number.setText(historyIntent.getStringExtra("transaction_number"));
        status.setText(historyIntent.getStringExtra("transaction_status"));
        payment_type.setText(historyIntent.getStringExtra("payment_type"));
        charges.setText("GHC " + historyIntent.getStringExtra("charges"));

        close_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
