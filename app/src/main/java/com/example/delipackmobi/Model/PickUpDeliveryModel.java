package com.example.delipackmobi.Model;

import java.util.HashMap;

public class PickUpDeliveryModel {
    private HashMap<String, String> fromInformation;
    private HashMap<String, String>  deliveryInformation;

    public PickUpDeliveryModel(){

    }

    public PickUpDeliveryModel(HashMap<String, String> fromInformation, HashMap<String, String> deliveryInformation) {
        this.fromInformation = fromInformation;
        this.deliveryInformation = deliveryInformation;
    }

    public HashMap<String, String> getFromInformation() {
        return fromInformation;
    }

    public void setFromInformation(HashMap<String, String> fromInformation) {
        this.fromInformation = fromInformation;
    }

    public HashMap<String, String> getDeliveryInformation() {
        return deliveryInformation;
    }

    public void setDeliveryInformation(HashMap<String, String> deliveryInformation) {
        this.deliveryInformation = deliveryInformation;
    }
}
