package ru.barmaglot.android.myfinance.app;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ru.barmaglot.android.myfinance.database.Initializer;


public class DBConnection {
    private static final String TAG = AppContext.class.getName();
    private static final String DB_NAME = "money.db";
    private static final String DRIVER_CLASS = "org.sqldroid.SQLDroidDriver";
    private static String dbFolder;
    private static String dbPath;


    public static void initConnection(Context context) {
        checkDbExist(context);

        String url = "jdbc:sqldroid:" + dbPath;
        Initializer.load(DRIVER_CLASS, url);
    }


    private static void checkDbExist(Context context) {
        dbFolder = context.getApplicationInfo().dataDir + "/databases/";
        dbPath = dbFolder + DB_NAME;

        if (!new File(dbPath).exists()) {
            copyDataBase(context);
        }
    }

    private static void copyDataBase(Context context) {
        File databaseFolder = new File(dbFolder);
        databaseFolder.mkdir();

        try (
                InputStream inputStream = context.getAssets().open(DB_NAME);
                OutputStream outputStream = new FileOutputStream(dbPath)
        ) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}

