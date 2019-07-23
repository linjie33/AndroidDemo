package com.planetwalk.ponion.rapi.converter;

import com.planetwalk.ponion.Gsons;
import com.planetwalk.ponion.Utils.DateUtil;
import com.planetwalk.ponion.db.Entity.SlideEntity;
import com.planetwalk.ponion.db.Entity.ThreadEntity;
import com.planetwalk.ponion.rapi.model.RemoteThread;

import java.util.List;

public class ThreadConverter {
    public static RemoteThread convertToRemote(List<SlideEntity> entityList) {
        return new RemoteThread(Gsons.value().toJson(entityList));
    }

    public static ThreadEntity convertToLocal(RemoteThread thread) {
        ThreadEntity entity = new ThreadEntity();
        entity.serverId = thread.id;
        entity.authorId = thread.uid;
        entity.content = thread.fragments;
        entity.publicTime = DateUtil.utcStringToDate(thread.gtmCreated);
        return entity;
    }
}
