package com.example.delipackmobi;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CustomerSupportView extends AppCompatActivity {

    private ImageView supportclose, facebookaccess, twitteraccess, instagramaccess;
    private Button requestCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support_view);
        supportclose = findViewById(R.id.support_close);
        facebookaccess = findViewById(R.id.facebook_app);
        twitteraccess = findViewById(R.id.twitter_app);
        instagramaccess = findViewById(R.id.instagram_app);
        requestCallback = findViewById(R.id.requestcallback);


        supportclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        facebookaccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appstring = "fb://profile/delipack";
                String webstring = "https://www.facebook.com/delipack";
                String apppackage = "com.facebook.katana";
                openSocialMediaAccount(CustomerSupportView.this, appstring, webstring, apppackage);
            }
        });

        twitteraccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appstring = "twitter://user?screen_name=delivpack";
                String webstring = "https://twitter.com/#!/delivpack";
                String apppackage = "com.twitter.android";
                openSocialMediaAccount(CustomerSupportView.this, appstring, webstring, apppackage);
            }
        });

        instagramaccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appstring = "http://instagram.com/_u/delivpack";
                String webstring = "http://instagram.com/delivpack";
                String apppackage = "com.instagram.android";
                openSocialMediaAccount(CustomerSupportView.this, appstring, webstring, apppackage);
            }
        });

        requestCallback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requestCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0503848404"));
                startActivity(requestCall);
            }
        });
    }



    public void openSocialMediaAccount(Context context, String socialapplink, String socialweblink,String socialpackage){

        Uri socialuri = Uri.parse(socialapplink);
        Intent socialIntent = new Intent(Intent.ACTION_VIEW, socialuri);
        socialIntent.setPackage(socialpackage);

        try{
            startActivity(socialIntent);
        }catch (ActivityNotFoundException ex){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(socialweblink)));
        }

//        context.getPackageManager().getPackageInfo()

    }

}
