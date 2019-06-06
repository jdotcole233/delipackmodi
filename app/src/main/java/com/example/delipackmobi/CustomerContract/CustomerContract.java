package com.example.delipackmobi.CustomerContract;

import android.content.Context;

import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class CustomerContract {
    public static final String LOGIN_URL = "http://172.20.10.7:8000/customer_login";
    public static final String GETCOMPANYDATA_URL = "http://172.20.10.7:8000/companydata";
    public static final String UPDATEDTRANSACTION_URL = "http://172.20.10.7:8000/updateTransaction";
    public static final String RATING_URL = "http://172.20.10.7:8000/ratedelivery";
    public static final String CUSTOMERTRANSACTIONHISTORY_URL = "http://172.20.10.7:8000/customertransactionhistory";
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

    public void deleteCookie(Cookie cookie){
        persistentCookieStore.deleteCookie(cookie);
    }

    public PersistentCookieStore getPersistentCookieStore() {
        return persistentCookieStore;
    }
}
