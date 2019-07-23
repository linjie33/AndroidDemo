package com.planetwalk.ponion.media;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestMediaResolver {

    private static final String[] filenames = new String[] {
            "544x1184x0.mp4", "1280x720x270.mp4", "audio_only.mp4"};
    private static final int[][] metaValues = new int[][] {
            new int[] {544, 1184, 0}, new int[] {1280, 720, 270}, new int[] {960,540,0}};

    @After
    public void removeFiles() {

    }

    @Before
    public void prepareFiles() {
        Context testContext = InstrumentationRegistry.getContext();
        Context targetContext = InstrumentationRegistry.getTargetContext();
        File dir = new File(targetContext.getCacheDir(), "test-video");
        if (!dir.exists()) {
            boolean success = dir.mkdir();
            if (!success) {
                return;
            }
        }
        for (int i = 0; i< filenames.length; i++) {
            File videoFile = new File(dir, filenames[i]);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            if (!videoFile.exists()) {
                try {
                    videoFile.createNewFile();
                    videoFile.deleteOnExit();
                    inputStream = testContext.getAssets().open(filenames[i]);
                    outputStream = new FileOutputStream(videoFile);
                    IOUtils.copy(inputStream, outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outputStream);
                    if (videoFile.exists()) {
                        videoFile.delete();
                    }
                }
            }
            if (!videoFile.exists()) {
                Log.d("Test", "copy file failed");
                continue;
            }
        }
    }

    @Test
    public void testZeroRotationResolver() {
        testMediaResolverMeta(0);
    }

    @Test
    public void testRotationResolver() {
        testMediaResolverMeta(1);
    }

    private void testMediaResolverMeta(int i) {
        Context context = InstrumentationRegistry.getTargetContext();
        File dir = new File(context.getCacheDir(), "test-video");
        File videoFile = new File(dir, filenames[i]);
        int[] meta = MediaResolver.getVideoMeta(videoFile.getAbsolutePath());
        Log.d("TestPo", meta[0] + ", " + meta[1]);
        Assert.assertEquals(metaValues[i][0], meta[0]);
        Assert.assertEquals(metaValues[i][1], meta[1]);
        Assert.assertNotEquals(0, meta[2]);
        Assert.assertEquals(metaValues[i][2], meta[3]);
    }

    @Test
    public void testZeroRotationCompressor() {
        testCompress(0);
    }

    @Test
    public void testRotationCompressor() {
       testCompress(1);
    }

    @Test
    public void testAudioOnlyCompressor() {
        testCompress(2);
    }

    private void testCompress(int i) {
        prepareFiles();
        Context context = InstrumentationRegistry.getTargetContext();
        File dir = new File(context.getCacheDir(), "test-video");
        File videoFile = new File(dir, filenames[i]);
        File outFile = MediaCompressor.compressVideo(context, videoFile);
        int[] meta = MediaResolver.getVideoMeta(videoFile.getAbsolutePath());
        int width = meta[0];
        int height = meta[1];
        int maxLen = (meta[3] / 90) % 2 == 0 ? width: height;
        float ratio = (float) Math.min(720, maxLen) / (float)maxLen;
        width = (int) (width * ratio);
        height = (int) (height * ratio);
        width = width % 2 == 0 ? width : width + 1;
        height = height % 2 == 0 ? height : height + 1;
        if ((meta[3] / 90) % 2 != 0) {
            int tmp = height;
            height = width;
            width = tmp;
        }
        meta = MediaResolver.getVideoMeta(outFile.getAbsolutePath());
        Assert.assertEquals(filenames[i], width, meta[0]);
        Assert.assertEquals(filenames[i], height, meta[1]);
        Assert.assertNotEquals(filenames[i], meta[2]);
        Assert.assertEquals(filenames[i], 0, meta[3]);
    }
}