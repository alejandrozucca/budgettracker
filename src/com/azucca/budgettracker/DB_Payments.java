package com.azucca.budgettracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DB_Payments extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "BTracker_pys.db";
    public static final String TABLE_NAME = "PAYMENTS";
    public static final String ID = "ID";
    public static final String METHOD = "METHODNAME";

    public DB_Payments(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDbSql = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " int primary key, " +
                METHOD + " text )";
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
    
    public int add(int id, String method){
        long insert;
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(METHOD, method);
        SQLiteDatabase db = getWritableDatabase();
        insert = db.insert(TABLE_NAME, null, values);
        db.close();
        if(insert == -1)
            id = -1;
        return id;
    }

    public void delete(int methodId){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ID + "=" + methodId + ";");
    }
    
    public void deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + ";");
    }
    
    public ArrayList<PaymentMethod> getAll(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME  
        		+  " ;",null);
        c.moveToFirst();
        ArrayList<PaymentMethod> methods = new ArrayList<PaymentMethod>();
        while(!c.isAfterLast()){
        	methods.add(new PaymentMethod(
        			c.getInt(c.getColumnIndex(ID)),
        			c.getString(c.getColumnIndex(METHOD))
        			));
            c.moveToNext();
        }
        db.close();
        return methods;
    }
    
    public PaymentMethod getMethod(int id){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = ?;",new String[]{"" + id});
        c.moveToFirst();
       PaymentMethod p = null;
        while(!c.isAfterLast()){
            p = new PaymentMethod(
                    c.getInt(c.getColumnIndex(ID)),
                    c.getString(c.getColumnIndex(METHOD))
            );
            c.moveToNext();
        }
        db.close();
        return p;
    }

    public void edit(int methodId, String methodName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME +
                " SET " + METHOD + " =? WHERE ID =? ;",new Object[]{
                    "" + methodName, methodId});
        db.close();
    }
    
}
