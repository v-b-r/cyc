package www.coral.innovations.cyc.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import www.coral.innovations.cyc.Model.DataModel;

/**
 * Created by srinu on 5/13/2018.
 */

public class DBManager {

    private DBHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String name, String watts, String quantity, String hours, String days, String total_units) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.ITEM_NAME, name);
        contentValue.put(DBHelper.ITEM_WATTS, watts);
        contentValue.put(DBHelper.ITEM_QTY, quantity);
        contentValue.put(DBHelper.ITEM_USAGE_HRS, hours);
        contentValue.put(DBHelper.ITEM_USAGE_DAYS, days);
        contentValue.put(DBHelper.ITEM_TOTAL_UNITS, total_units);
        database.insert(DBHelper.TABLE_NAME, null, contentValue);
    }

    public ArrayList<DataModel> fetch() {
        ArrayList<DataModel> list = new ArrayList<>();
        String[] columns = new String[] { DBHelper._ID, DBHelper.ITEM_NAME, DBHelper.ITEM_WATTS, DBHelper.ITEM_QTY, DBHelper.ITEM_USAGE_HRS,DBHelper.ITEM_USAGE_DAYS,DBHelper.ITEM_TOTAL_UNITS };
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext())
                try
                {
                    DataModel model = new DataModel();
                    model.setItem_name(cursor.getString(1));
                    model.setItem_power(cursor.getString(2));
                    model.setItem_qty(cursor.getString(3));
                    model.setUsage_hrs(cursor.getString(4));
                    model.setUsage_days(cursor.getString(5));
                    model.setTotal_units(cursor.getString(6));

                    list.add(model);
                }
                catch (Exception e) {
                    Log.e("MY_DEBUG_TAG", "Error " + e.toString());
                }

            }

        return list;
    }

    public int update(long _id, String name, String watts, String quantity, String hours, String days, String total_units) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.ITEM_NAME, name);
        contentValue.put(DBHelper.ITEM_WATTS, watts);
        contentValue.put(DBHelper.ITEM_QTY, quantity);
        contentValue.put(DBHelper.ITEM_USAGE_HRS, hours);
        contentValue.put(DBHelper.ITEM_USAGE_DAYS, days);
        contentValue.put(DBHelper.ITEM_TOTAL_UNITS, total_units);
        int i = database.update(DBHelper.TABLE_NAME, contentValue, DBHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DBHelper.TABLE_NAME, DBHelper._ID + "=" + _id, null);
    }

}
