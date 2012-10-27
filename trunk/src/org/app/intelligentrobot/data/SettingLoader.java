package org.app.intelligentrobot.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingLoader {

	private static final String SETTING = "setting";
	private static final String KNOWN = "set_known";
	private static final String MODETYPE = "set_mode";
	private static final String DEFAULTSMS = "set_default";

	public static void setKnown(Context context, boolean known) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING,Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putBoolean(KNOWN, known);
		edit.commit();
	}

	public static boolean getKnown(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING,Context.MODE_PRIVATE);
		return preferences.getBoolean(KNOWN, false);
	}

	public static void setModeType(Context context, int mode) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING,Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putInt(MODETYPE, mode);
		edit.commit();
	}
	/**
	 * -1代表没有设置任何模式  
	 * 0代表学习模式  
	 * 1代表替身模式 
	 * @param context
	 * @return
	 */
	public static int getModeType(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING,Context.MODE_PRIVATE);
		return preferences.getInt(MODETYPE, -1);
	}

	public static void setDefaultSMS(Context context,String defaultSms) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING,Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(DEFAULTSMS, defaultSms);
		edit.commit();
	}
	
	public static String getDefaultSMS(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING,Context.MODE_PRIVATE);
		return preferences.getString(DEFAULTSMS,null);
	}
}
