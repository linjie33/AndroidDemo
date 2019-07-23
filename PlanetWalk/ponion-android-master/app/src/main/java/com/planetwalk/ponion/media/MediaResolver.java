package com.planetwalk.ponion.media;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLConnection;

public class MediaResolver {

    public static String getUriRealPath(Context context, Uri uri) {
        if (isAboveKitKat()) {
            return getUriRealPathAboveKitkat(context, uri);
        } else {
            return getImageRealPath(context.getContentResolver(), uri, null);
        }
    }

    public static void cleanFileProvider(Context context, String realPath, Uri uri) {
        if (isFileProviderUri(context, uri)) {
            File file = new File(realPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static boolean isFileProviderUri(Context context, Uri uri) {
        return uri.getAuthority().startsWith(context.getPackageName());
    }

    public static String getFileProviderRealPath(Context context, Uri uri) {
        FileOutputStream outputStream = null;
        InputStream fpInputStream = null;
        try {
            fpInputStream = context.getContentResolver().openInputStream(uri);
            File cacheDir = new File(context.getCacheDir(), "file_provider");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File tmpfile = File.createTempFile("file_provider", "jpeg", cacheDir);
            outputStream = new FileOutputStream(tmpfile);
            ByteStreams.copy(fpInputStream, outputStream);
            return tmpfile.getAbsolutePath();
        } catch (Exception e) {
            return null;
        } finally {
            Closeables.closeQuietly(fpInputStream);
            try {
                outputStream.close();
            } catch (Exception e) {

            }
        }
    }

    private static String getUriRealPathAboveKitkat(Context context, Uri uri) {

        String ret = "";

        if (context != null && uri != null) {
            if (isFileProviderUri(context, uri)) {
                ret = getFileProviderRealPath(context, uri);
            } else if (isContentUri(uri)) {
                ret = getImageRealPath(context.getContentResolver(), uri, null);
            } else if (isFileUri(uri)) {
                ret = uri.getPath();
            } else if (isDocumentUri(context, uri)) {
                String documentId = DocumentsContract.getDocumentId(uri);
                String uriAuthority = uri.getAuthority();
                if (isMediaDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        String docType = idArr[0];
                        String realDocId = idArr[1];
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if ("image".equals(docType)) {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(docType)) {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(docType)) {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(context.getContentResolver(), mediaContentUri, whereClause);
                    }

                } else if (isDownloadDoc(uriAuthority)) {
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));
                    ret = getImageRealPath(context.getContentResolver(), downloadUriAppendId, null);
                } else if (isExternalStoreDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        String type = idArr[0];
                        String realDocId = idArr[1];
                        if ("primary".equalsIgnoreCase(type)) {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    private static boolean isAboveKitKat() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    private static boolean isDocumentUri(Context ctx, Uri uri) {
        if (ctx != null && uri != null) {
            return DocumentsContract.isDocumentUri(ctx, uri);
        }
        return false;
    }

    private static boolean isContentUri(Uri uri) {
        if (uri != null) {
            String uriSchema = uri.getScheme();
            return "content".equalsIgnoreCase(uriSchema);
        }
        return false;
    }

    private static boolean isFileUri(Uri uri) {
        if (uri != null) {
            String uriSchema = uri.getScheme();
            return "file".equalsIgnoreCase(uriSchema);
        }
        return false;
    }


    private static boolean isExternalStoreDoc(String uriAuthority) {

        return "com.android.externalstorage.documents".equals(uriAuthority);
    }

    private static boolean isDownloadDoc(String uriAuthority) {

        return "com.android.providers.downloads.documents".equals(uriAuthority);
    }

    private static boolean isMediaDoc(String uriAuthority) {

        return "com.android.providers.media.documents".equals(uriAuthority);
    }

    private static String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {

        Cursor cursor = contentResolver.query(
                uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {
                String columnName = MediaStore.Images.Media.DATA;
                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA;
                } else if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Audio.Media.DATA;
                } else if (uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Video.Media.DATA;
                }

                int imageColumnIndex = cursor.getColumnIndex(columnName);

                return cursor.getString(imageColumnIndex);
            }
        }

        return null;
    }

    public static String guessMimeType(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            String mimeType = URLConnection.guessContentTypeFromStream(fileInputStream);
            if (mimeType == null) {
                mimeType = URLConnection.guessContentTypeFromName(file.getName());
            }
            return mimeType;
        } catch (Exception e) {
            return null;
        } finally {
            Closeables.closeQuietly(fileInputStream);
        }
    }

    public static boolean isVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith("video/");
    }

    public static boolean isVideo(Context context, Uri uri) {
        String path = getFileProviderRealPath(context, uri);
        return isVideo(guessMimeType(new File(path)));
    }

    public static boolean isGif(String mimeType) {
        return mimeType.startsWith("image/gif");
    }

    public static int[] getVideoMeta(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String bitrateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        String rotateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        int width = 0, height = 0, bitrate = 0, rotate = 0;
        try {
            width = Integer.valueOf(widthStr);
            height = Integer.valueOf(heightStr);
            bitrate = Integer.valueOf(bitrateStr);
            rotate = Integer.valueOf(rotateStr);
        } catch (Exception e) {

        }
        return new int[]{width, height, bitrate, rotate};
    }
}
