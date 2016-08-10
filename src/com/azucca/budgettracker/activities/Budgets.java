package com.azucca.budgettracker.activities;

import java.util.ArrayList;
import java.util.Calendar;

import com.azucca.budgettracker.R;
import com.azucca.budgettracker.entities.Budget;
import com.azucca.budgettracker.entities.Expense;
import com.azucca.budgettracker.entities.PaymentMethod;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Budgets extends BudgetTrackerActivity {
	
	private Button add;
	private Button clear;
	private EditText textField;
	private TextView budgetFrom;
	private TextView budgetTo;	
	private DatePickerDialog dateFromDialog;
    private DatePickerDialog dateToDialog;
    private Spinner dropdown;
    private Budget selectedBudget;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budgets);
		
		int pi = 0;
        ArrayList<PaymentMethod> methods = db.getAllPayments();
        items = new String[methods.size()];
        for(PaymentMethod p : methods)
        	items[pi++] = p.getName();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        
		add = (Button) findViewById(R.id.add_budget);	
		clear = (Button) findViewById(R.id.clear_budget);
		textField = (EditText) findViewById(R.id.budget);
		budgetFrom = (TextView) findViewById(R.id.budget_datePickerFrom);
        budgetTo = (TextView) findViewById(R.id.budget_datePickerTo);
        results = (TableLayout) findViewById(R.id.budget_results);
        dropdown = (Spinner)findViewById(R.id.methodBudget);
        dropdown.setAdapter(adapter);
        
        for(Budget b : db.getAllBudgets())
        	addToTable(b, -1);
        updateColors();
                       
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
					if(add.getText().toString().compareTo(getResources().getString(R.string.button_add)) == 0){
						addToTable(addBudget(),0);						
				} else {
					Budget e = new Budget(
							selectedBudget.getId(),
							dropdown.getSelectedItem().toString(),
							budgetFrom.getText().toString() + " 00:00",
							budgetTo.getText().toString() + " 23:59",
							Float.parseFloat(textField.getText().toString())
						);
					db.editBudget(e);
					getChildren(selectedView).get(1).setText(dropdown.getSelectedItem().toString());
                    getChildren(selectedView).get(2).setText(budgetFrom.getText().toString().substring(0, budgetFrom.getText().toString().length() - 5));
                    getChildren(selectedView).get(3).setText(budgetTo.getText().toString().substring(0, budgetTo.getText().toString().length() - 5));
                    float exp = 0f;
        			for(Expense x : db.searchExpenses(				
        					 db.parseDateToDB(budgetFrom.getText().toString() + " 00:00"), 
        					 db.parseDateToDB(budgetTo.getText().toString() + " 23:59")				
        					))
        				exp += x.getPrice();
                    getChildren(selectedView).get(4).setText("" + (e.getAmount() - exp));
                    getChildren(selectedView).get(4).setTextColor(e.getStatus(exp));
                    add.setText(getResources().getString(R.string.button_add));
				}
				clear();
                updateColors();
			}
		});
        
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clear();
            }
        });
        
	}
	
	public void showOptions(View v){
        selectedView = v;
        PopupMenu popup = new PopupMenu(this, v);
        getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        editBudget();
                        return true;
                    case R.id.delete:
                        deleteBudget();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {			
			@Override
			public void onDismiss(PopupMenu menu) {
				updateColors();				
			}
		});
    }
	
	public void editBudget() {
    	selectedView.setBackgroundColor(getResources().getColor(R.color.c1));
        selectedBudget = db.getBudget(Integer.parseInt(getChildren(selectedView).get(0).getText().toString()));
        textField.setText("" + selectedBudget.getAmount());       
        budgetFrom.setText(selectedBudget.getDateFrom().split(" ")[0]);
        budgetTo.setText(selectedBudget.getDateTo().split(" ")[0]);
        for(int i= 0; i < items.length; i++)
            if(items[i].compareTo(selectedBudget.getMethod()) == 0)
                dropdown.setSelection(i);
        add.setText(getResources().getString(R.string.button_ok));
    }
	
	public void deleteBudget() {
	    db.deleteBudget(Integer.parseInt(getChildren(selectedView).get(0).getText().toString()));
	    results.removeView(selectedView);
	    updateColors();
	}
	
	public Budget addBudget(){		
		Budget b = new Budget(0,
				dropdown.getSelectedItem().toString(),
				budgetFrom.getText().toString() + " 00:00",
				budgetTo.getText().toString() + " 23:59",
				Float.parseFloat(textField.getText().toString()));
		db.addBudget(b);
		return b;
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
	 
	 public void addToTable(Budget e, int position){		
			float exp = 0f;
			for(Expense x : db.searchExpenses(				
					 db.parseDateToDB(e.getDateFrom()), 
					 db.parseDateToDB(e.getDateTo())				
					))
				exp += x.getPrice();						
	    	TableRow row = createRow(e, exp);
	    	row.setOnLongClickListener(new View.OnLongClickListener() {
	            @Override
	            public boolean onLongClick(View v) {
	                showOptions(v);
	                return true;
	            }
	        });
	    	if(position < 0)
	        	results.addView(row);
	        else
	        	results.addView(row, position);	       
	  }
	 
	 public void clear(){
	        selectedExpense = null;
	        selectedView = null;
	        textField.setText("");
	        dropdown.setSelection(0);
	        add.setText(getResources().getString(R.string.button_add));        
	        budgetFrom.setText("");
	        budgetTo.setText("");
	  }
	 
}