package org.app.intelligentrobot.data;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.app.intelligentrobot.entity.AskKeyWordEntity;
import org.app.intelligentrobot.entity.Conversation;
import org.app.intelligentrobot.utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDataHelper {

	private static final String DB_NAME = "SMSApp.db";
	private static final int DB_VERSION = 1;

	private static Context mContext;
	private static DatabaseHelper mDatabaseHelper;
	private static SQLiteDatabase mSQLiteDatabase;

	// 收件信息表
	private static final String DB_Local_SMS_Table = "table_smsreceive";
	public static final String KEY_Local_SMS_ID = "smsid";
	public static final String KEY_Local_SMS_RECEIVEPHONE = "phone";
	public static final String KEY_Local_SMS_CONTENT = "content";
	public static final String KEY_Local_SMS_RECEIVETIME = "receivetime";

	// 发送信息表
	private static final String DB_SMS_SEND_TABLE = "table_smssend";
	private static final String DB_SMS_SEND_ID = "_id";
	public static final String KEY_SMS_SEND_SMSID = "smsid";
	public static final String KEY_SMS_SEND_NUMBER = "phone";
	public static final String KEY_SMS_SEND_SENDCONTENT = "sendcontent";
	public static final String KEY_SMS_SEND_SENDTIME = "sendtime";
	public static final String KEY_SMS_SEND_RECEIVECONTENT = "receivecontent";
	public static final String KEY_SMS_SEND_RECEIVETIME = "receivetime";

	// 关键词信息表
	private static final String DB_KEYWORDS_TABLE = "table_keyword";
	public static final String KEY_KEYWORDS_ID = "id";
	public static final String KEY_KEYWORDS_CONTENT = "content";
	public static final String KEY_KEYWORDS_ANSWERS = "answers";

	// 模糊表
	private static final String DB_DIM_TABLE = "table_dim";
	public static final String KEY_DIM_ID = "id";
	public static final String KEY_DIM_CONTENT = "content";

	// sql for create SMS receive table
	private static final String CREATE_LOCAL_SMSRECEIVE_TABLE = "CREATE TABLE "
			+ DB_Local_SMS_Table + " (" + KEY_Local_SMS_ID
			+ " INTEGER PRIMARY KEY," + KEY_Local_SMS_CONTENT + " TEXT,"
			+ KEY_Local_SMS_RECEIVEPHONE + " TEXT," + KEY_Local_SMS_RECEIVETIME
			+ " TEXT )";

	// sql for create SMS send table
	private static final String CREATE_SMS_SEND_TABLE = "CREATE TABLE "
			+ DB_SMS_SEND_TABLE + " (" + DB_SMS_SEND_ID
			+ " INTEGER PRIMARY KEY," + KEY_SMS_SEND_SMSID + " INTEGER,"
			+ KEY_SMS_SEND_SENDCONTENT + " TEXT," + KEY_SMS_SEND_SENDTIME
			+ " INTEGER," + KEY_SMS_SEND_RECEIVECONTENT + " TEXT,"
			+ KEY_SMS_SEND_RECEIVETIME + " INTEGER," + KEY_SMS_SEND_NUMBER
			+ " TEXT )";

	// sql for create KEYWORD table
	private static final String CREATE_KEYWORD_TABLE = "CREATE TABLE "
			+ DB_KEYWORDS_TABLE + " (" + KEY_KEYWORDS_ID
			+ " INTEGER PRIMARY KEY," + KEY_KEYWORDS_CONTENT + " TEXT,"
			+ KEY_KEYWORDS_ANSWERS + " TEXT)";

	// sql for create dim table
	private static final String CREATE_DIM_TABLE = "CREATE TABLE "
			+ DB_DIM_TABLE + " (" + KEY_DIM_ID + " INTEGER PRIMARY KEY,"
			+ KEY_DIM_CONTENT + " TEXT )";

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
			db.execSQL(CREATE_DIM_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

	public LocalDataHelper(Context context) {
		mContext = context;
	}

	public static void open() throws SQLException {
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

	public void saveOrUpdateSendSMS(ArrayList<Conversation> list) {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			open();
		}
		Log.i(TAG, "list size is " + (list != null ? list.size() : 0));
		List<ContentValues> cvList = new ArrayList<ContentValues>();
		for (Conversation sms : list) {
			ContentValues cv = new ContentValues();
			cv.put(KEY_SMS_SEND_SMSID, sms.getSmsid());
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
						String send = cv.getAsString(KEY_SMS_SEND_SENDCONTENT);
						Log.i(TAG, "smsid is " + send);
						if (smsExist(send)) {
							if (mSQLiteDatabase.update(DB_SMS_SEND_TABLE, cv,
									KEY_SMS_SEND_SENDCONTENT + "=?",
									new String[] { send }) != -1) {
								Log.i(TAG,
										"Update new record: Key:"
												+ cv.getAsString(KEY_SMS_SEND_NUMBER));
							} else {
								Log.i(TAG, "Error while insert new record :"
										+ cv.getAsString(KEY_SMS_SEND_NUMBER));

							}
						} else {
							if (mSQLiteDatabase.insert(DB_SMS_SEND_TABLE, null,
									cv) != -1) {
								Log.i(TAG,
										"Insert new record: Key:"
												+ cv.getAsString(KEY_SMS_SEND_NUMBER));
							} else {
								Log.i(TAG, "Error while insert new record :"
										+ cv.getAsString(KEY_SMS_SEND_NUMBER));

							}
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

	private boolean smsExist(String send) {
		if (send == null) {
			return false;
		}
		Cursor cursor = mSQLiteDatabase.query(DB_SMS_SEND_TABLE, null,
				KEY_SMS_SEND_SENDCONTENT + "=?", new String[] { send }, null,
				null, null);

		boolean result = false;
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				result = true;
			}
			cursor.close();
		}

		return result;
	}

	public void insertOrUpdateKeywords(ArrayList<AskKeyWordEntity> list) {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			open();
		}
		Log.i(TAG, "list size is " + (list != null ? list.size() : 0));
		List<ContentValues> cvList = new ArrayList<ContentValues>();
		for (AskKeyWordEntity entity : list) {
			ContentValues cv = new ContentValues();
			cv.put(KEY_KEYWORDS_CONTENT, entity.question);
			cv.put(KEY_KEYWORDS_ANSWERS, entity.answer);
			cvList.add(cv);
		}

		if (mSQLiteDatabase != null) {
			synchronized (mSQLiteDatabase) {

				mSQLiteDatabase.beginTransaction();
				try {
					for (int j = 0; j < cvList.size(); j++) {
						ContentValues cv = cvList.get(j);
						String keyword = cv.getAsString(KEY_KEYWORDS_CONTENT);
						if (keywordExist(keyword)) {
							Log.i(TAG, "keyword is exist");
							// if
							// (mSQLiteDatabase.update(DB_KEYWORDS_TABLE,cv,KEY_KEYWORDS_CONTENT
							// + "=?",new String[]{keyword}) != -1) {
							// Log.i(TAG, "Update new record: Key:"+
							// cv.getAsString(KEY_KEYWORDS_CONTENT));
							// } else {
							// Log.i(TAG, "Error while insert new record :"+
							// cv.getAsString(KEY_KEYWORDS_CONTENT));
							//
							// }
						} else {
							if (mSQLiteDatabase.insert(DB_KEYWORDS_TABLE, null,
									cv) != -1) {
								Log.i(TAG,
										"keyword is "
												+ cv.getAsString(KEY_KEYWORDS_CONTENT));
							} else {
								Log.i(TAG, "Error while insert new record :"
										+ cv.getAsString(KEY_KEYWORDS_CONTENT));

							}
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

	public static boolean keywordExist(String keyword) {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			open();
		}

		if (keyword == null) {
			return false;
		}
		Cursor cursor = mSQLiteDatabase.query(DB_KEYWORDS_TABLE, null,
				KEY_KEYWORDS_CONTENT + "=?", new String[] { keyword }, null,
				null, null);

		boolean result = false;
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				result = true;
			}
			cursor.close();
		}

		return result;
	}

	public static String findKeyword(String keyword) {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			open();
		}

		if (keyword == null) {
			return null;
		}
		Cursor cursor = mSQLiteDatabase.query(DB_KEYWORDS_TABLE, null,
				KEY_KEYWORDS_CONTENT + "=?", new String[] { keyword }, null,
				null, null);
		// TODO Auto-generated method stub
		return "返回一个关键值";
	}

	public static ArrayList<String> loadDimList() {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(DB_DIM_TABLE,
				new String[] { KEY_DIM_CONTENT }, null, null, null, null, null);
		Log.i(TAG, "count is " + cursor.getCount());
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		ArrayList<String> list = new ArrayList<String>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String dimsms = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DIM_CONTENT));
			Log.i(TAG, "dimsms is " + dimsms);
			if (dimsms != null) {
				list.add(dimsms);
			}
		}
		return list;
	}


	public void saveOrUpdateDimsms(String dimcontent) {
		ContentValues value = new ContentValues();
		value.put(KEY_DIM_CONTENT, dimcontent);
		if(dimExist(dimcontent)) {
//			mSQLiteDatabase.update(DB_DIM_TABLE, value, KEY_DIM_CONTENT+" =? ", new String[]{dimcontent});
			Log.i(TAG, "update content is " + dimcontent);
		}else {
			mSQLiteDatabase.insert(DB_DIM_TABLE, KEY_DIM_ID, value);
		}
	}

	private boolean dimExist(String dimcontent) {
		if (dimcontent == null) {
			return false;
		}
		Cursor cursor = mSQLiteDatabase.query(DB_DIM_TABLE, null,
				KEY_DIM_CONTENT + "=?", new String[] { dimcontent }, null, null,
				null);

		boolean result = false;
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				result = true;
			}
			cursor.close();
		}

		return result;
	}

	public void deleteDimsms(String content) {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			open();
		}
		if(Utils.isNullOrEmpty(content))  {
			return;
		}
		mSQLiteDatabase.delete(DB_DIM_TABLE, KEY_DIM_CONTENT+"=?", new String[]{content});
	}
	
	
	public void saveOrUpdateKeyword(String ask,String answer) {
		ContentValues value = new ContentValues();
		value.put(KEY_KEYWORDS_CONTENT, ask);
		value.put(KEY_KEYWORDS_ANSWERS, answer);
		if(keywordExist(ask)) {
//			mSQLiteDatabase.update(DB_DIM_TABLE, value, KEY_DIM_CONTENT+" =? ", new String[]{dimcontent});
			Log.i(TAG, "update ask is " + ask + " answer is " + answer);
		}else {
			mSQLiteDatabase.insert(DB_KEYWORDS_TABLE, KEY_KEYWORDS_ID, value);
		}
	}
	
	public ArrayList<AskKeyWordEntity> loadKeyword() {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(DB_KEYWORDS_TABLE,
				new String[] { KEY_KEYWORDS_CONTENT,KEY_KEYWORDS_ANSWERS }, null, null, null, null, null);
		Log.i(TAG, "count is " + cursor.getCount());
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		AskKeyWordEntity entity = null;
		ArrayList<AskKeyWordEntity> list = new ArrayList<AskKeyWordEntity>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String ask = cursor.getString(cursor.getColumnIndexOrThrow(KEY_KEYWORDS_CONTENT));
			String answer = cursor.getString(cursor.getColumnIndexOrThrow(KEY_KEYWORDS_ANSWERS));
			entity = new AskKeyWordEntity();
			entity.setQuestion(ask);
			entity.setAnswer(answer);
			list.add(entity);
		}
		return list;
	}
	
	public String loadAnswer(String ask) {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(DB_KEYWORDS_TABLE,
				new String[] { KEY_KEYWORDS_ANSWERS }, KEY_KEYWORDS_CONTENT+" =? ", new String[]{ask}, null, null, null);
		Log.i(TAG, "count is " + cursor.getCount());
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndexOrThrow(KEY_KEYWORDS_ANSWERS));
	
	}
	
	public void deleteKeyword(String ask) {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			open();
		}
		if(Utils.isNullOrEmpty(ask))  {
			return;
		}
		mSQLiteDatabase.delete(DB_KEYWORDS_TABLE, KEY_KEYWORDS_CONTENT+"=?", new String[]{ask});
	}

	public boolean dimExist() {
		Cursor cursor = mSQLiteDatabase.query(DB_DIM_TABLE, null,null,null, null, null,
				null);

		boolean result = false;
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				result = true;
			}
			cursor.close();
		}

		return result;
	}

	public void insertOrUpdateDim(ArrayList<String> list) {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			open();
		}
		Log.i(TAG, "list size is " + (list != null ? list.size() : 0));
		List<ContentValues> cvList = new ArrayList<ContentValues>();
		for (String keyword : list) {
			ContentValues cv = new ContentValues();
			cv.put(KEY_DIM_CONTENT, keyword);
			cvList.add(cv);
		}

		if (mSQLiteDatabase != null) {
			synchronized (mSQLiteDatabase) {

				mSQLiteDatabase.beginTransaction();
				try {
					for (int j = 0; j < cvList.size(); j++) {
						ContentValues cv = cvList.get(j);
						String keyword = cv.getAsString(KEY_DIM_CONTENT);
						if (keywordExist(keyword)) {
							Log.i(TAG, "keyword is exist");
						} else {
							if (mSQLiteDatabase.insert(DB_DIM_TABLE, null,
									cv) != -1) {
								Log.i(TAG,
										"keyword is "+ cv.getAsString(KEY_DIM_CONTENT));
							} else {
								Log.i(TAG, "Error while insert new record :"
										+ cv.getAsString(KEY_DIM_CONTENT));

							}
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
