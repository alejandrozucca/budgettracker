package com.azucca.budgettracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DB_Budgets extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "BTracker_bds.db";
    public static final String TABLE_NAME = "BUDGETS";
    public static final String ID = "ID";
    public static final String DATEFROM = "DATEFROM";
    public static final String DATETO = "DATETO";
    public static final String AMOUNT = "AMOUNT";

    public DB_Budgets(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDbSql = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " int primary key, " +
                DATEFROM + " text, " +
                DATETO + " text, " +
                AMOUNT + " real)";
        db.execSQL(createDbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public int getNextId(){
        int id = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + ID + " FROM " + TABLE_NAME + " ORDER BY " + ID + " DESC LIMIT 1;", null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            id = c.getInt(c.getColumnIndex(ID));
            id++;
            c.moveToNext();
        }
        db.close();
        return id;
    }
    
    public void addBudget(Budget b){
        ContentValues values = new ContentValues();
        values.put(ID, b.getId());
        values.put(DATEFROM, b.getDateFrom());
        values.put(DATETO, b.getDateTo());
        values.put(AMOUNT, b.getAmount());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteBudget(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ID + "=" + id + ";");
    }
    
    public void deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + ";");
    }
    
    public ArrayList<Budget> getAllBudgets(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME  
        		+  " ;",null);
        c.moveToFirst();
        ArrayList<Budget> budgets = new ArrayList<Budget>();
        while(!c.isAfterLast()){
        	budgets.add(new Budget(
        			c.getInt(c.getColumnIndex(ID)),
        			c.getString(c.getColumnIndex(DATEFROM)),
        			c.getString(c.getColumnIndex(DATETO)),
        			c.getFloat(c.getColumnIndex(AMOUNT))
        			));
            c.moveToNext();
        }
        db.close();
        return budgets;
    }
    
    public Budget getBudget(int id){
    	SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = ?;",new String[]{"" + id});
        c.moveToFirst();
        Budget b = null;
        while(!c.isAfterLast()){
            b = new Budget(
            		c.getInt(c.getColumnIndex(ID)),
        			c.getString(c.getColumnIndex(DATEFROM)),
        			c.getString(c.getColumnIndex(DATETO)),
        			c.getFloat(c.getColumnIndex(AMOUNT))
            );
            c.moveToNext();
        }
        db.close();
        return b;
    }
    
    public void editBudget(Budget budget){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME +
                " SET " + DATEFROM + " =?, " + 
        		DATETO + " =?, " +
                AMOUNT + " =? " +
        		"WHERE ID =? ;",new Object[]{
                    "" + budget.getDateFrom(), budget.getDateTo(), budget.getAmount(), budget.getId()});
        db.close();
    }
    
}
