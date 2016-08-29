package com.daycle.daycleapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;

import com.daycle.daycleapp.models.AttendanceDayModel;
import com.daycle.daycleapp.models.AttendanceModel;
import com.daycle.daycleapp.models.UserModel;
import com.daycle.daycleapp.utils.L;

// SQLite 헬퍼
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3; // DB 스키마 버전
    public static final String DATABASE_NAME = "daycle.db"; // DB 이름
    public static String dbFilePath;

    SQLiteDatabase db;

    // 생성자
    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        String packageName = context.getPackageName(); // 현재 패키지명 알아오기
        String filePath = "/data/data/" + packageName + "/databases/" + DBHelper.DATABASE_NAME; // 데이터베이스가 설치되어 있는 경로
        dbFilePath = filePath;

        // DB 인스턴스 초기화
        db = getWritableDatabase();
    }

    // DB가 초기화 됐나 체크하기
    public boolean isFirst(Context ctx){

        // 디비 파일 있나 체크
        File file = new File(dbFilePath);
        if (file.exists()) {
            return false;
        }

        return true;
    }

    // DB가 최초 생성될때 호출
    // 테이블을 만든다던가 값을 미리 넣는다던가 디비 초기 세팅
    @Override
    public void onCreate(SQLiteDatabase db) {

        // 초기 유저 테이블 생성
        UserModel.createTable(db);

        // 출석 테이블 생성
        AttendanceModel.createTable(db);

        // 출석 날짜 생성
        AttendanceDayModel.createTable(db);
    }

    // 스키마가 업그레이드 되었을 경우에 호출
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        L.d("디비 업데이트됨");

        // 삭제하고...
        UserModel.deleteTable(db);
        AttendanceModel.deleteTable(db);
        AttendanceDayModel.deleteTable(db);

        // 다시 생성
        onCreate(db);
    }
}
