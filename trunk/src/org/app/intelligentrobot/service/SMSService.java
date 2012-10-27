package org.app.intelligentrobot.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.app.intelligentrobot.SMSApp;
import org.app.intelligentrobot.data.LocalDataHelper;
import org.app.intelligentrobot.entity.Conversation;
import org.app.intelligentrobot.receceiver.SmsObserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
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
	private static final String KEYWORDSPATH = "keywords.txt";
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
		// 初始化关键字词库
		// initKeyWords();
		// 拷贝已发送短信内容
		copySendSMS();
	}

	public void copySendSMS() {
		new AsyncTask<Void, Integer, Long>() {

			@Override
			protected Long doInBackground(Void... params) {
				Log.i(TAG, "copySendSMS is running");
				if (mLocalDataHelper == null) {
					mLocalDataHelper = new LocalDataHelper(context);
				}
				mLocalDataHelper.saveOrUpdateSendSMS(loadSendSMS());
				return null;
			}

		}.execute();

	}

	private void initKeyWords() {
		new AsyncTask<Void, Integer, Long>() {

			@Override
			protected Long doInBackground(Void... params) {
				Log.i(TAG, "copySendSMS is running");
				if (mLocalDataHelper == null) {
					mLocalDataHelper = new LocalDataHelper(context);
				}
				ArrayList<String> list = loadKeywords();
				if (list != null) {
					mLocalDataHelper.insertOrUpdateKeywords(list);
				}
				return null;
			}

		}.execute();

	}

	private ArrayList<String> loadKeywords() {

		ArrayList<String> list = null;
		AssetManager asset_manager = context.getAssets();

		InputStream fis = null;
		String line = null;
		try {
			fis = asset_manager.open(KEYWORDSPATH,
					AssetManager.ACCESS_STREAMING);
			BufferedReader bf = new BufferedReader(new InputStreamReader(fis,
					"UTF-8"));
			line = bf.readLine();
			while (null != line) {
				if (list == null) {
					list = new ArrayList<String>();
				}
				line = bf.readLine();
				if (null != line) {
					list.add(line);
				}
			}
			bf.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
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

	ArrayList<Conversation> list = null;

	public ArrayList<Conversation> getList() {
		if (list == null) {
			list = loadSendSMS();
		}
		return list;
	}

	public void setList(ArrayList<Conversation> list) {
		this.list = list;
	}

	private ArrayList<Conversation> loadSendSMS() {
		Cursor smsCursor = context.getContentResolver().query(
				SMS_URI,
				new String[] { "_id", "address", "person", "date", "read",
						"type", "body" }, " type = 1 ", null, " date ASC ");// 升序排列
		if (smsCursor != null) {
			int addressIndex = smsCursor.getColumnIndex("address");
			int bodyIndex = smsCursor.getColumnIndex("body");
			int typeIndex = smsCursor.getColumnIndex("type");
			int timeIndex = smsCursor.getColumnIndex("date");
			if (list == null) {
				list = new ArrayList<Conversation>();
			} else {
				list.clear();
			}
			for (smsCursor.moveToFirst(); !smsCursor.isAfterLast(); smsCursor
					.moveToNext()) {
				// 1.接收到的消息，2.发出去的消息
				String number = smsCursor.getString(addressIndex);
				String body = smsCursor.getString(bodyIndex);
				String type = smsCursor.getString(typeIndex);
				String time = smsCursor.getString(timeIndex);

				Cursor tempCursor = context.getContentResolver().query(
						SMS_URI,
						new String[] { "_id", "address", "person", "date",
								"read", "type", "body" },
						" address = " + number + " AND type = 2 AND date >"
								+ time, null, " date ASC ");// 降序排列
				if (tempCursor != null && tempCursor.getCount() > 0) {
					tempCursor.moveToFirst();
					int tempidIndex = tempCursor.getColumnIndex("_id");
					int tempaddressIndex = tempCursor.getColumnIndex("address");
					int tempbodyIndex = tempCursor.getColumnIndex("body");
					int temptypeIndex = tempCursor.getColumnIndex("type");
					int temptimeIndex = tempCursor
							.getColumnIndexOrThrow("date");
					int tempid = tempCursor.getInt(tempidIndex);
					String tempnumber = tempCursor.getString(tempaddressIndex);
					String tempbody = tempCursor.getString(tempbodyIndex);
					String temptype = tempCursor.getString(temptypeIndex);
					String temptime = tempCursor.getString(temptimeIndex);
					Conversation conversation = new Conversation();
					conversation.setSmsid(tempid);
					conversation.setPnum(number);
					conversation.setSendcontent(tempbody);
					conversation.setSendtime(temptime);
					conversation.setReceivecontent(body);
					conversation.setReceivetime(time + "");
					list.add(conversation);
				}
			}

		}
		return list;
	}

	public ArrayList<String> loadDim() {
		if (mLocalDataHelper == null) {
			mLocalDataHelper = new LocalDataHelper(context);
		}
		return mLocalDataHelper.loadDimList();
	}

	public void deleteDim(String content) {
		if (mLocalDataHelper == null) {
			mLocalDataHelper = new LocalDataHelper(context);
		}
	}

	public void saveDimSms(String dimcontent) {
		if (mLocalDataHelper == null) {
			mLocalDataHelper = new LocalDataHelper(context);
		}
	}

}
