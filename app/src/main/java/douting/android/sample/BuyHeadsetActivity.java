package douting.android.sample;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import douting.android.sample.base.ToolbarActivity;
import douting.hearing.core.Hearing;
import douting.hearing.core.callback.ExtCallback;

/**
 * @author by WuXiang on 2019/2/22.
 */
public class BuyHeadsetActivity extends ToolbarActivity {
    @Override
    protected int getContentView() {
        return R.layout.buy_headset_layout;
    }

    @Override
    protected void initView() {
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
            public void onFail(int i) {
                Log.d("onFail", "code = " + i);
            }
        });
    }

    public void addDays(View view) {

    }
}
