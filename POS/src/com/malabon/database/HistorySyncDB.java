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

import com.malabon.object.SyncHistory;

public class HistorySyncDB {
	public static final String TABLE_HISTORY_SYNC = "history_sync";

	public static final String KEY_ID = "id";
	public static final String KEY_DATE = "date";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_IS_MANUAL = "is_manual";
	public static final String KEY_IS_SYNCED = "is_synced";

	private DatabaseHelper DbHelper;
	private SQLiteDatabase db;
	private final Context context;

	Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final ArrayList<SyncHistory> synchistory_list = new ArrayList<SyncHistory>();

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

	public HistorySyncDB(Context ctx) {
		this.context = ctx;
	}

	public HistorySyncDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public int addHistorySync(SyncHistory syncHistory) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_DATE, syncHistory.StrSyncDate);
			values.put(KEY_USER_ID, syncHistory.UserId);
			values.put(KEY_IS_MANUAL, syncHistory.IsManual);
			values.put(KEY_IS_SYNCED, 0);

			db.insert(TABLE_HISTORY_SYNC, null, values);
			db.close();
			num = 1;
			Log.d("pos", "addHistorySync - suceess");
		} catch (Exception e) {
			Log.e("pos_error", "addHistorySync: " + e);
		}
		return num;
	}

	public ArrayList<SyncHistory> getSyncHistory() {
		try {
			synchistory_list.clear();
			String selectQuery = "SELECT * FROM " + TABLE_HISTORY_SYNC
					+ " ORDER BY " + KEY_DATE;

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					SyncHistory syncHistory = new SyncHistory();
					syncHistory.StrSyncDate = cursor.getString(1);
					syncHistory.UserId = cursor.getInt(2);
					syncHistory.IsManual = cursor.getInt(3) > 0;

					synchistory_list.add(syncHistory);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getSyncHistory - success");
		} catch (Exception e) {
			Log.e("pos_error", "getSyncHistory: " + e);
		}
		return synchistory_list;
	}
}
