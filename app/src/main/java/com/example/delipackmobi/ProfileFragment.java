package com.example.delipackmobi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.delipackmobi.CustomerContract.CustomerContract;
import com.example.delipackmobi.CustomerContract.UpdateHistory;
import com.example.delipackmobi.Model.CustomerHistoryAdapter;
import com.example.delipackmobi.Model.CustomerHistoryModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;


public class ProfileFragment extends Fragment implements UpdateHistory {

    private ImageView editButton;
    private Button tellfriendbtn, promotionsbtn, supportbtn,reportproblem_btn;
    private TextView customerBigName, customerBigEmail, customerSmallNumber, customerSmallEmail, customerOrderCount;
    private CustomerContract customerContract;
    private String customerfirstname, customerlastname, customeremail, customerphonenumber = "";
    private List<CustomerHistoryModel> customerHistoryModel;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile2, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editButton = getActivity().findViewById(R.id.profile_edit_button);
        tellfriendbtn = getActivity().findViewById(R.id.tell_friend_btn);
        promotionsbtn = getActivity().findViewById(R.id.promotions_btn);
        supportbtn = getActivity().findViewById(R.id.support_btn);
        reportproblem_btn = getActivity().findViewById(R.id.reportproblem_btn);

        customerBigName  = getActivity().findViewById(R.id.customerbigname);
        customerBigEmail = getActivity().findViewById(R.id.customerbigemail);
        customerSmallNumber = getActivity().findViewById(R.id.customersmallnumber);
        customerSmallEmail = getActivity().findViewById(R.id.customersmallemail);
        customerOrderCount = getActivity().findViewById(R.id.customerOrderTotal);

        if (customerHistoryModel.size() != 0){
            customerOrderCount.setText(customerHistoryModel.size() + " ");
        }

        customerContract = new CustomerContract(getActivity());

        for (Cookie cookie : customerContract.getPersistentCookieStore().getCookies()){
            if (cookie.getName().equals("customerInfomation")){
                try {
                    JSONObject jsonObject = new JSONObject(cookie.getValue());


                    customerfirstname =  jsonObject.getString("first_name");
                    customerlastname = jsonObject.getString("last_name");
                    customerphonenumber = jsonObject.getString("phone_number");
                    customeremail = jsonObject.getString("email");

//
//                    if (jsonObject.getString("email").isEmpty()){
//                        customerBigEmail.setText("N/A");
//                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        if (customeremail == null){
            customeremail = "N/A";
        }

        customerBigName.setText(customerfirstname + " " + customerlastname);
        customerSmallNumber.setText(customerphonenumber);
        customerSmallEmail.setText(customeremail);
        customerBigEmail.setText(customeremail);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editprofileintent = new Intent(getActivity(), EditCustomerProfile.class);
                editprofileintent.putExtra("first_name", customerfirstname);
                editprofileintent.putExtra("last_name", customerlastname);
                editprofileintent.putExtra("email", customeremail);
                editprofileintent.putExtra("phone_number", customerphonenumber);
                startActivity(editprofileintent);
            }
        });


        tellfriendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Charley tell a friend about delipack";
                Intent shareinfointent = new Intent();
                shareinfointent.setAction(Intent.ACTION_SEND);
                shareinfointent.putExtra(Intent.EXTRA_TEXT, message);
                shareinfointent.setType("text/plain");
                startActivity(shareinfointent);

            }
        });


        promotionsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent promotionIntent = new Intent(getActivity(), PromotionsView.class);
                startActivity(promotionIntent);
            }
        });

        supportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent supportIntent = new Intent(getActivity(), CustomerSupportView.class);
                startActivity(supportIntent);
            }
        });

        reportproblem_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = new Intent(getActivity(), ReportProblemView.class);
                startActivity(reportIntent);
            }
        });
    }

    @Override
    public void updateHistoryUI(List<CustomerHistoryModel> customerHistoryModels) {
        if (customerHistoryModels.size() != 0){
            customerHistoryModel = new ArrayList<>();
            customerHistoryModel.addAll(customerHistoryModels);
        }
    }
}
