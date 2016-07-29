package com.azucca.budgettracker.tables;

public class Payments {
	
	public static final String TABLE_NAME = "PAYMENTS";
    public static final String ID = "ID";
    public static final String METHOD = "METHODNAME";
    
    public static String createTable(){
	    return "CREATE TABLE " + Payments.TABLE_NAME + " (" +
	            Payments.ID + " int primary key, " +
	            Payments.METHOD + " text )";
    }
}
