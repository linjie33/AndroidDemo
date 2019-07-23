package com.planetwalk.ponion.media;

import android.content.Context;
import android.net.Uri;

import com.google.common.base.Strings;
import com.planetwalk.ponion.db.Entity.SlideEntity;
import com.planetwalk.ponion.media.exception.ConvertFailedException;
import com.planetwalk.ponion.rapi.Constants;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class SlideImageConverter {
    private static final List<String> REMOTE_URLS = Arrays.asList("http://", "https://");
    private static final String MEDIA_SERVER_BASE_URI = "/media/fetch?path=%s";

    private boolean isRemoteUri(String uri) {
        for (String prefix : REMOTE_URLS) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public String urlForMediaStore(String remotePath) {
        return Constants.PLANETWALK_BASE_URL +
                        String.format(MEDIA_SERVER_BASE_URI, URLEncoder.encode(remotePath));
    }

    public void convert(Context context, SlideEntity entity) throws ConvertFailedException {

        if (Strings.isNullOrEmpty(entity.mediaUrl) || isRemoteUri(entity.mediaUrl)) {
            return;
        }
        Uri uri = Uri.parse(entity.mediaUrl);
        String realPath = entity.localPath == null ?
            MediaResolver.getUriRealPath(context, Uri.parse(entity.mediaUrl)) : entity.localPath;
        String remoteId = new MediaUploader().uploadMedia(context, realPath, true);
        MediaResolver.cleanFileProvider(context, realPath, uri);
        if (remoteId != null) {
            entity.mediaUrl = urlForMediaStore(remoteId);
        } else {
            throw new ConvertFailedException(entity.mediaUrl, realPath);
        }
    }
}
