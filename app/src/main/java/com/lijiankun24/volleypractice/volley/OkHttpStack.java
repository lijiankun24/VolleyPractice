package com.lijiankun24.volleypractice.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * OkHttpStack.java
 * <p>
 * Created by lijiankun on 17/6/7.
 */

public class OkHttpStack implements HttpStack {

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
        return null;
    }
}
