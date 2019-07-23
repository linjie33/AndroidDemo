package com.planetwalk.ponion.rapi.service;

import com.planetwalk.ponion.db.Entity.PostEntity;
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

public interface PostService {

    @POST("/posts/add ")
    Observable<PlanetWalkResponse<Integer>> addPost(@Body PostEntity body, @Query("token") String token);

    @POST("/posts/list")
    Observable<PlanetWalkResponse<PagedData<PostEntity>>> fetchPosts(
            @Query("tid") long threadId,
            @Query("page") int page, @Query("size") int size,
            @Query("token") String token);

    @GET("/posts/v1/list")
    Observable<PlanetWalkResponse<List<PostEntity>>> fetchPostsV1(
            @Query("tid") long threadId,
            @Query("from") long from, @Query("size") int size,
            @Query("token") String token);
}
