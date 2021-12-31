# Android 8.1 模拟器静默安装APK
 
# 1. 启动模拟器
```powershell
#可写模式启动Android模拟器
cd C:\Android\SDK\emulator
./emulator.exe '@Android8' -writable-system
```
# 2. push要安装的apk文件到外部存储
```cmd
adb shell ls /storage/emulated/0
adb push .\MyApp1.apk /storage/emulated/0
```

# 3. 添加 android:sharedUserId="android.uid.system"

**添加AndroidManifest.xml中的权限申请**

```powershell
# 为什么要添加 android:sharedUserId="android.uid.system"
# 为什么要系统签名
1.普通app申请完外部存储权限以后，调用pm install报错
2.普通app安装到 system/app后，仍然不能运行 pm install 报错：Package xxx does not belong to xxxx
3.不进行系统签名，重启系统，/system/app中的apk不会自动安装
```

# 4. 进行系统签名

在这里找到platform签名文件： http://androidxref.com/8.1.0_r33/xref/build/target/product/security/

在这里找到 singapk.jar：http://androidxref.com/8.1.0_r33/xref/prebuilts/sdk/tools/lib/signapk.jar

在这里找到lib64：http://androidxref.com/8.1.0_r33/xref/prebuilts/sdk/tools/linux/lib64/

下载其中的libconscrypt_openjdk_jni.so

最终文件列表:
```text
APKInstaller.apk
libconscrypt_openjdk_jni.so
platform.pk8
platform.x509.pem
signapk.jar
```

在Linux系统中运行:
```powershell
java -Djava.library.path=. -jar signapk.jar platform.x509.pem platform.pk8 APKInstaller.apk APKInstallerSigned.apk
```

得到: **APKInstallerSigned.apk**

# 将APKInstaller安装到 system/app

```powershell
adb root
adb remount
adb push .\APKInstallerSigned.apk /system/app
adb reboot
```

# 此时启动APKInstaller，可执行pm install命令
```powershell
pm install -i 调用者包名 -r APK路径
#举例
pm install -i run.ccfish.android.apkinstaller -r /storage/emulated/0/MyApp1.apk
```

# 启动安装好的程序
```powershell
am start run.ccfish.android.myapp/.MainActivity
```

# TODO
```
1. 跟随系统启动，自动打开HTTP服务器
2. API: 运行shell
3. API: 设置开机运行指令
```