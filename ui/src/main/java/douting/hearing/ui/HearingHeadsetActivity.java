package douting.hearing.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.see.mvp.base.SeeBaseActivity;

import douting.hearing.core.Hearing;
import douting.hearing.ui.presenter.HearingHeadsetPresenter;

/**
 * @author by wuxiang@tinglibao.com.cn on 2021/4/20.
 */
public class HearingHeadsetActivity extends SeeBaseActivity<HearingHeadsetPresenter> {
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private TextView headset_price;

    @Override
    protected int getContentView() {
        return R.layout.hearing_choose_headset;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setTitle(R.string.hearing_connect_bluetooth);
        initBuyTest();
        findViewById(R.id.click_1).setOnClickListener(this);
        findViewById(R.id.click_2).setOnClickListener(this);
        headset_price = findViewById(R.id.headset_price);

        HearingCircleImageView member_pic = findViewById(R.id.member_pic);
        if (Hearing.sdkUseGender == Hearing.GENDER_MAN) {
            member_pic.setImageResource(R.drawable.hearing_icon_male);
        } else {
            member_pic.setImageResource(R.drawable.hearing_icon_female);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hearing_record_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.hearing_test_record) {
            startActivity(new Intent(mContext, HearingRecordActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void initBuyTest() {
        TextView call_service = findViewById(R.id.call_service);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(getString(R.string.hearing_call_service));
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#fc6400")),
                8, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        call_service.setText(builder);
        call_service.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CONNECT_DEVICE_INSECURE) {
            if (resultCode == Activity.RESULT_OK && data.getExtras() != null) {
                String address = data.getExtras().getString(HearingConnectActivity.EXTRA_DEVICE_ADDRESS);
                goToTesting(address);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.click_1) {
            getPresenter().checkWiredPrice();
        } else if (v.getId() == R.id.click_2) {
            checkDeviceStatus();
        } else if (v.getId() == R.id.call_service) {
            callService();
        }
    }

    private void checkDeviceStatus() {
        Intent intent = new Intent(this, HearingConnectActivity.class);
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE_INSECURE);
    }

    private void callService() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:02787519081")));
    }

    public void goBuyHeadset() {
        try {
            Intent intent = new Intent(Hearing.BUY_HEADSET);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void showHeadsetDescribe(String text) {
        if (!TextUtils.isEmpty(text)) {
            TextView headset_note = findViewById(R.id.headset_note);
            headset_note.setText(text);
        }
    }

    public void showBluetoothDescribe(String text) {
        if (!TextUtils.isEmpty(text)) {
            TextView bluetooth_note = findViewById(R.id.bluetooth_note);
            bluetooth_note.setText(text);
        }
    }

    public void showPromotions(String value) {
        if (!TextUtils.isEmpty(value)) {
            headset_price.setText(getString(R.string.hearing_price_promotions, value));
        }
    }

    public void showNeedPay() {
        headset_price.setText(R.string.hearing_price);
    }

    public void showCount(int count) {
        headset_price.setText(R.string.hearing_price_ready);
    }

    public void showEndTime(long endTime) {
        headset_price.setText(R.string.hearing_price_ready);
    }

    public void showEarModel(String earPath, String earModel) {
        if (!TextUtils.isEmpty(earModel)) {
            TextView ear_model = findViewById(R.id.ear_model);
            ear_model.setText(earModel);
        }
    }

    public void showNoFixDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.hearing_tips);
        builder.setMessage(R.string.hearing_bluetooth_no_fix);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.hearing_reconnect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkDeviceStatus();
            }
        });
        builder.setNeutralButton(R.string.hearing_buy_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callService();
            }
        });
        builder.setNegativeButton(R.string.hearing_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        Dialog dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    public void goToHeadsetTesting() {
        if (Hearing.isHeadsetOn(mContext)) {
            goToTesting(null);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.hearing_tips);
            builder.setMessage(R.string.hearing_please_on_headset);
            builder.setCancelable(false);
            builder.setNegativeButton(R.string.hearing_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public void goToTesting(String mac) {
        Intent intent = new Intent(mContext, HearingTestActivity.class);
        intent.putExtra("mac_address", mac);
        startActivity(intent);
    }
}
