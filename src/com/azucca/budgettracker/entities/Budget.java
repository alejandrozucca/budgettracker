package com.azucca.budgettracker.entities;

import java.util.Calendar;

import android.graphics.Color;

public class Budget {
	
	private int id;
	private String method;
	private String dateFrom;
	private String dateTo;
	private float amount;
	
	public Budget(){
		
	}
	
	public Budget(int id, String method, String dateFrom, String dateTo, float amount) {
		this.id = id;
		this.method = method;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.amount = amount;
	}

	public int getId() {
		return id;
	}
	
	public String getMethod(){
		return method;
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
	
	public int getStatus(float spent){		
		if(spent >= amount)
			return Color.RED;
		else {
			Calendar c = Calendar.getInstance();			
			c.set(Integer.parseInt(dateFrom.split(" ")[0].split("/")[2]),
					Integer.parseInt(dateFrom.split(" ")[0].split("/")[1]),
					Integer.parseInt(dateFrom.split(" ")[0].split("/")[0]));
			long from = c.getTime().getTime();
			c.set(Integer.parseInt(dateTo.split(" ")[0].split("/")[2]),
					Integer.parseInt(dateTo.split(" ")[0].split("/")[1]),
					Integer.parseInt(dateTo.split(" ")[0].split("/")[0]));
			long to = c.getTime().getTime();
			long range = (to - from) / (1000 * 60 * 60 * 24);			
			Calendar n = Calendar.getInstance();
			n.set(Calendar.MONTH, n.get(Calendar.MONTH) + 1);
			long timeSpent = (n.getTime().getTime() - from) / (1000 * 60 * 60 * 24);
			if((double) spent / amount > (double) timeSpent / range)
				return Color.YELLOW;			
		}
		return Color.GREEN;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setMethod(String method){
		this.method = method;
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
