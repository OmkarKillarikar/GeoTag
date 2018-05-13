package com.poc.okmac.geotag.Utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.Date;

public class AppFileManager {
    private Context context;
    private static String FOLDER_NAME = "GeoTag";

    public AppFileManager(Context context) {
        this.context = context;
    }

    public Uri getFileUriToWrite(String authority) {
        Uri fileUri = null;
        File imageFile = getNewFileToWrite();
        if (imageFile != null) {
            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                // For Marshmallow and below versions
                fileUri = Uri.fromFile(imageFile);
            } else {
                fileUri = FileProvider.getUriForFile(context, authority + ".provider", imageFile);
            }
        }
        return fileUri;
    }

    private File getNewFileToWrite() {
        long newImageName = new Date().getTime();
        boolean folderCreated;
        File cameraDirectory = new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME);
        if (!cameraDirectory.exists()) {
            folderCreated = cameraDirectory.mkdir();
            new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME + "/.noMedia");
        } else {
            new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME + "/.noMedia");
            folderCreated = true;
        }
        if (folderCreated) {
            return new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME + "/" + newImageName + ".jpg");
        } else {
            return null;
        }
    }

    public File getExistingFile(String fileName) {
        boolean folderCreated;
        File cameraDirectory = new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME);
        if (!cameraDirectory.exists()) {
            folderCreated = cameraDirectory.mkdir();
            new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME + "/.noMedia");
        } else {
            new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME + "/.noMedia");
            folderCreated = true;
        }
        if (folderCreated) {
            return new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME + "/" + fileName);
        } else {
            return null;
        }
    }

}
