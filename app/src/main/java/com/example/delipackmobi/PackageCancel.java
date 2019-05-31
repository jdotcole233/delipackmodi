package com.example.delipackmobi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class PackageCancel extends AppCompatActivity {
    private Button canceltransaction;
    private Button abortcancelingtransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_cancel);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout((int)(displayMetrics.widthPixels * 0.95), (int)(displayMetrics.heightPixels * 0.4));
        canceltransaction = findViewById(R.id.yescanceltransaction);
        abortcancelingtransaction = findViewById(R.id.abortcancel);

        abortcancelingtransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
