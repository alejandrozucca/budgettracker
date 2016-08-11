package com.azucca.budgettracker.activities;

import com.azucca.budgettracker.R;
import com.azucca.budgettracker.db.DBHelper;
import com.azucca.budgettracker.entities.Budget;
import com.azucca.budgettracker.entities.Expense;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class AlarmReceiver extends BroadcastReceiver {
  
    @Override
    public void onReceive(Context context, Intent intent) {    	
    	String notificationText = null;            	
    	DBHelper db = new DBHelper(context);
    	
    	for(Budget b : db.getAllBudgets()){
    		float exp = 0f;
    		for(Expense e : db.searchExpenses(b.getDateFrom(), b.getDateTo(), b.getMethod()))
    			exp += e.getPrice();
    		switch(b.getStatus(exp)){
    		case Color.RED:
    			notificationText = "Ya gastaste todo tu presupuesto de $" + b.getAmount() + "!";
    			break;
    		case Color.YELLOW:
    			notificationText = "Cuidado, llev�s gastados $" + exp + " de $" + b.getAmount() + "!";
    			break;
    		case Color.GREEN:
    			notificationText = "No te preocupes, todav�a pod�s seguir gastando tu presupuesto de $" + b.getAmount() + "!";
    			break;
    		}
    	}
    	
    	Notification.Builder mBuilder =
		        new Notification.Builder(context)
		        .setSmallIcon(R.drawable.buttons)
		        .setContentTitle("BudgetTracker")
		        .setContentText(notificationText)
		        .setAutoCancel(true);
		
		Intent resultIntent = new Intent(context, Budgets.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(Budgets.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());    		
    }

}
