package com.linjie.contactsdemo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class MyProvider extends ContentProvider {
    public static final int TABLE1_DIT = 0;
    public static final int TABLE1_ITEM = 1;
    public static final int TABLE2_DIR = 3;
    public static final int TABLE2_ITEM = 3;

    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.linjie.contactsdemo.provider","table1",TABLE1_DIT);
        uriMatcher.addURI("com.linjie.contactsdemo.provider","table1/#",TABLE1_ITEM);
        uriMatcher.addURI("com.linjie.contactsdemo.provider","table2",TABLE2_DIR);
        uriMatcher.addURI("com.linjie.contactsdemo.provider","table2/#",TABLE2_ITEM);
    }
    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,  String selection,  String[] selectionArgs,String sortOrder) {
        return null;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri,String selection,String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri,ContentValues values,String selection,String[] selectionArgs) {
        return 0;
    }
}
