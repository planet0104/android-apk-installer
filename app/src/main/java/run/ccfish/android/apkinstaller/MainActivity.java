package run.ccfish.android.apkinstaller;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.litesuits.common.utils.ShellUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "APKInstaller";
    /** 要安装的apk文件 */
    static final File INSTALL_APK =  new File(Environment.getExternalStorageDirectory(), "app.apk");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String cmd_install = "pm install -r -i "+getPackageName()+" "+INSTALL_APK.getAbsolutePath();
        PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(INSTALL_APK.getAbsolutePath(), 0);
        String cmd_start = "am start "+packageInfo.packageName+"/.MainActivity";
        Log.i(TAG, "安装命令: "+cmd_install);
        Log.i(TAG, "启动命令: "+cmd_start);

        new Thread(() -> {
            ShellUtil.CommandResult result = ShellUtil.execCommand(cmd_install, false);
            Log.i(TAG, "安装 result="+result.responseMsg);
            Log.i(TAG, "安装 error="+result.errorMsg);
            result = ShellUtil.execCommand(cmd_start, false);
            Log.i(TAG, "启动 result="+result.responseMsg);
            Log.i(TAG, "启动 error="+result.errorMsg);
            runOnUiThread(this::finish);
        }).start();
    }

    private void requestPermission(){
        int permission_write= ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_read=ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission_write!= PackageManager.PERMISSION_GRANTED
                || permission_read!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "正在请求权限", Toast.LENGTH_SHORT).show();
            //申请权限，特征码自定义为1，可在回调时进行相关判断
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }
}