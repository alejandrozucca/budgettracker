package com.azucca.budgettracker;

import com.azucca.budgettracker.db.DBHelper;

import android.app.Application;

public class BudgetTracker extends Application {

    private DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DBHelper(this);
    }

    public DBHelper getDbHelper() {
        return this.dbHelper;
    }
}
