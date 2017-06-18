package com.lijiankun24.volleypractice.volley;

/**
 * OnResponseInfoInterceptor.java
 * <p>
 * Created by lijiankun on 17/6/7.
 */

public interface OnResponseInfoInterceptor {
    /**
     * 网络请求耗时、网络请求结果的回调接口
     *
     * @param url           网络请求对应的 URL
     * @param networkTimeMs 网络请求的耗时
     * @param statusCode    网络请求结果的状态码(其实就是 Http 响应中的状态码)
     */
    void onResponseInfo(String url, long networkTimeMs, int statusCode);
}
