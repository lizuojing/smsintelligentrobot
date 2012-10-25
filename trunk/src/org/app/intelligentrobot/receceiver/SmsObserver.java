package org.app.intelligentrobot.receceiver;

import android.content.Context;
import android.database.ContentObserver;
import android.util.Log;

public class SmsObserver extends ContentObserver {

	private Context context;

	public SmsObserver(Context context) {
		super(null);
		this.context = context;
		Log.i("Leo-SmsObserver", "My Oberver on create");
	}

	@Override
	public void onChange(boolean selfChange) {
		// 监听短信变化 TODO
		// SMSApp.getApp(context).getService().updateSendSMS();
	}

}
