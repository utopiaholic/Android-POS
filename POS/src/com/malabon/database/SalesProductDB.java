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
import com.malabon.object.Item;
import com.malabon.object.Sale;

public class SalesProductDB {
	public static final String TABLE_SALES_PRODUCT = "sales_product";

	public static final String KEY_ID = "id";
	public static final String KEY_SALES_ID = "sales_id";
	public static final String KEY_PRODUCT_ID = "product_id";
	public static final String KEY_QUANTITY = "quantity";
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

	public SalesProductDB(Context ctx) {
		this.context = ctx;
	}

	public SalesProductDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public void addSaleProduct(Sale sale, int id) {
		Log.d("temp_debug", "addSaleProduct...");
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (Item item : sale.items) {

				Log.d("temp_debug", String.valueOf(id));
				Log.d("temp_debug", String.valueOf(item.id));
				Log.d("temp_debug", String.valueOf(item.quantity));

				ContentValues values = new ContentValues();
				values.put(KEY_SALES_ID, id);
				values.put(KEY_PRODUCT_ID, item.id);
				values.put(KEY_QUANTITY, item.quantity);
				values.put(KEY_IS_SYNCED, 0);
				db.insert(TABLE_SALES_PRODUCT, null, values);
			}
			db.close();
			Log.d("pos", "addSaleProduct - success");
		} catch (Exception e) {
			Log.e("pos_error", "addSaleProduct: " + e);
		}
		Log.d("temp_debug", "addSaleProduct end...");
	}

	public ArrayList<Sale> getRowsForPush() {
		ArrayList<Sale> sync_sale_product_list = new ArrayList<Sale>();

		try {
			String selectQuery = "SELECT * FROM " + TABLE_SALES_PRODUCT
					+ " WHERE " + KEY_IS_SYNCED + " = 0";

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Sale sale = new Sale();
					sale.id = cursor.getInt(0);
					sale.sales_id = NewID.GetStringID(cursor.getInt(1));
					sale.product_id = cursor.getInt(2);
					sale.product_quantity = cursor.getInt(3);

					sync_sale_product_list.add(sale);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getRowsForPush SalesProductDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "getRowsForPush SalesProductDB: " + e);
		}

		return sync_sale_product_list;
	}

	public int updateIsSynced(List<Integer> ids) {
		int num = 0;

		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (int id : ids) {
				ContentValues values = new ContentValues();
				values.put(KEY_IS_SYNCED, 1);

				String[] args = new String[] { String.valueOf(id) };
				num = db.update(TABLE_SALES_PRODUCT, values, KEY_ID + " =?",
						args);
			}
			db.close();
			Log.d("pos", "updateIsSynced SalesProductDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "updateIsSynced SalesProductDB: " + e);
		}
		return num;
	}
}
