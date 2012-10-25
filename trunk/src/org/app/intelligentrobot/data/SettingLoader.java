package org.app.intelligentrobot.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingLoader {

	private static final String SETTING = "setting";
	private static final String KNOWN = "set_known";

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

}
