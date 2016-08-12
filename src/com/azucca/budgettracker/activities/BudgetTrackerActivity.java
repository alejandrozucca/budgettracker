package com.azucca.budgettracker.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.azucca.budgettracker.R;
import com.azucca.budgettracker.db.DBHelper;
import com.azucca.budgettracker.entities.Budget;
import com.azucca.budgettracker.entities.Expense;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BudgetTrackerActivity extends Activity {
	
	protected DBHelper db;
	protected Point size;	
	protected String[] items;
	protected TableLayout results;
	protected View selectedView = null;
    protected Expense selectedExpense = null;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(db == null){
        	db = new DBHelper(this);       	
        	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	        boolean b = preferences.getBoolean("sendNotifications", false);			
	        if(b == false){					
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean("sendNotifications", true);
				editor.apply();
	        } else {
		        Intent alertIntent = new Intent(this,AlarmReceiver.class);
				AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);		
				alarmManager.setInexactRepeating(
					AlarmManager.RTC_WAKEUP, 
					new GregorianCalendar().getTimeInMillis() + 5000, 
					/*24 * 60 * 60 * 1000*/ 60000,
					PendingIntent.getBroadcast(this, 1, alertIntent, 0)
				);
	        }
		}
			
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);      
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	    
	    getMenuInflater().inflate(R.menu.menu_top, menu);
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {         
        switch (item.getItemId()) {
        case R.id.home:
        	goToHome();
        	finish();
            return true; 
        case R.id.settings:
        	goToSettings();
        	finish();
            return true; 
        case R.id.budgets:
        	goToBudgets();
        	finish();
        	return true;
        }
        return false;
    }
	
	public void goToHome(){
		startActivity(new Intent(this, Expenses.class));
	}
	
	public void goToSettings(){
		startActivity(new Intent(this, Settings.class));
	}
	
	public void goToBudgets(){
		startActivity(new Intent(this, Budgets.class));
	}
	
	public TableRow createRow(Expense e){
		int height = 40;
        TextView id = new TextView(this);
        TextView product = new TextView(this);
        TextView price = new TextView(this);
        TextView method = new TextView(this);
        TextView date = new TextView(this);
        id.setText("" + e.getId());
        id.setWidth(0);
        id.setHeight(height);
        product.setText(e.getProductName());
        product.setWidth((int)(size.x * 0.3));
        product.setHeight(height);
        price.setText("$ " + e.getPrice());
        price.setWidth((int)(size.x * 0.2));
        price.setHeight(height);
        method.setText(e.getMethod());
        method.setWidth((int)(size.x * 0.3));
        method.setHeight(height);
        String d = e.getDate().split(" ")[0];
        date.setText(d.substring(0, d.length() - 5));
        date.setWidth((int)(size.x * 0.2));
        date.setHeight(height);
        TableRow row = new TableRow(this);
        row.setPadding(10, 15, 10, 15);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(id);
        row.addView(product);
        row.addView(price);
        row.addView(method);
        row.addView(date);
        return row;        
	}
	
	public TableRow createRow(Budget e, float spent){
		int height = 40;
        TextView id = new TextView(this);        
        TextView method = new TextView(this);
        TextView from = new TextView(this);
        TextView to = new TextView(this);
        TextView left = new TextView(this);
        id.setText("" + e.getId());
        id.setWidth(0);
        id.setHeight(height);
        method.setText(e.getMethod());
        method.setWidth((int)(size.x * 0.3));
        method.setHeight(height);
        String f = e.getDateFrom().split(" ")[0];
        from.setText(f.substring(0, f.length() - 5));
        from.setWidth((int)(size.x * 0.2));
        from.setHeight(height);
        String t = e.getDateTo().split(" ")[0];
        to.setText(t.substring(0, t.length() - 5));
        to.setWidth((int)(size.x * 0.2));
        to.setHeight(height);        
        left.setText("" + (e.getAmount() - spent));
        left.setWidth((int)(size.x * 0.3));
        left.setHeight(height);
        left.setTextColor(e.getStatus(spent));
        TableRow row = new TableRow(this);
        row.setPadding(10, 15, 10, 15);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(id);
        row.addView(method);
        row.addView(from);
        row.addView(to);
        row.addView(left);
        return row;        
	}
	
	public void updateColors(){
        for(int i = 0; i < results.getChildCount(); i++)
        	if(results.getChildAt(i).getId() == -99)
        		 results.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.silver));
        	else if(i % 2 == 0)
                results.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.c2));
            else
                results.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
    }
	
	public ArrayList<TextView> getChildren(View view){
        ViewGroup group = (ViewGroup) view;
        ArrayList<TextView> views = new ArrayList<TextView>();
        for(int i = 0; i < group.getChildCount(); i++)
            views.add((TextView) group.getChildAt(i));
        return views;
    }
	
	public String getDate(){
		Calendar c = Calendar.getInstance();
		return parseDate(c.get(Calendar.YEAR) , c.get(Calendar.MONTH) , c.get(Calendar.DAY_OF_MONTH));
	}
	
	public String getTime(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE);		
	}
	
	public void deleteExpense() {
	    db.deleteExpense(Integer.parseInt(getChildren(selectedView).get(0).getText().toString()));
	    results.removeView(selectedView);
	    updateColors();
	}
	
	public String parseDate(int year, int monthOfYear, int dayOfMonth) {	
		String day, month;		
		if(dayOfMonth < 10)		day = "0" + dayOfMonth;			else day = "" + dayOfMonth;
		if(monthOfYear++ < 10)	month = "0" + monthOfYear;	else month = "" + monthOfYear;
		return day + "/" + month + "/" + year;
	}
	
	public void sendNotification(String text){
		Notification.Builder mBuilder =
		        new Notification.Builder(this)
		        .setSmallIcon(R.drawable.money)
		        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.moneybig))
		        .setContentTitle("BudgetTracker")
		        .setContentText(text)
		        .setAutoCancel(true);
		
		Intent resultIntent = new Intent(this, Budgets.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(Budgets.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
	}
		
}
