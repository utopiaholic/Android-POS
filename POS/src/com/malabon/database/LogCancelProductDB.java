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

import com.malabon.object.CancelledOrder;

public class LogCancelProductDB {
	public static final String TABLE_LOG_CANCEL_PRODUCT = "log_cancel_product";

	public static final String KEY_ID = "id";
	public static final String KEY_DATE = "date";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_PRODUCT_ID = "product_id";
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

	public LogCancelProductDB(Context ctx) {
		this.context = ctx;
	}

	public LogCancelProductDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public int addLogCancelProduct(List<CancelledOrder> cancelledOrders) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (CancelledOrder order : cancelledOrders) {
				ContentValues values = new ContentValues();
				values.put(KEY_DATE, formatter.format(new Date()));
				values.put(KEY_USER_ID, order.UserId);
				values.put(KEY_PRODUCT_ID,
						String.valueOf(order.CancelledItem.id));
				values.put(KEY_IS_SYNCED, 0);
				db.insert(TABLE_LOG_CANCEL_PRODUCT, null, values);
			}
			db.close();
			num = 1;
			Log.d("pos", "addLogCancelProduct - success");
		} catch (Exception e) {
			Log.e("pos_error", "addLogCancelProduct: " + e);
		}
		return num;
	}

	public ArrayList<CancelledOrder> getRowsForPush() {
		ArrayList<CancelledOrder> log_cancel_product_list = new ArrayList<CancelledOrder>();

		try {
			String selectQuery = "SELECT * FROM " + TABLE_LOG_CANCEL_PRODUCT
					+ " WHERE " + KEY_IS_SYNCED + " = 0";

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					CancelledOrder cancelledOrder = new CancelledOrder();
					cancelledOrder.id = cursor.getInt(0);
					cancelledOrder.str_date = cursor.getString(1);
					cancelledOrder.UserId = cursor.getInt(2);
					cancelledOrder.product_id = cursor.getInt(3);
					log_cancel_product_list.add(cancelledOrder);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getRowsForPush LogCancelProductDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "getRowsForPush LogCancelProductDB: " + e);
		}

		return log_cancel_product_list;
	}

	public int updateIsSynced(List<Integer> ids) {
		int num = 0;

		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (int id : ids) {
				ContentValues values = new ContentValues();
				values.put(KEY_IS_SYNCED, 1);

				String[] args = new String[] { String.valueOf(id) };
				num = db.update(TABLE_LOG_CANCEL_PRODUCT, values, KEY_ID
						+ " =?", args);
			}
			db.close();
			Log.d("pos", "updateIsSynced LogCancelProductDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "updateIsSynced LogCancelProductDB: " + e);
		}
		return num;
	}

}
