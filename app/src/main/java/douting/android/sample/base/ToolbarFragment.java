package douting.android.sample.base;

import android.support.v7.widget.Toolbar;

import douting.android.sample.R;


/**
 * Created by Xiao.Se on 2016/5/27.
 * ..
 */
public abstract class ToolbarFragment extends BaseFragment {
    protected Toolbar mToolbar;

    @Override
    protected void initView() {
        initToolbar();
        initToolbarView();
    }

    protected abstract void initToolbarView();

    public void initToolbar() {
        mToolbar = findView(R.id.main_toolbar);
    }
}
