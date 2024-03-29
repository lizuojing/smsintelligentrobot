package org.app.intelligentrobot;

import org.app.intelligentrobot.data.SettingLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends Activity implements OnClickListener{
	private static final String TAG = "SettingActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		RelativeLayout item1 = (RelativeLayout)findViewById(R.id.relativeLayout1);
		RelativeLayout item2 = (RelativeLayout)findViewById(R.id.relativeLayout2);
		RelativeLayout item3 = (RelativeLayout)findViewById(R.id.relativeLayout3);
		RelativeLayout item4 = (RelativeLayout)findViewById(R.id.relativeLayout4);
		CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox1);
		checkBox.setChecked(SettingLoader.getKnown(this));
		item1.setOnClickListener(this);
		item2.setOnClickListener(this);
		item3.setOnClickListener(this);
		item4.setOnClickListener(this);
		
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton checkbox, boolean known) {
				SettingLoader.setKnown(SettingActivity.this,known);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.relativeLayout1:
			Log.i(TAG, "relativeLayout1 is running");
			break;
		case R.id.relativeLayout2:
			Intent defaultIntent = new Intent(this,DefaultSMSActivity.class);
			startActivity(defaultIntent);
			break;
		case R.id.relativeLayout3:
			Intent dimIntent = new Intent(this,DIMSMSActivity.class);
			startActivity(dimIntent);
			break;
		case R.id.relativeLayout4:
			Intent keywordIntent = new Intent(this,KeyWordSetActivity.class);
			startActivity(keywordIntent);
			break;

		default:
			break;
		}
		
	}
}
