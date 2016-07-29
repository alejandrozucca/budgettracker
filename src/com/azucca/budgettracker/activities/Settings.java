package com.azucca.budgettracker.activities;

import com.azucca.budgettracker.R;
import com.azucca.budgettracker.entities.PaymentMethod;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Settings extends BudgetTrackerActivity {
	
	private Button add;
	private EditText textField;
	private PaymentMethod selectedMethod;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		add = (Button) findViewById(R.id.add_method);
		results = (TableLayout) findViewById(R.id.methods_table);
		textField = (EditText) findViewById(R.id.newMethod);
		
		//db.deleteAll();
		for(PaymentMethod p : db.getAllPayments())
			addPaymentMethod(p);
		updateColors();
		
		add.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(textField.getText().length() > 0){
					if(add.getText().toString().compareTo(getResources().getString(R.string.button_add)) == 0){
						PaymentMethod n = new PaymentMethod(db.getNextPaymentId(), textField.getText().toString());
						if (db.addPayment(n.getId(), n.getName()) != -1)
							addPaymentMethod(n);
						updateColors();
						textField.setText("");
					} else {
						db.editPayment(selectedMethod.getId(), textField.getText().toString());
						getChildren(selectedView).get(1).setText(textField.getText().toString());
						textField.setText("");
						updateColors();
                        add.setText(getResources().getString(R.string.button_add));
					}
				}
			}
		});
	}
	
	private void addPaymentMethod(PaymentMethod method){	
		TextView id = new TextView(this);
		id.setHeight(40);
		id.setText("" + method.getId());
		id.setWidth(0);
		TextView item = new TextView(this);
		item.setHeight(40);
		item.setText(method.getName());
		TableRow tr = new TableRow(this);
		tr.setPadding(10, 15, 10, 15);
        tr.setGravity(Gravity.CENTER_VERTICAL);
		tr.addView(id);
		tr.addView(item);
		tr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showOptions(v);
                return true;
            }
        });
		results.addView(tr);
	}
	
	private void showOptions(View v){		
		selectedView = v;
        PopupMenu popup = new PopupMenu(this, v);
        getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        editMethod();
                        return true;
                    case R.id.delete:
                        deleteMethod();
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
	
	public void editMethod() {
    	selectedView.setBackgroundColor(getResources().getColor(R.color.c1));
        selectedMethod = db.getMethod(Integer.parseInt(getChildren(selectedView).get(0).getText().toString()));
        textField.setText(selectedMethod.getName());        
        add.setText(getResources().getString(R.string.button_ok));
    }
	
	public void deleteMethod(){
		db.deletePayment(Integer.parseInt(getChildren(selectedView).get(0).getText().toString()));
	    results.removeView(selectedView);
	    updateColors();
	}
}
