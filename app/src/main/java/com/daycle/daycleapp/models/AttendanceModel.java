package com.daycle.daycleapp.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.utils.CalendarUtil;
import com.daycle.daycleapp.utils.L;

/**
 * Created by neoam on 2016-08-20.
 */

public class AttendanceModel implements Parcelable {

    public enum SelectMode {
        ALL, DOING, DONE
    }

    public int id;
    public String title;
    public int cnt;
    public int order;
    public boolean done;
    public String day;
    public String done_day;

    public AttendanceModel(){

    }

    public AttendanceModel(int id, String title, int cnt, int order, boolean done, String done_day){
        this.id = id;
        this.title = title;
        this.cnt = cnt;
        this.order = order;
        this.done = done;
        this.done_day = done_day;
    }

    // 출석 아이템 테이블
    public static final String TABLE_NAME = "attendances";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CNT = "cnt";
    public static final String COLUMN_ORDER = "ord";
    public static final String COLUMN_DONE = "done";
    public static final String COLUMN_DONE_DAY = "done_day";

    // 테이블 생성
    public static void createTable(SQLiteDatabase db){
        String CREATE_ATTENDANCE_TABLE = "CREATE TABLE " +
                TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_CNT + " INTEGER, " +
                COLUMN_ORDER + " INTEGER, " +
                COLUMN_DONE + " INTEGER," +
                COLUMN_DONE_DAY + " TEXT " +
                ")";
        db.execSQL(CREATE_ATTENDANCE_TABLE);

        L.d("CREATE_ATTENDANCE_TABLE");
    }

    // 데이터 삽입
    public static long insert(AttendanceModel model) {

        SQLiteDatabase db = App.getDataBase().getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, model.title);
        values.put(COLUMN_CNT, model.cnt);
        values.put(COLUMN_ORDER, model.order);
        values.put(COLUMN_DONE, model.done);
        values.put(COLUMN_DONE_DAY, model.done_day);

        try{
            long insertId = db.insert(TABLE_NAME, null, values);

            // 순번 업데이트
            updateOrder();

            return insertId; // 생성된 유니크 아이디 리턴

        }catch (SQLiteException ex){
            L.d("insert error: " + ex.getMessage());
        }

