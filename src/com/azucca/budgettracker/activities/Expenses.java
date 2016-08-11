package com.azucca.budgettracker.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
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
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import com.azucca.budgettracker.R;
import com.azucca.budgettracker.entities.Budget;
import com.azucca.budgettracker.entities.Expense;
import com.azucca.budgettracker.entities.PaymentMethod;

public class Expenses extends BudgetTrackerActivity {

    private Spinner dropdown;
    private EditText productName;
    private EditText price;
    private Button save;
    private Button clear;
    private TextView date;
    private TextView time;    
    private DatePickerDialog dateDialog;
    private TimePickerDialog timeDialog;    
    private int n = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int pi = 0;
        ArrayList<PaymentMethod> methods = db.getAllPayments();
        items = new String[methods.size()];
        for(PaymentMethod p : methods)
        	items[pi++] = p.getName();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);

        productName = (EditText) findViewById(R.id.productName);
        price = (EditText) findViewById(R.id.price) ;
        dropdown = (Spinner)findViewById(R.id.method);
        dropdown.setAdapter(adapter);
        results = (TableLayout) findViewById(R.id.results);
        save = (Button) findViewById(R.id.add);
        clear = (Button) findViewById(R.id.clear);
        date = (TextView) findViewById(R.id.datePicker);
        time = (TextView) findViewById(R.id.timePicker);
        
        date.setText(getDate());
        time.setText(getTime());
        
        for(Expense e : db.getMoreExpenses(999999))
    		addToTable(e, -1);  
        updateColors();
        
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(productName.getText().toString().length() > 0 && price.getText().toString().length() > 0)
                    if(save.getText().toString().compareTo(getResources().getString(R.string.button_add)) == 0) {
                        addToTable(createExpense(), 0);
                        updateColors();
                    } else {
                        db.editExpense(new Expense(
                                Integer.parseInt(getChildren(selectedView).get(0).getText().toString()),
                                productName.getText().toString(),
                                dropdown.getSelectedItem().toString(),
                                Float.parseFloat(price.getText().toString()),
                                date.getText().toString() + " " + time.getText().toString()
                        ));
                        getChildren(selectedView).get(1).setText(productName.getText().toString());
                        getChildren(selectedView).get(2).setText("$ " + price.getText().toString());
                        getChildren(selectedView).get(3).setText(dropdown.getSelectedItem().toString());
                        getChildren(selectedView).get(4).setText(date.getText().toString().substring(0, date.getText().toString().length() - 5));
                        clear();
                        updateColors();
                        save.setText(getResources().getString(R.string.button_add));                    
                    }
                for(Budget e : db.getAllBudgets()){
                	float exp = 0f;
        			for(Expense x : db.searchExpenses(				
        					 db.parseDateToDB(e.getDateFrom()), 
        					 db.parseDateToDB(e.getDateTo()),
        					 e.getMethod()
        					))
        				exp += x.getPrice();
        			switch (e.getStatus(exp)){
        			case Color.RED:
        				sendNotification("Ya gastaste todo tu presupuesto de $" + e.getAmount() + "!");
        				break;
        			case Color.YELLOW:
        				sendNotification("Cuidado, llevás gastados $" + exp + " de $" + e.getAmount() + "!");
        				break;
        			}
                }
             
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clear();
            }
        });
        
        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	showDateDialog();
            }
        });
        
        time.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	showTimeDialog();
            }
        });

    }
    
    public void showDateDialog(){
    	Calendar c = Calendar.getInstance();
    	if(dateDialog == null)
	        dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,	int dayOfMonth) {	
					date.setText(parseDate(year, monthOfYear, dayOfMonth));
				}
			}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }
    
    public void showTimeDialog(){
    	Calendar c = Calendar.getInstance();
    	if(timeDialog == null)
    		timeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {					
					time.setText(hourOfDay + ":" + minute);
				}}, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), true);
    	timeDialog.show();
    }

    public Expense createExpense(){
        Expense e = new Expense(
                db.getNextExpenseId(),
                productName.getText().toString(),
                dropdown.getSelectedItem().toString(),
                Float.parseFloat(price.getText().toString()),
                date.getText().toString() + " " + time.getText().toString()
        );
        if(db.addExpense(e) == -1)
            e = null;
        productName.setText("");
        price.setText("");
        return e;
    }

    public void addToTable(Expense e, int position){
    	TableRow row = createRow(e);
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
        if(n++ == 4){
	        addViewAll();
        }
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
                        editExpense();
                        return true;
                    case R.id.delete:
                        deleteExpense();
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

    public void editExpense() {
    	selectedView.setBackgroundColor(getResources().getColor(R.color.c1));
        selectedExpense = db.getExpense(Integer.parseInt(getChildren(selectedView).get(0).getText().toString()));
        productName.setText(selectedExpense.getProductName());
        price.setText(String.valueOf(selectedExpense.getPrice()));
        date.setText(selectedExpense.getDate().split(" ")[0]);
        time.setText(selectedExpense.getDate().split(" ")[1]);
        for(int i= 0; i < items.length; i++)
            if(items[i].compareTo(selectedExpense.getMethod()) == 0)
                dropdown.setSelection(i);
        save.setText(getResources().getString(R.string.button_ok));
    }

    public void clear(){
        selectedExpense = null;
        selectedView = null;
        productName.setText("");
        price.setText("");
        dropdown.setSelection(0);
        save.setText(getResources().getString(R.string.button_add));        
        date.setText(getDate());
        time.setText(getTime());
    }
    
    private void addViewAll(){
    	TextView viewAll = new TextView(this);        
        viewAll.setText("Ver todo");
        viewAll.setTextColor(getResources().getColor(R.color.link));
        viewAll.setTypeface(null, Typeface.BOLD);
        viewAll.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        TableRow row = new TableRow(this);
        row.setId(-99);
        row.setPadding(10, 20, 10, 20);
        row.addView(new TextView(this));
        row.addView(viewAll);
        row.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				viewExpenseList();
			}
		});
        results.addView(row);
    }
    
    private void viewExpenseList(){
        startActivity(new Intent(this, Search.class));
        finish();
    }

}
