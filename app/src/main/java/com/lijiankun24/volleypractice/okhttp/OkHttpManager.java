package com.lijiankun24.volleypractice.okhttp;

import com.lijiankun24.volleypractice.volley.OnHttpListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OkHttpManager.java
 * <p>
 * Created by lijiankun on 17/6/8.
 */

public class OkHttpManager {

    private static OkHttpManager INSTANCE = null;

    private static OkHttpClient mHttpClient = null;

    private OkHttpManager() {
        mHttpClient = new OkHttpClient();
    }

    public static OkHttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (OkHttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OkHttpManager();
                }
            }
        }
        return INSTANCE;
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    public void addGetStringRequest(String url, final OnHttpListener<String> httpListener) {
        Request request = new Request.Builder()
                .method("GET", null)
                .url(url)
                .build();

        Call call = getHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                httpListener.onSuccess(response.body().string());
            }
        });
    }

    public void addPostRequest(String url) {
        RequestBody requestBody = new FormBody.Builder()
                .add("", "")
                .build();
        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        Call call = getHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }
}
