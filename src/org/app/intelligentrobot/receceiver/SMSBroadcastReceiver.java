package org.app.intelligentrobot.receceiver;


import java.sql.Date;
import java.text.SimpleDateFormat;

import org.app.intelligentrobot.SMSApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "SMSBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] pdus = (Object[])intent.getExtras().get("pdus");//获取短信内容
		Log.i(TAG, "onReceive is running");
		for(Object pdu : pdus){
			byte[] data = (byte[]) pdu;//获取单条短信内容，短信内容以pdu格式存在
			SmsMessage message = SmsMessage.createFromPdu(data);//使用pdu格式的短信数据生成短信对象
			String sender = message.getOriginatingAddress();//获取短信的发送者
			String content = message.getMessageBody();//获取短信的内容
			Date date = new Date(message.getTimestampMillis());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sendtime = format.format(date);
			Log.i(TAG, "sender " + sender + " content is " + content);
			
			SMSApp.getApp(context).getService().saveReceiveSMS(sender,content,sendtime);
			
		}
	}

}