package org.app.intelligentrobot.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingLoader {

	private static final String SETTING = "setting";
	private static final String KNOWN = "set_known";
	private static final String MODETYPE = "set_mode";

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
	public static int getModeType(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(SETTING,Context.MODE_PRIVATE);
		return preferences.getInt(MODETYPE, 0);
	}
}