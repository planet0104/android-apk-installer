package run.ccfish.android.apkinstaller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FileUtil {
    /**
     * 是否有外部存储权限
     * @param context
     * @return
     */
    public static boolean hasExternalStoragePermission(Context context){
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int permission_write = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_read = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission_read == PackageManager.PERMISSION_GRANTED &&
                permission_write == PackageManager.PERMISSION_GRANTED;
    }

    public static File newRootFile(Context context, String fileName){
        return new File(Environment.getExternalStorageDirectory(), fileName);
    }

    public static String readExternalStorageFile(Context context, File file) throws IOException {
        if(hasExternalStoragePermission(context)){
            return inputStreamToString(new FileInputStream(file));
        }else{
            return null;
        }
    }

    public static boolean writeExternalStorageFile(Context context, File file, String data) throws IOException {
        if(hasExternalStoragePermission(context)){
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
            return true;
        }
        return false;
    }

    public static final int REQUEST_PERMISSION_CODE = 1;
    public static void requestExternalStoragePermissions(Activity context) {
        int permission_write = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_read = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission_read != PackageManager.PERMISSION_GRANTED ||
                permission_write != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }

    private static String inputStreamToString(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String str = sb.toString();
        return str;
    }

    public static void copyFile(File from, File to) throws IOException {
        InputStream inputStream = new FileInputStream(from);
        OutputStream outputStream = new FileOutputStream(to);
        int length;
        byte[] bytes = new byte[1024];
        while ((length = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, length);
        }
    }

    public static void copyFile(InputStream from, File to) throws IOException {
        OutputStream outputStream = new FileOutputStream(to);
        int length;
        byte[] bytes = new byte[1024];
        while ((length = from.read(bytes)) != -1) {
            outputStream.write(bytes, 0, length);
        }
    }
}
