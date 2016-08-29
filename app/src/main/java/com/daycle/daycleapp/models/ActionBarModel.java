package com.daycle.daycleapp.models;

/**
 * Created by neoam on 2016-08-29.
 */
public class ActionBarModel {
    public String title;
    public boolean showAddButton;
    public boolean showHomeButton = true;
    public boolean customHomeButton = true;
    public int backgroundColorResId;

    public ActionBarModel(String title,
            boolean showAddButton,
            boolean showHomeButton,
            int colorResId,
            boolean customHomeButton){
        this.title = title;
        this.showAddButton = showAddButton;
        this.showHomeButton = showHomeButton;
        this.backgroundColorResId = colorResId;
        this.customHomeButton = customHomeButton;
    }
    public ActionBarModel(){

    }

    public ActionBarModel(String title){
        this.title = title;
    }
}
