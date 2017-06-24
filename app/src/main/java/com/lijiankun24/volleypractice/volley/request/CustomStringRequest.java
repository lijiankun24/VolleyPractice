package com.lijiankun24.volleypractice.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * CustomStringRequest.java
 * <p>
 * Created by lijiankun on 17/6/6.
 */

public class CustomStringRequest extends StringRequest {

    private long mNetworkTimeMs = 0L;

    public CustomStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        if (response != null) {
            mNetworkTimeMs = response.networkTimeMs;
        }
        return super.parseNetworkResponse(response);
    }

    @Override
    protected void deliverResponse(String response) {
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

    /**
     * 在子类中重写此方法，即可得到网络请求耗时和网络请求结果
     *
     * @param networkTimeMs 网络请求耗时，单位：毫秒
     * @param statusCode    网络请求结果，成功则为1；失败则是具体的Http状态码，如404，500等(容易定位到请求失败的原因)
     */
    protected void onResponseTimeAndCode(long networkTimeMs, int statusCode) {
    }
}

