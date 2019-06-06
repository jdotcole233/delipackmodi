package com.example.delipackmobi.Model;

public class CustomerHistoryModel {
    private String company_name;
    private String pickup_location;
    private String delivery_location;
    private String price;

    public CustomerHistoryModel(){

    }

    public CustomerHistoryModel(String company_name, String pickup_location, String delivery_location, String price) {
        this.company_name = company_name;
        this.pickup_location = pickup_location;
        this.delivery_location = delivery_location;
        this.price = price;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public String getDelivery_location() {
        return delivery_location;
    }

    public String getPrice() {
        return price;
    }
}
