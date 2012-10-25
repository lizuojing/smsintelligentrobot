package org.app.intelligentrobot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btn_set = (Button) findViewById(R.id.button2);
		Button btn_learn = (Button) findViewById(R.id.button1);
		Button btn_replace = (Button) findViewById(R.id.button3);
		Button btn_exit = (Button) findViewById(R.id.button4);
		btn_set.setOnClickListener(this);
		btn_learn.setOnClickListener(this);
		btn_replace.setOnClickListener(this);
		btn_exit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.button2:
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.button1:

			break;
		case R.id.button3:

			break;
		case R.id.button4:
			SMSApp.getApp(this).stopDataService();
			finish();
			break;

		default:
			break;
		}
	}
}