        return 0;
    }

    // 새로운 데이터 삽입
    public static void restore(ArrayList<AttendanceModel> items) {

        SQLiteDatabase db = App.getDataBase().getWritableDatabase();

        // 자동증가 시퀀스 초기화
//        String query = "UPDATE SQLITE_SEQUENCE SET seq = 1 WHERE name = " + TABLE_NAME;
//        db.execSQL(query);

        // 테이블 삭제
        deleteTable(db);

        // 테이블 새로 생성
        createTable(db);

        // 데이터 새로 삽입
        for (AttendanceModel item: items) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, item.title);
            values.put(COLUMN_CNT, item.cnt);
            values.put(COLUMN_ORDER, item.order);
            values.put(COLUMN_DONE, item.done);
            values.put(COLUMN_DONE_DAY, item.done_day);

            try{
                db.insert(TABLE_NAME, null, values);

            }catch (SQLiteException ex){
                L.d("insert error: " + ex.getMessage());
            }
        }
    }

    // 전체 가져오기
    public static ArrayList<AttendanceModel> selectAll(SelectMode selectMode, boolean onlyThisMonth) {

        SQLiteDatabase db = App.getDataBase().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME;

        // 모드에 따라서 전체를 불러올지 말지 체크
        if(selectMode != SelectMode.ALL){
            query += " WHERE " + COLUMN_DONE + " = \"" + (selectMode == SelectMode.DONE ? 1 : 0) + "\"";
        }
        query += " ORDER BY " + COLUMN_ORDER + " ASC";

        Cursor c = db.rawQuery(query, null);

        ArrayList<AttendanceModel> items = new ArrayList<AttendanceModel>();
        while (c.moveToNext()){
            AttendanceModel item = new AttendanceModel();
            item.id = c.getInt(c.getColumnIndex(COLUMN_ID)); // 유니크 아이디
            item.title = c.getString(c.getColumnIndex(COLUMN_TITLE)); // 타이틀

            // 옵션에 따라 카운트 집계 달라짐
            // 이번달만
            if(onlyThisMonth){
                item.cnt = AttendanceDayModel.selectCountByMonth(item.id, CalendarUtil.getDayString(Calendar.getInstance()));
            }else{
                item.cnt = c.getInt(c.getColumnIndex(COLUMN_CNT)); // 카운트
            }
            item.order = c.getInt(c.getColumnIndex(COLUMN_ORDER)); // 순서
            item.done = (c.getInt(c.getColumnIndex(COLUMN_DONE)) != 0); // 카운트
            item.done_day = c.getString(c.getColumnIndex(COLUMN_DONE_DAY)); // 끝낸 날
            items.add(item);
        }

        c.close(); // 커서 닫기
        return items;
    }

    // 한개 가져오기
    public static AttendanceModel selectOne(int id) {

        SQLiteDatabase db = App.getDataBase().getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_ID + " = \"" + id + "\"";
        Cursor c = db.rawQuery(query, null);

        AttendanceModel item = null;
        if(c.moveToFirst()){
            item = new AttendanceModel();
            item.id = c.getInt(c.getColumnIndex(COLUMN_ID)); // 유니크 아이디
            item.title = c.getString(c.getColumnIndex(COLUMN_TITLE)); // 타이틀
            item.cnt = c.getInt(c.getColumnIndex(COLUMN_CNT)); // 카운트
            item.order = c.getInt(c.getColumnIndex(COLUMN_ORDER)); // 순서
            item.done = (c.getInt(c.getColumnIndex(COLUMN_DONE)) != 0); // 카운트
            item.done_day = c.getString(c.getColumnIndex(COLUMN_DONE_DAY)); // 끝낸 날
        }

        c.close(); // 커서 닫기
        return item;
    }

    // 업데이트
    public static boolean update(AttendanceModel model) {

        SQLiteDatabase db = App.getDataBase().getWritableDatabase();

        boolean result = false;

        // 셀렉트 쿼리
        String q = "SELECT * FROM " + TABLE_NAME;

        // 쿼리 실행
        Cursor c = db.rawQuery(q, null);
        if(c.moveToFirst()){
            ContentValues val = new ContentValues();
            val.put(COLUMN_TITLE, model.title);
            val.put(COLUMN_CNT, model.cnt);
            val.put(COLUMN_ORDER, model.order);
            val.put(COLUMN_DONE, model.done ? 1 : 0);
            val.put(COLUMN_DONE_DAY, model.done_day);
            int updateResult = db.update(TABLE_NAME, val, COLUMN_ID + "=?", new String[]{String.valueOf(model.id)});
            if(updateResult > 0)
                result = true;
        }

        c.close();
        return result;
    }

    // 순서 업데이트
    public static boolean updateOrder(ArrayList<AttendanceModel> items) {

        SQLiteDatabase db = App.getDataBase().getWritableDatabase();

        boolean result = true;

        // 순서를 뒤집는다
        //Collections.reverse(items);

        // 루프를 돌면서 순서를 업데이트한다.
        for (int i = 0; i < items.size(); i++) {
            ContentValues val = new ContentValues();
            val.put(COLUMN_ORDER, i + 1);
            int updateResult = db.update(TABLE_NAME, val,  COLUMN_ID + "=?", new String[]{String.valueOf(items.get(i).id)});
            if(updateResult <= 0){
                result = false;
                break;
            }
        }

        return result;
    }

    // 순서 업데이트
    public static void updateOrder() {

        SQLiteDatabase db = App.getDataBase().getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_DONE + " = 0 ORDER BY " + COLUMN_ORDER + " ASC";
        Cursor c = db.rawQuery(query, null);

        int order = 1;
        while (c.moveToNext()){
            int id = c.getInt(c.getColumnIndex(COLUMN_ID)); // 유니크 아이디
            ContentValues val = new ContentValues();
            val.put(COLUMN_ORDER, order);
            db.update(TABLE_NAME, val,  COLUMN_ID + "=?", new String[]{String.valueOf(id)});

            order++;
        }
    }

    // 삭제
    public static boolean delete(int id) {

        SQLiteDatabase db = App.getDataBase().getWritableDatabase();

        try{
            db.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
            return true;
        }catch (SQLiteException ex){
            L.d(ex.getMessage());
        }

        // 순번 업데이트
        updateOrder();

        return true;
    }

    // 테이블 삭제
    public static void deleteTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    ////////////////////////////////////// Parcel //////////////////////////////////////
    protected AttendanceModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        cnt = in.readInt();
        order = in.readInt();
        done = in.readByte() != 0x00;
        day = in.readString();
        done_day = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeInt(cnt);
        dest.writeInt(order);
        dest.writeByte((byte) (done ? 0x01 : 0x00));
        dest.writeString(day);
        dest.writeString(done_day);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AttendanceModel> CREATOR = new Parcelable.Creator<AttendanceModel>() {
        @Override
        public AttendanceModel createFromParcel(Parcel in) {
            return new AttendanceModel(in);
        }

        @Override
        public AttendanceModel[] newArray(int size) {
            return new AttendanceModel[size];
        }
    };

    ////////////////////////////////////// Parcel //////////////////////////////////////
}
