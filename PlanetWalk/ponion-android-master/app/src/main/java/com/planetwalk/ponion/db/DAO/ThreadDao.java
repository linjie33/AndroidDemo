package com.planetwalk.ponion.db.DAO;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.planetwalk.ponion.db.Entity.ThreadEntity;

import java.util.List;

@Dao
public interface ThreadDao {
    @Insert
    void insert(ThreadEntity threadEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<ThreadEntity> threadEntities);

    @Update
    void update(ThreadEntity threadEntity);

    @Query("DELETE FROM thread_table")
    void cleanAll();

    @Query("select * from thread_table ORDER BY server_id DESC")
    @Transaction
    LiveData<List<ThreadEntity>> getAllThreads();
}
