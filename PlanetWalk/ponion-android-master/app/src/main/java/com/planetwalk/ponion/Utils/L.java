package com.planetwalk.ponion.Utils;

import android.content.Context;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.planetwalk.ponion.BuildConfig;

public class L {

    private static final String LOG_DIR = "logs";

    private static String buildMessage(String tag, String message) {
        return tag + " " + message;
    }

    public static void init(Context context) {
        Printer android = new AndroidPrinter();
        if (!BuildConfig.DEBUG) {
            Printer file = new FilePrinter
                    .Builder(context.getExternalFilesDir(LOG_DIR).getAbsolutePath())
                    .fileNameGenerator(new DateFileNameGenerator())
                    .backupStrategy(new NeverBackupStrategy())
                    .build();
            XLog.init(LogLevel.DEBUG, android, file);
        } else {
            XLog.init(LogLevel.ALL, android);
        }
    }

    public static void i(String tag, String message) {
        XLog.i(buildMessage(tag, message));
    }

    public static void v(String tag, String message) {
        XLog.v(buildMessage(tag, message));
    }

    public static void d(String tag, String message) {
        XLog.d(buildMessage(tag, message));
    }

    public static void e(String tag, String message) {
        XLog.e(buildMessage(tag, message));
    }

    public static void e(String tag, String message, Throwable e) {
        XLog.e(buildMessage(tag, message), e);
    }

}
