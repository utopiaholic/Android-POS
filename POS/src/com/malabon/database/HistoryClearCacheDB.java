package com.malabon.database;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.malabon.object.ClearCacheHistory;

public class HistoryClearCacheDB {
	public static final String TABLE_HISTORY_CLEAR_CACHE = "history_clear_cache";

	public static final String KEY_ID = "ID";
	public static final String KEY_DATE = "DATE";
	public static final String KEY_USER_ID = "USER_ID";
	public static final String KEY_IS_SYNCED = "is_synced";

	private DatabaseHelper DbHelper;
	private SQLiteDatabase db;
	private final Context context;

	Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final ArrayList<ClearCacheHistory> clearcachehistory_list = new ArrayList<ClearCacheHistory>();

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

	public HistoryClearCacheDB(Context ctx) {
		this.context = ctx;
	}

	public HistoryClearCacheDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public int addHistoryClearCache(ClearCacheHistory clearCacheHistory) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_DATE,
					formatter.format(clearCacheHistory.StrClearDate));
			values.put(KEY_USER_ID, clearCacheHistory.UserId);
			values.put(KEY_IS_SYNCED, 0);

			db.insert(TABLE_HISTORY_CLEAR_CACHE, null, values);
			db.close();
			num = 1;
			Log.d("pos", "addHistoryClearCache - success");
		} catch (Exception e) {
			Log.e("pos_error", "addHistoryClearCache: " + e);
		}
		return num;
	}

	public ArrayList<ClearCacheHistory> getClearCacheHistory() {
		try {
			clearcachehistory_list.clear();
			String selectQuery = "SELECT * FROM " + TABLE_HISTORY_CLEAR_CACHE
					+ " ORDER BY " + KEY_DATE;

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					ClearCacheHistory clearCacheHistory = new ClearCacheHistory();
					clearCacheHistory.StrClearDate = cursor.getString(1);
					clearCacheHistory.UserId = cursor.getInt(2);

					clearcachehistory_list.add(clearCacheHistory);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getClearCacheHistory - success");
		} catch (Exception e) {
			Log.e("pos_error", "getClearCacheHistory: " + e);
		}
		return clearcachehistory_list;
	}
}
