package douting.hearing.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import douting.hearing.core.entity.DeviceItem;
import douting.hearing.core.entity.MapList;

/**
 * @author by wuxiang@tinglibao.com.cn on 2021/4/20.
 */

public class HearingConnectAdapter extends BaseAdapter {
    private MapList<String, DeviceItem> mDeviceMap;
    private Context mContext;
    private OnItemClickListener mListener;

    public HearingConnectAdapter(Context context, MapList<String, DeviceItem> devices) {
        this.mContext = context;
        this.mDeviceMap = devices;
    }

    public void add(String key, DeviceItem item) {
        mDeviceMap.add(key, item);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return mDeviceMap.getSize();
    }

    @Override
    public DeviceItem getItem(int position) {
        return mDeviceMap.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.hearing_device_list_item, parent, false);
            holder = new ViewHolder();
            holder.device_name = view.findViewById(R.id.device_name);
            holder.device_address = view.findViewById(R.id.device_address);
            holder.device_state = view.findViewById(R.id.device_state);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final DeviceItem deviceItem = mDeviceMap.getItem(position);
        holder.device_name.setText(deviceItem.getName());
        holder.device_address.setText(deviceItem.getAddress());

        if (deviceItem.getBondState() == BluetoothDevice.BOND_BONDING) {
            holder.device_state.setText(R.string.hearing_bluetooth_pairing);
        } else if (deviceItem.getA2dpProfileState() == BluetoothProfile.STATE_CONNECTING) {
            holder.device_state.setText(R.string.hearing_bluetooth_connecting);
        } else if (deviceItem.getA2dpProfileState() == BluetoothProfile.STATE_CONNECTED) {
            holder.device_state.setText(R.string.hearing_bluetooth_connected);
        } else {
            holder.device_state.setText(null);
        }

        if (mListener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(deviceItem);
                }
            });
        }
        return view;
    }

    class ViewHolder {
        TextView device_name, device_address, device_state;
    }

    public interface OnItemClickListener {
        void onItemClick(DeviceItem item);
    }
}
