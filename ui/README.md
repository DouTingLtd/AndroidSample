## **快速接入**

###  1. UI库依赖

- [ ] Module 的 build.gradle 中添加
```gradle
implementation 'com.github.doutingltd:ui:0.1.1'
```


所有UI界面需在`Hearing.setUser`之后才能正常启动。

###  2.启动测听
```java
Intent intent = new Intent(this, HearingHeadsetActivity.class);
startActivity(intent);
```

###  3.界面说明

界面需要带参数进入，不能直接启动

#### 1.耳机选择界面

```java
HearingHeadsetActivity
```

#### 2.测听界面

```java
HearingTestActivity
```

####  3. 测试结果界面

```java
HearingResultActivity
```

####  4. 测试记录列表

```java
HearingRecordActivity
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