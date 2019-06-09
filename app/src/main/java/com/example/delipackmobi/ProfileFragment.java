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


public class ProfileFragment extends Fragment {

    private ImageView editButton;
    private Button tellfriendbtn, promotionsbtn, supportbtn;

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

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editprofileintent = new Intent(getActivity(), EditCustomerProfile.class);
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
    }
}
