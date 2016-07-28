package com.azucca.budgettracker;

public class Budget {
	
	private int id;
	private String dateFrom;
	private String dateTo;
	private float amount;
	
	public Budget(){
		
	}
	
	public Budget(int id, String dateFrom, String dateTo, float amount) {
		this.id = id;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.amount = amount;
	}

	public int getId() {
		return id;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public float getAmount() {
		return amount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

}
