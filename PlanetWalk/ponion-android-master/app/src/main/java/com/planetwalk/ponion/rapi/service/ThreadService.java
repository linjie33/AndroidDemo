package com.planetwalk.ponion.rapi.service;

import com.planetwalk.ponion.rapi.model.EmojiComment;
import com.planetwalk.ponion.rapi.model.PagedData;
import com.planetwalk.ponion.rapi.model.PlanetWalkResponse;
import com.planetwalk.ponion.rapi.model.RemoteThread;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ThreadService {

    @POST("/threads/add")
    Observable<PlanetWalkResponse<Integer>> addThread(@Body RemoteThread body, @Query("token") String token);

    @GET("/threads/v1/list")
    Observable<PlanetWalkResponse<PagedData<RemoteThread>>> fetchThreadsV1(
            @Query("from") long from, @Query("size") int size,
            @Query("token") String token);

    @GET("/threads/gems")
    Observable<PlanetWalkResponse<List<EmojiComment>>> getEmojiComments(@Query("tid") long threadId, @Query("token") String token);
}
