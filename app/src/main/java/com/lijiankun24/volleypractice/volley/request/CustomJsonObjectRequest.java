package com.lijiankun24.volleypractice.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * CustomStringRequest.java
 * <p>
 * Created by lijiankun on 17/6/6.
 */

public class CustomJsonObjectRequest extends JsonObjectRequest {

    private long mNetworkTimeMs = 0L;

    public CustomJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public CustomJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest, listener, errorListener);
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        if (response != null) {
            mNetworkTimeMs = response.networkTimeMs;
        }
        return super.parseNetworkResponse(response);
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        super.deliverResponse(response);
        if (mNetworkTimeMs > 0) {
            this.onResponseTimeAndCode(mNetworkTimeMs, 1);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            this.onResponseTimeAndCode(response.networkTimeMs, response.statusCode);
        } else {
            // Http 协议中 417 表示 Expectation Failed
            this.onResponseTimeAndCode(error.getNetworkTimeMs(), 417);
        }
    }

    protected void onResponseTimeAndCode(long networkTimeMs, int statusCode) {
    }
}

