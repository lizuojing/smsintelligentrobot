package org.app.intelligentrobot.data;

import java.util.ArrayList;
import java.util.List;

import org.app.intelligentrobot.entity.Conversation;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDataHelper {

	private static final String DB_NAME = "SMSApp.db";
	private static final int DB_VERSION = 1;

	private Context mContext;
	private DatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mSQLiteDatabase;

	// 收件信息表
	private static final String DB_Local_SMS_Table = "table_smsreceive";
	public static final String KEY_Local_SMS_ID = "smsid";
	public static final String KEY_Local_SMS_RECEIVEPHONE = "phone";
	public static final String KEY_Local_SMS_CONTENT = "content";
	public static final String KEY_Local_SMS_RECEIVETIME = "receivetime";

	// 发送信息表
	private static final String DB_SMS_SEND_TABLE = "table_smssend";
	public static final String KEY_SMS_SEND_NUMBER = "phone";
	public static final String KEY_SMS_SEND_SENDCONTENT = "sendcontent";
	public static final String KEY_SMS_SEND_SENDTIME = "sendtime";
	public static final String KEY_SMS_SEND_RECEIVECONTENT = "receivetime";
	public static final String KEY_SMS_SEND_RECEIVETIME = "receivetime";

	// 关键词信息表
	private static final String DB_KEYWORDS_TABLE = "table_keyword";
	public static final String KEY_KEYWORDS_ID = "id";
	public static final String KEY_KEYWORDS_CONTENT = "content";

	// sql for create SMS receive table
	private static final String CREATE_LOCAL_SMSRECEIVE_TABLE = "CREATE TABLE "
			+ DB_Local_SMS_Table + " (" + KEY_Local_SMS_ID
			+ " INTEGER PRIMARY KEY," + KEY_Local_SMS_CONTENT + " TEXT,"
			+ KEY_Local_SMS_RECEIVEPHONE + " TEXT," + KEY_Local_SMS_RECEIVETIME
			+ " TEXT )";

	// sql for create SMS send table
	private static final String CREATE_SMS_SEND_TABLE = "CREATE TABLE "
			+ DB_SMS_SEND_TABLE + " (" + KEY_KEYWORDS_ID
			+ " INTEGER PRIMARY KEY,"
			+ KEY_SMS_SEND_SENDCONTENT + " TEXT,"
			+ KEY_SMS_SEND_SENDTIME + " INTEGER,"
			+ KEY_SMS_SEND_RECEIVECONTENT + " TEXT,"
			+ KEY_SMS_SEND_RECEIVETIME + " INTEGER,"
			+ KEY_SMS_SEND_NUMBER + " TEXT )";

	// sql for create SMS table
	private static final String CREATE_KEYWORD_TABLE = "CREATE TABLE "
			+ DB_KEYWORDS_TABLE + " (" + KEY_KEYWORDS_ID
			+ " INTEGER PRIMARY KEY," + KEY_KEYWORDS_CONTENT + " TEXT )";
	private static final String TAG = "LocalDataHelper";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_LOCAL_SMSRECEIVE_TABLE);
			db.execSQL(CREATE_SMS_SEND_TABLE);
			db.execSQL(CREATE_KEYWORD_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

	public LocalDataHelper(Context context) {
		mContext = context;
	}

	public void open() throws SQLException {
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	public void close() {
		mDatabaseHelper.close();
	}

	public void saveReceiveSMS(String sender, String content, String sendtime) {
		if (mSQLiteDatabase == null) {
			return;
		}
		if (!mSQLiteDatabase.isOpen()) {
			open();
		}

		ContentValues values = new ContentValues();
		values.put(KEY_Local_SMS_RECEIVEPHONE, sender);
		values.put(KEY_Local_SMS_CONTENT, content);
		values.put(KEY_Local_SMS_RECEIVETIME, sendtime);
		long insert = mSQLiteDatabase.insert(DB_Local_SMS_Table,
				KEY_Local_SMS_ID, values);
		Log.i(TAG, "insert is " + insert);
	}

	public void updateSendSMS(ArrayList<Conversation> list) {
		// 删除数据
		mSQLiteDatabase.delete(DB_SMS_SEND_TABLE, null, null);
		addSendSMS(list);
	}

	public void addSendSMS(ArrayList<Conversation> list) {
		if (mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Log.i(TAG, "list size is " + (list!=null?list.size():0));
		List<ContentValues> cvList = new ArrayList<ContentValues>();
		for (Conversation sms : list) {
			ContentValues cv = new ContentValues();
			cv.put(KEY_SMS_SEND_NUMBER, sms.getPnum());
			cv.put(KEY_SMS_SEND_SENDCONTENT, sms.getSendcontent());
			cv.put(KEY_SMS_SEND_SENDTIME, sms.getSendtime());
			cv.put(KEY_SMS_SEND_RECEIVECONTENT, sms.getReceivecontent());
			cv.put(KEY_SMS_SEND_RECEIVETIME, sms.getReceivetime());
			cvList.add(cv);
		}

		if (mSQLiteDatabase != null) {
			synchronized (mSQLiteDatabase) {

				mSQLiteDatabase.beginTransaction();
				try {
					for (int j = 0; j < cvList.size(); j++) {
						ContentValues cv = cvList.get(j);
						if (mSQLiteDatabase.insert(DB_SMS_SEND_TABLE, null, cv) != -1) {
							Log.i(TAG, "Insert new record: Key:"+ cv.getAsString(KEY_SMS_SEND_NUMBER));
						} else {
							Log.i(TAG, "Error while insert new record :"+ cv.getAsString(KEY_SMS_SEND_NUMBER));

						}
					}
					mSQLiteDatabase.setTransactionSuccessful();
				} catch (RuntimeException e) {
					mSQLiteDatabase.endTransaction();
					throw e;
				}
				mSQLiteDatabase.endTransaction();
			}
		}
	}
}
