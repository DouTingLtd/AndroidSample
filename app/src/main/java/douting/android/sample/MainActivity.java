package douting.android.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import douting.hearing.core.TLBHearingTest;
import douting.hearing.core.callback.HearingCallback;
import douting.hearing.core.chart.ChartView;
import douting.hearing.core.chart.PointDraw;
import douting.hearing.core.chart.ResultDataSet;
import douting.hearing.core.chart.ResultEntry;

/**
 * Created by WuXiang on 2016/9/6.
 * ..
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TLBHearingTest mTest;
    private ChartView chartView;
    private ResultDataSet rightDataSet, leftDataSet;
    private float[] needTest = {500f, 1000f, 2000f, 4000f};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        chartView = (ChartView) findViewById(R.id.chart_view);
        chartView.setOffset(getResources().getDimension(R.dimen.offic));
        rightDataSet = new ResultDataSet(true);
        leftDataSet = new ResultDataSet(false);
        chartView.addDataSet(rightDataSet);
        chartView.addDataSet(leftDataSet);
        chartView.setTableListener(true);
        chartView.getXAxis().setScale(needTest);

        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(this);
        Button pause = (Button) findViewById(R.id.pause);
        pause.setOnClickListener(this);
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);


//        chartView.setType(ChartView.VIEW_TYPE_OTHER);
        mTest = new TLBHearingTest.Builder()
                .setType(TLBHearingTest.TEST_TYPE_FAST)
                .setDuration(1500)
                .setFrequencyNeed(needTest)
                .setOnHearingListener(callback)
                .build();

    }

    private HearingCallback callback = new HearingCallback() {
        @Override
        public void onPlay(float frequency, float stimulus, int channel) {
            super.onPlay(frequency, stimulus, channel);
            Log.d("onPlay", "frequency=" + frequency + ";stimulus=" + stimulus + ";channel=" + channel);
            chartView.setTouchValue(frequency, stimulus);
            chartView.invalidate();
        }

        @Override
        public void onSaveEntry(float frequency, float stimulus, int channel) {
            super.onSaveEntry(frequency, stimulus, channel);
            Log.d("onSaveEntry", "frequency=" + frequency + ";stimulus=" + stimulus + ";channel=" + channel);
            if (channel == TLBHearingTest.CHANNEL_RIGHT) {
                rightDataSet.saveEntry(new ResultEntry(frequency, stimulus, PointDraw.POINT_TYPE_1));
            } else {
                leftDataSet.saveEntry(new ResultEntry(frequency, stimulus, PointDraw.POINT_TYPE_2));
            }
            chartView.invalidate();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                mTest.start();
                break;
            case R.id.pause:
                mTest.pause();
                break;
            case R.id.save:
                mTest.save();
                break;
        }
    }
}
