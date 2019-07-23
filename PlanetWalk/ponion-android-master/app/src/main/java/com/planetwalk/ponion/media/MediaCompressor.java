package com.planetwalk.ponion.media;

import android.content.Context;

import com.iceteck.silicompressorr.SiliCompressor;
import com.planetwalk.ponion.Utils.L;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class MediaCompressor {

    public static final String TAG = MediaCompressor.class.getSimpleName();
    private static final int VIDEO_COMPRESS_MAX_BITRATE = 3000000;
    private static final int VIDEO_COMPRESS_MAX_WIDTH = 720;

    public static File compressImage(Context context, File origin) {

        File cacheDir = new File(context.getCacheDir(), "compressed");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        try {
            return new Compressor(context)
                    .setDestinationDirectoryPath(cacheDir.getAbsolutePath())
                    .compressToFile(origin);
        } catch (IOException e) {
            L.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static File compressVideo(Context context, File origin) {
        File cacheDir = new File(context.getCacheDir(), "compressed_v");
        File convertDir = new File(context.getCacheDir(), "converted_v");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        if (!convertDir.exists()) {
            convertDir.mkdirs();
        }
        String filePath, convertPath;
        File targetFile, tmpFile;
        int outWidth = 0, outHeight = 0, outBitrate = 0, maxLen;
        try {
            int[] originMeta = MediaResolver.getVideoMeta(origin.getAbsolutePath());
            outWidth = originMeta[0];
            outHeight = originMeta[1];
            maxLen = (originMeta[3] / 90) % 2 == 0 ? outWidth : outHeight;
            float ratio = (float) Math.min(VIDEO_COMPRESS_MAX_WIDTH, maxLen) / (float)maxLen;
            outHeight = (int) (outHeight * ratio);
            outWidth = (int) (outWidth * ratio);
            outHeight = outHeight % 2 == 0 ? outHeight : outHeight + 1;
            outWidth = outWidth % 2 == 0 ? outWidth : outWidth + 1;
            outBitrate = Math.min(originMeta[2], VIDEO_COMPRESS_MAX_BITRATE);
        } catch (Exception e) {
            L.e(TAG, e.getMessage(), e);
        }
        try {
            filePath = SiliCompressor.with(context)
                    .compressVideo(
                            origin.getAbsolutePath(),
                            cacheDir.getAbsolutePath(),
                            outWidth, outHeight, outBitrate);

            tmpFile = new File(convertDir, origin.getName());
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            tmpFile.createNewFile();
            convertPath = tmpFile.getAbsolutePath();
            try {
                MoovConverter.fastStart(filePath, convertPath);
                tmpFile  = new File(filePath);
                targetFile = new  File(convertPath);
                if (!targetFile.exists() || targetFile.length() == 0) {
                    File t = targetFile;
                    targetFile = tmpFile;
                    tmpFile = t;
                }
            } catch (Exception e) {
                L.d(TAG, "convert mp4 file failed:" + origin.getAbsolutePath());
                tmpFile  = new File(convertPath);
                targetFile = new File(filePath);
            }
            if (tmpFile.exists()){
                tmpFile.delete();
            }
            return targetFile;
        } catch (Exception e) {
            return null;
        }
    }
}
