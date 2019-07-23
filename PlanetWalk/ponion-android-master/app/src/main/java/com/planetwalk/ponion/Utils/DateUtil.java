package com.planetwalk.ponion.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final String TAG = DateUtil.class.getSimpleName();
    private static SimpleDateFormat utcFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static Date utcStringToDate(String utcString) {
        try {
            return utcFormater.parse(utcString);
        } catch (ParseException e) {
            L.e(TAG, e.getMessage(), e);
            return new Date();
        }
    }
}
