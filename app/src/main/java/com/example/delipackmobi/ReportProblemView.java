package com.example.delipackmobi;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static com.example.delipackmobi.CustomerContract.CustomerContract.CUSTOMERREPORT_URL;

public class ReportProblemView extends AppCompatActivity {

    private Spinner problemtype;
    private EditText problemmessage;
    private ImageView report_close;
    private TextView problemdesc;
    private Button sendreportbtn;
    private String reportcatselected, customerID, manufacturer, reportmessage;
    private Integer customer_android_version;
    private CustomerContract customerContract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem_view);

        problemtype = findViewById(R.id.report_choice);
        problemmessage = findViewById(R.id.reportmessage);
        report_close = findViewById(R.id.report_close);
        problemdesc = findViewById(R.id.problemdesc);
        sendreportbtn = findViewById(R.id.sendcustomerreport);
        customerContract = new CustomerContract(this);

        ArrayAdapter<CharSequence> reportchoices = ArrayAdapter.createFromResource(this, R.array.report_problem, R.layout.dropdown_selection_color);
        reportchoices.setDropDownViewResource(R.layout.selection_check_color);
        problemtype.setAdapter(reportchoices);

        for (Cookie cookie: customerContract.getPersistentCookieStore().getCookies()){
            if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());
                    customerID = jsonObject.getString("customer_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        manufacturer = Build.MANUFACTURER;
        customer_android_version = Build.VERSION.SDK_INT;



        report_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        problemtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reportcatselected =  parent.getItemAtPosition(position).toString();
                if(reportcatselected.equals("Something isn't working")){
                    String message = "SOMETHING ISN'T WORKING\n\nBriefly explain what happened. How can we reproduce the issue";
                    problemdesc.setText(message);
                } else if (reportcatselected.equals("General Feedback")){
                    String message = "GENERAL FEEDBACK\n\nBriefly explain what you love, or what could improve";
                    problemdesc.setText(message);
                } else if (reportcatselected.equals("Select report class")){
                    String message = "YOUR FEEDBACK IS APPRECIATED.\n\nPlease select either {SOMETHING ISN'T WORKING} or {GENERAL FEEDBACK} ";
                    problemdesc.setText(message);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String message = "Your feedback is appreciated";
                problemdesc.setText(message);

            }
        });



        sendreportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reportcatselected.equals("Select report class")){
                    new DeliPackAlert(ReportProblemView.this, "Report selection error", "Please select either {Something isn't working} or {General feedback}").showDeliPackAlert();
                    return;
                }

                if (problemmessage.getText().length() == 0){
                    new DeliPackAlert(ReportProblemView.this, "Report message error", "Please provide a valid supporting statement").showDeliPackAlert();
                    return;
                }


                makereportrequest(reportcatselected, problemmessage.getText().toString(), manufacturer, customer_android_version, customerID);


            }
        });

    }


    public void makereportrequest(String category,String message, String manufact, Integer version, String custID){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("report_type", category);
        requestParams.put("report_message", message);
        requestParams.put("android_version", version);
        requestParams.put("manufacturer", manufact);
        requestParams.put("customer_id", custID);

        asyncHttpClient.post(CUSTOMERREPORT_URL, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getString("Done").equals("Successful")){
                        String message = "Thanks for your concern. Your report has been received successfully";
                        new DeliPackAlert(ReportProblemView.this, "Report success", message).showDeliPackAlert();
                        problemmessage.setText("");
                        problemmessage.setHint("Enter your message");


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("DeliPackMessage", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("DeliPackMessage", responseString);

            }
        });
    }
}
