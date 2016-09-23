# 豆听科技Android SDK使用说明

## **导入教程**

###  1. 下载
 请联系商务人员或技术支持或者去这个地址下载sdk

###  2. Eclipse
 - [ ] 请将```Sdk\Eclipse\hearing_sdk.jar```这个放入```libs```文件夹内
 - [ ] 由于sdk内部网络通信使用的开源库[Retrofit2][1]，所以还需要添加retrofit相关的jar到```libs```下面。包括如下jar（如果你的项目正好也使用了retrofit，可以不用添加这个）：
 - converter-gson-2.1.0.jar
 - gson-2.7.jar
 - okhttp-3.3.0.jar
 - okio-1.8.0.jar
 - retrofit-2.1.0.jar

###  3. AndroidStudio

## **初始化**
###  1. AndroidManifest.xml中权限添加
- [ ] 由于需要联网获取校准数据，必须添加网络权限```<uses-permission android:name="android.permission.INTERNET" />```

###  2. Application中初始化
在Application中的```onCreate```中调用下面方法，其中```8a2b000000000000000000000000000b```为appKey，需要找商务人员或技术支持申请。
```java
Hearing.init(this, "8a2b000000000000000000000000000b");
```

## **使用**
###  TLBHearingTest
TLBHearingTest是核心测听类，由Builder模式创建
- [ ] 标题s 
```java
code
```
具体使用方法请参照demo中的代码，里面有注释基本介绍了所有的方法。如果遇到问题请联系技术支持。
 
  [1]: http://square.github.io/retrofit/