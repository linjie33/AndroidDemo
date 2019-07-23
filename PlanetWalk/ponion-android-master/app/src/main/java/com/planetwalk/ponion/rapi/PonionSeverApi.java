package com.planetwalk.ponion.rapi;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PonionSeverApi {

    private Retrofit retrofit;

    private static class Internal {
        static PonionSeverApi sInstance = new PonionSeverApi();
    }

    private PonionSeverApi() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.PONION_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static Retrofit api() {
        return Internal.sInstance.retrofit;
    }

}
