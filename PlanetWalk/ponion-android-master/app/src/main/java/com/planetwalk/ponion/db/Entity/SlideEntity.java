package com.planetwalk.ponion.db.Entity;

import java.io.Serializable;

public class SlideEntity implements Serializable {
    public String mediaUrl;
    public int mediaType;
    public String text;
    public boolean isLock;
    public int backgroundColor;

    public transient String localPath;
}
