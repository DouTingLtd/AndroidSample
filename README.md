# 豆听科技Android SDK使用说明

## **导入教程**

###  1. 下载
 请联系商务人员或技术支持或者~~去这个地址下载sdk~~，并保持版本是最新状态。

###  2. Eclipse（不支持）

###  3. AndroidStudio
- [ ] 请将```Sdk\AndroidStudio\hearing_sdk.aar```这个放入```libs```文件夹内
- [ ] 在你Module的```build.gradle```里面添加如下配置：
```java
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    compile(name:'hearing_sdk', ext:'aar')
}
```
- [ ] 由于sdk内部网络通信使用的开源库[Retrofit2][1]，所以还需要添加retrofit相关依赖（如果你的项目正好也使用了retrofit，可以不用添加这个）：
```java
dependencies {
    compile 'com.squareup.retrofit2:retrofit:2.3.0'
    compile 'com.squareup.retrofit2:converter-gson:2.3.0'
}
```

###  4. 混淆
Proguard中添加如下配置
```java
# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class douting.hearing.core.entity.**{ *; }
-keep class douting.hearing.core.chart.**{ *; }

#Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}
```

## **初始化**

###  1. Application中初始化
在Application中的```onCreate```中调用下面方法，其中```8a2b000000000000000000000000000b```为appKey，需要找商务人员或技术支持申请。
```java
Hearing.init(this, "8a2b000000000000000000000000000b");
```

###  2. 用户信息共享
必须设置用户信息，才能使用测听功能，方便我们以后继续合作。其中```唯一标识```、```性别```和```生日```将会影响测试结果的评分标准，为必填项。其他的信息选择设置。只用调用一次。改变用户信息才调用，不用每次启动都调用。
```java
//sdkUseId 将为用户的唯一标识
HearingUser userInfo = new HearingUser(sdkUseId);
//Gender 只支持 Hearing.GENDER_MAN 和 Hearing.GENDER_WOMAN
userInfo.setGender(Hearing.GENDER_MAN);
//Birthday 格式为 ‘‘yyyyMMdd’’
userInfo.setBirthday("20001102");
//手机号等其他用户信息
userInfo.setPhone("13026100183");
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

###  1. AndroidManifest中添加相应的Activity
注意如果不添加```android:configChanges="keyboard|keyboardHidden|navigation"```，蓝牙设备连接成功后会重启Activity的生命周期，造成不必要的问题。
```java
<activity
    android:name="douting.hearing.core.ui.HearingTestActivity"
    android:configChanges="keyboard|keyboardHidden|navigation"
    android:screenOrientation="portrait" />
<activity
    android:name="douting.hearing.core.ui.HearingResultActivity"
    android:screenOrientation="portrait" />
<activity
    android:name="douting.hearing.core.ui.HearingRecordActivity"
    android:screenOrientation="portrait" />
<activity
    android:name="douting.hearing.core.ui.HearingConnectActivity"
    android:configChanges="keyboard|keyboardHidden|navigation"
    android:screenOrientation="portrait"
    android:theme="@style/Theme.AppCompat.Dialog" />
