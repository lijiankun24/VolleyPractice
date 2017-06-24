package com.lijiankun24.volleypractice.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lijiankun24.volleypractice.volley.request.CustomImageRequest;
import com.lijiankun24.volleypractice.volley.request.CustomJsonArrayRequest;
import com.lijiankun24.volleypractice.volley.request.CustomJsonObjectRequest;
import com.lijiankun24.volleypractice.volley.request.CustomStringRequest;
import com.lijiankun24.volleypractice.volley.request.GsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * VolleyManager.java
 * <p>
 * Created by lijiankun on 17/6/6.
 */

public class VolleyManager {

    private static VolleyManager INSTANCE = null;

    private static WeakReference<Context> mWRContext = null;

    private RequestQueue mQueue = null;

    private OnResponseInfoInterceptor mResponseInfoInterceptor = null;

    private VolleyManager(Context context) {
        if (mWRContext == null || mWRContext.get() == null) {
            mWRContext = new WeakReference<>(context);
        }
        mQueue = getRequestQueue();
    }

    public static VolleyManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (VolleyManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new VolleyManager(context);
                }
            }
        }
        return INSTANCE;
    }

    public void setOnResponseInfoInterceptor(OnResponseInfoInterceptor responseInfoListener) {
        mResponseInfoInterceptor = responseInfoListener;
    }

    public <T> void addGsonResquest(String url, OnHttpListener<T> httpListener, Class<T> aClass) {
        this.addGsonResquest(Request.Method.GET, url, httpListener, aClass);
    }

    public <T> void addGsonResquest(int method, String url, OnHttpListener<T> httpListener, Class<T> aClass) {
        this.addGsonResquest(method, url, httpListener, aClass, mResponseInfoInterceptor);
    }

    public <T> void addGsonResquest(int method, String url, final OnHttpListener<T> httpListener, Class<T> aClass, OnResponseInfoInterceptor interceptor) {
        GsonRequest<T> request = new GsonRequest<T>(method, url, new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                if (httpListener != null) {
                    httpListener.onSuccess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (httpListener != null) {
                    httpListener.onError(error);
                }
            }
        }, aClass);
        addRequest(request);
    }

    public void addStringRequest(String url, OnHttpListener httpListener) {
        this.addStringRequest(Request.Method.GET, url, httpListener);
    }

    public void addStringRequest(int method, String url, OnHttpListener<String> httpListener) {
        this.addStringRequest(method, url, httpListener, mResponseInfoInterceptor);
    }

    public void addStringRequest(int method, final String url,
                                 final OnHttpListener<String> httpListener, final OnResponseInfoInterceptor interceptor) {
        StringRequest request = new CustomStringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                httpListener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                httpListener.onError(error);
            }
        }) {
            @Override
            protected void onResponseTimeAndCode(long networkTimeMs, int statusCode) {
                handleInterceptor(url, networkTimeMs, statusCode, interceptor);
            }
        };
        addRequest(request);
    }

    public void addJsonObjectRequest(String url, JSONObject jsonRequest, final OnHttpListener<JSONObject> httpListener) {
        this.addJsonObjectRequest(jsonRequest == null ? Request.Method.GET : Request.Method.POST, url, jsonRequest, httpListener);
    }

    public void addJsonObjectRequest(int method, final String url, JSONObject jsonRequest, final OnHttpListener<JSONObject> httpListener) {
        this.addJsonObjectRequest(method, url, jsonRequest, httpListener, mResponseInfoInterceptor);
    }

    public void addJsonObjectRequest(int method, final String url, JSONObject jsonRequest,
                                     final OnHttpListener<JSONObject> httpListener, final OnResponseInfoInterceptor interceptor) {
        JsonObjectRequest request = new CustomJsonObjectRequest(method, url, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                httpListener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                httpListener.onError(error);
            }
        }) {
            @Override
            protected void onResponseTimeAndCode(long networkTimeMs, int statusCode) {
                handleInterceptor(url, networkTimeMs, statusCode, mResponseInfoInterceptor);
            }
        };
        addRequest(request);
    }

    public void addJsonArrayRequest(final String url, final OnHttpListener<JSONArray> httpListener) {
        this.addJsonArrayRequest(Request.Method.GET, url, null, httpListener);
    }

    public void addJsonArrayRequest(int method, final String url, JSONArray jsonRequest, final OnHttpListener<JSONArray> httpListener) {
        this.addJsonArrayRequest(method, url, jsonRequest, httpListener, mResponseInfoInterceptor);
    }

    public void addJsonArrayRequest(int method, final String url, JSONArray jsonRequest,
                                    final OnHttpListener<JSONArray> httpListener, final OnResponseInfoInterceptor interceptor) {
        JsonArrayRequest request = new CustomJsonArrayRequest(method, url, jsonRequest, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                httpListener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                httpListener.onError(error);
            }
        }) {
            @Override
            protected void onResponseTimeAndCode(long networkTimeMs, int statusCode) {
                handleInterceptor(url, networkTimeMs, statusCode, mResponseInfoInterceptor);
            }
        };
        addRequest(request);
    }

    public void addImageRequest(String url, int maxWidth, int maxHeight,
                                Bitmap.Config decodeConfig, final OnHttpListener<Bitmap> httpListener) {
        this.addImageRequest(url, maxWidth, maxHeight, ImageView.ScaleType.CENTER_INSIDE, decodeConfig, httpListener);
    }

    public void addImageRequest(final String url, int maxWidth, int maxHeight,
                                ImageView.ScaleType scaleType, Bitmap.Config decodeConfig,
                                final OnHttpListener<Bitmap> httpListener) {
        this.addImageRequest(url, maxWidth, maxHeight, scaleType, decodeConfig, httpListener, mResponseInfoInterceptor);
    }

    public void addImageRequest(final String url, int maxWidth, int maxHeight,
                                ImageView.ScaleType scaleType, Bitmap.Config decodeConfig,
                                final OnHttpListener<Bitmap> httpListener,
                                final OnResponseInfoInterceptor interceptor) {
        ImageRequest request = new CustomImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                httpListener.onSuccess(response);
            }
        }, maxWidth, maxHeight, scaleType, decodeConfig, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                httpListener.onError(error);
            }
        }) {
            @Override
            protected void onResponseTimeAndCode(long networkTimeMs, int statusCode) {
                handleInterceptor(url, networkTimeMs, statusCode, mResponseInfoInterceptor);
            }
        };
        addRequest(request);
    }


    /**
     * 处理 OnResponseInfoInterceptor 拦截器
     *
     * @param url            网络请求对应的URL
     * @param startTimeStamp 网络请求开始的时间，用于计算网络请求耗时
     * @param statusCode     网络请求结果的状态，1表示网络请求成功，0表示网络请求失败
     * @param interceptor    拦截器 {@link OnResponseInfoInterceptor} ，有一个默认的拦截器 mResponseInfoInterceptor，用户也可以通过{@link #addStringRequest(int, String,
     *                       OnHttpListener, OnResponseInfoInterceptor)} 给网络请求设置单独的拦截器
     */
    private void handleInterceptor(String url, long startTimeStamp, int statusCode,
                                   OnResponseInfoInterceptor interceptor) {
        long apiDuration = SystemClock.elapsedRealtime() - startTimeStamp;
        if (apiDuration < 0 || TextUtils.isEmpty(url) || interceptor == null) {
            return;
        }
        interceptor.onResponseInfo(url, apiDuration, statusCode);
    }

    private void addRequest(Request request) {
        if (request == null) {
            return;
        }
        mQueue.add(request);
    }

    private RequestQueue getRequestQueue() {
        if (mQueue == null && mWRContext != null && mWRContext.get() != null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mQueue = Volley.newRequestQueue(mWRContext.get().getApplicationContext());
        }
        return mQueue;
    }
}
