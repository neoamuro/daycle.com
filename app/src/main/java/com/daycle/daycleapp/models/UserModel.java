package com.daycle.daycleapp.models;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by neoam on 2016-08-20.
 */
public class UserModel {

    public int id;
    public String device_uid;

    public UserModel(int id, String device_uid){
        this.id = id;
        this.device_uid = device_uid;
    }

    // 유저 테이블
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "_id"; // 유저 유니크 아이디
    public static final String COLUMN_DEVICE_UID = "device_uid"; // 디바이스 유니크 아이디

    // 출석 테이블 생성
    public static void createTable(SQLiteDatabase db){
        String CREATE_USER_TABLE = "CREATE TABLE " +
                TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DEVICE_UID + " TEXT" +
                ")";
        db.execSQL(CREATE_USER_TABLE);
    }

    public static void deleteTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
