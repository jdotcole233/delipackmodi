package com.delipackport.delipackmobi.Model;

import java.io.Serializable;

public class CustomerHistoryModel implements Serializable {
    private String company_name, company_id;
    private String pickup_location;
    private String delivery_location, transaction_status, payment_type;
    private String price, ridername, bike_registration, transaction_date, transaction_id;


    public CustomerHistoryModel(String company_id, String company_name, String pickup_location, String delivery_location, String transaction_status, String payment_type, String price, String ridername, String bike_registration, String transaction_date, String transaction_id) {
        this.company_id = company_id;
        this.company_name = company_name;
        this.pickup_location = pickup_location;
        this.delivery_location = delivery_location;
        this.transaction_status = transaction_status;
        this.payment_type = payment_type;
        this.price = price;
        this.ridername = ridername;
        this.bike_registration = bike_registration;
        this.transaction_date = transaction_date;
        this.transaction_id = transaction_id;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public void setDelivery_location(String delivery_location) {
        this.delivery_location = delivery_location;
    }

    public void setTransaction_status(String transaction_status) {
        this.transaction_status = transaction_status;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRidername(String ridername) {
        this.ridername = ridername;
    }

    public void setBike_registration(String bike_registration) {
        this.bike_registration = bike_registration;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
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

    public String getTransaction_status() {
        return transaction_status;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public String getPrice() {
        return price;
    }

    public String getRidername() {
        return ridername;
    }

    public String getBike_registration() {
        return bike_registration;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public String getCompany_id() {
        return company_id;
    }
}
