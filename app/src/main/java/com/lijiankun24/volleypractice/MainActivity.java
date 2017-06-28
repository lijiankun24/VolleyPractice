package com.lijiankun24.volleypractice;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lijiankun24.volleypractice.about.AboutActivity;
import com.lijiankun24.volleypractice.model.AndroidModel;
import com.lijiankun24.volleypractice.okhttp.OkHttpManager;
import com.lijiankun24.volleypractice.util.L;
import com.lijiankun24.volleypractice.volley.OkHttpStack;
import com.lijiankun24.volleypractice.volley.OnHttpListener;
import com.lijiankun24.volleypractice.volley.VolleyManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
        , Toolbar.OnMenuItemClickListener {

    private LinearLayout mLayout = null;

    private String mUrl = "http://gank.io/api/data/Android/10/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
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
            case R.id.tv_volley_gson:
                sendVolleyGsonGet();
                break;
            case R.id.tv_volley_okhttp:
                sendVolleyOkHttpGet();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void sendVolleyGet() {
        VolleyManager.getInstance(MainActivity.this).addStringRequest(mUrl, new OnHttpListener<String>() {
            @Override
            public void onSuccess(String result) {
                L.i("onSuccess " + result);
                showSnackbar("send Volley Get Success");
            }

            @Override
            public void onError(VolleyError error) {
                L.i("onError ");
                showSnackbar("send Volley Get Error");
            }
        });
    }

    private void sendOkHttpGet() {
        OkHttpManager.getInstance().addGetStringRequest(mUrl, new OnHttpListener<String>() {
            @Override
            public void onSuccess(String result) {
                L.i("onSuccess " + result);
                showSnackbar("send OkHttp Get Success");
            }

            @Override
            public void onError(VolleyError error) {
                L.i("onError ");
                showSnackbar("send OkHttp Get Error");
            }
        });
    }

    private void sendVolleyGsonGet() {
        VolleyManager.getInstance(MainActivity.this)
                .addGsonResquest(mUrl, new OnHttpListener<AndroidModel>() {
                    @Override
                    public void onSuccess(AndroidModel result) {
                        L.i("onSuccess ");
                        showSnackbar("send Volley GsonRequest Success");
                    }

                    @Override
                    public void onError(VolleyError error) {
                        L.i("onError ");
                        showSnackbar("send Volley GsonRequest Error");
                    }
                }, AndroidModel.class);


    }

    private void sendVolleyOkHttpGet() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this,
                new OkHttpStack(OkHttpManager.getInstance().getHttpClient()));
        StringRequest request = new StringRequest(mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.i("onResponse " + response);
                showSnackbar("send Volley OkHttp Get Success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.i("onErrorResponse " + error);
                showSnackbar("send Volley OkHttp Get error");
            }
        });

        queue.add(request);
    }

    private void initView() {
        findViewById(R.id.tv_volley_get).setOnClickListener(this);
        findViewById(R.id.tv_okhttp_get).setOnClickListener(this);
        findViewById(R.id.tv_volley_gson).setOnClickListener(this);
        findViewById(R.id.tv_volley_okhttp).setOnClickListener(this);

        mLayout = (LinearLayout) findViewById(R.id.ll_main_root);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    private void showSnackbar(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Snackbar.make(mLayout, msg, Snackbar.LENGTH_SHORT).show();
    }
}
