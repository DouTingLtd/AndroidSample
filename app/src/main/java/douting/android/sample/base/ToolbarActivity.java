package douting.android.sample.base;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import douting.android.sample.R;


/**
 * Created by WuXiang on 2016/3/31.
 * <p/>
 * ..
 */
public abstract class ToolbarActivity extends BaseActivity {
    protected ActionBar mActionBar;

    @Override
    protected void initBaseView() {
        initToolbar();
        initView();
    }

    public void initToolbar() {
        Toolbar toolbar = findView(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if (null != mActionBar) {
            //mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected abstract void initView();

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
