package com.example.discountcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "FeedReader.db";
    public static final String TABLE = "discount";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE + " (id integer primary key autoincrement, price_after_disc text, saving text, original_price text, priceTax text, tax double, discount1 double, discount2 double)");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean insertStuff (String priceAfter, String saving, String original_price, String priceTax, double tax, double discount1, double discount2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("price_after_disc", priceAfter);
        contentValues.put("saving", saving);
        contentValues.put("original_price", original_price);
        contentValues.put("priceTax", priceTax);
        contentValues.put("tax", tax);
        contentValues.put("discount1", discount1);
        contentValues.put("discount2", discount2);
       long result = db.insert(TABLE, null, contentValues);
        if(result == -1) return false;
        else return true;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + TABLE, null );
        return res;
    }

    public Integer deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
      return db.delete(TABLE, "id is not null", null);
    }

}
