package fypnctucs.bcar.device;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import java.io.ObjectInput;
import java.util.ArrayList;

import fypnctucs.bcar.MyDBHelper;

/**
 * Created by kamfu.wong on 3/10/2016.
 */

public class BleDeviceDAO {
    public static final String TABLE_NAME = "DEVICES";

    public static final String KEY_ID = "ID";

    public static final String NAME_COLUMN = "NAME";
    public static final String TYPE_COLUMN = "TYPE";
    public static final String DEVICE_COLUMN = "DEVICE";
    public static final String NOTIFIED_COLUMN = "NOTIFIED";
    public static final String AUTOCONNECT_COLUMN = "AUTOCONNECT";
    public static final String AUTORECORD_COLUMN = "AUTORECORD";
    public static final String LASTLNG_COLUMN = "LASTLNG";
    public static final String LASTLAT_COLUMN = "LASTLAT";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME_COLUMN + " TEXT, " +
                    TYPE_COLUMN + " INT, " +
                    DEVICE_COLUMN + " TEXT, " +
                    NOTIFIED_COLUMN + " INT, " +
                    AUTOCONNECT_COLUMN + " INT, " +
                    AUTORECORD_COLUMN + " INT, " +
                    LASTLNG_COLUMN + " REAL, " +
                    LASTLAT_COLUMN + " REAL)";

    private SQLiteDatabase db;

    public BleDeviceDAO(Context context) {
        this.db = MyDBHelper.getDatabase(context);
    }

    public long insert(BleDevice device) {
        ContentValues cv = new ContentValues();

        Parcel p = Parcel.obtain();
        device.getDevice().writeToParcel(p, 0);
        byte[] dev = p.marshall();

        cv.put(NAME_COLUMN, device.getName());
        cv.put(TYPE_COLUMN, device.getType());
        cv.put(DEVICE_COLUMN, dev);
        cv.put(NOTIFIED_COLUMN, device.isNotified());
        cv.put(AUTOCONNECT_COLUMN, device.isAutoConnect());
        cv.put(AUTORECORD_COLUMN, device.isAutoRecord());
        cv.put(LASTLNG_COLUMN, device.getLast_lng());
        cv.put(LASTLAT_COLUMN, device.getLast_lat());

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
        cv.put(NOTIFIED_COLUMN, device.isNotified());
        cv.put(AUTOCONNECT_COLUMN, device.isAutoConnect());
        cv.put(AUTORECORD_COLUMN, device.isAutoRecord());
        cv.put(LASTLNG_COLUMN, device.getLast_lng());
        cv.put(LASTLAT_COLUMN, device.getLast_lat());

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
        boolean notified = cursor.getShort(4) != 0 ? true : false;
        boolean autoConnect = cursor.getShort(5) != 0 ? true : false;
        boolean autoRecord = cursor.getShort(6) != 0 ? true : false;
        double last_lng = cursor.getFloat(7);
        double last_lat = cursor.getFloat(8);

        Parcel p = Parcel.obtain();
        p.unmarshall(arr, 0, arr.length);
        p.setDataPosition(0); // This is extremely important!

        BluetoothDevice dev = BluetoothDevice.CREATOR.createFromParcel(p);

        BleDevice device = new BleDevice(name, type, dev, false, notified, autoConnect, autoRecord, last_lng, last_lat);
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
