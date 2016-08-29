package com.daycle.daycleapp.models;

/**
 * Created by neoam on 2016-08-25.
 */

public class SettingsModel {

    public enum AttendanceMode {
        ALL(0), ONLY_MONTH(1), NO_DISPLAY(2);

        private int value;
        private AttendanceMode(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }

    public AttendanceMode attendance_mode = AttendanceMode.ALL;
    public String language = "en";

    public SettingsModel(AttendanceMode attendance_mode, String language){
        this.attendance_mode = attendance_mode;
        this.language = language;
    }
}
