package com.planetwalk.ponion.db.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "post_table")
public class PostEntity {
    @PrimaryKey
    public long id;

    @ColumnInfo(name = "server_thread_id")
    public long tid;

    @ColumnInfo(name = "user_id")
    public long uid;

    public String title;

    public String content;

    public String extra;

    @ColumnInfo(name = "gmt_created")
    public String gtmCreated;
}
