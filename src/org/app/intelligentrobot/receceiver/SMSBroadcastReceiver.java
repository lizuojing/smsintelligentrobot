package org.app.intelligentrobot.receceiver;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.app.intelligentrobot.MainActivity;
import org.app.intelligentrobot.SMSApp;
import org.app.intelligentrobot.data.LocalDataHelper;
import org.app.intelligentrobot.data.SettingLoader;
import org.app.intelligentrobot.entity.Conversation;
import org.app.intelligentrobot.entity.CosineSimilarAlgorithm;
import org.app.intelligentrobot.utils.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "SMSBroadcastReceiver";
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");// 获取短信内容
		Log.i(TAG, "onReceive is running");
		this.context = context;
		for (Object pdu : pdus) {
			byte[] data = (byte[]) pdu;// 获取单条短信内容，短信内容以pdu格式存在
			SmsMessage message = SmsMessage.createFromPdu(data);// 使用pdu格式的短信数据生成短信对象
			String sender = message.getOriginatingAddress();// 获取短信的发送者
			String content = message.getMessageBody();// 获取短信的内容
			Date date = new Date(message.getTimestampMillis());

			Toast.makeText(context, "收到" + sender + "的短信", Toast.LENGTH_SHORT)
					.show();
			if (MainActivity.STATE == MainActivity.SUBSTITUTE) {
				Log.e("替身模式生效.", "yes");
				Utils.sendSMS(context, sender, findTheAnswer(content));
			}
		}
	}

	private String findTheAnswer(String content) {

		String answer = "";
		if (SettingLoader.getKnown(context)) {
			answer = "[替身机器人帮我回复：]";
		}
		String keywordAnswer = LocalDataHelper.loadAnswer(content);
		if (keywordAnswer != null) {
			Log.e("关键字里查找的结果：", "::" + keywordAnswer);
			answer = answer + keywordAnswer;
		} else {
			String messageAnswer = getAnswerFromMessage(content);
			if (messageAnswer != null) {
				Log.e("收件箱里匹配的：", "::" + messageAnswer);
				answer = answer + messageAnswer;
			} else {
				ArrayList<String> strings = LocalDataHelper.loadDimList();
				int readomWordIndex = (int) (Math.random() * strings.size());
				String hanhuMessage = strings.get(readomWordIndex);
				Log.e("含糊短信:", "::" + hanhuMessage);
				answer = answer + hanhuMessage;
			}

		}
		Log.e("answer:", "" + answer);
		return answer;
	}

	private String getAnswerFromMessage(String content) {
		Conversation temp = null;
		double maxRate = 0;
		ArrayList<Conversation> messageAnswer = SMSApp.getApp(context)
				.getService().getList();
		for (Conversation conversation : messageAnswer) {
			String a = conversation.getReceivecontent();
			double rate = isSameRate(content, a);
			if (rate >= 0.5d && rate > maxRate) {
				maxRate = rate;
				temp = conversation;
			}
		}
		if (temp == null) {
			return null;
		} else {
			Log.e("收件箱里较为相似的短信：", temp.getReceivecontent() + "");
			return temp.getSendcontent();
		}
	}

	private double isSameRate(String s1, String s2) {
		return CosineSimilarAlgorithm.getSimilarity(s1, s2);

	}

}
