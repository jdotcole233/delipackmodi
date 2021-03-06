package com.delipackport.delipackmobi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.delipackport.delipackmobi.CustomerContract.NetworkAllowanceCheck;

public class NetworkConnectionView extends AppCompatActivity {

    private Button networkConnection;
    private NetworkAllowanceCheck networkAllowanceCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_connection_view);

        networkConnection = findViewById(R.id.checkconnectionbutton);
        networkAllowanceCheck = new NetworkAllowanceCheck(this);

        networkConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkAllowanceCheck.enable(NetworkConnectionView.this);

            }
        });
    }


}
