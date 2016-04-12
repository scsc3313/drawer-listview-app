package com.example.hsh.healthrecordapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by HSH on 16. 3. 29..
 */
public class DbAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_SET = "_set";
    public static final String KEY_COUNT = "count";
    private static final String TAG = "DbAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    /**
     *
     * Database creation sql statement
     */
    private static final String DATABASE_NAME = "data.db";
    private static final String DATABASE_TABLE = "health";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE + " ("
            + KEY_ROWID + " integer primary key, "
            + KEY_NAME  + " text not null, "
            + KEY_WEIGHT  + " text not null, "
            + KEY_SET  + " integer not null, "
            + KEY_COUNT+ " text not null);";


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        Log.e(TAG, mDbHelper.toString());
        mDb = mDbHelper.getWritableDatabase();
        Log.e(TAG, mDb.toString());
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long create(int id, String name, String weight) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, id);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_WEIGHT, weight);
        initialValues.put(KEY_SET, 0);
        initialValues.put(KEY_SET, 0);
        Log.i(TAG, "Create id : " + id + " name : " + name + " weight : " + weight);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean delete(long rowId) {
        Log.i(TAG, "Delete  id : " + rowId);
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAll() {
        return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_NAME, KEY_WEIGHT}, null, null, null, null, null);
    }

    public Cursor fetch(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_NAME, KEY_WEIGHT}, KEY_ROWID
                + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean update(long rowId, String name, String weight) {
        ContentValues args = new ContentValues();
//        String attr;
//        switch (type){
//            case 0 :
//                attr = KEY_NAME;
//                break;
//            case 1 :
//                attr = KEY_WEIGHT;
//                break;
//            default:
//                attr = "";
//                try {
//                    throw new Exception("type is not null");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//        }
        ++rowId;
        args.put(KEY_NAME, name);
        args.put(KEY_WEIGHT, weight);
        Log.i(TAG, "Update rowId : " + rowId +" KEY_NAME : " + name + " KEY_WEIGHT : " +weight);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + " = " + rowId, null) > 0;
    }

    public String printData() {
        String str = "";
        Cursor cursor = mDb.rawQuery("select * from health", null);
        while(cursor.moveToNext()) {
            str += "id : " + cursor.getInt(0)
                    + "name : "
                    + cursor.getString(1)
                    + ", weight : "
                    + cursor.getString(2)
                    + "\n";
        }
        return str;
    }


}
