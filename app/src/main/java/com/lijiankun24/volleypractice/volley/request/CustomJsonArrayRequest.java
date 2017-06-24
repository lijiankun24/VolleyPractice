package com.lijiankun24.volleypractice.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

/**
 * CustomJsonArrayRequest.java
 * <p>
 * Created by lijiankun on 17/6/7.
 */

public class CustomJsonArrayRequest extends JsonArrayRequest {

    private long mNetworkTimeMs = 0L;

    public CustomJsonArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public CustomJsonArrayRequest(int method, String url, JSONArray jsonRequest, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        if (response != null) {
            mNetworkTimeMs = response.networkTimeMs;
        }
        return super.parseNetworkResponse(response);
    }

    @Override
    protected void deliverResponse(JSONArray response) {
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
