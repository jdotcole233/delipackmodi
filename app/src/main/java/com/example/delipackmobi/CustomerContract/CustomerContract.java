package com.example.delipackmobi.CustomerContract;

import android.content.Context;

import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class CustomerContract {
    public static final String LOGIN_URL = "http://192.168.100.2:8000/customer_login";
    public static final String GETCOMPANYDATA_URL = "http://192.168.100.2:8000/companydata";
    public static final String UPDATEDTRANSACTION_URL = "http://192.168.100.2:8000/updateTransaction";
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
    }

    public JSONObject convertResponseToObject(String jsonstring) throws JSONException {
        return new JSONObject(jsonstring);
    }

    public PersistentCookieStore getPersistentCookieStore() {
        return persistentCookieStore;
    }
}
