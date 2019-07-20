package com.albert.uaes.bluetensorflow.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    private static FileUtils mInstance;

    private static final String TAG = FileUtils.class.getName();

    private static final String pathName = Environment.getExternalStorageDirectory().getPath()+"/ScanResult/";


    public static FileUtils getInstance() {
        if (mInstance == null) {
            synchronized (FileUtils.class) {
                if (mInstance == null) {
                    mInstance = new FileUtils();
                }
            }
        }
        return mInstance;
    }

    public File createFile(String fileName){
        File path = new File(pathName);
        boolean isCreated = false;

        if (!path.exists()){
            path.mkdir();
        }

        File file =  new File(pathName+fileName);

        if (!file.exists()){
            try {
                isCreated = file.createNewFile();
                if (isCreated){
                    Log.d(TAG,fileName+" create success");
                    Log.e(TAG,file.getAbsolutePath());

                }else {
                    Log.d(TAG,fileName+" create failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (isCreated){
            return file;
        }else {
            return null;
        }
    }
}
