package at.co.netconsulting.wakeonlan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "wifi.db";
    public static final String WIFI_TABLE_NAME = "wifi";
    public static final String WIFI_COLUMN_ID = "id";
    //public static final String WIFI_COLUMN_IMAGEVIEW = "imageView";
    public static final String WIFI_COLUMN_HOSTNAME = "hostname";
    public static final String WIFI_COLUMN_NIC_MAC = "nic_mac";
    public static final String WIFI_COLUMN_IP_ADDRESS = "ip_address";
    public static final String WIFI_COLUMN_GROUP_NAME = "group_name";
    public static final String WIFI_COLUMN_COMMENT = "comment";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table wifi " +
                        "(id integer primary key, hostname String, nic_mac String, ip_address String, group_name String, comment String)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS wifi");
        onCreate(db);
    }

    public boolean insertWifi (String hostname, String nic_mac, String ip_address, String group_name, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("hostname", hostname);
        contentValues.put("nic_mac", nic_mac);
        contentValues.put("ip_address", ip_address);
        contentValues.put("group_name", group_name);
        contentValues.put("comment", comment);
        db.insert(WIFI_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from wifi where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, WIFI_TABLE_NAME);
        return numRows;
    }

    public boolean updateLocation (Integer id, String hostname, String nic_mac, String ip_address, String group_name, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("hostname", hostname);
        contentValues.put("nic_mac", nic_mac);
        contentValues.put("ip_address", ip_address);
        contentValues.put("group_name", group_name);
        contentValues.put("comment", comment);
        db.update(WIFI_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteLocation (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(WIFI_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public List getAllEntries() {
        List allEntries = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT hostname, nic_mac, ip_address, group_name, comment FROM wifi", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            allEntries.add(res.getString(res.getColumnIndex(WIFI_TABLE_NAME)));
            res.moveToNext();
        }
        return allEntries;
    }

    public ArrayList<String> getHostnameByName(String hostname) {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM wifi WHERE hostname = " + hostname, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(WIFI_TABLE_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllHostnames() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT hostname FROM wifi", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(WIFI_TABLE_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public long countAllEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, WIFI_TABLE_NAME);
        db.close();
        return count;
    }

    public void deleteAllEntries () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ WIFI_TABLE_NAME);
    }
}