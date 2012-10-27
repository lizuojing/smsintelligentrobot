package org.app.intelligentrobot;

import org.app.intelligentrobot.data.SettingLoader;
import org.app.intelligentrobot.utils.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DefaultSMSActivity extends Activity {
	
	private EditText defaultSmsEdit;
	private Button btn_save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.defaultsms);
		defaultSmsEdit = (EditText)findViewById(R.id.editText1);
		defaultSmsEdit.setText(SettingLoader.getDefaultSMS(this));
		btn_save = (Button)findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String defaultSms = defaultSmsEdit.getText().toString();
				if(Utils.isNullOrEmpty(defaultSms)) {
					Toast.makeText(DefaultSMSActivity.this, "没有输入任何内容哦！", Toast.LENGTH_SHORT).show();
					return;
				}
				Log.i("defaultSms", "defaultSms is " + defaultSms);
				SettingLoader.setDefaultSMS(DefaultSMSActivity.this,defaultSms);
				finish();
			}

		});
	}
	

}
