package douting.android.sample;

import android.app.Application;

import douting.hearing.core.Hearing;

/**
 * Created by WuXiang on 2016/9/21.
 * ..
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Hearing.init(this, "8a2bbc595769488501576ab27b2f02c7");
    }
}
