package fypnctucs.bcar.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by kamfu.wong on 28/9/2016.
 */

public class GattData {
    final static public String sort_uuid(String uuid) { return "0000"+uuid+"-0000-1000-8000-00805f9b34fb"; }

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

    public final static String CHARACTERISTIC = sort_uuid("ffe1");
    public final static String SERVICE = sort_uuid("ffe0");

    public final static String defaultC1 = sort_uuid("2a05");
    public final static String defaultC2 = sort_uuid("2a00");
    public final static String defaultC3 = sort_uuid("2a01"); // Device Name
    public final static String defaultC4 = sort_uuid("2aa6");

    public final static String defaultS1 = sort_uuid("1800");
    public final static String defaultS2 = sort_uuid("1801");

    public static String PROPERTY[] = setPROPERTY();
    public static String PERMISSION[] = setPERMISSION();

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

        UuidName.put(CHARACTERISTIC, "Characteristic");
        UuidName.put(SERVICE, "Service");

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
    public GattData() {
    }

    public GattData(BluetoothGatt tmp) {
        bluetoothGatt = tmp;
        init();

        for (BluetoothGattService service : bluetoothGatt.getServices()) {
            addService(service);
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                addCharacteristic(characteristic);
            }
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
}
