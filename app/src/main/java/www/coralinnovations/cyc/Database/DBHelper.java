package www.coralinnovations.cyc.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import www.coralinnovations.cyc.Model.DataModel;
import www.coralinnovations.cyc.Storage.Utility;

/**
 * Created by srinu on 5/13/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "COUNTRIES";
    public static final String APPLIANCES_TABLE_NAME = "APPLIANCES_TABLE_NAME";

    // Table columns
    public static final String _ID = "_id";
    public static final String ITEM_NAME = "ITEM_NAME";
    public static final String ITEM_WATTS = "ITEM_WATTS";
    public static final String ITEM_MAKER = "ITEM_MAKER";
    public static final String ITEM_QTY = "ITEM_QTY";
    public static final String ITEM_USAGE_HRS = "ITEM_USAGE_HRS";
    public static final String ITEM_USAGE_DAYS = "ITEM_USAGE_DAYS";
    public static final String ITEM_TOTAL_UNITS = "ITEM_TOTAL_UNITS";

    // Database Information
    static final String DB_NAME = "APPLIANCES.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM_NAME + " TEXT , " + ITEM_WATTS + " TEXT, " + ITEM_QTY + " TEXT, " + ITEM_USAGE_HRS + " TEXT, " + ITEM_USAGE_DAYS + " TEXT, " + ITEM_TOTAL_UNITS + " TEXT);";

    private static final String CREATE_APPLIANCES_TABLE = "create table " + APPLIANCES_TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM_NAME + " TEXT , " + ITEM_MAKER + " TEXT, " + ITEM_WATTS + " TEXT, " + ITEM_USAGE_HRS + " TEXT, " + ITEM_TOTAL_UNITS + " TEXT);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_APPLIANCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_APPLIANCES_TABLE);
        onCreate(db);
    }

    public void addItems(String name, String watts, String quantity, String hours, String days, String total_units){
        Log.i("All_Values", "-->"+name+"-"+ watts+"-"+quantity+"-"+hours+"-"+days+"-"+total_units);
       // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.ITEM_NAME, name);
        contentValue.put(DBHelper.ITEM_WATTS, watts);
        contentValue.put(DBHelper.ITEM_QTY, quantity);
        contentValue.put(DBHelper.ITEM_USAGE_HRS, hours);
        contentValue.put(DBHelper.ITEM_USAGE_DAYS, days);
        contentValue.put(DBHelper.ITEM_TOTAL_UNITS, total_units);
        db.insert(DBHelper.TABLE_NAME, null, contentValue);

        db.close();
    }

    public void insertAppliances(String name, String maker, String watts, String usage, String total_units){
        Log.i("All_Values", "-->"+name+"-"+ maker+"-"+watts+"-"+usage+"-"+total_units);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.ITEM_NAME, name);
        contentValue.put(DBHelper.ITEM_MAKER, maker);
        contentValue.put(DBHelper.ITEM_WATTS, watts);
        contentValue.put(DBHelper.ITEM_USAGE_HRS, usage);
        contentValue.put(DBHelper.ITEM_TOTAL_UNITS, total_units);
        db.insert(DBHelper.APPLIANCES_TABLE_NAME, null, contentValue);

        db.close();
    }
    public List<DataModel> getAllAppliances() {
        List<DataModel> list = new ArrayList<>();
        String query = "SELECT  * FROM " + APPLIANCES_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DataModel model = null;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                model = new DataModel();
                model.setItem_name(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_NAME)));
                model.setItem_maker(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_MAKER)));
                model.setWatts(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_WATTS)));
                model.setUsage_hrs(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_USAGE_HRS)));
                model.setTotal_units(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_TOTAL_UNITS)));

                list.add(model);

                cursor.moveToNext();
            }
        }
        for (DataModel mo : list){
            Log.i("ITEM_NAME","--->"+mo.getItem_name());
        }

        return list;
    }

    public List<DataModel> getAllRecords() {
        List<DataModel> list = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DataModel model = null;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                model = new DataModel();
                model.setItem_name(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_NAME)));
                model.setItem_power(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_WATTS)));
                model.setItem_qty(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_QTY)));
                model.setUsage_hrs(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_USAGE_HRS)));
                model.setUsage_days(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_USAGE_DAYS)));
                model.setTotal_units(cursor.getString(cursor.getColumnIndexOrThrow(ITEM_TOTAL_UNITS)));

                list.add(model);

                cursor.moveToNext();
            }
        }
        for (DataModel mo : list){
            Log.i("ITEM_NAME","--->"+mo.getItem_name());
        }

        return list;
    }



    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void deleteRecord(DataModel model) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_NAME, //table name
                ITEM_NAME+" = ?",  // selections
                new String[] { String.valueOf(model.getItem_name()) }); //selections args

        // 3. close
        db.close();

        Log.d("deleteBook", model.toString());

    }

    public void deleteAppliance(Context context, String name, String watts)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(APPLIANCES_TABLE_NAME, //table name
                ITEM_NAME+" = ?",  // selections
                new String[] { name });
        db.close();
        Utility.showToast(context, "Record Deleted Successfully...!");
    }

    public void updateRecord(String name, String watts, String quantity, String hours, String days, String total_units) {

        /*// 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
       *//* ContentValues values = new ContentValues();
        values.put("title", model.getTitle()); // get title
        values.put("author", book.getAuthor()); // get author

        // 3. updating row
        int i = db.update(TABLE_BOOKS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(book.getId()) }); //selection args

        // 4. close
        db.close();*//*

        //return i;
        return 0;*/

        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.ITEM_NAME, name);
        contentValue.put(DBHelper.ITEM_WATTS, watts);
        contentValue.put(DBHelper.ITEM_QTY, quantity);
        contentValue.put(DBHelper.ITEM_USAGE_HRS, hours);
        contentValue.put(DBHelper.ITEM_USAGE_DAYS, days);
        contentValue.put(DBHelper.ITEM_TOTAL_UNITS, total_units);
        db.update(DBHelper.TABLE_NAME, contentValue, DBHelper.ITEM_NAME+"=?"+name, null);

        db.close();

    }
}
