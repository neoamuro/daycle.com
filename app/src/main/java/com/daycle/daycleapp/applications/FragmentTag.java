package com.daycle.daycleapp.applications;

/**
 * Created by neoam on 2016-08-24.
 */
public enum FragmentTag {
    INTRO(1), ATTENDANCE(2), DONE(3), CALENDAR(4), LANGUAGE(5), ATTENDANCE_SETTINGS(6), ABOUT(7), TUTORIAL(8), TERMS(9), BACKUP(10);

    private int value;
    private FragmentTag(int value){
        this.value = value;
    }

    @Override
    public String toString() {
        switch (value){
            case 1:
                return "INTRO";
            case 2:
                return "ATTENDANCE";
            case 3:
                return "DONE";
            case 4:
                return "CALENDAR";

            default:
                return "???";
        }
    }
}
