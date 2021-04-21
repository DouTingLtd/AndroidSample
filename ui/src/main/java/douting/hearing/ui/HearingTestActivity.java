package douting.hearing.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.see.mvp.base.SeeBaseActivity;

import douting.hearing.core.Hearing;
import douting.hearing.core.testing.chart.HearingChart;
import douting.hearing.core.testing.chart.PointDraw;
import douting.hearing.core.testing.chart.PureToneResult;
import douting.hearing.core.testing.chart.ResultDataSet;
import douting.hearing.core.testing.chart.ResultEntry;
import douting.hearing.core.testing.chart.TouchDataSet;
import douting.hearing.ui.presenter.HearingTestPresenter;

/**
 * @author by wuxiang@tinglibao.com.cn on 2021/4/20.
 */
public class HearingTestActivity extends SeeBaseActivity<HearingTestPresenter> {
    private AudioManager mAudioManager;
    private int mCurrentVolume;

    private HearingChart mChartView;
    private ResultDataSet mDataSet;
    private Button hearing_bt;

    @Override
    protected int getContentView() {
        return R.layout.hearing_test_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        hearing_bt = findViewById(R.id.starting_Hearing_btn_1);
        initAudioManager();
        initHearButton();
        initCharView();
        showStartDialog();
    }

    // 启动测听必须将系统音量为最大
    public void initAudioManager() {
        mAudioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int systemMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, systemMax, 0);
    }

    /**
     * 测听图控件 HearingChart 说明
     */
    private void initCharView() {
        mChartView = findViewById(R.id.chart_view);
        // 设置纵坐标的点，5dB间隔，需要和测听范围对于，SDK只支持 15dB ~ 90dB 不可修改
        mChartView.getYAxis().setScale(new float[]{15f, 20f, 25f, 30f, 35f,
                40f, 45f, 50f, 55f, 60f, 65f, 70f, 75f, 80f, 85f, 90f});
        // 设置横线的间隔（2 的意思就是隔行显示）
        mChartView.getYAxis().setSpaceNum(1);
        // 设置很坐标的点，需要和 HearingTest 设置的测听范围对应
        mChartView.getXAxis().setScale(Hearing.TEST_FREQUENCY_FLOAT);

        setTitle(R.string.hearing_test_right);
        // Activity 这边的 mDataSet 仅仅做测听时的实时展示，存储的数据在 Presenter 那边
        mDataSet = new ResultDataSet(true);
        // 用户触摸的点的颜色和形状，当改变左右耳的时候需要在改动
        mChartView.setTouchColor(Color.RED);
        // 右耳是 O 左耳是 X
        mChartView.setTouchStyle(TouchDataSet.TOUCH_STYLE_RIGHT_POINT);

        // 设置是否把每个点用线连起来，一般测的时候不连，展示结果的时候连
        mDataSet.setLine(false);
        mDataSet.setPainStrokeWidth(6f);
        mDataSet.setPointRadius(18f);
        mChartView.addDataSet(mDataSet);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initHearButton() {
        hearing_bt.setOnClickListener(this);
        hearing_bt.setOnTouchListener(new View.OnTouchListener() {
            boolean move = false;
            final int[] last = new int[]{0, 0};
            final int[] start = new int[]{0, 0};

            //按钮可以拖动
            public boolean onTouch(View v, MotionEvent event) {
                int eventAction = event.getAction();
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (eventAction) {
                    case MotionEvent.ACTION_DOWN:
                        last[0] = (int) event.getX();
                        last[1] = y - v.getTop();

                        start[0] = (int) event.getRawX();
                        start[1] = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.layout(x - last[0], y - last[1], x + v.getWidth()
                                - last[0], y - last[1] + v.getHeight());
                        break;
                    case MotionEvent.ACTION_UP:
                        //检测移动的距离，如果很微小可以认为是点击事件
                        move = !(Math.abs(start[0] - x) < 10 && Math.abs(start[1] - y) < 10);
                        break;
                }
                return move;
            }
        });
    }

    private void showStartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.hearing_tips);
        builder.setMessage(R.string.hearing_test_start);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.hearing_test_start_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPresenter().createHearing();
            }
        });
        Dialog dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void showChangEarDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.hearing_tips);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.hearing_test_change_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPresenter().onResume();
            }
        });
        Dialog dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.starting_Hearing_btn_1) {
            getPresenter().onHear();
        }
    }

    public void onSaveEntry(float frequency, float stimulus, int channel, boolean response) {
        int state = response ? HearingChart.RESPONSE : HearingChart.UN_RESPONSE;
        if (channel == Hearing.RIGHT_EAR) {
            state |= HearingChart.RIGHT;
            mDataSet.saveEntry(new ResultEntry(frequency, stimulus, PointDraw.POINT_TYPE_1, state));
        } else {
            state |= HearingChart.LEFT;
            mDataSet.saveEntry(new ResultEntry(frequency, stimulus, PointDraw.POINT_TYPE_2, state));
        }
        mChartView.invalidate();
    }

    public void onPlay(float frequency, float stimulus, int channel) {
        mChartView.setTouchValue(frequency, stimulus);
        mChartView.invalidate();
    }

    public void onChangeChannel(int newChannel) {
        String message;
        if (newChannel == Hearing.LEFT_EAR) {
            message = getString(R.string.hearing_test_start_left);
            setTitle(R.string.hearing_test_left);
            mChartView.setTouchColor(Color.BLUE);
            mChartView.setTouchStyle(TouchDataSet.TOUCH_STYLE_LEFT_POINT);

            mDataSet.setRight(false);
            mDataSet.clearData();
        } else {
            message = getString(R.string.hearing_test_start_right);
            setTitle(R.string.hearing_test_right);
            mChartView.setTouchColor(Color.RED);
            mChartView.setTouchStyle(TouchDataSet.TOUCH_STYLE_RIGHT_POINT);

            mDataSet.setRight(true);
            mDataSet.clearData();
        }

        getPresenter().onPause();
        showChangEarDialog(message);
    }

    public void onOver() {
        hearing_bt.setVisibility(View.GONE);
    }

    public void onPushSuccess(PureToneResult toneResult) {
        Intent intent = new Intent(mContext, HearingResultActivity.class);
        intent.putExtra(HearingResultActivity.RESULT_JSON, toneResult);
        startActivity(intent);
        finish();
    }

    public void onPushError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.hearing_tips);
        builder.setMessage(R.string.hearing_commit_error);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.hearing_out, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        Dialog dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    // 测听结束后恢复音量
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume, 0);
    }
}
