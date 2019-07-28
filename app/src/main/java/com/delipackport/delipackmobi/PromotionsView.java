package com.delipackport.delipackmobi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class PromotionsView extends AppCompatActivity {

    private ImageView close_promoview;
    private Button promotionButton;
    private EditText promotTextEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions_view);

        close_promoview = findViewById(R.id.editpromo_close);
        promotionButton = findViewById(R.id.promotionButton);
        promotTextEdit = findViewById(R.id.promotTextEdit);

        promotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String promotionText = promotTextEdit.getText().toString();
                String message;

                if (promotionText.isEmpty()){
                    message = "Please enter a valid promotion code";
                } else {
                    message = "Sorry, the promotion code entered is invalid at this time, check the code and try again";
                }

                new DeliPackAlert(PromotionsView.this, "Promotion!!", message).showDeliPackAlert();
                return;
            }
        });

        close_promoview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
