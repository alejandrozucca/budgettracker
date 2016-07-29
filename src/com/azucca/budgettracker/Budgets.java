package com.azucca.budgettracker;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class Budgets extends BudgetTrackerActivity {
	
	private Button add;
	private EditText textField;
	private TextView budgetFrom;
	private TextView budgetTo;
	private TextView budgetText;
	private DatePickerDialog dateFromDialog;
    private DatePickerDialog dateToDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budgets);
		
		add = (Button) findViewById(R.id.add_budget);		
		textField = (EditText) findViewById(R.id.budget);
		budgetFrom = (TextView) findViewById(R.id.budget_datePickerFrom);
        budgetTo = (TextView) findViewById(R.id.budget_datePickerTo);
        budgetText = (TextView) findViewById(R.id.budgetText);
       
        Budget existingBudget = db.getBudget(0);
        if(existingBudget != null){
        	float exp = 0f;
    		for(Expense e : db.searchExpenses(				
    				 existingBudget.getDateFrom(), 
    				 existingBudget.getDateTo()	
    				))
    			exp += e.getPrice();
    		budgetText.setText(existingBudget.getAmount() +
    				" para gastar desde " + existingBudget.getDateFrom() +
    				" hasta " + existingBudget.getDateTo()
    				+ " y llevas gastado " + exp);        	
        }
        
        budgetFrom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	showFromDateDialog();
            }
        });	        
        budgetTo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	showToDateDialog();
            }
        });	
        add.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(textField.getText().toString().length() > 1 &&
						budgetFrom.getText().toString().length() > 0 &&
						budgetTo.getText().toString().length() > 0)
					updateBudget();
				}
		});
	}
	
	public void updateBudget(){
		float exp = 0f;
		for(Expense e : db.searchExpenses(				
				 db.parseDate(budgetFrom.getText().toString() + " 00:00"), 
				 db.parseDate(budgetTo.getText().toString() + " 23:59")				
				))
			exp += e.getPrice();
		budgetText.setText(textField.getText().toString() +
				" para gastar desde " + budgetFrom.getText().toString() +
				" hasta " + budgetTo.getText().toString()
				+ " y llevas gastado " + exp);
		
		if(db.getAllBudgets().size() == 0)
			db.addBudget(new Budget(0,
					db.parseDate(budgetFrom.getText().toString() + " 00:00"),
					db.parseDate(budgetTo.getText().toString() + " 23:59"),
					Float.parseFloat(textField.getText().toString()))
			);
		else
			db.editBudget(new Budget(0,
					db.parseDate(budgetFrom.getText().toString() + " 00:00"),
					db.parseDate(budgetTo.getText().toString() + " 23:59"),
					Float.parseFloat(textField.getText().toString()))
			);
	}
	
	public void showFromDateDialog(){
		 Calendar c = Calendar.getInstance();
	    	if(dateFromDialog == null)
		        dateFromDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,	int dayOfMonth) {	
						budgetFrom.setText(parseDate(year, monthOfYear, dayOfMonth));
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	        dateFromDialog.show();
	    }
	 
	 public void showToDateDialog(){
		 Calendar c = Calendar.getInstance();
	    	if(dateToDialog == null)
	    		dateToDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,	int dayOfMonth) {	
						budgetTo.setText(parseDate(year, monthOfYear, dayOfMonth));
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	    	dateToDialog.show();
	 }
		
}
