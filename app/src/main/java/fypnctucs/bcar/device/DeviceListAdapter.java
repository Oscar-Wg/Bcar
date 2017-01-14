package fypnctucs.bcar.device;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fypnctucs.bcar.R;
import fypnctucs.bcar.DataFormat;
import fypnctucs.bcar.ble.GattData;

/**
 * Created by kamfu.wong on 29/9/2016.
 */

public class DeviceListAdapter extends BaseAdapter {

    private ArrayList<BleDevice> devicesList;
    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setList(ArrayList<BleDevice> devicesList) {
        this.devicesList = devicesList;
    }

    @Override
    public int getCount() {
        return devicesList.size();
    }

    @Override
    public BleDevice getItem(int position) {
        return devicesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(activity.getApplicationContext(), R.layout.item_device_list, null);
            new DeviceListAdapter.ViewHolder(convertView);
        }

        ViewHolder holder = (DeviceListAdapter.ViewHolder) convertView.getTag();
        final BleDevice item = getItem(position);
        holder.name.setText(item.getName());

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isConnected()) {
                    item.data.writeCharacteristic(GattData.CHARACTERISTIC, "1".getBytes());
                }
            }
        });

        if (item.isConnected()) {
            holder.icon.setImageResource(DataFormat.CONNECT_DEVICE_ICON[item.getType()]);
        } else
            holder.icon.setImageResource(DataFormat.DISCONNECT_DEVICE_ICON[item.getType()]);
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView name;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.iv_icon);
            name = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(this);
        }
    }

}