package com.example.delipackmobi.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.example.delipackmobi.CustomerContract.UpdateDownloadText;
import com.example.delipackmobi.CustomerContract.UpdateHistory;
import com.example.delipackmobi.DeliPackEventLoader;
import com.example.delipackmobi.R;
import com.example.delipackmobi.SearchResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.net.URL;

import cz.msebera.android.httpclient.Header;

import static com.example.delipackmobi.CustomerContract.CustomerContract.GETCOMPANYDATA_URL;

public class ReadRiderInformationBackAsynTask extends AsyncTask<String, Void, Boolean> {

    AsyncHttpClient getCompanyInformation;
    RequestParams requestParams;
    Context context;
    Boolean isRead = false;
    TextView downloaddisp;
    CustomerContract customerContract;


    public ReadRiderInformationBackAsynTask(Context context){
        this.context = context;
        getCompanyInformation = new AsyncHttpClient();
        requestParams = new RequestParams();
        customerContract = new CustomerContract(context);
//        downloaddisp =  DeliPackEventLoader.searchRiderActivity.findViewById(R.id.downloadtext);
    }


    @Override
    protected Boolean doInBackground(final String... strings) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (isCancelled()){
                    return;
                }

                requestParams.add("rider_id", strings[0]);

                getCompanyInformation.post(GETCOMPANYDATA_URL, requestParams, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        String responsefromserver = response.toString();

                        Log.i("DeliPackMessage", "Before if statement" + response.toString());
//                        if(response.length() != 0){
                            Log.i("DeliPackMessage", "In if statement ");
                        customerContract.setBasicCookies("company_details", response.toString(),2, "/");
                        Intent sendRiderID = new Intent(context, SearchResult.class);
                        context.startActivity(sendRiderID);
                            isRead = true;
//                        } else {
//                            Log.i("DeliPackMessage", response.toString());
//                            isRead = false;
//                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        System.out.println("On failure in Read Rider information1 " + errorResponse);
                        isRead = false;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        System.out.println("On failure in Read Rider information2 "  + throwable.getMessage() + " " + responseString + " " + statusCode);
                        isRead = false;
                    }
                });
            }
        };

        handler.post(runnable);


        return isRead;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        downloaddisp.setText("Gathering rider data");
        System.out.println("Gathering rider data");
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        System.out.println("thank you outside of if " + aBoolean);
        if (!aBoolean){
            System.out.println("Thank you for your patience");
//            downloaddisp.setText("Thank you for your patience");


        }
    }
}
