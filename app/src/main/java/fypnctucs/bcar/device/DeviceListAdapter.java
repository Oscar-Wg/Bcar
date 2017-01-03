package fypnctucs.bcar.device;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fypnctucs.bcar.MainActivity;
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

        final ViewHolder holder = (DeviceListAdapter.ViewHolder) convertView.getTag();
        final BleDevice item = getItem(position);
        holder.name.setText(item.getName());

        holder.speaker.setImageResource(R.drawable.ic_speaker);
        if (item.isConnected()) {
            holder.speaker.getLayoutParams().width = (int)(activity.getResources().getDisplayMetrics().density * 50);
            holder.speaker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isConnected()) {
                        char c = 49;
                        item.data.writeCharacteristic(GattData.CHARACTERISTIC, String.valueOf(c).getBytes());
                       // Log.d("icon.setOnClickListener", "click " + String.valueOf(c).getBytes());
                    }
                }
            });
        } else
            holder.speaker.getLayoutParams().width = 0;

        final int RSSI_FRESH_TIME = 1000;
        Runnable getRssi = new Runnable() {
            @Override
            public void run() {
                if (item.isConnected()) {
                    if (((MainActivity)activity).fragmentPos == 0)
                        ((MainActivity)activity).mHandler.postDelayed(this, RSSI_FRESH_TIME);
                    else {
                        item.RSSI = 0;
                        return;
                    }
                    item.data.getRssi();
                    switch(item.RSSI) {
                        case 1:
                            holder.find.setImageResource(R.drawable.signal1);
                            break;
                        case 2:
                            holder.find.setImageResource(R.drawable.signal2);
                            break;
                        case 3:
                            holder.find.setImageResource(R.drawable.signal3);
                            break;
                        case 4:
                            holder.find.setImageResource(R.drawable.signal4);
                            break;
                    }
                } else item.RSSI = 0;
            }
        };

        holder.find.setOnClickListener(new View.OnClickListener() {

            private ArrayList<BluetoothDevice> foundDevicesArray;
            private ScanCallback findScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    BluetoothDevice device = result.getDevice();
                    if (device.getAddress().equals(item.getDevice().getAddress())) {
                        Log.d("onScanResult", "found");
                        item.connect(activity);
                        ((MainActivity)activity).mBLEClient.BLEScan(false);
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                }

                @Override
                public void onScanFailed(int errorCode) {
                    Log.e("Scan Failed", "Error Code: " + errorCode);
                }

            };

            @Override
            public void onClick(View v) {

                switch(item.findStatus) {
                    case BleDevice.DISCONNECT:
                        holder.find.setImageResource(R.drawable.ic_loading);
                        item.findStatus = BleDevice.TRYING;

                        foundDevicesArray = new ArrayList<>();
                        ((MainActivity)activity).mBLEClient.SetScanCallback(findScanCallback);
                        ((MainActivity)activity).mBLEClient.BLEScan(true);
                        Log.d("Scan", "start");

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (item.findStatus == BleDevice.TRYING) {
                                    ((MainActivity)activity).mBLEClient.BLEScan(false);
                                    holder.find.setImageResource(R.drawable.ic_finding);
                                    item.findStatus = BleDevice.DISCONNECT;
                                    ((MainActivity)activity).status("尋找失敗");
                                }
                            }
                        }, 5000);

                        break;
                    case BleDevice.TRYING:
                        break;
                    case BleDevice.CONNECTED:
                        break;
                }
            }
        });

        if (item.isConnected()) {
            holder.icon.setImageResource(DataFormat.CONNECT_DEVICE_ICON[item.getType()]);
        } else
            holder.icon.setImageResource(DataFormat.DISCONNECT_DEVICE_ICON[item.getType()]);

        if (item.isConnected() && item.RSSI == 0) {
            item.RSSI = 4;
            getRssi.run();
        } else if (item.findStatus == BleDevice.DISCONNECT) {
            holder.find.setImageResource(R.drawable.ic_finding);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView name;
        ImageView find;
        ImageView speaker;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.iv_icon);
            name = (TextView) view.findViewById(R.id.tv_name);
            find = (ImageView) view.findViewById(R.id.find_btn);
            speaker = (ImageView) view.findViewById(R.id.speaker_btn);

            view.setTag(this);
        }
    }

}