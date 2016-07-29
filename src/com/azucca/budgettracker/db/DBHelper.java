package com.azucca.budgettracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import com.azucca.budgettracker.entities.Budget;
import com.azucca.budgettracker.entities.Expense;
import com.azucca.budgettracker.entities.PaymentMethod;
import com.azucca.budgettracker.tables.Budgets;
import com.azucca.budgettracker.tables.Expenses;
import com.azucca.budgettracker.tables.Payments;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "BTracker.db";
    
    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {        
        db.execSQL(Expenses.createTable());
        db.execSQL(Payments.createTable());
        db.execSQL(Budgets.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public int getNextExpenseId(){
        int id = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + Expenses.ID +
        		" FROM " + Expenses.TABLE_NAME +
        		" ORDER BY " + Expenses.ID +
        		" DESC LIMIT 1;", null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            id = c.getInt(c.getColumnIndex(Expenses.ID));
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

    public int addExpense(Expense expense){
        long insert;
        int id = getNextExpenseId();
        ContentValues values = new ContentValues();
        values.put(Expenses.ID, id);
        values.put(Expenses.PRODUCTNAME, expense.getProductName());
        values.put(Expenses.METHOD, expense.getMethod());
        values.put(Expenses.PRICE, expense.getPrice());
        values.put(Expenses.DATE, parseDate(expense.getDate()));
        SQLiteDatabase db = getWritableDatabase();
        insert = db.insert(Expenses.TABLE_NAME, null, values);
        db.close();
        if(insert == -1)
            id = -1;
        return id;
    }

    public void deleteExpense(int expenseId){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + Expenses.TABLE_NAME + " WHERE " + Expenses.ID + "=" + expenseId + ";");
    }
    
    public ArrayList<Expense> getMoreExpenses(int lastId){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Expenses.TABLE_NAME  
        		+ " WHERE " + Expenses.ID + " < " + lastId 
        		+ " ORDER BY " + Expenses.DATE + " DESC LIMIT 5;",null);
        c.moveToFirst();
        ArrayList<Expense> expenses = new ArrayList<Expense>();
        while(!c.isAfterLast()){
            expenses.add(new Expense(
                    c.getInt(c.getColumnIndex(Expenses.ID)),
                    c.getString(c.getColumnIndex(Expenses.PRODUCTNAME)),
                    c.getString(c.getColumnIndex(Expenses.METHOD)),
                    c.getFloat(c.getColumnIndex(Expenses.PRICE)),
                    parseDate2(c.getString(c.getColumnIndex(Expenses.DATE)))
            ));
            System.out.println(c.getString(c.getColumnIndex(Expenses.DATE)));
            c.moveToNext();
        }
        db.close();
        return expenses;
    }

    public Expense getExpense(int id){
    	SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Expenses.TABLE_NAME +
        		" WHERE Expenses.ID = ?;",
        		new String[]{"" + id});
        c.moveToFirst();
        Expense e = null;
        while(!c.isAfterLast()){
            e = new Expense(
                    c.getInt(c.getColumnIndex(Expenses.ID)),
                    c.getString(c.getColumnIndex(Expenses.PRODUCTNAME)),
                    c.getString(c.getColumnIndex(Expenses.METHOD)),
                    c.getFloat(c.getColumnIndex(Expenses.PRICE)),
                    parseDate2(c.getString(c.getColumnIndex(Expenses.DATE)))
            );
            c.moveToNext();
        }
        db.close();
        return e;
    }

    public void editExpense(Expense e){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + Expenses.TABLE_NAME +
                " SET " + Expenses.PRODUCTNAME + " = ?, " +
                Expenses.METHOD + " =?, " +
                Expenses.PRICE + " =?, " +
                Expenses.DATE + " =? " +
                " WHERE Expenses.ID =? ;",new Object[]{
                    "" + e.getProductName(),
                    "" + e.getMethod(),
                    "" + e.getPrice(),
                    "" + e.getDate(),
                    e.getId()});
        db.close();
    }
    
    public ArrayList<Expense> searchExpenses(String dateFrom, String dateTo){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Expenses.TABLE_NAME  
        		+ " WHERE " + Expenses.DATE + " >= '" + dateFrom 
        		+ "' AND " + Expenses.DATE + " <= '" + dateTo +
        		"' ORDER BY " + Expenses.DATE + ";",null);
        c.moveToFirst();
        ArrayList<Expense> expenses = new ArrayList<Expense>();
        System.out.println("Searching: " + dateFrom + " " + dateTo);
        while(!c.isAfterLast()){
            expenses.add(new Expense(
                    c.getInt(c.getColumnIndex(Expenses.ID)),
                    c.getString(c.getColumnIndex(Expenses.PRODUCTNAME)),
                    c.getString(c.getColumnIndex(Expenses.METHOD)),
                    c.getFloat(c.getColumnIndex(Expenses.PRICE)),
                    parseDate2(c.getString(c.getColumnIndex(Expenses.DATE)))
            ));
            c.moveToNext();
        }
        db.close();
        return expenses;
    }
    
    public int getNextPaymentId(){
        int id = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + Payments.ID + " FROM " + Payments.TABLE_NAME + " ORDER BY " + Payments.ID + " DESC LIMIT 1;", null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            id = c.getInt(c.getColumnIndex(Payments.ID));
            id++;
            c.moveToNext();
        }
        db.close();
        return id;
    }
    
    public int addPayment(int id, String method){
        long insert;
        ContentValues values = new ContentValues();
        values.put(Payments.ID, id);
        values.put(Payments.METHOD, method);
        SQLiteDatabase db = getWritableDatabase();
        insert = db.insert(Payments.TABLE_NAME, null, values);
        db.close();
        if(insert == -1)
            id = -1;
        return id;
    }

    public void deletePayment(int methodId){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + Payments.TABLE_NAME + " WHERE " + Payments.ID + "=" + methodId + ";");
    }
    
    public void deleteAllPayments(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + Payments.TABLE_NAME + ";");
    }
    
    public ArrayList<PaymentMethod> getAllPayments(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Payments.TABLE_NAME  
        		+  " ;",null);
        c.moveToFirst();
        ArrayList<PaymentMethod> methods = new ArrayList<PaymentMethod>();
        while(!c.isAfterLast()){
        	methods.add(new PaymentMethod(
        			c.getInt(c.getColumnIndex(Payments.ID)),
        			c.getString(c.getColumnIndex(Payments.METHOD))
        			));
            c.moveToNext();
        }
        db.close();
        return methods;
    }
    
    public PaymentMethod getMethod(int id){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Payments.TABLE_NAME + " WHERE Payments.ID = ?;",new String[]{"" + id});
        c.moveToFirst();
       PaymentMethod p = null;
        while(!c.isAfterLast()){
            p = new PaymentMethod(
                    c.getInt(c.getColumnIndex(Payments.ID)),
                    c.getString(c.getColumnIndex(Payments.METHOD))
            );
            c.moveToNext();
        }
        db.close();
        return p;
    }

    public void editPayment(int methodId, String methodName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + Payments.TABLE_NAME +
                " SET " + Payments.METHOD + " =? WHERE Payments.ID =? ;",new Object[]{
                    "" + methodName, methodId});
        db.close();
    }
    
    public int getNextBudgetId(){
        int id = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + Budgets.ID +
        		" FROM " + Budgets.TABLE_NAME +
        		" ORDER BY " + Budgets.ID + " DESC LIMIT 1;", null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            id = c.getInt(c.getColumnIndex(Budgets.ID));
            id++;
            c.moveToNext();
        }
        db.close();
        return id;
    }
    
    public void addBudget(Budget b){
        ContentValues values = new ContentValues();
        values.put(Budgets.ID, b.getId());
        values.put(Budgets.DATEFROM, b.getDateFrom());
        values.put(Budgets.DATETO, b.getDateTo());
        values.put(Budgets.AMOUNT, b.getAmount());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(Budgets.TABLE_NAME, null, values);
    }

    public void deleteBudget(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + Budgets.TABLE_NAME + " WHERE " + Budgets.ID + "=" + id + ";");
    }
    
    public void deleteAllBudgets(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + Budgets.TABLE_NAME + ";");
    }
    
    public ArrayList<Budget> getAllBudgets(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Budgets.TABLE_NAME +  " ;",null);
        c.moveToFirst();
        ArrayList<Budget> budgets = new ArrayList<Budget>();
        while(!c.isAfterLast()){
        	budgets.add(new Budget(
        			c.getInt(c.getColumnIndex(Budgets.ID)),
        			c.getString(c.getColumnIndex(Budgets.DATEFROM)),
        			c.getString(c.getColumnIndex(Budgets.DATETO)),
        			c.getFloat(c.getColumnIndex(Budgets.AMOUNT))
        			));
            c.moveToNext();
        }
        db.close();
        return budgets;
    }
    
    public Budget getBudget(int id){
    	SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " +
	    	Budgets.TABLE_NAME +
	    	" WHERE Budgets.ID = ?;",new String[]{"" + id});
        c.moveToFirst();
        Budget b = null;
        while(!c.isAfterLast()){
            b = new Budget(
            		c.getInt(c.getColumnIndex(Budgets.ID)),
        			c.getString(c.getColumnIndex(Budgets.DATEFROM)),
        			c.getString(c.getColumnIndex(Budgets.DATETO)),
        			c.getFloat(c.getColumnIndex(Budgets.AMOUNT))
            );
            c.moveToNext();
        }
        db.close();
        return b;
    }
    
    public void editBudget(Budget budget){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + Budgets.TABLE_NAME +
                " SET " + Budgets.DATEFROM + " =?, " + 
        		Budgets.DATETO + " =?, " +
                Budgets.AMOUNT + " =? " +
        		"WHERE Budgets.ID =? ;",
        		new Object[]{"" + budget.getDateFrom(), budget.getDateTo(), budget.getAmount(), budget.getId()});
        db.close();
    }

}