```

###  2. 在需要启动测试的时候执行
```java
Hearing.startTest(mContext);//需在Hearing.setUser之后才能正常执行。
```

###  3. 在需要查看测试记录的时候执行
```java
Hearing.startRecord(mContext);//需在Hearing.setUser之后才能正常执行。
```

###  4. 获取本地所有测试记录
```java
//需在Hearing.setUser之后才能正常执行。返回resultList可能为NULL，或者size = 0。
List<PureToneResult> resultList = Hearing.getRecord(mContext);
```

## **支付相关**

###  1. 购买耳机测听次数
用户进入到耳机选择界面的时候，sdk会去听力宝后台查询此用户是否有耳机测听的权限。此时用户选择进行耳机测听：

#### 1.1 有权限
> * 进入测听流程。

#### 1.2 没有权限
> * 用户点击耳机之后，Sdk 会发出 Intent 通知 App 此用户需要购买，并附上建议的价格。
> * App 需要监听此 Action(```DOUTING.ACTION.SDK_BUY_HEADSET```) 的 Intent，并作出相应的处理。比如，打开你们的商城引导用户付费购买。
> * 当用户购买成功之后，调用 Sdk 中的接口```hearing.addHeadsetCount(int number)``` or ```hearing.addHeadsetDays (int days)```。Sdk会给此用户添加相应的测听次数或时长。
> * 用户返回耳机界面，Sdk会再次查询权限。

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
> * 当用户点击购买检测仪的按钮的时候 Sdk 会发出 Intent 通知 App 此用户需要购买，并附上建议的价格。
> * App需要监听此 Action(```DOUTING.ACTION.SDK_BUY_BT```) 的 Intent，并作出相应的处理。比如，打开你们的商城引导用户付费购买。

## **自定义接入**
###  TLBHearingTest.Builder
TLBHearingTest是核心测听类，由Builder模式创建。

- [ ] 创建一个构造器，Sdk只支持听力检测仪，必须传入检测仪的MAC地址。
```java
TLBHearingTest.Builder build = new TLBHearingTest.Builder(this, mac);
```
- [ ] 设置测试的模式（目前支持的模式有自动测试```TLBHearingTest.TEST_TYPE_FAST```和手动测试```TLBHearingTest.TEST_TYPE_MANUAL```）。如不设置，默认为自动测试。
```java
build.setType(TLBHearingTest.TEST_TYPE_FAST);
```
- [ ] 设置每个强度的播放时长（单位为毫秒，支持范围为1000~7000）。如不设置，默认为4秒。
```java
build.setDuration(1500);
```
- [ ] 设置初始的强度（支持范围为0f~80f）。如不设置，默认起始强度为40db。
```java
build.setInitialStimulus(40f);
```
- [ ] 设置需要测试的频率点（参数为```float[]```，支持的频率有```Hearing.HZ_150```，```Hearing.HZ_250```，```Hearing.HZ_500```，```Hearing.HZ_1000```，```Hearing.HZ_2000```，```Hearing.HZ_4000```，```Hearing.HZ_8000```）。如不设置，默认只测试500hz，1kHz，2kHz，4kHz。
```java
build.setFrequencyNeed(needTest);
```
- [ ] 设置首先测试的耳朵（参数为：```TLBHearingTest.CHANNEL_RIGHT```，```TLBHearingTest.CHANNEL_LEFT```)。如不设置，默认右耳开始测试。
```java
build.setFirstChannel(atChannel);
```
- [ ] 构造一个TLBHearingTest，需联网异步获取校准数据，耗时操作建议加上进度条。
```java
    build.build(new BuilderCallback() {
        @Override
        public void onSuccess(TLBHearingTest hearingTest, boolean isFix) {
            mTest = hearingTest;
            mTest.start();
        }

        @Override
        public void onFail(int code) {
            Toast.makeText(TestActivity.this, "onFail code = " + code, Toast.LENGTH_SHORT).show();
            finish();
        }
    });
```
###  HearingCallback
测听时的回调事件，由```setOnHearingListener```设置。

- [ ] 回调正在播放的（声音频率，强度，左耳或右耳）。
```java
public void onPlay(float frequency, float stimulus, int channel)
```
- [ ] 回调保存的听阈（声音频率，强度，左耳或右耳）。
```java
public void onSaveEntry(float frequency, float stimulus, int channel)
```
- [ ] 当左右耳切换的时候回调。
```java
public void onChangeChannel(int newChannel)
```
- [ ] 当测听状态改变的时候回调，```true```为正在测听，```false```为暂停或者终止了。
```java
public void onStateChange(boolean isPlaying)
```
- [ ] 当自动测试时，回调测试进度（范围 0 ~ 1）。
```java
public void onProgress(float progress)
```
- [ ] 当测试完成时，回调测试结果。
```java
public void onTestResult(PureToneResult result)
```

###  TLBHearingTest

核心类

- [ ] 开始测试
```java
public void start()
```
- [ ] 暂停测试（暂停后从新启动请用```start()```）
```java
public void pause()
```
- [ ] 停止测试，会清空当前测试保存的状态、数据等。
```java
public void stop()
```
- [ ] 当手动测试时，主动播放测试声音，参数分别为：频率、强度、左右耳。
```java
public void play(float frequency, float stimulus, int channel) {
```
- [ ] 记录当前频率点
```java
public void save()
```
- [ ] 设置监听
```java
public void setOnHearingListener(HearingCallback listener)
```

###  ChartView

测听表格UI，选用。

具体使用方法请参照demo中的代码，里面有注释基本介绍了所有的方法。如果遇到问题请联系技术支持。
 
  [1]: http://square.github.io/retrofit/