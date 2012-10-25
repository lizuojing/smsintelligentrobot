package org.app.intelligentrobot.service;

import java.util.ArrayList;

import org.app.intelligentrobot.SMSApp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
	private static ArrayList<ServiceHandler> mServiceHandlers = new ArrayList<ServiceHandler>();
	public static Context context;

	public static abstract class ServiceHandler extends Handler {
		public static final int ID_Message_STATE = 2012;

		public void handleMessage(Message msg) {

			Log.i("ServiceHandler", "handleMessage, msg.what:" + msg.what
					+ " msg.obj:" + msg.obj + " msg.arg1:" + msg.arg1
					+ "msg.arg2:" + msg.arg2);

			super.handleMessage(msg);

			switch (msg.what) {
			case ID_Message_STATE:
				onProcessState(msg);
				break;
			default:
				defaultMessageProcess(msg);
				break;
			}
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

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		context = SMSApp.getAppContext();
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "onStart is running");
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
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

}
