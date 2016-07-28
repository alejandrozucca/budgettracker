package com.azucca.budgettracker;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ExpenseList extends BudgetTrackerActivity {
		
	private Button search;
	private Button clear;
	private TextView searchFrom;
	private TextView searchTo;		
	private int n = 0;
    private int lastId;
    private DatePickerDialog dateFromDialog;
    private DatePickerDialog dateToDialog;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.expense_list);
	        
	        results = (TableLayout) findViewById(R.id.search_results);
	        search = (Button) findViewById(R.id.search_ok);
	        clear = (Button) findViewById(R.id.search_clear);
	        searchFrom = (TextView) findViewById(R.id.search_datePickerFrom);
	        searchTo = (TextView) findViewById(R.id.search_datePickerTo);	       
	        searchFrom.setText(getDate());
	        searchTo.setText(getDate());
	        
	        for(Expense e : db.getMore(999999))
	        		addToTable(e, -1);
	        updateColors();
	        
	        searchFrom.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	showFromDateDialog();
	            }
	        });	        
	        searchTo.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	showToDateDialog();
	            }
	        });	        
	        search.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	searchExpenses();
	            }
	        });	        
	        clear.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	clearSearch();
	            }
	        });
	 }
	
	public void addToTable(Expense e, int position){    	
    	lastId = e.getId();
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
	        addViewMore();
        }
    }
	
	private void addViewMore(){
    	TextView viewAll = new TextView(this);        
        viewAll.setText("Ver mas");
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
				loadMore();
				updateColors();
			}
		});
        results.addView(row);
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
	
	private void loadMore(){
		for(Expense e: db.getMore(lastId)){
			addToTable(e,results.getChildCount() - 1);			
		}
		if(db.getMore(lastId).size() <= 0)
			results.removeViewAt(results.getChildCount() -1);
	}
		 
	 public void showFromDateDialog(){
		 Calendar c = Calendar.getInstance();
	    	if(dateFromDialog == null)
		        dateFromDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,	int dayOfMonth) {	
						searchFrom.setText(parseDate(year, monthOfYear, dayOfMonth));
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
						searchTo.setText(parseDate(year, monthOfYear, dayOfMonth));
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	    	dateToDialog.show();
	    }
	 
	 public void searchExpenses(){		 
		 results.removeAllViews();
		 for(Expense e : db.search(
				 db.parseDate(searchFrom.getText().toString() + " 00:00"), 
				 db.parseDate(searchTo.getText().toString() + " 23:59"))){
			 addToTable(e, -1);
		 }
		 updateColors();
	 }
	 
	 public void clearSearch(){
		 n = 0;
		 selectedExpense = null;
	     selectedView = null;
	     searchFrom.setText(getDate());
	     searchTo.setText(getDate());
	     results.removeAllViews();
	     for(Expense e : db.getMore(999999))
     		addToTable(e, -1);
	     updateColors();
	 }
	 	 
}
