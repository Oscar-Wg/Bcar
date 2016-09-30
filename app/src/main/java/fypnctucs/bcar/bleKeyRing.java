package fypnctucs.bcar;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;

/**
 * Created by kamfu.wong on 29/9/2016.
 */

public class bleKeyRing {

    private ListAdapter SaveDevicesAdapter;
    private Activity activity;
    private list_fragment fragment;

    private String  name;
    private int type;

    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;

    protected gattData data;

    private boolean connected;
    private boolean connecting;

    bleKeyRing() {
        this(null, null, null, "unknow", 0, null, false);
    }

    bleKeyRing(Activity activity, list_fragment fragment, ListAdapter SaveDevicesAdapter, String name, int type, BluetoothDevice device, boolean connected) {
        this.activity = activity;
        this.fragment = fragment;
        this.SaveDevicesAdapter = SaveDevicesAdapter;
        this.name = name;
        this.type = type;
        this.device = device;
        this.connected = connected;
        connecting = false;
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
        data = new gattData(bluetoothGatt);
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

                //gatt.requestMtu(64);
                //((MainActivity)getActivity()).status("requestMtu sent");

                gatt.discoverServices();
                //((MainActivity)activity).status("discoverServices sent");
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                ((MainActivity)activity).status(getName() + " 連線中斷");
                setConnected(false);
            }
            fragment.refrestAdapter();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //((MainActivity)activity).status("onServicesDiscovered");
                initData();

            }// else ((MainActivity)activity).status("onServicesDiscovered fail");

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


}
