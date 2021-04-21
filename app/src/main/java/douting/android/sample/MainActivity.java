package douting.android.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.see.mvp.base.SeeBaseActivity;

import douting.hearing.core.Hearing;
import douting.hearing.core.callback.ExtCallback;
import douting.hearing.core.entity.HearingUser;
import douting.hearing.ui.HearingHeadsetActivity;

/**
 * @author by WuXiang on 2016/11/28.
 */

public class MainActivity extends SeeBaseActivity {
    @Override
    protected int getContentView() {
        return R.layout.main_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //Android 6.0 版本之后蓝牙扫描需要定位权限，请需提前申请 判断是否有权限
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
        HearingUser userInfo = new HearingUser("FGHRTYUILADFBAECCF", "19890512", "13026100215", Hearing.GENDER_MAN);
        //其他用户信息
        userInfo.setUsername("XiaoSe");
        userInfo.setAddress("WuHan");
        userInfo.setEmail("wuxiang@tinglibao.com.cn");

        Hearing.setUser(userInfo, new ExtCallback<HearingUser>() {
            @Override
            public void onSuccess(HearingUser hearingUser) {
                Log.d("Hearing.setUser", "onSuccess");
            }

            @Override
            public void onFail(int i, String s) {
                Log.d("Hearing.setUser", "onFail code = " + i + " msg = " + s);
            }
        });
    }

    public void startTest(View view) {
        Intent intent = new Intent(this, HearingHeadsetActivity.class);
        startActivity(intent);
    }
}
