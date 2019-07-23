package com.planetwalk.ponion;

import android.content.Context;

public class GlobalData {
    public static final String PET_PREFERENCE = "com.planetwalk.poketalk.petEntity";

    public static int[] petAvatars = {
    };

    public static int[] avatars = {R.drawable.avatar_images_001, R.drawable.avatar_images_002, R.drawable.avatar_images_003,
            R.drawable.avatar_images_004, R.drawable.avatar_images_005};

    public static int[] BKG_COLOR_LIST = {
            R.color.bg1,
            R.color.bg2,
            R.color.bg3,
            R.color.bg4,
            R.color.bg5,
            R.color.bg6,
            R.color.bg7,
            R.color.bg8};

    public static Context sApplicationContext;

    private static boolean sIsFirstTimeInit = true;

    public static boolean isFirstTimeInit() {
        boolean ret = sIsFirstTimeInit;
        sIsFirstTimeInit = false;
        return ret;
    }
}
