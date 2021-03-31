# 豆听科技Android SDK使用说明

## **导入教程**

###  1. Gradle 依赖
- [ ] Module 的 build.gradle 中添加
```gradle
implementation 'com.github.doutingltd:core:0.8.7'
```
- [ ] 记得再根 build.gradle 中添加 maven 库快照地址：
```gradle
...
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
...
```
- [ ] 由于SDK内部依赖了一些开源库，如果你的项目正好也使用了，如果有版本冲突，可以排除
```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
}
```

###  2. 支持
> * SDK 支持的 Android 最低版本号 minSdkVersion 16（4.1）
> * Android Plugin version 4.1.1+
> * Required Gradle version 6.5+
> * Android 6.0 版本之后蓝牙扫描需要定位权限，请需提前申请

###  3. 混淆
Proguard 添加如下配置
```gradle
# Retrofit Okhttp Okio Gson
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.-KotlinExtensions
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# 实体类
-keep class douting.hearing.core.entity.** { *; }
-keep class douting.hearing.core.testing.chart.PureToneResult { *; }
-keep class douting.hearing.core.testing.chart.ResultDataSet { *; }
-keep class douting.hearing.core.testing.chart.ResultEntry { *; }
```

## **初始化**

###  1. Application中初始化
在Application中的```onCreate```中调用下面方法，其中```8a2b000000000000000000000000000b```为appKey，需要找商务人员或技术支持申请。
```java
Hearing.init(this, "8a2b000000000000000000000000000b");
```

###  2. 用户信息共享
必须设置用户信息，才能使用测听功能，方便我们以后继续合作。其中```唯一标识```、```电话```、```性别```和```生日```将会影响测试结果的评分标准，为必填项。其他的信息选择设置。只用调用一次。用户 ID 会存在本地，每次启动 App 会自动读取，改变用户信息才需重新调用，不用每次启动都调用。
```java
//sdkUseId 将为用户的唯一标识
//Gender 只支持 Hearing.GENDER_MAN 和 Hearing.GENDER_WOMAN
//Birthday 格式为 ‘‘yyyyMMdd’’
HearingUser userInfo = new HearingUser("FGHRTYUILADFBAECCB", "19890512", "13026100216", Hearing.GENDER_MAN);
//其他用户信息
userInfo.setUsername("XiaoSe");
userInfo.setAddress("WuHan");
userInfo.setEmail("wuxiang@tinglibao.com.cn");
...
```

```java
Hearing.setUser(this, userInfo, new ExtCallback<HearingUser>() {
    @Override
    public void onSuccess(HearingUser data) {
        Log.d("onSuccess", "onSuccess");
    }

    @Override
    public void onFail(int code) {
        Log.e("onFail", "code = " + code);
    }
});
```
## **快速接入**

###  1. 在需要启动测试的时候执行
```java
Hearing.startTest(mContext);//需在Hearing.setUser之后才能正常执行。
```

###  2. 在需要查看测试记录的时候执行
```java
Hearing.startRecord(mContext);//需在Hearing.setUser之后才能正常执行。
```

###  3. 获取本地所有测试记录
```java
//需在Hearing.setUser之后才能正常执行。返回resultList可能为NULL，或者size = 0。
List<PureToneResult> resultList = Hearing.getRecord(mContext);
```

## **支付相关**

###  1. 购买耳机测听次数
用户进入到耳机选择界面的时候，SDK 会去听力宝后台查询此用户是否有耳机测听的权限。此时用户选择进行耳机测听：

#### 1.1 有权限
> * 进入测听流程。

#### 1.2 没有权限
> * 用户点击耳机之后，SDK 会发出 Intent 通知 App 此用户需要购买，并附上建议的价格。
> * App 需要监听此 Action(```DOUTING.ACTION.SDK_BUY_HEADSET```) 的 Intent，并作出相应的处理。比如，打开你们的商城引导用户付费购买。
> * 当用户购买成功之后，调用 SDK 中的接口```hearing.addHeadsetCount(int number)```。SDK 会给此用户添加相应的测听次数。
> * 用户返回耳机界面，SDK会再次查询权限。

### 2. 购买检测仪
用户进入到耳机选择界面并选择检测仪测听时会弹出连接引导框：

#### 2.1 连接正确检测仪
> * 进入测听流程

#### 2.2 连接错误检测仪
提示连接失败，不会进入测听流程。错误种类可能是：
> * 并不是听力宝官方的设备；
> * 检测仪使用的用户超过5个；
> * 其他…

#### 2.3 界面上会预留购买检测仪的按钮
> * 当用户点击购买检测仪的按钮的时候 SDK 会发出 Intent 通知 App 此用户需要购买，并附上建议的价格。
> * App需要监听此 Action(```DOUTING.ACTION.SDK_BUY_BT```) 的 Intent，并作出相应的处理。比如，打开你们的商城引导用户付费购买。