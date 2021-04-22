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

###  3.听力图控件`HearingChart`

#### 1.xml可设置属性

```xml
    <declare-styleable name="HearingChart">
        <!-- 中间的表格边距，坐标轴的单位用的就是此位置。如果要调整字体大小需条件边距 -->
        <attr name="hearing_cv_offset" format="dimension" />
        <attr name="hearing_cv_offsetStart" format="dimension" />
        <attr name="hearing_cv_offsetEnd" format="dimension" />
        <attr name="hearing_cv_offsetTop" format="dimension" />
        <attr name="hearing_cv_offsetBottom" format="dimension" />
        <!-- 听力图的表现形式 -->
        <attr name="hearing_cv_type">
            <!-- 标准听力图，倍频和半倍频区别表现。SDK不支持半倍频测听，所以用不上此模式 -->
            <flag name="norm" value="0x00" />
            <!-- 均分听力图 -->
            <flag name="other" value="0x01" />
            <!-- 纵坐标强度dB单位显示在哪边 -->
            <flag name="left" value="0x0" />
            <flag name="right" value="0x4" />
            <!-- 横坐标频率单位显示在哪边 -->
            <flag name="bottom" value="0x00" />
            <flag name="top" value="0x10" />
            <flag name="bottomTop" value="0x20" />
            <flag name="topBottom" value="0x30" />
            <!-- 单位（dBHL、Hz）标注在边上还是在值上 -->
            <flag name="side" value="0x00" />
            <flag name="in" value="0x80" />
        </attr>
        <!-- 坐标单位文字大小 -->
        <attr name="hearing_cv_text_size" format="dimension" />
    </declare-styleable>
```

#### 2.代码设置的属性

``` java
        mChartView = findViewById(R.id.chart_view);
        // 设置纵坐标的点，5dB间隔，需要和测听范围对于，SDK只支持 15dB ~ 90dB 不可修改
        mChartView.getYAxis().setScale(new float[]{15f, 20f, 25f, 30f, 35f,
                40f, 45f, 50f, 55f, 60f, 65f, 70f, 75f, 80f, 85f, 90f});
        // 设置横线的间隔（2 的意思就是隔行显示）
        mChartView.getYAxis().setSpaceNum(1);
        // 设置很坐标的点，需要和 HearingTest 设置的测听范围对应
        mChartView.getXAxis().setScale(Hearing.TEST_FREQUENCY_FLOAT);
        // 用户触摸的点的颜色和形状，当改变左右耳的时候需要在改动
        mChartView.setTouchColor(Color.RED);
        // 右耳是 O 左耳是 X
        mChartView.setTouchStyle(TouchDataSet.TOUCH_STYLE_RIGHT_POINT);
        // 当自动测试时，需要手动调用当前触摸的点
        mChartView.setTouchValue(frequency, stimulus);
        // 当手动测试时，设置听力图监听，通过此回调得知用户点击的频率和强度
        mChartView.setTableListener(new HearingChart.TableListener() {
            /**
             * @param xVal 频率
             * @param yVal 强度
             */
            @Override
            public void onTouch(float xVal, float yVal) {
                mHearingTest.play(xVal, yVal);
            }
        });
```

#### 3.填充数据

- [ ] `PureToneResult` ：表示一条测听记录，听力图不直接加载此数据
- [ ] `ResultDataSet`：表示一条测听记录中的左右耳的记录



### 4.测听流程`HearingTest`

#### 1.创建

``` java
        HearingTest.Builder builder = new HearingTest.Builder();
        //设置需要测试频率 [Hearing.ALL_HZ] 和 [Hearing.TEST_FREQUENCY] ,也可以自己组合 new int[]{Hearing.HZ_1000,Hearing.HZ_4000}
        builder.setFrequencyNeed(Hearing.TEST_FREQUENCY);
        //设置测试的模式 自动还是手动
        builder.setAuto(true);
        //设置测试流程 目前实现了只有 快速测试[FlowType.FAST] 和 手动测试[‘]FlowType.MANUAL]
        builder.setFlowType(FlowType.FAST);
        //设置播放器 目前实现了只有 有线耳机[PlayerType.DUAL_LINE] 和 单耳蓝牙耳机[PlayerType.MONO_BLE]
        if (null == mAddress) {
            builder.setPlayerType(PlayerType.DUAL_LINE);
        } else {
            builder.setPlayerType(PlayerType.MONO_BLE);
            builder.setAddress(mAddress);
        }
        //设置优先的耳朵 Hearing.RIGHT_EAR 和 Hearing.LEFT_EAR
        builder.setFirstChannel(Hearing.RIGHT_EAR);
        //设置播放时长 每个强度的播放时长，自动测试是，如果用户没有反应这个时间后就自动跳转到下一个点
        builder.setDuration(5000);
        //设置初始强度
        builder.setInitialStimulus(40f);
        //设置监听
        builder.addListener(this);

        builder.build(new ExtCallback<HearingTest>() {
            @Override
            public void onSuccess(HearingTest hearingTest) {
                mHearingTest = hearingTest;
                mHearingTest.start();
            }

            @Override
            public void onFail(int i, String s) {

            }
        });
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