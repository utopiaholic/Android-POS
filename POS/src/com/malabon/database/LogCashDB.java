package com.malabon.database;

import java.text.Format;
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

import com.malabon.object.CashInOut;
import com.malabon.object.Sync;

public class LogCashDB {
	public static final String TABLE_LOG_CASH = "log_cash";

	public static final String KEY_ID = "id";
	public static final String KEY_IS_CASH_IN = "is_cash_in";
	public static final String KEY_AMOUNT = "amount";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_DATE = "date";
	public static final String KEY_IS_SYNCED = "is_synced";

	private DatabaseHelper DbHelper;
	private SQLiteDatabase db;
	private final Context context;

	Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

	public LogCashDB(Context ctx) {
		this.context = ctx;
	}

	public LogCashDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public double getLogCashTotal(Date dateFrom, Date DateTo) {
		double amount = 0.0;
		try {
			String selectQuery = "SELECT " + "((SELECT SUM(" + KEY_AMOUNT
					+ ") FROM " + TABLE_LOG_CASH + " where " + KEY_IS_CASH_IN
					+ " == 1 " + "and " + KEY_DATE + " between "
					+ formatter.format(dateFrom) + " and "
					+ formatter.format(dateFrom) + ") " + " - "
					+ "(SELECT SUM(" + KEY_AMOUNT + ") FROM " + TABLE_LOG_CASH
					+ " where " + KEY_IS_CASH_IN + " == 0 " + "and " + KEY_DATE
					+ " between " + formatter.format(dateFrom) + " and "
					+ formatter.format(dateFrom) + ") ";
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor != null) {
				cursor.moveToFirst();
				amount = cursor.getDouble(0);
			}
			cursor.close();
			db.close();
			Log.d("pos", "getLogCashTotal - success");
		} catch (Exception e) {
			Log.e("pos_error", "getLogCashTotal: " + e);
		}
		return amount;
	}

	public int addLogCash(int iscashin, double amount, String description) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_IS_CASH_IN, iscashin);
			values.put(KEY_AMOUNT, amount);
			values.put(KEY_USER_ID, Sync.user.user_id);
			values.put(KEY_DESCRIPTION, description);
			values.put(KEY_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_LOG_CASH, null, values);

			db.close();
			Log.d("pos", "addLogCash - success");
			num = 1;
		} catch (Exception e) {
			Log.e("pos_error", "addLogCash: " + e);
		}
		return num;
	}

	public ArrayList<CashInOut> getRowsForPush() {
		ArrayList<CashInOut> sync_log_cash_list = new ArrayList<CashInOut>();

		try {
			String selectQuery = "SELECT * FROM " + TABLE_LOG_CASH + " WHERE "
					+ KEY_IS_SYNCED + " = 0";

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					CashInOut cashInOut = new CashInOut();
					cashInOut.id = cursor.getInt(0);
					cashInOut.is_cash_in = cursor.getInt(1) > 0;
					cashInOut.amount = cursor.getDouble(2);
					cashInOut.user_id = cursor.getInt(3);
					cashInOut.description = cursor.getString(4);
					cashInOut.str_date = cursor.getString(5);
					sync_log_cash_list.add(cashInOut);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getRowsForPush LogCashDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "getRowsForPush LogCashDB: " + e);
		}

		return sync_log_cash_list;
	}

	public int updateIsSynced(List<Integer> ids) {
		int num = 0;

		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (int id : ids) {
				ContentValues values = new ContentValues();
				values.put(KEY_IS_SYNCED, 1);

				String[] args = new String[] { String.valueOf(id) };
				num = db.update(TABLE_LOG_CASH, values, KEY_ID + " =?", args);
			}
			db.close();
			Log.d("pos", "updateIsSynced LogCashDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "updateIsSynced LogCashDB: " + e);
		}
		return num;
	}
}
