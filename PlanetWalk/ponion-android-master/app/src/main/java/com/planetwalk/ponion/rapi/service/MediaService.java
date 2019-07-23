package com.planetwalk.ponion.rapi.service;

import com.planetwalk.ponion.rapi.model.MediaStore;
import com.planetwalk.ponion.rapi.model.PlanetWalkResponse;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MediaService {

    @Multipart
    @POST("/media/add")
    Observable<PlanetWalkResponse<MediaStore>> uploadMedia(@Part MultipartBody.Part media);

    @Multipart
    @POST("/media/add")
    Call<PlanetWalkResponse<MediaStore>> uploadMediaCall(@Part MultipartBody.Part media);
}
