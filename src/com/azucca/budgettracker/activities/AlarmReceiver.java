package com.azucca.budgettracker.activities;

import java.util.ArrayList;

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
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class AlarmReceiver extends BroadcastReceiver {
  
    @Override
    public void onReceive(Context context, Intent intent) {    	
    	String notificationText = "Parece que todavía no planificaste ningún presupuesto!";            	
    	DBHelper db = new DBHelper(context);
    	
    	ArrayList<Budget> budgets = db.getAllBudgets();
    	
    	if(budgets.size() == 0){
    		
    		Notification.Builder mBuilder =
			        new Notification.Builder(context)
			        .setSmallIcon(R.drawable.money)
			        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.moneybig))
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
			
			System.out.println(notificationText);
			
    	}
    	else for(Budget b : budgets){
    		
    		float exp = 0f;
    		for(Expense e : db.searchExpenses(b.getDateFrom(), b.getDateTo(), b.getMethod()))
    			exp += e.getPrice();
    		
    		switch(b.getStatus(exp)){
    		case Color.RED:
    			notificationText = "Ya gastaste todo tu presupuesto de $" + b.getAmount() + "!";
    			break;
    		case Color.YELLOW:
    			notificationText = "Cuidado, llevás gastados $" + exp + " de $" + b.getAmount() + "!";
    			break;
    		case Color.GREEN:
    			notificationText = "No te preocupes, todavía podés seguir gastando tu presupuesto de $" + b.getAmount() + "!";
    			break;
    		}
    	
	    	Notification.Builder mBuilder =
			        new Notification.Builder(context)
			        .setSmallIcon(R.drawable.money)
			        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.moneybig))
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
			
			System.out.println(notificationText);
    	}
    	
    }

}
