package com.planetwalk.ponion.rapi.service;

import com.planetwalk.ponion.rapi.model.PlanetWalkResponse;
import com.planetwalk.ponion.rapi.model.RemoteThreadLike;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LikeService {

    @FormUrlEncoded
    @POST("/like/up")
    Observable<PlanetWalkResponse<Object>> like(
            @Field("tid") long tid,
            @Field("kind") int kind,
            @Field("oldKind") int old,
            @Query("token") String token
    );

    @FormUrlEncoded
    @POST("/like/down")
    Observable<PlanetWalkResponse<Object>> revoke(
            @Field("tid") long tid,
            @Field("kind") int kind,
            @Query("token") String token
    );

    @GET("/like/count")
    Observable<PlanetWalkResponse<Long>> count(
            @Query("tid") long tid,
            @Query("kind") long kind,
            @Query("token") String token
    );

    @GET("/like/thread")
    Observable<PlanetWalkResponse<List<RemoteThreadLike>>> thread(
            @Query("tid") long tid,
            @Query("token") String token
    );
}
