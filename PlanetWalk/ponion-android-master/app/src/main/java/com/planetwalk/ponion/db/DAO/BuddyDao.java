package com.planetwalk.ponion.db.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.planetwalk.ponion.db.Entity.BuddyEntity;

import java.util.List;

@Dao
public interface BuddyDao {
    @Insert
    void insert(BuddyEntity buddyEntity);

    @Update
    void update(BuddyEntity buddyEntity);

    @Query("update buddy_table set coins=coins-:coin where type=2")
    int descreseCoins(int coin);

    @Query("SELECT * from buddy_table where account=:account")
    BuddyEntity queryBuddy(long account);

    @Query("SELECT * from buddy_table ORDER BY id ASC")
    LiveData<List<BuddyEntity>> getAllBuddies();

    @Query("SELECT * from buddy_table where type=2")
    LiveData<BuddyEntity> getMeBuddy();
}
