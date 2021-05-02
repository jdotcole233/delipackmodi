package com.delipackport.delipackmobi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DeliPackAlert {
    private Context context;
    private String alertTitle;
    private String message;
    private LayoutInflater layoutInflater;

    public DeliPackAlert(Context context, String title, String message){
            this.context = context;
            alertTitle = title;
            this.message = message;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void showDeliPackAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = layoutInflater.inflate(R.layout.delipack_custom_alert, null);
        TextView titletextView = view.findViewById(R.id.alertTitle);
        TextView messagetextView = view.findViewById(R.id.alertMessage);
        titletextView.setText(alertTitle);
        messagetextView.setText(message);

        builder.setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create();
        builder.show();
    }

}
