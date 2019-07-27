package com.delipackport.delipackmobi.CustomerContract;

import android.content.Context;

import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class CustomerContract {
    public static final String LOGIN_URL = "http://delipackport.com/api/customer_login";
    public static final String GETCOMPANYDATA_URL = "http://delipackport.com/api/companydata";
    public static final String UPDATEDTRANSACTION_URL = "http://delipackport.com/api/updateTransaction";
    public static final String RATING_URL = "http://delipackport.com/api/ratedelivery";
    public static final String CUSTOMERTRANSACTIONHISTORY_URL = "http://delipackport.com/api/customertransactionhistory";
    public static final String CUSTOMERREPORT_URL = "http://delipackport.com/api/sendandroidcustomerreport";
    public static final String CUSTOMERERRANDSESSIONCANCEL_URL = "http://delipackport.com/api/customererrandsessioncancel";


    private PersistentCookieStore persistentCookieStore;

    public CustomerContract(Context context){
        persistentCookieStore = new PersistentCookieStore(context);
    }

    public void setBasicCookies(String cookieKey, String cookieValue, Integer cookieVersion, String domain){
        BasicClientCookie basicClientCookie = new BasicClientCookie(cookieKey, cookieValue);
        basicClientCookie.setVersion(cookieVersion);
        basicClientCookie.setDomain(domain);
        basicClientCookie.setPath("/");
        persistentCookieStore.addCookie(basicClientCookie);
        System.out.println("Cookie saved successfully");
    }

    public JSONObject convertResponseToObject(String jsonstring) throws JSONException {
        return new JSONObject(jsonstring);
    }

    public void deleteCookie(Cookie cookie){
        persistentCookieStore.deleteCookie(cookie);
    }

    public void signoutcookies(){
        persistentCookieStore.clear();
    }

    public PersistentCookieStore getPersistentCookieStore() {
        return persistentCookieStore;
    }
}