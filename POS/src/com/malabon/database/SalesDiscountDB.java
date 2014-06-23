package com.malabon.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.malabon.function.NewID;
import com.malabon.object.Sale;

public class SalesDiscountDB {
	public static final String TABLE_SALES_DISCOUNT = "sales_discount";

	public static final String KEY_ID = "id";
	public static final String KEY_SALES_ID = "sales_id";
	public static final String KEY_DISCOUNT_ID = "discount_id";
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

	public SalesDiscountDB(Context ctx) {
		this.context = ctx;
	}

	public SalesDiscountDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public void addSaleDiscount(int salesid, int discountid) {
		Log.d("temp_debug", "addSaleDiscount...");
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			Log.d("temp_debug", String.valueOf(salesid));
			Log.d("temp_debug", String.valueOf(discountid));

			ContentValues values = new ContentValues();
			values.put(KEY_SALES_ID, salesid);
			values.put(KEY_DISCOUNT_ID, discountid);
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_SALES_DISCOUNT, null, values);

			db.close();
			Log.d("pos", "addSaleDiscount - success");
		} catch (Exception e) {
			Log.e("pos_error", "addSaleDiscount: " + e);
		}
		Log.d("temp_debug", "addSaleDiscount end...");
	}

	public ArrayList<Sale> getRowsForPush() {
		ArrayList<Sale> sync_sale_discount_list = new ArrayList<Sale>();

		try {
			String selectQuery = "SELECT * FROM " + TABLE_SALES_DISCOUNT
					+ " WHERE " + KEY_IS_SYNCED + " = 0";

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Sale sale = new Sale();
					sale.id = cursor.getInt(0);
					sale.sales_id = NewID.GetStringID(cursor.getInt(1));
					sale.discount_id = cursor.getInt(2);

					sync_sale_discount_list.add(sale);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getRowsForPush SalesDiscountDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "getRowsForPush SalesDiscountDB: " + e);
		}

		return sync_sale_discount_list;
	}

	public int updateIsSynced(List<Integer> ids) {
		int num = 0;

		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (int id : ids) {
				ContentValues values = new ContentValues();
				values.put(KEY_IS_SYNCED, 1);

				String[] args = new String[] { String.valueOf(id) };
				num = db.update(TABLE_SALES_DISCOUNT, values, KEY_ID + " =?",
						args);
			}
			db.close();
			Log.d("pos", "updateIsSynced SalesDiscountDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "updateIsSynced SalesDiscountDB: " + e);
		}
		return num;
	}
}
