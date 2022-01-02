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

> **添加AndroidManifest.xml中的权限申请**
> 添加了android:sharedUserId="android.uid.system" 的程序，默认拥有外部存储读写权限

```powershell
# 为什么要添加 android:sharedUserId="android.uid.system"
# 为什么要系统签名
1.普通app申请完外部存储权限以后，调用pm install报错
2.普通app安装到 system/app后，仍然不能运行 pm install 报错：Package xxx does not belong to xxxx
3.不进行系统签名，重启系统，/system/app中的apk不会自动安装
```

# 4. APK系统签名

> **注意：不同厂家系统源码的platform签名不一样，需要去各自的源码中下载对应的签名文件**

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
# 系统中需要java8版本，如果没有请安装
# sudo apt-get install openjdk-8-jre-headless
java -Djava.library.path=. -jar signapk.jar platform.x509.pem platform.pk8 APKInstaller.apk APKInstallerSigned.apk
```

得到: **APKInstallerSigned.apk**

# 将APKInstaller安装到 system/app

> 用adb install APKInstallerSigned.apk，也可以静默安装apk，不必非得安装到system/app
> system/app 中的程序无法卸载

```powershell
adb root
adb remount
adb push .\APKInstallerSigned.apk /system/app
adb reboot
```

# 此时启动APKInstaller，可执行pm install命令, -r 是覆盖安装
```powershell
pm install -i 调用者包名 -r APK路径
#举例
pm install -r -i run.ccfish.android.apkinstaller /storage/emulated/0/MyApp1.apk
```

# 启动安装好的程序
```powershell
am start run.ccfish.android.myapp/.MainActivity
```

# 具有系统签名的APK静默升级方案

1. 在storage根目录释放一个APKInstaller.apk
2. 下载要安装的apk，保存至在storage跟目录下，命名为: app.apk
3. 安装APKInstaller.apk
4. 启动APKInstaller.apk
5. APKInstaller启动后，安装App.apk，然后启动安装好的程序，然后关闭自己。

# 常用shell命令

## 根据包名找到apk路径
```shell
adb shell pm list packages -f | grep 包名
```
