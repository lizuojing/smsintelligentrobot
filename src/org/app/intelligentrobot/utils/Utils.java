package org.app.intelligentrobot.utils;

import org.app.intelligentrobot.MainActivity;
import org.app.intelligentrobot.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

public class Utils {
	public static void sendSMS(Context context, String number, String text) {
		SmsManager manager = SmsManager.getDefault();
		manager.sendTextMessage(number, null, text, null, null); // TODO: check
																	// if sent
																	// successful.

		ContentValues values = new ContentValues();
		values.put("address", number);
		values.put("body", text);
		context.getContentResolver().insert(Uri.parse("content://sms/sent"),
				values);
	}

	public static void sysSendSMS(Context context, String number, String text) {
		Uri smsToUri = Uri.parse(String.format("smsto:%s", number));
		Intent intent = new Intent(android.content.Intent.ACTION_SENDTO,
				smsToUri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("sms_body", text);
		context.startActivity(intent);
	}
	
	public static void showNotification(Context context, int id, String title, String message)
	{
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String tickerText = message;

        Notification notification = new Notification(R.drawable.notification, tickerText, System.currentTimeMillis());
        notification.defaults |= Notification.DEFAULT_SOUND;

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from_notification", true);
        
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        notification.setLatestEventInfo(context, title, tickerText, contentIntent);
        
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(id, notification);
	}
	
	public static void deleteNotification(Context context, int id) 
	{
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
	
}
