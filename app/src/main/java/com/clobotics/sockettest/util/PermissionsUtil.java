package com.clobotics.sockettest.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Author: Aya
 * Date: 2020/3/6
 * Description:
 */
public class PermissionsUtil {
    private static final int REQUEST_PERMISSION_CODE = 12345;

    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
    };

    private static List<String> missingPermission = new ArrayList<>();

    public static boolean hasPermissions(Context context){
        return EasyPermissions.hasPermissions(context, REQUIRED_PERMISSION_LIST);
    }


    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    public static void checkAndRequestPermissions(Activity activity) {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(activity, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (!missingPermission.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, missingPermission.toArray(new String[missingPermission.size()]), REQUEST_PERMISSION_CODE);
        }
    }

    public static void requestPhotoPermissions(Activity activity, int param) {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        EasyPermissions.requestPermissions(activity, "拍照需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", param, perms);
    }



}
