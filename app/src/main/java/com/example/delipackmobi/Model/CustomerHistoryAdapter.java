package com.example.delipackmobi.Model;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.delipackmobi.HistoryDetails;
import com.example.delipackmobi.R;

import java.util.List;

public class CustomerHistoryAdapter extends RecyclerView.Adapter<CustomerHistoryAdapter.MyViewHolder> {


    private List<CustomerHistoryModel> customerHistoryList;
    private Context context;

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
                intent.putExtra("paynent_type", customerHistoryList.get(i).getPayment_type());
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

        public MyViewHolder(View itemView) {
            super(itemView);

            company_name = itemView.findViewById(R.id.company_name);
            pickup = itemView.findViewById(R.id.pick_up_from);
            delivery = itemView.findViewById(R.id.deliver_to);
            detailsBtn = itemView.findViewById(R.id.detailsbtn);
//            price = itemView.findViewById(R.id.price);


        }
    }


}
