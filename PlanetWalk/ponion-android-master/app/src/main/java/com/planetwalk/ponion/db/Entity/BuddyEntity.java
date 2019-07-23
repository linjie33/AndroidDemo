package com.planetwalk.ponion.db.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "buddy_table")
public class BuddyEntity {
    // 表示在本地数据库的字段。
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "account")
    public Long account;

    @ColumnInfo(name = "display_name")
    public String displayName;

    public Integer coins;

    public Integer sex;

    // 好友类型，具体类型在BuddyConstants中查询
    public int type;

    public String photoUrl;
}
