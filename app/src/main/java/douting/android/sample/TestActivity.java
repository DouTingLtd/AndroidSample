package douting.android.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import douting.hearing.core.Hearing;
import douting.hearing.core.TLBHearingTest;
import douting.hearing.core.callback.BuilderCallback;
import douting.hearing.core.callback.HearingCallback;
import douting.hearing.core.chart.ChartView;
import douting.hearing.core.chart.PointDraw;
import douting.hearing.core.chart.ResultDataSet;
import douting.hearing.core.chart.ResultEntry;

/**
 * Created by WuXiang on 2016/9/22.
 * ..
 */

public class TestActivity extends AppCompatActivity {
    public static final String IS_FAST = "IS_FAST";
    private TLBHearingTest mTest;
    private ChartView chartView;
    private ResultDataSet rightDataSet, leftDataSet;
    private int atChannel = TLBHearingTest.CHANNEL_RIGHT;
    private float[] needTest = {Hearing.HZ_250, Hearing.HZ_500, Hearing.HZ_1000, Hearing.HZ_4000};
    private ProgressBar progressBar;
    private boolean isFastStyle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        isFastStyle = getIntent().getBooleanExtra(IS_FAST, false);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        chartView = (ChartView) findViewById(R.id.chart_view);
        chartView.setOffset(getResources().getDimension(R.dimen.offic));
        if (atChannel == TLBHearingTest.CHANNEL_LEFT) {
            chartView.setTouchColor(Color.BLUE);
        } else {
            chartView.setTouchColor(Color.RED);
        }
        rightDataSet = new ResultDataSet(true);
        leftDataSet = new ResultDataSet(false);
        chartView.addDataSet(rightDataSet);
        chartView.addDataSet(leftDataSet);
        chartView.getXAxis().setScale(needTest);

        TLBHearingTest.Builder build = new TLBHearingTest.Builder(this);
        build.setDuration(1500);
        build.setInitialStimulus(40f);
        build.setFirstChannel(atChannel);
        //build.setDeviceMac("16:07:07:00:E0:1F");
        build.setFrequencyNeed(needTest);
        build.setOnHearingListener(callback);

        if (isFastStyle) {
            build.setType(TLBHearingTest.TEST_TYPE_FAST);
        } else {
            chartView.setTableListener(tableListener);
            build.setType(TLBHearingTest.TEST_TYPE_MANUAL);
        }


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

    }

    private ChartView.TableListener tableListener = new ChartView.TableListener() {
        @Override
        public void onTouch(float xVal, float yVal) {
            mTest.play(xVal, yVal, atChannel);
        }
    };

    private HearingCallback callback = new HearingCallback() {
        @Override
        public void onPlay(float frequency, float stimulus, int channel) {
            super.onPlay(frequency, stimulus, channel);
            if (isFastStyle) {
                chartView.setTouchValue(frequency, stimulus);
                chartView.invalidate();
            }
        }

        @Override
        public void onSaveEntry(float frequency, float stimulus, int channel) {
            super.onSaveEntry(frequency, stimulus, channel);
            if (channel == TLBHearingTest.CHANNEL_RIGHT) {
                rightDataSet.saveEntry(new ResultEntry(frequency, stimulus, PointDraw.POINT_TYPE_1));
            } else {
                leftDataSet.saveEntry(new ResultEntry(frequency, stimulus, PointDraw.POINT_TYPE_2));
            }
            chartView.invalidate();
        }

        @Override
        public void onChangeChannel(int newChannel) {
            super.onChangeChannel(newChannel);
            atChannel = newChannel;
            if (atChannel == TLBHearingTest.CHANNEL_LEFT) {
                Toast.makeText(TestActivity.this, "开始测试左耳", Toast.LENGTH_SHORT).show();
                chartView.setTouchColor(Color.BLUE);
            } else {
                Toast.makeText(TestActivity.this, "开始测试右耳", Toast.LENGTH_SHORT).show();
                chartView.setTouchColor(Color.RED);
            }
        }

        @Override
        public void onProgress(float progress) {
            super.onProgress(progress);
            progressBar.setProgress((int) (progress * 100));
        }
    };

    public void pause(View view) {
        if (mTest != null) {
            mTest.pause();
        }
    }

    public void save(View view) {
        if (mTest != null) {
            mTest.save();
        }
    }

    public void stop(View view) {
        if (mTest != null) {
            mTest.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTest != null) {
            mTest.stop();
        }
    }
}
