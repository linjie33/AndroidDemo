package com.planetwalk.ponion.media;

import android.content.Context;

import com.planetwalk.ponion.Utils.L;
import com.planetwalk.ponion.rapi.PlanetWalkMediaSeverApi;
import com.planetwalk.ponion.rapi.model.MediaStore;
import com.planetwalk.ponion.rapi.model.PlanetWalkResponse;
import com.planetwalk.ponion.rapi.service.MediaService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class MediaUploader {

    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final String TAG = MediaUploader.class.getSimpleName();

    public String uploadMedia(Context context, String filePth, boolean shouldCompress) {
        File file = new File(filePth);
        boolean shouldCleanFile = false;

        if (!file.exists()) {
            return null;
        }
        File targetFile;
        String mimeType = MediaResolver.guessMimeType(file);
        // 暂时不加入GIF的压缩
        if (shouldCompress && !MediaResolver.isGif(mimeType)) {
            if (MediaResolver.isVideo(mimeType)) {
                targetFile = MediaCompressor.compressVideo(context, file);
            } else {
                targetFile = MediaCompressor.compressImage(context, file);
            }
            if (targetFile != null) {
                shouldCleanFile = true;
            }
        } else {
            targetFile = file;
        }

        RequestBody uploadFile = RequestBody
                .create(MediaType.parse("application/octet-stream"),
                targetFile == null ? file : targetFile);

        MultipartBody.Part body = MultipartBody.Part
                .createFormData("media", file.getName(), uploadFile);

        Call<PlanetWalkResponse<MediaStore>> call = PlanetWalkMediaSeverApi.api()
                .create(MediaService.class).uploadMediaCall(body);
        int retry = DEFAULT_RETRY_COUNT;
        String ret = null;
        do {
            try {
                Response<PlanetWalkResponse<MediaStore>> response = call.execute();
                PlanetWalkResponse<MediaStore> planetWalkResponse = response.body();
                if (planetWalkResponse.getCode() != 200) {
                    ret = null;
                } else {
                    ret = planetWalkResponse.getData().target;
                }
            } catch (Exception e) {
                L.d(TAG, "retry for " + e);
                retry--;
            }
        } while (retry >= 0);
        if (shouldCleanFile && targetFile.exists()) {
            targetFile.delete();
        }
        return ret;
    }
}
