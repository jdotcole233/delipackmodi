package com.delipackport.delipackmobi.Model;

import java.util.HashMap;

public class PickUpDeliveryModel {
    private HashMap<String, String> fromInformation;
    private HashMap<String, String>  deliveryInformation;

    public PickUpDeliveryModel(){
        fromInformation = new HashMap<>();
        deliveryInformation = new HashMap<>();

    }

    public PickUpDeliveryModel(HashMap<String, String> fromInformation, HashMap<String, String> deliveryInformation) {
        this.fromInformation = fromInformation;
        this.deliveryInformation = deliveryInformation;
    }

    public HashMap<String, String> getFromInformation() {
        return fromInformation;
    }

    public void setFromInformation(String pickupkey, String pickupvalue) {
        this.fromInformation.put(pickupkey, pickupvalue);
    }

    public HashMap<String, String> getDeliveryInformation() {
        return deliveryInformation;
    }

    public void setDeliveryInformation(String key,String value) {
        this.deliveryInformation.put(key, value);
    }

    public void resetsetDeliveryInformation() {
        this.deliveryInformation = null;
    }

    public void resetFromInformation() {
        this.fromInformation = null;
    }
}
