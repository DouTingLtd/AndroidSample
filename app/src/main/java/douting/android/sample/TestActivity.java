package douting.android.sample;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import douting.android.sample.base.ToolbarActivity;
import douting.hearing.core.Hearing;
import douting.hearing.core.TLBHearingTest;
import douting.hearing.core.callback.BuilderCallback;
import douting.hearing.core.callback.HearingCallback;
import douting.hearing.core.chart.ChartView;
import douting.hearing.core.chart.PointDraw;
import douting.hearing.core.chart.PureToneResult;
import douting.hearing.core.chart.ResultDataSet;
import douting.hearing.core.chart.ResultEntry;

/**
 * Created by WuXiang on 2016/9/22.
 * ..
 */

public class TestActivity extends ToolbarActivity {
    public static final String IS_FAST = "IS_FAST";
    public static final String MAC = "MAC";
    private TLBHearingTest mTest;
    private ChartView chartView;
    private ResultDataSet rightDataSet, leftDataSet;
    private int atChannel = TLBHearingTest.CHANNEL_RIGHT;
    private float[] needTest = {Hearing.HZ_500, Hearing.HZ_1000, Hearing.HZ_2000, Hearing.HZ_4000};
    private ProgressBar progressBar;
    private boolean isFastStyle;

    @Override
    protected int getContentView() {
        return R.layout.test_layout;
    }

    @Override
    protected void initView() {
        isFastStyle = getIntent().getBooleanExtra(IS_FAST, true);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        chartView = (ChartView) findViewById(R.id.chart_view);
        chartView.setOffset(getResources().getDimension(R.dimen.offic));
        if (atChannel == TLBHearingTest.CHANNEL_LEFT) {
            setTitle(R.string.test_left);
            chartView.setTouchColor(Color.BLUE);
        } else {
            setTitle(R.string.test_right);
            chartView.setTouchColor(Color.RED);
        }
        rightDataSet = new ResultDataSet(true);
        leftDataSet = new ResultDataSet(false);
        chartView.addDataSet(rightDataSet);
        chartView.addDataSet(leftDataSet);
        chartView.getXAxis().setScale(needTest);

        createTesting();

        initButton();

    }

    private void initButton() {
        Button starting_Hearing_btn_1 = findView(R.id.starting_Hearing_btn_1);
        starting_Hearing_btn_1.setOnTouchListener(new View.OnTouchListener() {
            int[] temp = new int[]{0, 0};

            //按钮可以拖动
            public boolean onTouch(View v, MotionEvent event) {
                int eventAction = event.getAction();
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (eventAction) {
                    case MotionEvent.ACTION_DOWN: // touch down so check if the
                        temp[0] = (int) event.getX();
                        temp[1] = y - v.getTop();
                        break;
                    case MotionEvent.ACTION_MOVE: // touch drag with the ball
                        v.layout(x - temp[0], y - temp[1], x + v.getWidth()
                                - temp[0], y - temp[1] + v.getHeight());
                        // v.postInvalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
    }

    private void createTesting() {
        String mac = getIntent().getStringExtra(MAC);

        TLBHearingTest.Builder build = new TLBHearingTest.Builder(this, mac);
        build.setDuration(1500);
        build.setInitialStimulus(40f);
        build.setFirstChannel(atChannel);
        build.setFrequencyNeed(needTest);

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
                mTest.setOnHearingListener(callback);
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
                Toast.makeText(TestActivity.this, R.string.test_start_left, Toast.LENGTH_SHORT).show();
                setTitle(R.string.test_left);
                chartView.setTouchColor(Color.BLUE);
            } else {
                Toast.makeText(TestActivity.this, R.string.test_start_right, Toast.LENGTH_SHORT).show();
                setTitle(R.string.test_right);
                chartView.setTouchColor(Color.RED);
            }
        }

        @Override
        public void onProgress(float progress) {
            super.onProgress(progress);
            progressBar.setProgress((int) (progress * 100));
        }

        @Override
        public void onTestResult(PureToneResult pureToneResult) {
            super.onTestResult(pureToneResult);
            String json = new Gson().toJson(pureToneResult);
            Intent intent = new Intent(mContext, ResultActivity.class);
            intent.putExtra(ResultActivity.RESULT_JSON, json);
            startActivity(intent);
            finish();
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
