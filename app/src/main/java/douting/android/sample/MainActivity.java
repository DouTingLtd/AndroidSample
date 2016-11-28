package douting.android.sample;

import android.content.Intent;
import android.view.View;

import douting.android.sample.base.ToolbarActivity;
import douting.hearing.core.Hearing;

/**
 * Created by WuXiang on 2016/11/28.
 * ..
 */

public class MainActivity extends ToolbarActivity {
    @Override
    protected int getContentView() {
        return R.layout.main_layout;
    }

    @Override
    protected void initView() {
        findView(R.id.easy_start).setOnClickListener(this);
        findView(R.id.custom_start).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.easy_start:
                Hearing.startTest(mContext);
                break;
            case R.id.custom_start:
                startActivity(new Intent(mContext, CustomActivity.class));
                break;
        }
    }
}
