package org.app.intelligentrobot;

import org.app.intelligentrobot.service.SMSService;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class SMSApp extends Application {
	private static final String TAG = "TelePhoneApp";
	private static Context mContext;
    private SMSService teleService;
    
	
	  private ServiceConnection mConnection = new ServiceConnection() 
	    {

			public void onServiceConnected(ComponentName className, IBinder service) 
	        {
				teleService = ((SMSService.LocalBinder)service).getService();
				
	        }

	        public void onServiceDisconnected(ComponentName className) 
	        {
	        	teleService = null;
	        }
	    };

	@Override
	public void onCreate() 
	{
		super.onCreate();
		Log.i(TAG, "App onCreate");
		mContext = this.getApplicationContext();
		
		startService(new Intent(this, SMSService.class));
		bindService(new Intent(this, SMSService.class), mConnection, Context.BIND_AUTO_CREATE);
		
	}
	
	public static Context getAppContext() {
		return mContext;
	}
	
	public SMSService getService()
	{
		return teleService;
	}
	
	public static SMSApp getApp(Context context)
	{
		return (SMSApp)context.getApplicationContext();
	}
	
	@Override
	public void onTerminate() 
	{
		stopDataService();
		
		super.onTerminate();
	}
	
	public void stopDataService()
	{
		if (mConnection != null)
		{
			Log.i(TAG, "stopservice");
			unbindService(mConnection);
			mConnection = null;
			stopService(new Intent(this, SMSService.class));
		}
	}
}
