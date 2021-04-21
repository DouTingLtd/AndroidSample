package douting.android.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.see.mvp.base.SeeBaseActivity;

import douting.hearing.core.Hearing;
import douting.hearing.core.callback.ExtCallback;

/**
 * 购买耳机测听次数说明：
 * https://github.com/DouTingLtd/AndroidSample#1-%E8%B4%AD%E4%B9%B0%E8%80%B3%E6%9C%BA%E6%B5%8B%E5%90%AC%E6%AC%A1%E6%95%B0
 *
 * @author by WuXiang on 2019/2/22.
 */
public class BuyHeadsetActivity extends SeeBaseActivity {
    @Override
    protected int getContentView() {
        return R.layout.buy_headset_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        String data = getIntent().getStringExtra("prices");
        TextView textView = findViewById(R.id.intent_data);
        textView.setText(data);
    }

    public void addCount(View view) {
        Hearing.addHeadsetCount(1, new ExtCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFail(int i, String s) {
                Log.d("onFail", "code = " + i + " msg = " + s);
            }
        });
    }

    public void addDays(View view) {

    }
}
