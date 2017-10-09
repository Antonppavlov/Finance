package ru.barmaglot.android.myfinance;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class AppContext extends Application {

    private static final String TAG = AppContext.class.getName();
    private static final String DB_NAME = "money.db";
    private static String dbFolder;
    private static String dbPath;

    @Override
    public void onCreate() {
        super.onCreate();
        checkDbExist(this);
    }

    private void checkDbExist(Context context) {
        dbFolder = context.getApplicationInfo().dataDir + "/database/";
        dbPath = dbFolder + DB_NAME;

        if (!new File(dbPath).exists()) {
            copyDataBase(context);
        }
    }

    private void copyDataBase(Context context) {
        try (
                InputStream inputStream = context.getAssets().open(DB_NAME);
                OutputStream outputStream = new FileOutputStream(dbPath);
        ) {
            File databaseFolder = new File(dbFolder);
            databaseFolder.mkdir();

            byte[] buffer = new byte[1024];
            int length;
            while ((length=inputStream.read(buffer))>0){
                outputStream.write(buffer,0,length);
            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
