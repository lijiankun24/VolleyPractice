package com.lijiankun24.volleypractice;

import android.app.Application;

import com.lijiankun24.volleypractice.util.L;
import com.lijiankun24.volleypractice.volley.OnResponseInfoInterceptor;
import com.lijiankun24.volleypractice.volley.VolleyManager;

/**
 * MyApplication.java
 * <p>
 * Created by lijiankun on 17/6/6.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyManager.getInstance(MyApplication.this).setOnResponseInfoInterceptor(new OnResponseInfoInterceptor() {
            @Override
            public void onResponseInfo(String url, long networkTimeMs, int statusCode) {
                L.i("url " + url);
                L.i("networkTimeMs " + networkTimeMs);
                L.i("statusCode " + statusCode);
            }
        });
    }
}
