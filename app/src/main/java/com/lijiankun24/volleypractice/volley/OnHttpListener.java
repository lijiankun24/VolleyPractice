package com.lijiankun24.volleypractice.volley;

import com.android.volley.VolleyError;

/**
 * OnHttpListener.java
 * <p>
 * Created by lijiankun on 17/6/6.
 */

public interface OnHttpListener<T> {

    void onSuccess(T result);

    void onError(VolleyError error);
}
