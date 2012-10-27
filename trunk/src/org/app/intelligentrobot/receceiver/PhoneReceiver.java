package org.app.intelligentrobot.receceiver;

import java.lang.reflect.Method;

import org.app.intelligentrobot.MainActivity;
import org.app.intelligentrobot.data.SettingLoader;
import org.app.intelligentrobot.utils.Utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

public class PhoneReceiver extends BroadcastReceiver {

	public static final String TAG = "PhoneReceiver";
	public Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context = context;
		System.out.println("action" + intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			// 如果是去电（拨出）
			;
		} else {
			// 查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电

			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);

			tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

			// 设置一个监听器
		}
	}

	PhoneStateListener listener = new PhoneStateListener() {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				if (MainActivity.STATE == MainActivity.SUBSTITUTE) {
					if (endCall(context)) {
						Utils.sendSMS(context, incomingNumber,
								SettingLoader.getDefaultSMS(context));
					}
				}
				;
				break;
			}
		}

	};

	public static boolean endCall(Context cx) { // 挂断电话
		TelephonyManager telMag = (TelephonyManager) cx
				.getSystemService(Context.TELEPHONY_SERVICE);
		Class<TelephonyManager> c = TelephonyManager.class;
		Method mthEndCall = null;
		boolean is;
		try {
			mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
			mthEndCall.setAccessible(true);
			ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag,
					(Object[]) null);
			is = iTel.endCall();
			Toast.makeText(cx, "当前是替身模式，机器人会自动挂断电话，并给他发条消息。",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			is = false;
		}
		return is;

	}

	private void answerCall(Context cx) { // 接听电话
		TelephonyManager telMag = (TelephonyManager) cx
				.getSystemService(Context.TELEPHONY_SERVICE);
		Class<TelephonyManager> c = TelephonyManager.class;
		Method mthEndCall = null;
		try {
			mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
			mthEndCall.setAccessible(true);
			ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag,
					(Object[]) null);
			iTel.answerRingingCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}