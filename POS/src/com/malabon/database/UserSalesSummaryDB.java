package com.malabon.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.malabon.function.NewID;
import com.malabon.object.UserSalesSummary;

public class UserSalesSummaryDB {
	public static final String TABLE_USER_SALES_SUMMARY = "user_sales_summary";

	public static final String KEY_SALES_SUMMARY_ID = "sales_summary_id";
	public static final String KEY_CASH_TOTAL = "cash_total";
	public static final String KEY_CASH_EXPECTED = "cash_expected";
	public static final String KEY_IS_SYNCED = "is_synced";

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

	public UserSalesSummaryDB(Context ctx) {
		this.context = ctx;
	}

	public UserSalesSummaryDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public String addUserSalesSummary(int user, double cash_total,
			double cash_expected) {
		String id = "";

		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();

			id = new NewID().GetSalesSummaryID(String.valueOf(user),
					new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			values.put(KEY_SALES_SUMMARY_ID, id);
			values.put(KEY_CASH_TOTAL, cash_total);
			values.put(KEY_CASH_EXPECTED, cash_expected);
			values.put(KEY_IS_SYNCED, 0);

			db.insert(TABLE_USER_SALES_SUMMARY, null, values);
			db.close();
			Log.d("pos", "addUserSalesSummary - success");
		} catch (Exception e) {
			Log.e("pos_error", "addUserSalesSummary: " + e);
		}
		return id;
	}

	public ArrayList<UserSalesSummary> getRowsForPush() {
		ArrayList<UserSalesSummary> sync_user_sales_summary_list = new ArrayList<UserSalesSummary>();

		try {
			String selectQuery = "SELECT * FROM " + TABLE_USER_SALES_SUMMARY
					+ " WHERE " + KEY_IS_SYNCED + " = 0";

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					UserSalesSummary userSalesSummary = new UserSalesSummary();
					userSalesSummary.sales_summary_id = cursor.getString(0);
					userSalesSummary.cash_total = cursor.getDouble(1);
					userSalesSummary.cash_expected = cursor.getDouble(2);

					sync_user_sales_summary_list.add(userSalesSummary);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getRowsForPush UserSalesSummaryDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "getRowsForPush UserSalesSummaryDB: " + e);
		}

		return sync_user_sales_summary_list;
	}

	public int updateIsSynced(List<String> ids) {
		int num = 0;

		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (String id : ids) {
				ContentValues values = new ContentValues();
				values.put(KEY_IS_SYNCED, 1);

				String[] args = new String[] { String.valueOf(id) };
				num = db.update(TABLE_USER_SALES_SUMMARY, values,
						KEY_SALES_SUMMARY_ID + " =?", args);
			}
			db.close();
			Log.d("pos", "updateIsSynced UserSalesSummaryDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "updateIsSynced UserSalesSummaryDB: " + e);
		}
		return num;
	}
}
