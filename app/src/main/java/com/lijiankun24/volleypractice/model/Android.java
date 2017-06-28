package com.lijiankun24.volleypractice.model;

/**
 * Android.java
 * <p>
 * Created by lijiankun on 17/6/24.
 */

public class Android {
    public final String _id;

    public final String createdAt;

    public final String desc;

    public final String publishedAt;

    public final String source;

    public final String type;

    public final String url;

    public final boolean used;

    public final String who;

    public Android(String _id, String createdAt, String desc,
                   String publishedAt, String source, String type,
                   String url, boolean used, String who) {
        this._id = _id;
        this.createdAt = createdAt;
        this.desc = desc;
        this.publishedAt = publishedAt;
        this.source = source;
        this.type = type;
        this.url = url;
        this.used = used;
        this.who = who;
    }
}
