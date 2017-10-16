package com.example.haibeey.crypyocompare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by haibeey on 10/15/2017.
 */
//helper class for storing data in sqlite

public class DBhelper extends SQLiteOpenHelper {
    final static String dbName = "mydb";
    Context con;
    String[] currency;
    String[] worldCurrency;
    int noOfRows=0;

    public DBhelper(Context context) {
        //constructor
        super(context, dbName, null, 1);
        con = context;
        currency = con.getResources().getStringArray(R.array.cryptocurrency);
        worldCurrency = con.getResources().getStringArray(R.array.worldCurrency);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //create a table
        db.execSQL("create table data (id integer primary key, cryptocurrency text,rate text,currency text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void  initDb(){
        //since we know our data before hand,I updated the new database with dummy rate value
        //so we just have to update it each time
        for (int i = 0; i < currency.length; i++) {
            for (int j = 0; j < worldCurrency.length; j++) {
                insertRecord(currency[i], "", worldCurrency[j]);
            }
        }
    }
    public void insertRecord(String cryptocurrency, String rate, String currency) {
        //method to insert record
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("cryptocurrency", cryptocurrency);
        contentValues.put("rate", rate);
        contentValues.put("currency", currency);

        db.insert("data", null, contentValues);
    }

    public ArrayList<String[]> getData() {
        //select all data from db
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from data", null);

        ArrayList<String[]> arrayList = new ArrayList<>();

        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            if(!cur.getString(2).equals("")){
                arrayList.add(new String[] {cur.getString(1),cur.getString(2),cur.getString(3)});
            }
            cur.moveToNext();
        }
        cur.close();
        noOfRows=arrayList.size();
        return arrayList;
    }

    public void update(String cryptocurrency, String rate, String currency) {
        //update the database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("cryptocurrency", cryptocurrency);
        contentValues.put("rate", rate);
        contentValues.put("currency", currency);

        db.update("data", contentValues, "id =?", new String[] {Integer.toString(getId(cryptocurrency,currency))});
    }


    private int getId(String cryptoCurrency, String Currency) {
        if (cryptoCurrency == "BTC") {
            for (int i = 0; i < worldCurrency.length; i++) {
                if (worldCurrency[i].equals(Currency))
                    return i + 1;
            }
        } else {
            for (int i = 0; i < worldCurrency.length; i++) {
                if (worldCurrency[i].equals(Currency))
                    return 2 * (i + 1);
            }
        }
        return 1;
    }
}