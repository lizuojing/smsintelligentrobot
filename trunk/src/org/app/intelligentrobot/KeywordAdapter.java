package org.app.intelligentrobot;

import java.util.ArrayList;

import org.app.intelligentrobot.entity.AskKeyWordEntity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class KeywordAdapter extends BaseAdapter {
	public Context context;
	ArrayList<AskKeyWordEntity> list = new ArrayList<AskKeyWordEntity>();

	public KeywordAdapter(Context context, ArrayList<AskKeyWordEntity> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public AskKeyWordEntity getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		TextView tView = new TextView(context);
		tView.setText(getItem(arg0).toString());
		tView.setTextSize(12);
		tView.setPadding(10, 20, 10, 20);
		return tView;
	}

}
