package douting.android.sample.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by WuXiang on 2016/4/1.
 * Fragment基类
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    public Context mContext;
    public View mRootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(getContentView(), container, false);
        initView();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createPresenter();
    }

    protected abstract int getContentView();

    protected abstract void initView();

    protected abstract void createPresenter();

    protected <T extends View> T findView(int id) {
        return (T) mRootView.findViewById(id);
    }

    @Override
    public void onClick(View v) {

    }
}
