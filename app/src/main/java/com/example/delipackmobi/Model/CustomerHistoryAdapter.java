package com.example.delipackmobi.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
            myViewHolder.company_name.setText(customerHistoryList.get(i).getCompany_name());
            myViewHolder.pickup.setText(customerHistoryList.get(i).getPickup_location());
            myViewHolder.delivery.setText(customerHistoryList.get(i).getDelivery_location());
            myViewHolder.price.setText(customerHistoryList.get(i).getPrice());
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

        public MyViewHolder(View itemView) {
            super(itemView);

            company_name = itemView.findViewById(R.id.company_name);
            pickup = itemView.findViewById(R.id.pick_up_from);
            delivery = itemView.findViewById(R.id.deliver_to);
            price = itemView.findViewById(R.id.price);

        }
    }


}
