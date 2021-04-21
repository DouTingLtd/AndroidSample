package douting.hearing.ui;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

import douting.hearing.core.Hearing;
import douting.hearing.core.entity.DeviceItem;
import douting.hearing.core.entity.MapList;

/**
 * @author by wuxiang@tinglibao.com.cn on 2021/4/20.
 */
public class HearingConnectActivity extends Activity {
    public static String EXTRA_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private static final int REQUEST_ENABLE_BT = 3;
    private static final String NAMES = "T100#T100S#T200#T200S";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothA2dp mA2dpService;
    private BluetoothHeadset mHeadsetService;

    private MapList<String, DeviceItem> mPairedDevicesMap;
    private MapList<String, DeviceItem> mNewDevicesMap;
    private HearingConnectAdapter mPairedDevicesAdapter;
    private HearingConnectAdapter mNewDevicesAdapter;

    private TextView mTitle, empty_word;
    private LinearLayout button_ll;
    private boolean isInitReceiver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.hearing_device_list);
        setResult(Activity.RESULT_CANCELED);
        mTitle = findViewById(R.id.devices_title);
        button_ll = findViewById(R.id.button_ll);
        empty_word = findViewById(R.id.empty_word);

        //开启蓝牙
        if (openBluetooth()) {
            //获取Profile实例
            getProfileProxy();
        }
    }

    protected void setThisTitle(int resid) {
        mTitle.setText(resid);
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
        isInitReceiver = true;
    }

    private boolean openBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            finish();
            return false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                return false;
            } else {
                return true;
            }
        }
    }

    private void getProfileProxy() {
        mBluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (mA2dpService == null) {
                    mA2dpService = (BluetoothA2dp) proxy;
                    initView();
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
            }
        }, BluetoothProfile.A2DP);

        mBluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (mHeadsetService == null) {
                    mHeadsetService = (BluetoothHeadset) proxy;
                    initView();
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
            }
        }, BluetoothProfile.HEADSET);
    }

    private void initView() {
        if (mA2dpService != null && mHeadsetService != null) {
            //注册广播接收器
            initReceiver();

            //实例化两个List
            initPairedView();
            initNewView();
        }
        initBuyButton();
    }

    private void initBuyButton() {
        Button buyButton = findViewById(R.id.button_buy);
        buyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Hearing.BUY_BT));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initPairedView() {
        mPairedDevicesMap = new MapList<>();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                if (checkName(device.getName())) {
                    mPairedDevicesMap.add(device.getAddress(), toDeviceItem(device));
                }
            }
        }

        mPairedDevicesAdapter = new HearingConnectAdapter(this, mPairedDevicesMap);
        mPairedDevicesAdapter.setOnItemClickListener(mDeviceClickListener);
        ListView pairedListView = findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesAdapter);
        pairedListView.setEmptyView(empty_word);
    }

    private void initNewView() {
        // Initialize the button to perform device discovery
        Button scanButton = findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                button_ll.setVisibility(View.GONE);
                empty_word.setVisibility(View.GONE);
            }
        });

        mNewDevicesMap = new MapList<>();

        mNewDevicesAdapter = new HearingConnectAdapter(this, mNewDevicesMap);
        mNewDevicesAdapter.setOnItemClickListener(mDeviceClickListener);
        ListView newDevicesListView = findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesAdapter);
    }

    private DeviceItem toDeviceItem(BluetoothDevice device) {
        DeviceItem item = new DeviceItem();
        item.setDevice(device);
        item.setName(device.getName());
        item.setAddress(device.getAddress());
        item.setBondState(device.getBondState());
        item.setA2dpProfileState(mA2dpService.getConnectionState(device));
        item.setHeadsetProfileState(mHeadsetService.getConnectionState(device));
        return item;
    }

    /**
     * 配对
     */
    private void pairDevice(DeviceItem item) {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        //如果设备没有绑定
        if (item.getBondState() == BluetoothDevice.BOND_NONE) {
            BluetoothDevice device = item.getDevice();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //Android 4.4 API 19 以上才开放Bond接口
                device.createBond();
            } else {
                //API 19 以下用反射调用Bond接口
                try {
                    device.getClass().getMethod("connect").invoke(device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (item.getBondState() == BluetoothDevice.BOND_BONDED) {
            connectDevice(item);
        }
    }

    /**
     * 连接
     * 最主要连接A2dp协议，Headset附带
     */
    private void connectDevice(DeviceItem item) {
        if (item.getBondState() == BluetoothDevice.BOND_BONDED) {
            //如果A2dp Profile 没有连接的情况
            if (item.getA2dpProfileState() == BluetoothProfile.STATE_DISCONNECTED) {
                //API 不开放连接 Profile 的接口，利用反射调用连接方法
                try {
                    mA2dpService.getClass().getMethod("connect", BluetoothDevice.class)
                            .invoke(mA2dpService, item.getDevice());

                    mHeadsetService.getClass().getMethod("connect", BluetoothDevice.class)
                            .invoke(mHeadsetService, item.getDevice());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (item.getA2dpProfileState() == BluetoothProfile.STATE_CONNECTED) {
                returnTheMac(item);
            }
        }
    }

    private void returnTheMac(DeviceItem item) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, item.getAddress());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    //获取Profile实例
                    getProfileProxy();
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (isInitReceiver) {
            unregisterReceiver(mReceiver);
        }

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();

            mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mA2dpService);
            mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mHeadsetService);
            mA2dpService = null;
            mHeadsetService = null;
        }
        super.onDestroy();
    }

    /**
     * 开始搜索设备
     */
    private void doDiscovery() {
        setThisTitle(R.string.hearing_scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    private boolean checkName(String name) {
        return name != null && NAMES.contains(name);
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private HearingConnectAdapter.OnItemClickListener mDeviceClickListener =
            new HearingConnectAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DeviceItem item) {
                    pairDevice(item);
                }
            };


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //发现新设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (checkName(device.getName())) {
                        mNewDevicesAdapter.add(device.getAddress(), toDeviceItem(device));
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //搜索结束
                if (mNewDevicesMap.getSize() == 0) {
                    setThisTitle(R.string.hearing_none_found);
                } else {
                    setThisTitle(R.string.hearing_select_device);
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                //设备绑定状态改变
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                DeviceItem item = null;

                if (mPairedDevicesMap.getItem(device.getAddress()) != null) {
                    item = mPairedDevicesMap.getItem(device.getAddress());
                    item.setBondState(bondState);
                    mPairedDevicesAdapter.notifyDataSetChanged();
                } else if (mNewDevicesMap.getItem(device.getAddress()) != null) {
                    item = mNewDevicesMap.getItem(device.getAddress());
                    item.setBondState(bondState);
                    mNewDevicesAdapter.notifyDataSetChanged();
                }

                //收到绑定成功的通知后自动连接
                if (item != null && bondState == BluetoothDevice.BOND_BONDED) {
                    connectDevice(item);
                }
            } else if (BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                //A2dp连接状态改变
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int profileState = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED);
                DeviceItem item = null;

                if (mPairedDevicesMap.getItem(device.getAddress()) != null) {
                    item = mPairedDevicesMap.getItem(device.getAddress());
                    item.setA2dpProfileState(profileState);
                    mPairedDevicesAdapter.notifyDataSetChanged();
                } else if (mNewDevicesMap.getItem(device.getAddress()) != null) {
                    item = mNewDevicesMap.getItem(device.getAddress());
                    item.setA2dpProfileState(profileState);
                    mNewDevicesAdapter.notifyDataSetChanged();
                }

                //收到连接成功的通知
                if (item != null && profileState == BluetoothProfile.STATE_CONNECTED) {
                    returnTheMac(item);
                }
            }
        }
    };
}
