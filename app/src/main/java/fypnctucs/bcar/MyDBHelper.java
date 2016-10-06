package fypnctucs.bcar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import fypnctucs.bcar.device.BleDeviceDAO;
import fypnctucs.bcar.history.History;
import fypnctucs.bcar.history.HistoryDAO;

/**
 * Created by kamfu.wong on 3/10/2016.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BCAR";
    public static final int VERSION = 1;
    private static SQLiteDatabase database;

    public MyDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BleDeviceDAO.CREATE_TABLE);
        db.execSQL(HistoryDAO.CREATE_TABLE);
        HistoryDAO.insert(db, new History("68:4C:BD:E4:39:0C", "2016-10-04 00:54", 24.789261, 121.001489));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BleDeviceDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HistoryDAO.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BleDeviceDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HistoryDAO.TABLE_NAME);
        onCreate(db);
    }
}
