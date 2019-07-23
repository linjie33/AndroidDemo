package com.planetwalk.ponion.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.planetwalk.ponion.PonionApplication;
import com.planetwalk.ponion.Utils.CommonUtils;
import com.planetwalk.ponion.Utils.L;
import com.planetwalk.ponion.db.Entity.ThreadEntity;
import com.planetwalk.ponion.rapi.PonionSeverApi;
import com.planetwalk.ponion.rapi.converter.ThreadConverter;
import com.planetwalk.ponion.rapi.model.RemoteThread;
import com.planetwalk.ponion.rapi.service.ThreadService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SyncThreadService extends Service {

    private boolean isWorking = false;
    private static final String TAG = SyncThreadService.class.getSimpleName();
    private static final int PAGE_FROM = 0;
    private static final int PAGE_SIZE = 50;
    private PonionApplication application;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {
        super.onCreate();
        application = (PonionApplication) getApplication();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startSyncThreads();
        return START_STICKY;
    }

    private void startSyncThreads(long from, int size) {
        Disposable disposable = PonionSeverApi.api().create(ThreadService.class)
                .fetchThreadsV1(from, size, CommonUtils.getToken(getApplicationContext()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> {
                    if (response.getCode() != 200 || response.getData().size == 0) {
                        return;
                    }
                    List<ThreadEntity> entities = new ArrayList();
                    for (RemoteThread thread : response.getData().list) {
                        entities.add(ThreadConverter.convertToLocal(thread));
                    }
                    application.getRepository().insertThreads(entities);
                    AndroidSchedulers.mainThread().scheduleDirect(() -> isWorking = false);
                }, err -> {
                    AndroidSchedulers.mainThread().scheduleDirect(() -> isWorking = false);
                });

        compositeDisposable.add(disposable);

    }

    private void startSyncThreads() {
        L.d(TAG, "begin startSyncThreads");
        if (isWorking || CommonUtils.getToken(getApplicationContext()) == null) {
            return;
        }

        isWorking = true;
        // TODO: 2019-06-11 configuration
        startSyncThreads(PAGE_FROM, PAGE_SIZE);
    }
}
