package org.app.intelligentrobot;

import java.util.ArrayList;

import org.app.intelligentrobot.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DIMSMSActivity extends Activity {
	private static final String TAG = "DIMSMSActivity";
	private Button btn_create;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private EditText dimEdit;
	private ArrayList<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dimsms);
		btn_create = (Button) findViewById(R.id.btn_create);
		dimEdit = (EditText) findViewById(R.id.dimedit);
		btn_create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String dimcontent = dimEdit.getText().toString();
				if (Utils.isNullOrEmpty(dimcontent)) {
					Toast.makeText(DIMSMSActivity.this, "没有输入任何内容哦！",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (list == null) {
					list = new ArrayList<String>();
				}
				list.add(dimcontent);
				SMSApp.getApp(DIMSMSActivity.this).getService().saveDimSms(dimcontent);
				if (list != null && list.size() > 0) {
					adapter = new ArrayAdapter<String>(DIMSMSActivity.this,
							R.layout.list_item, list);
					listView.setAdapter(adapter);
				}
				adapter.notifyDataSetChanged();
				dimEdit.setText("");
			}
		});

		listView = (ListView) findViewById(R.id.listView1);
		listView.setCacheColorHint(Color.TRANSPARENT);
		list = loadDim();
		if (list != null && list.size() > 0) {
			adapter = new ArrayAdapter<String>(this,
					R.layout.list_item, list);
			// ListAdapter adapter = new ListAdapter(this,loadDim());
			listView.setAdapter(adapter);
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					long arg3) {
				Log.i("aa", "================");
				new AlertDialog.Builder(DIMSMSActivity.this)
						.setTitle("删除该模糊信息").setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String content = list.get(arg2);
										SMSApp.getApp(DIMSMSActivity.this).getService().deleteDim(content);
										list.remove(arg2);
										adapter.notifyDataSetChanged();

									}
								}).setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).show();
			}

		});
	}

	private ArrayList<String> loadDim() {
		return SMSApp.getApp(this).getService().loadDim();
	}

}
