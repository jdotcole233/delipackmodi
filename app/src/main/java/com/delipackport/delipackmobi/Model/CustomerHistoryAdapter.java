package com.delipackport.delipackmobi.Model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.delipackport.delipackmobi.HistoryDetails;
import com.delipackport.delipackmobi.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CustomerHistoryAdapter extends RecyclerView.Adapter<CustomerHistoryAdapter.MyViewHolder> {


    private List<CustomerHistoryModel> customerHistoryList;
    private Context context;
    private final String LOGO_BASE_URL = "https://superuser.delipackport.com/company_logos/";


    public CustomerHistoryAdapter(List<CustomerHistoryModel> customerHistoryList, Context context) {
        this.customerHistoryList = customerHistoryList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_card_display, viewGroup, false);
        return new MyViewHolder(view);
    }




    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
            myViewHolder.company_name.setText(customerHistoryList.get(i).getCompany_name());
            myViewHolder.pickup.setText(customerHistoryList.get(i).getPickup_location());
            myViewHolder.delivery.setText(customerHistoryList.get(i).getDelivery_location());
            myViewHolder.company_history_logo.setImageUrl(LOGO_BASE_URL + customerHistoryList.get(i).getCompany_logo_path());

//        loadCompanyLogo(context, customerHistoryList.get(i).getCompany_name(), customerHistoryList.get(i).getCompany_id(), myViewHolder.company_history_logo);

//            myViewHolder.price.setText(customerHistoryList.get(i).getPrice());

        myViewHolder.detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerHistoryList.get(i).getCompany_name();

               Intent intent =  new Intent(v.getContext() , HistoryDetails.class);
                intent.putExtra("charges", customerHistoryList.get(i).getPrice());
                intent.putExtra("pick_up", customerHistoryList.get(i).getPickup_location());
                intent.putExtra("deliver_to", customerHistoryList.get(i).getDelivery_location());
                intent.putExtra("company_name", customerHistoryList.get(i).getCompany_name());
                intent.putExtra("rider_name", customerHistoryList.get(i).getRidername());
                intent.putExtra("biker_number", customerHistoryList.get(i).getBike_registration());
                intent.putExtra("created_at", customerHistoryList.get(i).getTransaction_date());
                intent.putExtra("transaction_number", customerHistoryList.get(i).getTransaction_id() );
                intent.putExtra("transaction_status", customerHistoryList.get(i).getTransaction_status());
                intent.putExtra("payment_type", customerHistoryList.get(i).getPayment_type());
               v.getContext().startActivity(intent);
            }
        });
    }




    @Override
    public int getItemCount() {
        return customerHistoryList.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView company_name;
        private TextView pickup;
        private TextView delivery;
        private TextView price;
        private Button detailsBtn;
        private SmartImageView company_history_logo;

        public MyViewHolder(View itemView) {
            super(itemView);

            company_name = itemView.findViewById(R.id.company_name);
            pickup = itemView.findViewById(R.id.pick_up_from);
            delivery = itemView.findViewById(R.id.deliver_to);
            detailsBtn = itemView.findViewById(R.id.detailsbtn);
            company_history_logo = itemView.findViewById(R.id.company_history_logo);
//            price = itemView.findViewById(R.id.price);


        }
    }


    public void loadCompanyLogo(Context context, String companyName, String companyID, final ImageView view){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String logourl = "http://superuser.delipackport.com/company_logos/"+companyName.toLowerCase()+companyID;

        asyncHttpClient.get(logourl, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                    view.setImageBitmap(bitmap);
                    Log.i("DeliPackMessag", "found in company logo");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });

    }


}
