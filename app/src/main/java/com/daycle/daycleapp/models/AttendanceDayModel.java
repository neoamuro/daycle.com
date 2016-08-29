package com.daycle.daycleapp.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.utils.L;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by neoam on 2016-08-20.
 */
public class AttendanceDayModel {

    public enum SelectMode {
        ALL, DOING, DONE
    }

    public int id;
    public int attendance_id;
    public String day;
    public boolean is_future;

    public AttendanceDayModel(){}

    public AttendanceDayModel(int attendance_id, String day, boolean is_future){
        this.attendance_id = attendance_id;
        this.day = day;
        this.is_future = is_future;
    }

    // 출석 날짜 테이블
    public static final String TABLE_NAME = "attendance_days";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ATTENDANCE_ID = "attendance_id";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_IS_FUTURE = "is_future";

    // 출석 날짜 테이블 생성
    public static void createTable(SQLiteDatabase db){
        String CREATE_ATTENDANCE_DAY_TABLE = "CREATE TABLE " +
                TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ATTENDANCE_ID + " INTEGER," +
                COLUMN_DAY + " TEXT, " +
                COLUMN_IS_FUTURE + " INTEGER" +
                ")";
        db.execSQL(CREATE_ATTENDANCE_DAY_TABLE);

        L.d("CREATE_ATTENDANCE_DAY_TABLE");
    }

    // 데이터 삽입
    public static long insert(AttendanceDayModel model) {

        SQLiteDatabase db = App.getDataBase().getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ATTENDANCE_ID, model.attendance_id);
        values.put(COLUMN_DAY, model.day);
        values.put(COLUMN_IS_FUTURE, model.is_future ? 1 : 0);

        try{
            long insertId = db.insert(TABLE_NAME, null, values);
            return insertId;

        }catch (SQLiteException ex){
            L.d("insert error: " + ex.getMessage());
        }

        return 0;
    }

    // 데이터 새로 복구
    public static void restore(ArrayList<AttendanceDayModel> items) {

        SQLiteDatabase db = App.getDataBase().getWritableDatabase();

        // 자동증가 시퀀스 초기화
//        String query = "UPDATE SQLITE_SEQUENCE SET seq = 1 WHERE name = " + TABLE_NAME;
//        db.execSQL(query);

        // 테이블 삭제
        deleteTable(db);

        // 테이블 새로 생성
        createTable(db);

        // 데이터 새로 삽입
        for (AttendanceDayModel item: items) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ATTENDANCE_ID, item.attendance_id);
            values.put(COLUMN_DAY, item.day);
            values.put(COLUMN_IS_FUTURE, item.is_future ? 1 : 0);

            try{
                long insertId = db.insert(TABLE_NAME, null, values);

            }catch (SQLiteException ex){
                L.d("insert error: " + ex.getMessage());
            }
        }
    }

    // 전체 가져오기
    public static ArrayList<AttendanceDayModel> selectAll(int attendanceId, SelectMode selectMode) {

        SQLiteDatabase db = App.getDataBase().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME;

        if(selectMode != SelectMode.ALL){
            query += " WHERE " + COLUMN_ATTENDANCE_ID + " = \"" + attendanceId + "\"";
        }

        // 출석이 끝난 경우는 예정 출석은 안가져옴
        if(selectMode == SelectMode.DONE){
            query += " AND " + COLUMN_IS_FUTURE + "= 0";
        }

        query += " ORDER BY " + COLUMN_ID + " DESC";

        Cursor c = db.rawQuery(query, null);

        ArrayList<AttendanceDayModel> items = new ArrayList<AttendanceDayModel>();
        while (c.moveToNext()){
            AttendanceDayModel item = new AttendanceDayModel();
            item.id = c.getInt(c.getColumnIndex(COLUMN_ID)); // 유니크 아이디
            item.attendance_id = c.getInt(c.getColumnIndex(COLUMN_ATTENDANCE_ID));
            item.day = c.getString(c.getColumnIndex(COLUMN_DAY));
            item.is_future = c.getInt(c.getColumnIndex(COLUMN_IS_FUTURE)) != 0;
            items.add(item);
        }

        c.close(); // 커서 닫기
        return items;
    }

    // 한개 가져오기
    public static AttendanceDayModel selectOne(int attendanceId, String day) {

        SQLiteDatabase db = App.getDataBase().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_ATTENDANCE_ID + "=" + attendanceId + " AND " + COLUMN_DAY + "= \"" + day + "\"";
        Cursor c = db.rawQuery(query, null);

        AttendanceDayModel item = null;
        if(c.moveToFirst()){
            item = new AttendanceDayModel();
            item.id = c.getInt(c.getColumnIndex(COLUMN_ID)); // 유니크 아이디
            item.attendance_id = c.getInt(c.getColumnIndex(COLUMN_ATTENDANCE_ID));
            item.day = c.getString(c.getColumnIndex(COLUMN_DAY));
            item.is_future = c.getInt(c.getColumnIndex(COLUMN_IS_FUTURE)) != 0;
        }

        c.close(); // 커서 닫기
        return item;
    }

    // 특정월의 출석 카운트 가져오기
    public static int selectCountByMonth(int attendanceId, String day) {

        SQLiteDatabase db = App.getDataBase().getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " +
                COLUMN_ATTENDANCE_ID + " = \"" + attendanceId + "\" AND SUBSTR(" + COLUMN_DAY + ", 0, 8) = SUBSTR(\"" + day + "\", 0, 8) AND " +
                COLUMN_IS_FUTURE + "=0";

//        String query = "SELECT SUBSTR(" + COLUMN_DAY + ", 0, 8) FROM " + TABLE_NAME + " WHERE " +
//                COLUMN_ATTENDANCE_ID + " = \"" + attendanceId + "\"";



        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        String d = c.getString(0);
        L.d("c.getString(0): " + d);
        int cnt = c.getInt(0);
//        int cnt = 1;
        c.close(); // 커서 닫기

        return cnt;
    }

    // 삭제
    public static boolean delete(int id) {

        L.d("id:" + id);

        SQLiteDatabase db = App.getDataBase().getWritableDatabase();

        try{
            db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
            return true;
        }catch (SQLiteException ex){
            L.d(ex.getMessage());
        }

        return true;
    }

    // 오늘날짜보다 작은 미래의 출석 체크 삭제
    public static boolean deleteFutureDay() {
        SQLiteDatabase db = App.getDataBase().getWritableDatabase();

        // 오늘 날짜
        Calendar today = Calendar.getInstance();
        String day = today.get(Calendar.YEAR) + "" + today.get(Calendar.MONTH) + "" + today.get(Calendar.DAY_OF_MONTH);

        try{
            db.delete(TABLE_NAME, COLUMN_IS_FUTURE + "=1" + " AND CAST(replace(" + COLUMN_DAY + ",'-', '') AS DECIMAL) <= CAST(? AS DECIMAL)", new String[]{String.valueOf(day)});
            return true;
        }catch (SQLiteException ex){
            L.d(ex.getMessage());
        }

        return true;
    }

    // 테이블 삭제
    public static void deleteTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
