package com.planetwalk.ponion.rapi.model;

import android.text.TextUtils;

public class RemoteThread {
    public Integer id;
    public String title;
    public String extra;
    public String fragments;
    public String gtmCreated;
    public Integer uid;

    public RemoteThread(String fragments) {
        this(null, fragments);
    }

    public RemoteThread(String title, String fragments) {
        this(title, fragments, null);
    }

    public RemoteThread(String title, String fragments, String extra) {
        this.title = TextUtils.isEmpty(title) ? "" : title;
        this.fragments = fragments;
        this.extra = extra;
    }
}
