package douting.android.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import douting.android.sample.base.ToolbarActivity;
import douting.hearing.core.Hearing;
import douting.hearing.core.callback.ExtCallback;
import douting.hearing.core.entity.HearingUser;

/**
 * @author by WuXiang on 2016/11/28.
 */

public class MainActivity extends ToolbarActivity {
    @Override
    protected int getContentView() {
        return R.layout.main_layout;
    }

    @Override
    protected void initView() {
        //判断是否有权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }

    public void setUser(View view) {
        //sdkUseId 将为用户的唯一标识
        //Gender 只支持 Hearing.GENDER_MAN 和 Hearing.GENDER_WOMAN
        //Birthday 格式为 ‘‘yyyyMMdd’’
        HearingUser userInfo = new HearingUser("FGHRTYUILADFBAECCF", "19890512", "13026100214", Hearing.GENDER_MAN);
        //其他用户信息
        userInfo.setUsername("XiaoSe");
        userInfo.setAddress("WuHan");
        userInfo.setEmail("wuxiang@tinglibao.com.cn");

        Hearing.setUser(this, userInfo, new ExtCallback<HearingUser>() {
            @Override
            public void onSuccess(HearingUser data) {
                Log.d("onSuccess", "onSuccess");
            }

            @Override
            public void onFail(int code) {
                Log.d("onFail", "code = " + code);
            }
        });
    }

    public void startTest(View view) {
        Hearing.startTest(mContext);
    }
}
