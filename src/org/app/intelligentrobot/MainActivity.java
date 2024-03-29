package org.app.intelligentrobot;

import org.app.intelligentrobot.data.SettingLoader;
import org.app.intelligentrobot.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	public static int STATE = -1;
	public static final int SUBSTITUTE = 0;
	public static final int LEARNING = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btn_set = (Button) findViewById(R.id.btn_setting);
		Button btn_learn = (Button) findViewById(R.id.btn_learn);
		Button btn_replace = (Button) findViewById(R.id.btn_replace);
		Button btn_exit = (Button) findViewById(R.id.btn_exit);
		btn_set.setOnClickListener(this);
		btn_learn.setOnClickListener(this);
		btn_replace.setOnClickListener(this);
		btn_exit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_setting:
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_learn:
			// 学习模式
			Utils.showNotification(this, R.drawable.notification, "学习模式",
					"机器人管家向您请教");
			SettingLoader.setModeType(this, SUBSTITUTE);// 0代表学习模式 1代表替身模式
			STATE = LEARNING;
			break;
		case R.id.btn_replace:
			// 替身模式
			Utils.showNotification(this, R.drawable.notification, "替身模式",
					"机器人管家为您服务");
			SettingLoader.setModeType(this, LEARNING);
			STATE = SUBSTITUTE;
			break;
		case R.id.btn_exit:
			SMSApp.getApp(this).stopDataService();
			finish();

			break;

		default:
			break;
		}
	}
}
