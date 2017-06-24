package com.lijiankun24.volleypractice.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.lijiankun24.volleypractice.util.L;

/**
 * ByteRequest.java
 * <p>
 * Created by lijiankun on 17/6/24.
 */

public class ByteRequest extends Request<byte[]> {

    private Response.Listener<byte[]> mListener = null;

    public ByteRequest(String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    public ByteRequest(int method, String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        try {
            if (response.data == null) {
                return Response.error(new ParseError(response));
            } else {
                return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (OutOfMemoryError error) {
            L.i("OutOfMemoryError " + error);
            return Response.error(new ParseError(response));
        }
    }

    @Override
    protected void deliverResponse(byte[] response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }
}
