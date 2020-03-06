package com.clobotics.sockettest.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Author: Aya
 * Date: 2020/3/6
 * Description:
 */
public class FileManager {
    private static FileManager mInstance;
    private static int count = 0;

    public static FileManager getInstance() {
        if (mInstance == null) {
            mInstance = new FileManager();
        }
        return mInstance;
    }

    private FileManager() {
    }

    private String folderPath = "/Clobotics/SocketTestDemo/";

    private boolean isFolderExistsOrCreate(String folder) {
        File file = new File(folder);
        if (file.exists() == false) {
            if (file.mkdirs() == true) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void saveData(String data) {
        File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录
        String fileDirectory = sdCardDir + folderPath;
        if (isFolderExistsOrCreate(fileDirectory) == true) {
            saveFile(fileDirectory + "/getTurbine"+"-"+count+".txt", data);
            count++;
        }
    }

    private void saveFile(String filename, String... contents) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            for (String content : contents) {
                content = content + "\n";
                fos.write(content.getBytes());
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
