package com.planetwalk.ponion.db.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "thread_table", indices = {@Index(value = "server_id", unique = true)})
public class ThreadEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "author_id")
    public long authorId;

    @ColumnInfo(name = "public_time")
    public Date publicTime;

    public String content;

    @ColumnInfo(name = "server_id")
    public long serverId;

    @ColumnInfo(name = "emoji_comment_info_list")
    public String emojiCommentInfoList;

    @ColumnInfo(name = "my_emoji_comment_id")
    public int myEmojiCommentId;
}
