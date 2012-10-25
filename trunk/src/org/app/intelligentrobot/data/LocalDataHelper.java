package org.app.intelligentrobot.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDataHelper {

	private static final String DB_NAME = "SMSApp.db";
	private static final int DB_VERSION = 1;

	private Context mContext;
	private DatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mSQLiteDatabase;

	// 信息表
	private static final String DB_Local_SMS_Table = "table_sms";
	public static final String KEY_Local_SMS_ID = "smsid";
	public static final String KEY_Local_SMS_SENDER = "sender";
	public static final String KEY_Local_SMS_RECEIVER = "receiver";
	public static final String KEY_Local_SMS_SENDTIME = "sendtime";
	public static final String KEY_Local_SMS_RECEIVETIME = "receivetime";

	
	//sql for create SMS table
	private static final String CREATE_LOCAL_SMS_TABLE = "CREATE TABLE " + DB_Local_SMS_Table + 
														 		" (" + KEY_Local_SMS_ID + " INTEGER PRIMARY KEY," + 
														 		KEY_Local_SMS_SENDER + " TEXT," + 
														 		KEY_Local_SMS_RECEIVER + " TEXT," + 
														 		KEY_Local_SMS_SENDTIME + " INTEGER," + 
														 		KEY_Local_SMS_RECEIVETIME + " TEXT )";



	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			 db.execSQL(CREATE_LOCAL_SMS_TABLE);
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
	
	
	

}
