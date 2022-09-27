package chatapp.extras;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Sely on 10-Sep-16.
 */
public class Permissions {
    public static final int PERMISSION_READ_PHONE_STATE = 0;
    public static final int PERMISSION_READ_CONTACTS = 1;
    public static final int PERMISSION_CAMERA = 2;
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 3;
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 4;

    public static boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    public static void getPermissionForCamera(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        }
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("MyApp", "SDK >= 23");
            if (activity.checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyApp", "Request permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_REQUEST_CODE);

                if (! activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showMessageOKCancel("You need to allow camera usage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(new String[] {Manifest.permission.CAMERA},
                                            MY_REQUEST_CODE);
                                }
                            });
                }
            }
            else {
                Log.d("MyApp", "Permission granted: taking pic");
                takePicture();
            }
        }
        else {
            Log.d("MyApp", "Android < 6.0");
        }*/
    }

    public static void getPermissionForContacts(Activity activity) {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACTS);
        }
    }

    public static void getPermissionForReadPhoneState(Activity activity) {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_PHONE_STATE);
        }
    }

    public static void getPermissionForWriteExternalStorage(Activity activity) {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    public static void getPermissionForReadExternalStorage(Activity activity) {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }

    public static void getAllRequiredPermissions(Activity activity){
        getPermissionForContacts(activity);
        getPermissionForReadPhoneState(activity);
        getPermissionForCamera(activity);
        getPermissionForWriteExternalStorage(activity);
        getPermissionForReadExternalStorage(activity);
    }
}
