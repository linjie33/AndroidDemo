package com.planetwalk.ponion.db.DAO;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.planetwalk.ponion.db.Entity.PostEntity;
import com.planetwalk.ponion.db.Entity.ThreadEntity;

import java.util.List;

@Dao
public interface PostDao {
    @Insert
    void insert(PostEntity postEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<PostEntity> postEntities);

    @Update
    void update(PostEntity postEntity);

    @Query("DELETE FROM post_table")
    void cleanAll();

    @Query("select * from post_table where server_thread_id=:threadId ORDER BY id DESC")
    @Transaction
    LiveData<List<PostEntity>> getAllPosts(long threadId);
}
