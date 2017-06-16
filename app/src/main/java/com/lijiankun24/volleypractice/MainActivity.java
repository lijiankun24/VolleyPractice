package com.lijiankun24.volleypractice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lijiankun24.volleypractice.okhttp.OkHttpManager;
import com.lijiankun24.volleypractice.util.L;
import com.lijiankun24.volleypractice.volley.OkHttpStack;
import com.lijiankun24.volleypractice.volley.OnHttpListener;
import com.lijiankun24.volleypractice.volley.VolleyManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String mUrl = "http://gank.io/api/data/Android/10/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_volley_get:
                sendVolleyGet();
                break;
            case R.id.tv_okhttp_get:
                sendOkHttpGet();
                break;
            case R.id.tv_volley_okhttp:
                sendVolleyOkHttpGet();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void sendVolleyGet() {
        VolleyManager.getInstance(MainActivity.this).addStringRequest(mUrl, new OnHttpListener<String>() {
            @Override
            public void onSuccess(String result) {
                L.i("onSuccess " + result);
            }

            @Override
            public void onError(VolleyError error) {
                L.i("onError ");
            }
        });
    }

    private void sendOkHttpGet() {
        OkHttpManager.getInstance().addGetStringRequest(mUrl, new OnHttpListener<String>() {
            @Override
            public void onSuccess(String result) {
                L.i("onSuccess " + result);
            }

            @Override
            public void onError(VolleyError error) {
                L.i("onError ");
            }
        });
    }

    private void sendVolleyOkHttpGet() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this,
                new OkHttpStack(OkHttpManager.getInstance().getHttpClient()));
        StringRequest request = new StringRequest(mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.i("onResponse " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.i("onErrorResponse " + error);
            }
        });

        queue.add(request);
    }

    private void initView() {
        findViewById(R.id.tv_volley_get).setOnClickListener(this);
        findViewById(R.id.tv_okhttp_get).setOnClickListener(this);
        findViewById(R.id.tv_volley_okhttp).setOnClickListener(this);
    }
}
