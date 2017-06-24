package com.lijiankun24.volleypractice.volley.model;

import java.util.List;

/**
 * AndroidModel.java
 * <p>
 * Created by lijiankun on 17/6/24.
 */

public class AndroidModel {
    public final boolean error;
    public final List<Android> results;

    public AndroidModel(boolean error, List<Android> results) {
        this.error = error;
        this.results = results;
    }
}
