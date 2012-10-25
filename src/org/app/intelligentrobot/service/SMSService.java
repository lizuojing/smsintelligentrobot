package org.app.intelligentrobot.service;

import java.util.ArrayList;

import org.app.intelligentrobot.SMSApp;
import org.app.intelligentrobot.data.LocalDataHelper;
import org.app.intelligentrobot.entity.Conversation;
import org.app.intelligentrobot.receceiver.SmsObserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * 消息机制实现方案
 * 
 * @author Administrator
 * 
 */
public class SMSService extends Service {

	private static final String TAG = "SMSService";
	private static final Uri SMS_URI = Uri.parse("content://sms/");
	private static ArrayList<ServiceHandler> mServiceHandlers = new ArrayList<ServiceHandler>();
	public static Context context;

	public static abstract class ServiceHandler extends Handler {
		public static final int ID_Message_STATE = 2012;
		public static final int ID_LEARNING = 2013;
		public static final int ID_SUBSTITUTE = 2014;

		public void handleMessage(Message msg) {

			Log.i("ServiceHandler", "handleMessage, msg.what:" + msg.what
					+ " msg.obj:" + msg.obj + " msg.arg1:" + msg.arg1
					+ "msg.arg2:" + msg.arg2);

			super.handleMessage(msg);

			switch (msg.what) {
			case ID_Message_STATE:
				onProcessState(msg);

				break;
			case ID_LEARNING:

				break;
			case ID_SUBSTITUTE:

				break;
			default:
				defaultMessageProcess(msg);

				break;
			}
		}

		private void changeToLearn() {

		}

		private void changeToSubstitute() {

		}

		private void onProcessState(Message msg) {
		}

		private void defaultMessageProcess(Message msg) {
		}
	}

	public class LocalBinder extends Binder {
		public SMSService getService() {
			return SMSService.this;
		}
	}

	private final IBinder mBinder = new LocalBinder();
	private SmsObserver smsObserver;
	private LocalDataHelper mLocalDataHelper;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		context = SMSApp.getAppContext();
		mLocalDataHelper = new LocalDataHelper(this);
		super.onCreate();
	}

	private void initData() {
		new AsyncTask<Void, Integer, Long>() {

			@Override
			protected Long doInBackground(Void... params) {
				// 拷贝已发送短信内容
				copySendSMS();
				// 初始化关键词库

				return null;
			}

		}.execute();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "onStart is running");
		smsObserver = new SmsObserver(this);
		getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true, smsObserver);
		initData();
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		getContentResolver().unregisterContentObserver(smsObserver);
		Log.i(TAG, "onDestroy is running");
		super.onDestroy();
	}

	public static void addServiceHandler(ServiceHandler handler) {
		synchronized (mServiceHandlers) {
			if (mServiceHandlers == null) {
				mServiceHandlers = new ArrayList<ServiceHandler>();
			}
			mServiceHandlers.add(handler);
		}
	}

	public static void removeServiceHandler(ServiceHandler handler) {
		synchronized (mServiceHandlers) {
			if (null != mServiceHandlers)
				mServiceHandlers.remove(handler);
		}
	}

	public static void sendMessageToHandler(Message msg) {
		synchronized (mServiceHandlers) {
			for (ServiceHandler handler : mServiceHandlers) {
				Message newMsg = Message.obtain(msg);
				handler.sendMessage(newMsg);
			}
		}
	}

	public static void sendMessageToHandlerDelay(Message msg, int seconds) {
		synchronized (mServiceHandlers) {
			for (ServiceHandler handler : mServiceHandlers) {
				Message newMsg = Message.obtain(msg);
				handler.sendMessageDelayed(newMsg, 1000 * seconds);
			}
		}
	}

	public void saveReceiveSMS(String sender, String content, String sendtime) {
		if (mLocalDataHelper == null) {
			mLocalDataHelper = new LocalDataHelper(context);
		}
		mLocalDataHelper.saveReceiveSMS(sender, content, sendtime);
	}

	private ArrayList<Conversation> loadSendSMS() {
		ArrayList<Conversation> list = null;
		 Cursor smsCursor = context.getContentResolver().query(SMS_URI,
	        		new String[] { "_id", "address", "person", "date", "read","type", "body" },
	        		"type = 1", null, "  ORDER BY date ASC ");//降序排列
		 if( smsCursor != null ) {
			    int addressIndex = smsCursor.getColumnIndex("address");
		        int bodyIndex = smsCursor.getColumnIndex("body");
		        int typeIndex = smsCursor.getColumnIndex("type");
		        int timeIndex = smsCursor.getColumnIndexOrThrow("date");
			    if(list==null) {
			    	list = new ArrayList<Conversation>();
			    }else {
			    	list.clear();
			    }
			    for(smsCursor.moveToFirst();!smsCursor.isAfterLast();smsCursor.moveToNext()) {
			    	//1.接收到的消息，2.发出去的消息  
			   		String number = smsCursor.getString(addressIndex);
			   		String body = smsCursor.getString(bodyIndex);
			   		String type = smsCursor.getString(typeIndex);
			   		int time = smsCursor.getInt(timeIndex);
	    			Cursor tempCursor = context.getContentResolver().query(SMS_URI,
	    		        		new String[] { "_id", "address", "person", "date", "read","type", "body" },
	    		        		number+"=? AND type = 2 AND date > " + time, null, "  ORDER BY date ASC ");//降序排列
	    			Log.i(TAG, "number is " + number + " time is " + number);
	    			if(tempCursor!=null&&tempCursor.getCount()>0) {
	    				tempCursor.moveToFirst();
		    			String tempnumber = tempCursor.getString(addressIndex);
				   		String tempbody = tempCursor.getString(bodyIndex);
				   		String temptype = tempCursor.getString(typeIndex);
				   		int temptime = tempCursor.getInt(timeIndex);
				   		Conversation conversation = new Conversation();
		    			conversation.setPnum(number);
		    			conversation.setSendcontent(tempbody);
		    			conversation.setSendtime(temptime+"");
		    			conversation.setReceivecontent(body);
		    			conversation.setReceivetime(time+"");
		    			list.add(conversation);
	    			}
			    }
			    
			   
		 }
		 return  list;
	}


	public void updateSendSMS() {
		if (mLocalDataHelper == null) {
			mLocalDataHelper = new LocalDataHelper(context);
		}

		mLocalDataHelper.updateSendSMS(loadSendSMS());

	}

	private void copySendSMS() {
		Log.i(TAG, "copySendSMS is running");
		if (mLocalDataHelper == null) {
			mLocalDataHelper = new LocalDataHelper(context);
		}
		mLocalDataHelper.addSendSMS(loadSendSMS());
	}
}
