package com.azucca.budgettracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "BTracker.db";
    public static final String TABLE_NAME = "EXPENSES";
    public static final String ID = "ID";
    public static final String PRODUCTNAME = "PRODUCTNAME";
    public static final String METHOD = "METHOD";
    public static final String PRICE = "PRICE";
    public static final String DATE = "DATE";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDbSql = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " int primary key, " +
                PRODUCTNAME + " text," +
                METHOD + " text, " +
                PRICE + " text," +
                DATE + " text )";
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
    
    public String parseDate(String dateTime){
    	String date = dateTime.split(" ")[0];
    	String time = dateTime.split(" ")[1];
    	String year = date.split("/")[2];
    	String month = date.split("/")[1];
    	String day = date.split("/")[0];    	    	
    	return year + "-" + month + "-" + day + " " + time;
    }
    
    public String parseDate2(String dateTime){
    	String date = dateTime.split(" ")[0];
    	String time = dateTime.split(" ")[1];
    	String year = date.split("-")[0];
    	String month = date.split("-")[1];
    	String day = date.split("-")[2];    	    	
    	return day + "/" + month + "/" + year + " " + time;
    }

    public int add(Expense expense){
        long insert;
        int id = getNextId();
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(PRODUCTNAME, expense.getProductName());
        values.put(METHOD, expense.getMethod());
        values.put(PRICE, expense.getPrice());
        values.put(DATE, parseDate(expense.getDate()));
        SQLiteDatabase db = getWritableDatabase();
        insert = db.insert(TABLE_NAME, null, values);
        db.close();
        if(insert == -1)
            id = -1;
        return id;
    }

    public void delete(int expenseId){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ID + "=" + expenseId + ";");
    }
    
    public ArrayList<Expense> getMore(int lastId){
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME  
        		+ " WHERE " + ID + " < " + lastId 
        		+ " ORDER BY " + DATE + " DESC LIMIT 5;",null);
        c.moveToFirst();
        ArrayList<Expense> expenses = new ArrayList<Expense>();

        while(!c.isAfterLast()){
            expenses.add(new Expense(
                    c.getInt(c.getColumnIndex(ID)),
                    c.getString(c.getColumnIndex(PRODUCTNAME)),
                    c.getString(c.getColumnIndex(METHOD)),
                    c.getFloat(c.getColumnIndex(PRICE)),
                    parseDate2(c.getString(c.getColumnIndex(DATE)))
            ));
            System.out.println(c.getString(c.getColumnIndex(DATE)));
            c.moveToNext();
        }

        db.close();
        return expenses;
    }

    public Expense getExpense(int id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = ?;",new String[]{"" + id});
        c.moveToFirst();
        Expense e = null;

        while(!c.isAfterLast()){
            e = new Expense(
                    c.getInt(c.getColumnIndex(ID)),
                    c.getString(c.getColumnIndex(PRODUCTNAME)),
                    c.getString(c.getColumnIndex(METHOD)),
                    c.getFloat(c.getColumnIndex(PRICE)),
                    parseDate2(c.getString(c.getColumnIndex(DATE)))
            );
            c.moveToNext();
        }

        db.close();
        return e;
    }

    public void edit(Expense e){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME +
                " SET " + PRODUCTNAME + " = ?, " +
                METHOD + " =?, " +
                PRICE + " =?, " +
                DATE + " =? " +
                " WHERE ID =? ;",new Object[]{
                    "" + e.getProductName(),
                    "" + e.getMethod(),
                    "" + e.getPrice(),
                    "" + e.getDate(),
                    e.getId()});
        db.close();
    }
    
    public ArrayList<Expense> search(String dateFrom, String dateTo){
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME  
        		+ " WHERE " + DATE + " >= '" + dateFrom 
        		+ "' AND " + DATE + " <= '" + dateTo + "' ORDER BY " + DATE + ";",null);
        c.moveToFirst();
        ArrayList<Expense> expenses = new ArrayList<Expense>();
        System.out.println("Searching: " + dateFrom + " " + dateTo);
        while(!c.isAfterLast()){
            expenses.add(new Expense(
                    c.getInt(c.getColumnIndex(ID)),
                    c.getString(c.getColumnIndex(PRODUCTNAME)),
                    c.getString(c.getColumnIndex(METHOD)),
                    c.getFloat(c.getColumnIndex(PRICE)),
                    parseDate2(c.getString(c.getColumnIndex(DATE)))
            ));
            c.moveToNext();
        }

        db.close();
        return expenses;
    }

}
