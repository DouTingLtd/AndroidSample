package douting.hearing.ui.presenter;

import androidx.annotation.NonNull;

import com.see.mvp.base.Presenter;

import douting.hearing.core.Hearing;
import douting.hearing.core.callback.ExtCallback;
import douting.hearing.core.testing.chart.HearingChart;
import douting.hearing.core.testing.chart.PointDraw;
import douting.hearing.core.testing.chart.PureToneResult;
import douting.hearing.core.testing.chart.ResultDataSet;
import douting.hearing.core.testing.chart.ResultEntry;
import douting.hearing.core.testing.control.FlowType;
import douting.hearing.core.testing.control.HearingCallback;
import douting.hearing.core.testing.control.HearingTest;
import douting.hearing.core.testing.control.PlayerType;
import douting.hearing.ui.HearingTestActivity;

import static douting.hearing.core.testing.chart.HearingChart.UN_RESPONSE;

/**
 * @author by wuxiang@tinglibao.com.cn on 2021/4/20.
 */
public class HearingTestPresenter extends Presenter<HearingTestActivity> implements HearingCallback {
    private HearingTest mHearingTest;
    private String mAddress;
    private PureToneResult mTestResult;
    private ResultDataSet rightDataSet, leftDataSet;

    @Override
    protected void onCreateView(@NonNull HearingTestActivity view) {
        super.onCreateView(view);
        initTestResult();
        mAddress = getIntent().getStringExtra("mac_address");
    }

    private void initTestResult() {
        // 一个 PureToneResult 表示一条测听记录
        mTestResult = new PureToneResult();
        // 一条测听记录里面包含左耳和右耳两条数据
        rightDataSet = new ResultDataSet(true);
        leftDataSet = new ResultDataSet(false);
        mTestResult.setLeftDataSet(leftDataSet);
        mTestResult.setRightDataSet(rightDataSet);
        // 0：纯音测试；1：言语测试（暂不支持言语）
        mTestResult.setTestType(0);
    }

    public void createHearing() {
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
    }

    /**
     * 当前正在测听点的回调
     *
     * @param frequency 频率
     * @param stimulus  强度
     * @param channel   左右耳
     */
    @Override
    public void onPlay(float frequency, float stimulus, int channel) {
        getView().onPlay(frequency, stimulus, channel);
    }

    /**
     * 患者听到了，需要调用 mHearingTest.onHear() 后就会回调此方法
     *
     * @param frequency 频率
     * @param stimulus  强度
     * @param channel   左右耳
     * @param response  是否响应（自动测试时，如果强度到达最大90dB 还没有听到 response = false）
     */
    @Override
    public void onSaveEntry(float frequency, float stimulus, int channel, boolean response) {
        getView().onSaveEntry(frequency, stimulus, channel, response);
        int state = response ? HearingChart.RESPONSE : UN_RESPONSE;
        if (channel == Hearing.RIGHT_EAR) {
            state |= HearingChart.RIGHT;
            rightDataSet.saveEntry(new ResultEntry(frequency, stimulus, PointDraw.POINT_TYPE_1, state));
        } else {
            state |= HearingChart.LEFT;
            leftDataSet.saveEntry(new ResultEntry(frequency, stimulus, PointDraw.POINT_TYPE_2, state));
        }
    }

    /**
     * 切换左右耳回调
     *
     * @param newChannel 新的耳朵
     */
    @Override
    public void onChangeChannel(int newChannel) {
        getView().onChangeChannel(newChannel);
    }

    /**
     * 测听状态回调 有问题未实现
     *
     * @param isPlaying 是否在测听
     */
    @Override
    public void onStateChange(boolean isPlaying) {

    }

    /**
     * 测听进度
     *
     * @param progress 0 ~ 1 （1 表示完成）
     */
    @Override
    public void onProgress(float progress) {

    }

    /**
     * 测听完成回调
     *
     * @param isComplete 一把为 true 无需关注
     */
    @Override
    public void onOver(boolean isComplete) {
        getView().onOver();
        //算分
        mTestResult.settlement();
        getView().onPushSuccess(mTestResult);
    }

    public void playAtChanel(int frequency, float stimulus) {
        mHearingTest.play(frequency, stimulus);
    }

    /**
     * 用户点击听到了按钮
     */
    public void onHear() {
        if (mHearingTest != null) {
            mHearingTest.onHear();
        }
    }

    /**
     * 界面恢复时，恢复测听
     */
    @Override
    public void onResume() {
        if (mHearingTest != null) {
            mHearingTest.start();
        }
    }

    /**
     * 界面不可见时，暂停测听
     */
    @Override
    public void onPause() {
        if (mHearingTest != null) {
            mHearingTest.pause();
        }

    }

    /**
     * 释放资源
     */
    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        if (mHearingTest != null) {
            mHearingTest.over();
        }
    }
}