package org.app.intelligentrobot.data;

import java.util.ArrayList;
import java.util.List;

import org.app.intelligentrobot.entity.Conversation;

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

	// sql for create SMS receive table
	private static final String CREATE_LOCAL_SMSRECEIVE_TABLE = "CREATE TABLE "
			+ DB_Local_SMS_Table + " (" + KEY_Local_SMS_ID
			+ " INTEGER PRIMARY KEY," + KEY_Local_SMS_CONTENT + " TEXT,"
			+ KEY_Local_SMS_RECEIVEPHONE + " TEXT," + KEY_Local_SMS_RECEIVETIME
			+ " TEXT )";

	// sql for create SMS send table
	private static final String CREATE_SMS_SEND_TABLE = "CREATE TABLE "
			+ DB_SMS_SEND_TABLE + " (" + DB_SMS_SEND_ID
			+ " INTEGER PRIMARY KEY,"
			+ KEY_SMS_SEND_SMSID + " INTEGER,"
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


	public void saveOrUpdateSendSMS(ArrayList<Conversation> list) {
		if (mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Log.i(TAG, "list size is " + (list!=null?list.size():0));
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
						String id = cv.getAsString(KEY_SMS_SEND_SMSID);
						Log.i(TAG, "smsid is " + id);
						if(smsExist(id)) {
							if (mSQLiteDatabase.update(DB_SMS_SEND_TABLE,cv,KEY_SMS_SEND_SMSID + "=?",new String[]{id}) != -1) {
								Log.i(TAG, "Update new record: Key:"+ cv.getAsString(KEY_SMS_SEND_NUMBER));
							} else {
								Log.i(TAG, "Error while insert new record :"+ cv.getAsString(KEY_SMS_SEND_NUMBER));

							}
						}else {
							if (mSQLiteDatabase.insert(DB_SMS_SEND_TABLE, null, cv) != -1) {
								Log.i(TAG, "Insert new record: Key:"+ cv.getAsString(KEY_SMS_SEND_NUMBER));
							} else {
								Log.i(TAG, "Error while insert new record :"+ cv.getAsString(KEY_SMS_SEND_NUMBER));

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

	private boolean smsExist(String id) {
		if(id==null) {
			return false;
		}
		 Cursor cursor = mSQLiteDatabase.query(DB_SMS_SEND_TABLE, null, KEY_SMS_SEND_SMSID+"=?", new String[]{id}, null, null, null);
		   
		   boolean result = false;
		   if( cursor!= null ) {
			   if( cursor.getCount() > 0 ) {
				   result = true;
			   }
			   cursor.close();
		   }
		   
		   return result;
	}

	public void insertOrUpdateKeywords(ArrayList<String> list) {
		if (mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Log.i(TAG, "list size is " + (list!=null?list.size():0));
		List<ContentValues> cvList = new ArrayList<ContentValues>();
		for (String keyword : list) {
			ContentValues cv = new ContentValues();
			cv.put(KEY_KEYWORDS_CONTENT, keyword);
			cvList.add(cv);
		}

		if (mSQLiteDatabase != null) {
			synchronized (mSQLiteDatabase) {

				mSQLiteDatabase.beginTransaction();
				try {
					for (int j = 0; j < cvList.size(); j++) {
						ContentValues cv = cvList.get(j);
						String keyword = cv.getAsString(KEY_KEYWORDS_CONTENT);
						if(keywordExist(keyword)) {
							Log.i(TAG, "keyword is exist");
//							if (mSQLiteDatabase.update(DB_KEYWORDS_TABLE,cv,KEY_KEYWORDS_CONTENT + "=?",new String[]{keyword}) != -1) {
//								Log.i(TAG, "Update new record: Key:"+ cv.getAsString(KEY_KEYWORDS_CONTENT));
//							} else {
//								Log.i(TAG, "Error while insert new record :"+ cv.getAsString(KEY_KEYWORDS_CONTENT));
//
//							}
						}else {
							if (mSQLiteDatabase.insert(DB_KEYWORDS_TABLE, null, cv) != -1) {
								Log.i(TAG, "keyword is " + cv.getAsString(KEY_KEYWORDS_CONTENT));
							} else {
								Log.i(TAG, "Error while insert new record :"+ cv.getAsString(KEY_KEYWORDS_CONTENT));

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

	private boolean keywordExist(String keyword) {
		if(keyword==null) {
			return false;
		}
		 Cursor cursor = mSQLiteDatabase.query(DB_KEYWORDS_TABLE, null, KEY_KEYWORDS_CONTENT+"=?", new String[]{keyword}, null, null, null);
		   
		   boolean result = false;
		   if( cursor!= null ) {
			   if( cursor.getCount() > 0 ) {
				   result = true;
			   }
			   cursor.close();
		   }
		   
		   return result;
	}
}
