package com.example.chandranichatterjee.myapplicationloc.DBUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class SqliteHelper extends SQLiteOpenHelper {

    private String firebasetoken;
    private static String dbName = "LocDB";
    private String tableName = "TOKEN";
    private String tabToken = "create table TOKEN (userid varchar(30), token varchar(30), indate Date , primary key (userid,indate))";
    private String droptab = "Drop table if exists TOKEN";

    public SqliteHelper(Context context) {
        super(context, dbName, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(tabToken);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(droptab);
        onCreate(db);
    }

    public boolean insertToken(String username, String token, String indate){
        try{

            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("userid",username);
            contentValues.put("token",token);
            contentValues.put("indate",indate);
            sqLiteDatabase.insert(tableName,null,contentValues);
            return true;
        }catch (Exception e){
            Log.e("DB insertion", e.getMessage().toString());
            return false;
        }
    }
}
