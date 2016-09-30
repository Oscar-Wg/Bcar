package fypnctucs.bcar;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by kamfu.wong on 28/9/2016.
 */

public class gattData {
    final static public String sort_uuid(String uuid) { return "0000"+uuid+"-0000-1000-8000-00805f9b34fb"; }
    final static public String custom_uuid(String uuid) { return "0001"+uuid+"-0000-1000-8000-00805f9babcd"; }
    final static private String[] setPROPERTY() {
        String[] PROPERTY = new String[512];
        PROPERTY[BluetoothGattCharacteristic.PROPERTY_WRITE] = "WRITE";
        PROPERTY[BluetoothGattCharacteristic.PROPERTY_READ] = "READ";
        PROPERTY[BluetoothGattCharacteristic.PROPERTY_NOTIFY] = "NOTIFY";
        PROPERTY[BluetoothGattCharacteristic.PROPERTY_INDICATE] = "INDICATE";
        PROPERTY[BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE] = "WRITE_NO_RESPONSE";
        return PROPERTY;
    }
    final static private String[] setPERMISSION() {
        String[] PERMISSION = new String[512];
        PERMISSION[BluetoothGattCharacteristic.PERMISSION_READ] = "READ";
        PERMISSION[BluetoothGattCharacteristic.PERMISSION_WRITE] = "WRITE";
        return PERMISSION;
    }

    final static String UAV = custom_uuid("0010"); // service
    final static String AutopilotCmd  = custom_uuid("0011"); // characteristic

    final static String defaultC1 = sort_uuid("2a05");
    final static String defaultC2 = sort_uuid("2a00");
    final static String defaultC3 = sort_uuid("2a01"); // Device Name
    final static String defaultC4 = sort_uuid("2aa6");

    final static String defaultS1 = sort_uuid("1800");
    final static String defaultS2 = sort_uuid("1801");

    static String PROPERTY[] = setPROPERTY();
    static String PERMISSION[] = setPERMISSION();

    private HashMap<String, BluetoothGattService> Services;
    private HashMap<String, BluetoothGattCharacteristic> Characteristics;
    private HashMap<String, String> UuidName;

    private BluetoothGattServer bluetoothGattServer;
    private BluetoothGatt bluetoothGatt;
    private BluetoothManager bluetoothManager;

    private void init() {
        Characteristics = new HashMap<>();
        Services = new HashMap<>();
        UuidName = new HashMap<>();

        UuidName.put(UAV, "UAV");
        UuidName.put(AutopilotCmd, "AutopilotCmd");

        // default Characteristics
        UuidName.put(defaultC1, "Service Changed");
        UuidName.put(defaultC2, "Device Name");
        UuidName.put(defaultC3, "Appearance");
        UuidName.put(defaultC4, "Central Address Resolution");

        // default Services
        UuidName.put(defaultS1, "Generic Access");
        UuidName.put(defaultS2, "Generic Attribute");
    }

    // same

    public void addService(BluetoothGattService s) { Services.put(s.getUuid().toString(), s); }

    public void addCharacteristic(BluetoothGattCharacteristic c) { Characteristics.put(c.getUuid().toString(), c); }

    public Set<String> getCharacteristic() { return Characteristics.keySet(); }

    public BluetoothGattCharacteristic getCharacteristic(String uuid) { return Characteristics.get(uuid); }

    public Set<String> getServices() { return Services.keySet(); }

    public BluetoothGattService getService(String uuid) { return Services.get(uuid); }



    public String getName(String uuid) {
        String name = UuidName.get(uuid);
        if (name == null) return uuid;
        return name;
    }

    public String getName(BluetoothGattCharacteristic characteristic) {
        String name = UuidName.get(characteristic.getUuid().toString());
        if (name == null) return characteristic.getUuid().toString();
        return name;
    }

    // client

    gattData(BluetoothGatt tmp) {
        bluetoothGatt = tmp;
        init();

        for (BluetoothGattService service : bluetoothGatt.getServices()) {
            addService(service);
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics())
                addCharacteristic(characteristic);
        }
    }

    public void readCharacteristic(String uuid) {
        bluetoothGatt.readCharacteristic(getCharacteristic(uuid));
    }

    public void writeCharacteristic(String uuid, byte[] value) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(uuid);
        characteristic.setValue(value);

        bluetoothGatt.writeCharacteristic(characteristic);

    }

    public void setCharacteristicNotification(String uuid, boolean enable) {
        bluetoothGatt.setCharacteristicNotification(getCharacteristic(uuid), enable);
    }

    public void getRssi() {
        bluetoothGatt.readRemoteRssi();
    }

    public void replaceCharacteristic(BluetoothGattCharacteristic c) { Characteristics.put(c.getUuid().toString(), c); }

    // server

    gattData(BluetoothGattServer tmp, BluetoothManager tmp2) {
        bluetoothGattServer = tmp;
        bluetoothManager = tmp2;
        init();

        for (BluetoothGattService service : bluetoothGattServer.getServices()) {
            addService(service);
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics())
                addCharacteristic(characteristic);
        }
    }

    public void updateCharacteristic(String uuid, byte[] value) {
        getCharacteristic(uuid).setValue(value);
    }

    public void sentNotifyCharacteristic(String uuid, boolean crl) {
        BluetoothGattCharacteristic c = getCharacteristic(uuid);
        List<String> devices = new ArrayList<>();
        for (BluetoothDevice device : bluetoothManager.getConnectedDevices(BluetoothProfile.GATT))
            if (!devices.contains(device.getAddress())) {
                bluetoothGattServer.notifyCharacteristicChanged(device, c, crl);
                devices.add(device.getAddress());
            }
    }

    public void sendResponse(BluetoothDevice device, int requestid, int status, int offset, byte[] value) {
        bluetoothGattServer.sendResponse(device, requestid, status, offset, value);
    }
}
