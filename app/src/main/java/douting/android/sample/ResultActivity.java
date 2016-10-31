package douting.android.sample;

import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;

import douting.android.sample.base.ToolbarActivity;
import douting.hearing.core.Hearing;
import douting.hearing.core.chart.ChartView;
import douting.hearing.core.chart.PureToneResult;

/**
 * Created by WuXiang on 2016/10/8.
 * ..
 */

public class ResultActivity extends ToolbarActivity {
    public static final String RESULT_JSON = "RESULT_JSON";
    private PureToneResult mResult;
//    private float[] needTest = {Hearing.HZ_500, Hearing.HZ_1000, Hearing.HZ_2000, Hearing.HZ_4000};


    @Override
    protected int getContentView() {
        return R.layout.result_layout;
    }

    @Override
    protected void initView() {
        String json = getIntent().getStringExtra(RESULT_JSON);
        if (!TextUtils.isEmpty(json)) {
            mResult = new Gson().fromJson(json, PureToneResult.class);
            ChartView chartView = findView(R.id.result_char);
//            chartView.getXAxis().setScale(needTest);
            chartView.addDataSet(mResult.getLeftDataSet());
            chartView.addDataSet(mResult.getRightDataSet());
        } else {
            finish();
        }
        showText();
    }

    private void showText() {
        TextView result_right_text = findView(R.id.result_right_text);
        result_right_text.setText(String.format(getString(R.string.test_right_state), mResult.getRightLoss(), diagnoseWord(mResult.getRightLoss())));
        TextView result_left_text = findView(R.id.result_left_text);
        result_left_text.setText(String.format(getString(R.string.test_left_state), mResult.getLeftLoss(), diagnoseWord(mResult.getLeftLoss())));
    }

    /**
     * 根据损失的DB数返回文字诊断结果
     *
     * @param result 损失的DB数
     * @return 文字诊断结果
     */
    private String diagnoseWord(int result) {
        String resultStrings[] = getResources().getStringArray(R.array.diagnose_word);
        if (result == -100) {
            return resultStrings[0];
        } else if (result <= 25) {
            return resultStrings[1];
        } else if (result <= 40) {
            return resultStrings[2];
        } else if (result <= 55) {
            return resultStrings[3];
        } else if (result <= 70) {
            return resultStrings[4];
        } else if (result <= 90) {
            return resultStrings[5];
        } else {
            return resultStrings[6];
        }
    }

}
