package com.lijiankun24.volleypractice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.VolleyError;
import com.lijiankun24.volleypractice.okhttp.OkHttpManager;
import com.lijiankun24.volleypractice.util.L;
import com.lijiankun24.volleypractice.volley.OnHttpListener;
import com.lijiankun24.volleypractice.volley.VolleyManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
                sendStringRequest();
                break;
            case R.id.tv_okhttp_get:
                sendOkHttpGet();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void sendStringRequest() {
        String url = "http://gank.io/api/data/Android/10/1";
        VolleyManager.getInstance(MainActivity.this).addStringRequest(url, new OnHttpListener<String>() {
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

    private void sendOkHttpGet(){
        String url = "http://gank.io/api/data/Android/10/1";
        OkHttpManager.getInstance().addGetStringRequest(url, new OnHttpListener<String>() {
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

    private void initView() {
        findViewById(R.id.tv_volley_get).setOnClickListener(this);
        findViewById(R.id.tv_okhttp_get).setOnClickListener(this);
    }
}
