package com.planetwalk.ponion.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.google.gson.Gson;
import com.planetwalk.ponion.Gsons;
import com.planetwalk.ponion.Utils.NetworkUtils;
import com.planetwalk.ponion.db.Entity.BuddyEntity;
import com.planetwalk.ponion.db.Entity.PostEntity;
import com.planetwalk.ponion.db.Entity.SlideEntity;
import com.planetwalk.ponion.db.Entity.ThreadEntity;

import java.util.List;

public class DataRepository {
    private static DataRepository sInstance;
    private final AppDatabase mDatabase;

    private MediatorLiveData<BuddyEntity> mObservableBuddy;
    private MediatorLiveData<List<ThreadEntity>> mObservableThreads;
    private MediatorLiveData<List<PostEntity>> mObservablePosts;

    private DataRepository(AppDatabase database) {
        mDatabase = database;

        mObservableBuddy = new MediatorLiveData<>();
        mObservableBuddy.addSource(mDatabase.buddyDao().getMeBuddy(), buddyEntity -> {
            if (mDatabase.getDatabaseCreated().getValue() != null) {
                mObservableBuddy.postValue(buddyEntity);
            }
        });

        mObservableThreads = new MediatorLiveData<>();
        mObservableThreads.addSource(mDatabase.threadDao().getAllThreads(), threadEntityList -> {
            if (mDatabase.getDatabaseCreated().getValue() != null) {
                mObservableThreads.postValue(threadEntityList);
            }
        });
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    public void insertBuddy(BuddyEntity buddyEntity) {
        if (buddyEntity != null) {
            BuddyEntity result = mDatabase.buddyDao().queryBuddy(buddyEntity.account);
            if (result == null) {
                mDatabase.buddyDao().insert(buddyEntity);
            } else {
                mDatabase.buddyDao().update(buddyEntity);
            }
        }
    }

    public LiveData<BuddyEntity> getMeBuddy() {
        return mObservableBuddy;
    }

    public LiveData<List<ThreadEntity>> getThreads() {
        return mObservableThreads;
    }

    public void updateBuddy(BuddyEntity buddyEntity) {
        mDatabase.buddyDao().update(buddyEntity);
    }

    public void insertThread(List<SlideEntity> slideEntityList, long serverId) {
        ThreadEntity threadEntity = new ThreadEntity();
        threadEntity.authorId = mObservableBuddy.getValue().account;
        threadEntity.content = Gsons.value().toJson(slideEntityList);
        threadEntity.serverId = serverId;
        mDatabase.threadDao().insert(threadEntity);
    }

    public void updateThread(ThreadEntity threadEntity){
        mDatabase.threadDao().update(threadEntity);
    }

    public void insertThreads(List<ThreadEntity> entities) {
        mDatabase.threadDao().insertAll(entities);
    }

    public void insertPosts(List<PostEntity> postEntities) {
        mDatabase.postDao().insertAll(postEntities);
    }
}
