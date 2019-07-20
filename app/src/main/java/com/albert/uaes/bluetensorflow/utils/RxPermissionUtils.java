package com.albert.uaes.bluetensorflow.utils;

import android.app.Activity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class RxPermissionUtils {



//    public static boolean applyPermission(Activity activity, final ApplyPermissionCallback permissionCallback, String... permissions){
//        RxPermissions rxPermissions = new RxPermissions(activity);
//
//        rxPermissions.requestEach(permissions).subscribe(new Consumer<Permission>() {
//            @Override
//            public void accept(Permission permission) throws Exception {
//                if (permission.granted) {
//                    XLog.d("permission_granted" + permission.name);
//                }
//                if (permission.shouldShowRequestPermissionRationale) {
//                    permissionCallback.onPermissionsDenied();
//                    XLog.d("permission_denied" + permission.name);
//                    return;
//                }
//                permissionCallback.onPermissionsGranted();
//            }
//        });
//        return true;
//    }

    public static void checkPermissionRequest(Activity activity, final ApplyPermissionCallback permissionCallback, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(permissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            permissionCallback.onPermissionsGranted();
                        }else {
                            permissionCallback.onPermissionsDenied();
                        }
                    }
                });
    }

    public  interface ApplyPermissionCallback{
        void onPermissionsGranted();
        void onPermissionsDenied();
    }

}
