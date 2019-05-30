package com.example.delipackmobi.CustomerContract;

import android.content.Context;

import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class CustomerContract {
    public static final String LOGIN_URL = "http://172.20.10.3:8000/customer_login";
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
