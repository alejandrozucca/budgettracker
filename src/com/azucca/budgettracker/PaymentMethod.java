package com.azucca.budgettracker;

public class PaymentMethod {
	
	private int id;
	private String name;
	
	public PaymentMethod(){
		
	}
	
	public PaymentMethod(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "PaymentMethods [id=" + id + ", name=" + name + "]";
	}

}
