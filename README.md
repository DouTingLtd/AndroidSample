# 豆听科技Android SDK使用说明

## **导入教程**

###  1. 核心库依赖

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
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
}
```

###  2. 支持
> * SDK 支持的 Android 最低版本号 minSdkVersion 16（4.1）
> * Android Plugin version 4.1.1+
> * Required Gradle version 6.5+
> * Android 6.0 版本之后蓝牙扫描需要定位权限，请需提前申请

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

### 3.[详细使用见ui库 >](https://github.com/DouTingLtd/AndroidSample/tree/master/ui#readme)