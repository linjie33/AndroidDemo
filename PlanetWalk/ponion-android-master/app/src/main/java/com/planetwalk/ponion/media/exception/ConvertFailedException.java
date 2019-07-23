package com.planetwalk.ponion.media.exception;

public class ConvertFailedException extends Exception {

    public static final int REASON_UPLOAD_FAIL = 0;

    private String mediaUrl;
    private String mediaPath;

    public int reason;

    public String getUrl() {
        return mediaUrl;
    }

    public String getPath() {
        return mediaPath;
    }

    public int getReason() {
        return reason;
    }

    public ConvertFailedException(String url, String path) {
        super();
        mediaPath = path;
        mediaUrl = url;
        reason = REASON_UPLOAD_FAIL;
    }
}
