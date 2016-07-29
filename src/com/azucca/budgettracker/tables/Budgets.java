package com.azucca.budgettracker.tables;

public class Budgets {
	
	 	public static final String TABLE_NAME = "BUDGETS";
	    public static final String ID = "ID";
	    public static final String DATEFROM = "DATEFROM";
	    public static final String DATETO = "DATETO";
	    public static final String AMOUNT = "AMOUNT";
	    
	    public static String createTable(){
	    	return "CREATE TABLE " + Budgets.TABLE_NAME + " (" +
	                Budgets.ID + " int primary key, " +
	                Budgets.DATEFROM + " text, " +
	                Budgets.DATETO + " text, " +
	                Budgets.AMOUNT + " real)";
	    	}

}
