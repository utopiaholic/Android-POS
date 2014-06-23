package com.malabon.database;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.malabon.object.Sync;
import com.malabon.object.User;

public class UserDB {
	public static final String TABLE_USER = "user";

	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_IS_ADMIN = "is_admin";

	private DatabaseHelper DbHelper;
	private SQLiteDatabase db;
	private final Context context;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DBAdapter.DATABASE_NAME, null,
					DBAdapter.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	public UserDB(Context ctx) {
		this.context = ctx;
	}

	public UserDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public boolean validateLogin(String username, String password) {
		Boolean isvalid = false;
		try {
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER
					+ " WHERE " + KEY_USERNAME + " = '" + username + "'"
					+ " AND " + KEY_PASSWORD + " = '" + password + "'", null);

			if (cursor != null) {
				cursor.moveToFirst();
				Sync.SetUser(cursor.getInt(0), cursor.getString(1),
						cursor.getString(2));
				isvalid = true;

				Log.d("pos", "userid: " + String.valueOf(cursor.getInt(0)));
				Log.d("pos", "username: " + cursor.getString(1));
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			Log.e("pos_error", "validateLogin: " + e);
		}
		return isvalid;
	}

	public boolean validateAdmin(String username, String password) {
		boolean isadmin = false;
		try {
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER
					+ " WHERE (" + KEY_USERNAME + " = '" + username + "')"
					+ " AND (" + KEY_PASSWORD + " = '" + password + "')"
					+ " AND (" + KEY_IS_ADMIN + " = 1)", null);

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				isadmin = true;

				Log.d("pos", "isadmin: " + String.valueOf(isadmin));
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			Log.e("pos_error", "validateAdmin: " + e);
		}
		return isadmin;
	}

	public int adduser(List<User> list) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			int intBool = 0;

			for (User u : list) {
				if (u.is_admin)
					intBool = 1;
				else
					intBool = 0;

				String query = "INSERT OR REPLACE INTO " + TABLE_USER + " ( "
						+ KEY_USER_ID + ", " + KEY_USERNAME + " , "
						+ KEY_PASSWORD + " ," + KEY_IS_ADMIN + ") VALUES ("
						+ u.user_id + ", ? , ? ," + intBool + ");";
				SQLiteStatement stmt = db.compileStatement(query);
				stmt.bindString(1, u.username);
				stmt.bindString(2, u.password);
				stmt.execute();
			}

			db.close();
			num = 1;
			Log.d("pos_sync", "adduser - success");
		} catch (Exception e) {
			Log.e("pos_error", "adduser: " + e);
		}
		return num;
	}

	// TODO: delete after testing
	// --------------------------------------------------------------------------

	public int getUserCount() {
		int num = 0;
		try {
			String countQuery = "SELECT " + KEY_USER_ID + " FROM " + TABLE_USER;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			num = cursor.getCount();

			cursor.close();
			db.close();
			Log.d("pos", "getUsersCount: " + String.valueOf(num));
		} catch (Exception e) {
			Log.e("pos_error", "getUsersCount" + e);
		}
		return num;
	}

	public void tempAddUsers() {
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = null;

			values = new ContentValues();
			values.put(KEY_USER_ID, 7);
			values.put(KEY_USERNAME, "admin");
			values.put(KEY_PASSWORD, "@dmin");
			values.put(KEY_IS_ADMIN, 1);
			db.insert(TABLE_USER, null, values);

			values = new ContentValues();
			values.put(KEY_USER_ID, 2);
			values.put(KEY_USERNAME, "gela");
			values.put(KEY_PASSWORD, "pass");
			values.put(KEY_IS_ADMIN, 0);
			db.insert(TABLE_USER, null, values);

			values = new ContentValues();
			values.put(KEY_USER_ID, 3);
			values.put(KEY_USERNAME, "kate");
			values.put(KEY_PASSWORD, "pass");
			values.put(KEY_IS_ADMIN, 0);
			db.insert(TABLE_USER, null, values);

			db.close();
			Log.d("pos", "tempAddUsers - success");
		} catch (Exception e) {
			Log.e("pos_error", "tempAddUsers" + e);
		}
	}
}
