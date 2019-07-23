package com.planetwalk.ponion;

import android.app.Application;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.planetwalk.ponion.Service.SyncThreadService;
import com.planetwalk.ponion.Utils.L;
import com.planetwalk.ponion.db.AppDatabase;
import com.planetwalk.ponion.db.DataRepository;

public final class PonionApplication extends MultiDexApplication {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        L.init(this);
        mAppExecutors = new AppExecutors();
        GlobalData.sApplicationContext = this;
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }

    public AppExecutors getAppExecutors(){
        return mAppExecutors;
    }
}
