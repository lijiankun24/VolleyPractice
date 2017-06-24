package com.lijiankun24.volleypractice.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * GsonRequest.java
 * <p>
 * Created by lijiankun on 17/6/24.
 */

public class GsonRequest<T> extends Request<T> {

    private Gson mGson = new Gson();

    private Response.Listener<T> mListener = null;

    private Class<T> mClass = null;

    public GsonRequest(String url, Response.Listener<T> listener, Response.ErrorListener errorListener, Class<T> aClass) {
        this(Method.GET, url, listener, errorListener, aClass);
    }

    public GsonRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener, Class<T> aClass) {
        super(method, url, errorListener);
        mListener = listener;
        mClass = aClass;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(res, mClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

}
