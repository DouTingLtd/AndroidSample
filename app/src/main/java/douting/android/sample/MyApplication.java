package douting.android.sample;

import android.app.Application;

import douting.hearing.core.Hearing;

/**
 * @author  by WuXiang on 2016/9/21.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Hearing.init(this, "8a2bbc596883f7bc0168fe9c23505899");
    }
}
