package org.app.intelligentrobot.utils;

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
}
