package com.planetwalk.ponion.rapi;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlanetWalkMediaSeverApi {

    private Retrofit retrofit;

    private static class Internal {
        static PlanetWalkMediaSeverApi sInstance = new PlanetWalkMediaSeverApi();
    }

    private PlanetWalkMediaSeverApi() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.PLANETWALK_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static Retrofit api() {
        return Internal.sInstance.retrofit;
    }

}
