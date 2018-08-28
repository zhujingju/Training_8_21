package com.grasp.training.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhujingju on 2017/8/28.
 */

public class AddSQLiteHelper extends SQLiteOpenHelper {
   //uid 设备 id
    public String CREATE_NEW = "create table AddEquipment ("
            + "id integer primary key autoincrement, "
            + "uid text) "
            ;

    public AddSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                           int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEW);
//        this.db=db;
    }
//    SQLiteDatabase db;
//    public void execSQL(String tabName){
//        String CREATE_NEWS = "create table La"+tabName+" ("
//                + "id integer primary key autoincrement, "
//                + "title text, "
//                + "index text, "
//                + "path text"
//                ;
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
