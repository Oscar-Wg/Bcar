package fypnctucs.bcar.device;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

import fypnctucs.bcar.MainActivity;
import fypnctucs.bcar.ble.GattData;
import fypnctucs.bcar.fragment.list_fragment;
import fypnctucs.bcar.history.History;

/**
 * Created by kamfu.wong on 29/9/2016.
 */

public class BleDevice {

    private DeviceListAdapter devicesAdapter;
    private Activity activity;
    private list_fragment fragment;

    private long id;
    private String  name;
    private int type;
    private BluetoothDevice device;

    private BluetoothGatt bluetoothGatt;
    protected GattData data;

    private boolean connected;
    private boolean connecting;

    public BleDevice() {
        this(null, null, null, "unknow", 0, null, false);
    }

    public BleDevice(Activity activity, list_fragment fragment, DeviceListAdapter devicesAdapter, String name, int type, BluetoothDevice device, boolean connected) {
        this.activity = activity;
        this.fragment = fragment;
        this.devicesAdapter = devicesAdapter;
        this.name = name;
        this.type = type;
        this.device = device;
        this.connected = connected;
        connecting = false;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void connect(MainActivity activity) {
        bluetoothGatt = device.connectGatt(activity, false, gattCallback);
        connecting = true;
    }

    public void disconnect() {
        if (bluetoothGatt != null)
            bluetoothGatt.disconnect();
    }

    public void initData() {
        data = new GattData(bluetoothGatt);
    }

    public void setConnected(boolean status) {
        connected = status;
        if (!connected)
            bluetoothGatt = null;
    }

    // BLE gatt client callback
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            connecting = false;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                setConnected(true);
                ((MainActivity)activity).status(getName() + " 已連線");
                fragment.getLocation(DeviceOneTimeLocationListener);
                //gatt.requestMtu(64);
                gatt.discoverServices();
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                setConnected(false);
                ((MainActivity)activity).status(getName() + " 連線中斷");
                fragment.getLocation(DeviceOneTimeLocationListener);
            }
            fragment.refreshDevicesAdapter();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS)
                initData();
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            /*
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ((MainActivity)activity).status("onCharacteristicRead");
            } else ((MainActivity)activity).status("onCharacteristicRead fail");
*/
            data.replaceCharacteristic(characteristic);

            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            /*
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ((MainActivity)activity).status("onCharacteristicWrite");
            } else ((MainActivity)activity).status("onCharacteristicRead fail");
*/
            data.replaceCharacteristic(characteristic);

            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //((MainActivity)activity).status("onCharacteristicChanged");
            data.replaceCharacteristic(characteristic);

            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onMtuChanged (BluetoothGatt gatt, int mtu, int status) {
            /*
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ((MainActivity)activity).status("onMtuChanged: " + mtu);
            } else ((MainActivity)activity).status("onMtuChanged fail");
*/
            super.onMtuChanged(gatt, mtu, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    };

    public String timeString(int[] t) {
        String time = String.format("%04d", t[0]) + "-" +
                String.format("%02d", t[1]) + "-" +
                String.format("%02d", t[2]) + " " +
                String.format("%02d", t[3]) + ":" +
                String.format("%02d", t[4]) + ":" +
                String.format("%02d", t[5]);
        return time;
    }

    private LocationListener DeviceOneTimeLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Calendar calendar = Calendar.getInstance();
                int[] date = new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)};
                Log.d("LocationListener", "History_insert");
                fragment.History_insert(new History(device.getAddress(), timeString(date), location.getLatitude(), location.getLongitude()));
                ((MainActivity)activity).StopLocationListener(this);
            } else {
                Log.d("onLocationChanged", "Location is null");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

}
