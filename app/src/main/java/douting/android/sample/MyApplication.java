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

        Hearing.init(this, "8a2b0565573bda9601574becbaac000b");
    }
}
