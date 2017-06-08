package com.lijiankun24.volleypractice.volley;

/**
 * OnResponseInfoListener.java
 * <p>
 * Created by lijiankun on 17/6/7.
 */

public interface OnResponseInfoListener {
    void onResponseInfo(String url, long networkTimeMs, int statusCode);
}
