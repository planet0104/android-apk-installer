package run.ccfish.android.apkinstaller;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.litesuits.common.utils.ShellUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "APKInstaller";
    Button btn_install;
    ProgressDialog progressDialog;
    EditText et_cmd;
    Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_cmd = findViewById(R.id.et_cmd);
        btn_start = findViewById(R.id.btn_start);
        //必须要申请权限才能调用 pm install
        //如果是 system权限的app(android:sharedUserId="android.uid.system")，不需要申请这个权限
//        requestPermission();

        btn_install = findViewById(R.id.btn_install);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在安装");

        btn_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeCommand(et_cmd.getText().toString());
            }
        });
    }

/*

{
    # 编译RK3399 Android源码 https://blog.csdn.net/tangjie134/article/details/102796330
    # 下载RK3399 android源码
    # https://www.t-firefly.com/doc/download/page/id/3.html#other_144
}

     */

    public void executeCommand(String cmd) {
        //pm install -i 作为安装者的应用包名 –user 0 需要安装的应用在移动设备上的路径
        Log.i(TAG, "执行安装命令:"+cmd);
        Runtime runtime = Runtime.getRuntime();
        try {
            Log.i(TAG, "开始执行cmd");
            Process process = runtime.exec(cmd);
            Log.i(TAG, "运行结束");
            InputStream errorInput = process.getErrorStream();
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String error = "";
            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(errorInput));
            while ((line = bufferedReader.readLine()) != null) {
                error += line;
            }
            Log.i(TAG, "运行结果 result="+result);
            Log.i(TAG, "运行结果 error="+error);
        } catch (IOException e) {
            Log.i(TAG, "运行异常："+e.getMessage());
            e.printStackTrace();
        }
    }

    private void testSu(){
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add("mount -o rw,remount -t auto /system");
        String cmd1 = "cp APKInstallerSigned.apk APKInstallerSigned.apk1";
        cmd.add(cmd1);
        ShellUtil.CommandResult result = ShellUtil.execCommand(cmd, true, true);
        Log.i(TAG, "result:"+result.result);
        Log.i(TAG, "errorMsg:"+result.errorMsg);
        Log.i(TAG, "responseMsg:"+result.responseMsg);
    }

    private void testSystem(){
        Log.i(TAG, "测试system权限");
        File f = new File("/system/app/1.txt");
        Log.i(TAG, "文件路径:"+f.getAbsolutePath());
        try {
            f.createNewFile();
            Log.i(TAG, "createNewFile调用成功");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write("Hello!".getBytes(StandardCharsets.UTF_8));
            Log.i(TAG, "write调用成功");
            fos.flush();
            fos.close();
            Log.i(TAG, "文件关闭！");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "文件创建失败"+e.getMessage());
        }
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