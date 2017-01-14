package fypnctucs.bcar.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fypnctucs.bcar.MyDBHelper;

/**
 * Created by kamfu.wong on 4/10/2016.
 */

public class HistoryDAO {
    public static final String TABLE_NAME = "HISTORY";

    public static final String KEY_ID = "ID";

    public static final String BTADDRESS_COLUMN = "BTADDRESS";
    public static final String DATE_COLUMN = "DATE";
    public static final String LNG_COLUMN = "LNG";
    public static final String LAT_COLUMN = "LAT";
    public static final String ADDRESS_COLUMN = "ADDRESS";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BTADDRESS_COLUMN + " TEXT, " +
                    DATE_COLUMN + " TEXT, " +
                    LAT_COLUMN + " REAL, " +
                    LNG_COLUMN + " REAL, " +
                    ADDRESS_COLUMN + " TEXT)";

    private SQLiteDatabase db;

    public HistoryDAO(Context context) {
        this.db = MyDBHelper.getDatabase(context);
    }

    public long insert(History history) {
        ContentValues cv = new ContentValues();

        cv.put(BTADDRESS_COLUMN, history.getBtaddress());
        cv.put(DATE_COLUMN, history.getDate());
        cv.put(LAT_COLUMN, history.getLat());
        cv.put(LNG_COLUMN, history.getLng());
        cv.put(ADDRESS_COLUMN, history.getAddress());

        long id = db.insert(TABLE_NAME, null, cv);

        return id;
    }

    final static public long insert(SQLiteDatabase db, History history) {
        ContentValues cv = new ContentValues();

        cv.put(BTADDRESS_COLUMN, history.getBtaddress());
        cv.put(DATE_COLUMN, history.getDate());
        cv.put(LAT_COLUMN, history.getLat());
        cv.put(LNG_COLUMN, history.getLng());
        cv.put(ADDRESS_COLUMN, history.getAddress());

        long id = db.insert(TABLE_NAME, null, cv);

        return id;
    }

    public boolean update(History history) {
        ContentValues cv = new ContentValues();

        cv.put(BTADDRESS_COLUMN, history.getBtaddress());
        cv.put(DATE_COLUMN, history.getDate());
        cv.put(LAT_COLUMN, history.getLat());
        cv.put(LNG_COLUMN, history.getLng());
        cv.put(ADDRESS_COLUMN, history.getAddress());

        String where = KEY_ID + "=" + history.getId();

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public boolean delete(long id) {
        String where = KEY_ID + "=" + id;
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    public List<History> getAll() {
        List<History> result = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext())
            result.add(getRecord(cursor));

        cursor.close();
        return result;
    }

    public List<History> getByBtaddress(String btaddress) {
        List<History> result = new ArrayList<>();
        String where = BTADDRESS_COLUMN + "=" + btaddress;
        String orderBy = KEY_ID + " DESC";

        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, orderBy, null);

        while (cursor.moveToNext())
            result.add(getRecord(cursor));

        cursor.close();
        return result;
    }

    public History get(long id) {
        History history = null;
        String where = KEY_ID + "=" + id;

        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            history = getRecord(result);
        }

        result.close();
        return history;
    }

    public History getRecord(Cursor cursor) {

        long id = cursor.getLong(0);
        String btaddress = cursor.getString(1);
        String date = cursor.getString(2);
        double lat = cursor.getDouble(3);
        double lng = cursor.getDouble(4);
        String address = cursor.getString(5);

        History history = new History(btaddress, date, lat, lng, address);
        history.setId(id);

        return history;
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
