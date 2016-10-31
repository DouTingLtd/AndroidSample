package douting.android.sample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

import douting.android.sample.base.ToolbarActivity;

/**
 * Created by WuXiang on 2016/9/6.
 * ..
 */
public class MainActivity extends ToolbarActivity {
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private ImageView disconnect_im;
    private Button start_bt;
    private boolean isConnect = false;
    private String bluetoothMac;

    @Override
    protected int getContentView() {
        return R.layout.main_layout;
    }


    @Override
    protected void initView() {
        mActionBar.setDisplayHomeAsUpEnabled(false);

        disconnect_im = findView(R.id.disconnect_im);
        start_bt = findView(R.id.start_bt);
        start_bt.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetooth();
//        connectReady("16:07:07:00:C3:55");
    }

    private void checkBluetooth() {
        int a2dp = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        int headset = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        int health = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);

        int flag = -1;
        if (a2dp == BluetoothProfile.STATE_CONNECTED) {
            flag = a2dp;
        } else if (headset == BluetoothProfile.STATE_CONNECTED) {
            flag = headset;
        } else if (health == BluetoothProfile.STATE_CONNECTED) {
            flag = health;
        }

        if (flag != -1) {
            bluetoothAdapter.getProfileProxy(MainActivity.this, new BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceDisconnected(int profile) {
                }

                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    List<BluetoothDevice> mDevices = proxy.getConnectedDevices();
                    if (mDevices != null && mDevices.size() > 0) {
                        for (BluetoothDevice device : mDevices) {
                            connectReady(device.getAddress());
                        }
                    } else {
                        showDisconnectIcon();
                    }
                }
            }, flag);
        } else {
            showDisconnectIcon();
        }
    }

    private void connectReady(String mac) {
        bluetoothMac = mac;
        isConnect = true;
        disconnect_im.setVisibility(View.GONE);
        start_bt.setText(R.string.listening_start);
    }

    private void showDisconnectIcon() {
        isConnect = false;
        disconnect_im.setVisibility(View.VISIBLE);
        start_bt.setText(R.string.connect_bluetooth);
    }

    private void startSetting() {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intent);
    }

    private void startTest() {
        if (!TextUtils.isEmpty(bluetoothMac)) {
            Intent intent = new Intent(mContext, TestActivity.class);
            intent.putExtra(TestActivity.MAC, bluetoothMac);
            startActivity(intent);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.test_record:
//                break;
//            case R.id.test_setting:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.start_bt:
                if (isConnect) {
                    startTest();
                } else {
                    startSetting();
                }
                break;
        }
    }
}
