package fypnctucs.bcar.device;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.util.Log;

import java.io.ObjectInput;
import java.util.ArrayList;

import fypnctucs.bcar.MyDBHelper;
import fypnctucs.bcar.fragment.list_fragment;

/**
 * Created by kamfu.wong on 3/10/2016.
 */

public class BleDeviceDAO {
    public static final String TABLE_NAME = "DEVICES";

    public static final String KEY_ID = "ID";

    public static final String NAME_COLUMN = "NAME";
    public static final String TYPE_COLUMN = "TYPE";
    public static final String DEVICE_COLUMN = "DEVICE";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME_COLUMN + " TEXT, " +
                    TYPE_COLUMN + " INT, " +
                    DEVICE_COLUMN + " TEXT)";

    private SQLiteDatabase db;

    private DeviceListAdapter devicesAdapter;
    private Activity activity;
    private list_fragment fragment;

    public BleDeviceDAO(list_fragment fragment, DeviceListAdapter devicesAdapter) {
        this.devicesAdapter = devicesAdapter;
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.db = MyDBHelper.getDatabase(activity.getApplicationContext());
    }

    public long insert(BleDevice device) {
        ContentValues cv = new ContentValues();

        Parcel p = Parcel.obtain();
        device.getDevice().writeToParcel(p, 0);
        byte[] dev = p.marshall();

        cv.put(NAME_COLUMN, device.getName());
        cv.put(TYPE_COLUMN, device.getType());
        cv.put(DEVICE_COLUMN, dev);

        long id = db.insert(TABLE_NAME, null, cv);

        return id;
    }

    public boolean update(BleDevice device) {
        ContentValues cv = new ContentValues();

        Parcel p = Parcel.obtain();
        device.getDevice().writeToParcel(p, 0);
        byte[] dev = p.marshall();

        cv.put(NAME_COLUMN, device.getName());
        cv.put(TYPE_COLUMN, device.getType());
        cv.put(DEVICE_COLUMN, dev);

        String where = KEY_ID + "=" + device.getId();

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public boolean delete(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    public ArrayList<BleDevice> getAll() {
        ArrayList<BleDevice> result = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public BleDevice get(long id) {
        BleDevice device = null;
        String where = KEY_ID + "=" + id;

        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            device = getRecord(result);
        }

        result.close();
        return device;
    }

    public BleDevice getRecord(Cursor cursor) {

        ObjectInput objInput = null;

        long id = cursor.getLong(0);
        String name = cursor.getString(1);
        int type = cursor.getInt(2);
        byte[] arr = cursor.getBlob(3);

        Parcel p = Parcel.obtain();
        p.unmarshall(arr, 0, arr.length);
        p.setDataPosition(0); // This is extremely important!

        BluetoothDevice dev = BluetoothDevice.CREATOR.createFromParcel(p);

        BleDevice device = new BleDevice(activity, fragment, devicesAdapter, name, type, dev, false);
        device.setId(id);

        return device;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext())
            result = cursor.getInt(0);

        return result;
    }

    public void close() {
        db.close();
    }

}
