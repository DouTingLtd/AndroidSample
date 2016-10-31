package douting.android.sample.base;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


/**
 * Created by WuXiang on 2016/2/5.
 * Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        mContext = this;

        initBaseView();
    }

    protected abstract int getContentView();

    protected abstract void initBaseView();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {

    }
}
