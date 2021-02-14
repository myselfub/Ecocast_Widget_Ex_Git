package com.example.ecocast_widget.utils;

import android.os.Binder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogWrapper {
    private static final String TAG = LogWrapper.class.getSimpleName();
    private static final int LOG_FILE_SIZE_LIMIT = 512 * 1024;
    private static final int LOG_FILE_MAX_COUNT = 2;
    private static final String LOG_FILE_NAME = "Ecocast_Log_File_%g.txt";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private static final Date date = new Date();
    private static Logger logger;
    private static FileHandler fileHandler;
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath();

    static {
        try {
            fileHandler = new FileHandler(path + File.separator + LOG_FILE_NAME, LOG_FILE_SIZE_LIMIT, LOG_FILE_MAX_COUNT, true);
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    date.setTime(System.currentTimeMillis());
                    StringBuilder stringBuilder = new StringBuilder(80);
                    stringBuilder.append(formatter.format(date)).append(record.getMessage());
                    return stringBuilder.toString();
                }
            });
            logger = Logger.getLogger(LogWrapper.class.getName());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);
            Log.d(TAG, "Init Success");
        } catch (IOException ioe) {
            Log.d(TAG, "Init Failure");
            ioe.printStackTrace();
        }
    }

    public static void v(String tag, String msg) {
        if (logger != null) {
            logger.log(Level.INFO, String.format("V/%s(%d): %s\n", tag, Binder.getCallingPid(), msg));
        }
        Log.v(tag, msg);
    }
}
