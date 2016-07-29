package com.azucca.budgettracker.tables;

public class Expenses {
	
	public static final String TABLE_NAME = "EXPENSES";
    public static final String ID = "ID";
    public static final String PRODUCTNAME = "PRODUCTNAME";
    public static final String METHOD = "METHOD";
    public static final String PRICE = "PRICE";
    public static final String DATE = "DATE";
    
    public static String createTable(){
	    return "CREATE TABLE " + Expenses.TABLE_NAME + " (" +
	            Expenses.ID + " int primary key, " +
	            Expenses.PRODUCTNAME + " text," +
	            Expenses.METHOD + " text, " +
	            Expenses.PRICE + " text," +
	            Expenses.DATE + " text )";
    }
}
