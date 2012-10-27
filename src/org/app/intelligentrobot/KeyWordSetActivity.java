package org.app.intelligentrobot;

import java.util.ArrayList;

import org.app.intelligentrobot.data.LocalDataHelper;
import org.app.intelligentrobot.entity.AskKeyWordEntity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class KeyWordSetActivity extends Activity {
	private Button btn_create;
	private ListView listView;
	private KeywordAdapter adapter;
	private EditText question, answer;
	private ArrayList<AskKeyWordEntity> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyword);
		btn_create = (Button) findViewById(R.id.btn_create);
		question = (EditText) findViewById(R.id.question);
		answer = (EditText) findViewById(R.id.answer);
		btn_create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AskKeyWordEntity awe = new AskKeyWordEntity();
				String questions = question.getText().toString();
				String answers = answer.getText().toString();
				if (Utils.isNullOrEmpty(questions)
						|| Utils.isNullOrEmpty(answers)) {
					Toast.makeText(KeyWordSetActivity.this, "输入内容不完整。",
							Toast.LENGTH_SHORT).show();
					return;
				}
				awe.setQuestion(questions);
				awe.setAnswer(answers);
				if (list == null) {
					list = new ArrayList<AskKeyWordEntity>();
				}
				list.add(awe);

				LocalDataHelper.saveOrUpdateKeyword(awe.getQuestion(),
						awe.getAnswer());

				if (list != null && list.size() > 0) {
					adapter = new KeywordAdapter(KeyWordSetActivity.this, list);
					listView.setAdapter(adapter);
				}
				adapter.notifyDataSetChanged();
				question.setText("");
				answer.setText("");
			}
		});

		listView = (ListView) findViewById(R.id.listView1);
		listView.setCacheColorHint(Color.TRANSPARENT);
		list = LocalDataHelper.loadKeyword();
		if (list != null && list.size() > 0) {
			adapter = new KeywordAdapter(KeyWordSetActivity.this, list);
			// ListAdapter adapter = new ListAdapter(this,loadDim());
			listView.setAdapter(adapter);
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				Log.i("aa", "================");
				new AlertDialog.Builder(KeyWordSetActivity.this)
						.setTitle("删除该对应关键字嘛。")
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										AskKeyWordEntity content = list
												.get(arg2);

										LocalDataHelper.deleteKeyword(content
												.getQuestion());
										list.remove(arg2);
										adapter.notifyDataSetChanged();

									}
								})
						.setNegativeButton("取消",
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

}
