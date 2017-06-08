package com.lijiankun24.volleypractice.volley;

import android.content.Context;
import android.graphics.Bitmap;
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

    private OnResponseInfoListener mResponseInfoListener = null;

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

    public void setResponseInfoListener(OnResponseInfoListener responseInfoListener) {
        mResponseInfoListener = responseInfoListener;
    }

    public void addStringRequest(String url, final OnHttpListener httpListener) {
        this.addStringRequest(Request.Method.GET, url, httpListener);
    }

    public void addStringRequest(int method, final String url, final OnHttpListener<String> httpListener) {
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
                sendResponseInfo(url, networkTimeMs, statusCode);
            }
        };
        addRequest(request);
    }

    public void addJsonObjectRequest(String url, JSONObject jsonRequest, final OnHttpListener<JSONObject> httpListener) {
        this.addJsonObjectRequest(jsonRequest == null ? Request.Method.GET : Request.Method.POST, url, jsonRequest, httpListener);
    }

    public void addJsonObjectRequest(int method,final String url, JSONObject jsonRequest, final OnHttpListener<JSONObject> httpListener) {
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
                sendResponseInfo(url, networkTimeMs, statusCode);
            }
        };
        addRequest(request);
    }

    public void addJsonArrayRequest(final String url, final OnHttpListener<JSONArray> httpListener) {
        this.addJsonArrayRequest(Request.Method.GET, url, null, httpListener);
    }

    public void addJsonArrayRequest(int method, final String url, JSONArray jsonRequest, final OnHttpListener<JSONArray> httpListener) {
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
                sendResponseInfo(url, networkTimeMs, statusCode);
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
                sendResponseInfo(url, networkTimeMs, statusCode);
            }
        };
        addRequest(request);
    }

    private void sendResponseInfo(String url, long networkTimeMs, int statusCode) {
        if (mResponseInfoListener == null) {
            return;
        }
        mResponseInfoListener.onResponseInfo(url, networkTimeMs, statusCode);
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
