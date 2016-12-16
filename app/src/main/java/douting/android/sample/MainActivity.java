package douting.android.sample;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import douting.android.sample.base.ToolbarActivity;
import douting.hearing.core.Hearing;
import douting.hearing.core.callback.ExtCallback;
import douting.hearing.core.entity.HearingUser;

/**
 * Created by WuXiang on 2016/11/28.
 * WuXiang.
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
        findView(R.id.easy_record).setOnClickListener(this);

        setUser();
    }

    private void setUser() {
        HearingUser userInfo = new HearingUser("FGHRTYUILADFBAEE");
        userInfo.setPhone("13026100183");
        userInfo.setGender(Hearing.GENDER_MAN);
        userInfo.setBirthday("20001102");

        Hearing.setUser(userInfo, new ExtCallback<HearingUser>() {
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.easy_start:
                Hearing.startTest(mContext);
                break;
            case R.id.easy_record:
                Hearing.startRecord(mContext);
                break;
            case R.id.custom_start:
                startActivity(new Intent(mContext, CustomActivity.class));
                break;
        }
    }
}
