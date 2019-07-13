package com.delipackport.delipackmobi;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.delipackport.delipackmobi.Model.ConfirmCashTransaction;

public class TransactionLoader extends AppCompatActivity {

    private ConfirmCashTransaction confirmCashTransaction;
    private String riderIDfound, customerID, cashoptionseleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_loader);
//        ConfirmCashTransaction.transactionActivity = this;
        confirmCashTransaction = new ConfirmCashTransaction(this);
        Intent transactionparams = getIntent();

        if(transactionparams != null){
            riderIDfound = transactionparams.getStringExtra("riderID");
            customerID = transactionparams.getStringExtra("customerID");
            cashoptionseleted = transactionparams.getStringExtra("cashoptionSelected");
            Log.i("DeliPackMessage", cashoptionseleted + " In intent");
            asyntaskcall(riderIDfound, customerID, cashoptionseleted);
        }


    }



    public void asyntaskcall(String riderID, String customerid, String cashoptionselect){

        try{
            if (confirmCashTransaction.getStatus() != AsyncTask.Status.RUNNING){
                confirmCashTransaction.cancel(true);
                confirmCashTransaction = new ConfirmCashTransaction(this);
                confirmCashTransaction.execute(riderID, customerid, cashoptionselect);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
