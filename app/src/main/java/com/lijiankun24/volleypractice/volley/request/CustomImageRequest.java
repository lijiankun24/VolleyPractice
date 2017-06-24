package com.lijiankun24.volleypractice.volley.request;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

/**
 * CustomImageRequest.java
 * <p>
 * Created by lijiankun on 17/6/7.
 */

public class CustomImageRequest extends ImageRequest {

    private long mNetworkTimeMs = 0L;

    public CustomImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth,
                              int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config decodeConfig,
                              Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, scaleType, decodeConfig, errorListener);
    }

    public CustomImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth,
                              int maxHeight, Bitmap.Config decodeConfig,
                              Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, decodeConfig, errorListener);
    }

    @Override
    protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
        if (response != null) {
            mNetworkTimeMs = response.networkTimeMs;
        }
        return super.parseNetworkResponse(response);
    }

    @Override
    protected void deliverResponse(Bitmap response) {
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
