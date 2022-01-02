package run.ccfish.android.apkinstaller;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.util.Log;

import com.litesuits.common.utils.ShellUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 更新程序
 */
public class UpdateUtil {
    static final String TAG = UpdateUtil.class.getSimpleName();

    public static String updateApp(Context context, String newApkUrl) throws IOException {
        final File installDir = Environment.getExternalStorageDirectory();
        Log.i(TAG, "下载apk: "+newApkUrl);
        File new_apk = new File(installDir, "app.apk");
        downloadFile(newApkUrl, new_apk.getAbsolutePath());
        Log.i(TAG, "安装包下载成功: "+new_apk.getAbsolutePath());

        File installer_apk = new File(installDir, "APKInstaller.apk");
        Log.i(TAG, "释放APKInstaller: "+installer_apk.getAbsolutePath());
        FileUtil.copyFile(context.getAssets().open("APKInstaller-release-signed.apk"), installer_apk);
        Log.i(TAG, "APKInstaller保存成功: "+installer_apk.getAbsolutePath());

        Log.i(TAG, "安装APKInstaller...");
        installAndStart(context, installer_apk);

        Log.i(TAG, "APKInstaller安装成功 启动之后开始更新app");
        return null;
    }

    /**
     * 下载网络文件
     * @param url
     * @param file
     * @return
     */
    public static void downloadFile(String url, String file) throws IOException {
        int bytesum = 0;
        int byteread = 0;
        URL httpURL = new URL(url);
        URLConnection conn = httpURL.openConnection();
        InputStream inStream = conn.getInputStream();
        FileOutputStream fs = new FileOutputStream(file);

        byte[] buffer = new byte[1204];
        int length;
        while ((byteread = inStream.read(buffer)) != -1) {
            bytesum += byteread;
            fs.write(buffer, 0, byteread);
        }
        fs.flush();
        fs.close();
    }

    public static void installAndStart(Context context, File apk){
        String cmd_install = "pm install -r -i "+context.getPackageName()+" "+apk.getAbsolutePath();
        //获取要安装的apk包名，用来启动它
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apk.getAbsolutePath(), 0);
        String cmd_start = "am start "+packageInfo.packageName+"/.MainActivity";
        Log.i(TAG, "安装命令: "+cmd_install);
        Log.i(TAG, "启动命令: "+cmd_start);

        ShellUtil.CommandResult result = ShellUtil.execCommand(cmd_install, false);
        Log.i(TAG, "安装 result="+result.responseMsg);
        Log.i(TAG, "安装 error="+result.errorMsg);
        result = ShellUtil.execCommand(cmd_start, false);
        Log.i(TAG, "启动 result="+result.responseMsg);
        Log.i(TAG, "启动 error="+result.errorMsg);
    }
}
