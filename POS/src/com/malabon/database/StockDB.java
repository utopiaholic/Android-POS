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
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.malabon.object.Stock;

public class StockDB {
	public static final String TABLE_STOCK = "stock";

	public static final String KEY_STOCK_ID = "stock_id";
	public static final String KEY_STOCK_TYPE_ID = "stock_type_id";
	public static final String KEY_ID = "id";
	public static final String KEY_QUANTITY = "quantity";
	public static final String KEY_LAST_UPDATED_DATE = "last_updated_date";
	public static final String KEY_LAST_UPDATED_USER_ID = "last_updated_user_id";
	public static final String KEY_IS_SYNCED = "is_synced";

	private final ArrayList<Stock> ingredient_stock_list = new ArrayList<Stock>();

	Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

	public StockDB(Context ctx) {
		this.context = ctx;
	}

	public StockDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public ArrayList<Stock> getAllPerishableStocks(String stocktypename) {
		try {
			int stocktypeid = 0;
			StockTypeDB stockTypeDB = new StockTypeDB(context);
			stockTypeDB.open();
			stocktypeid = stockTypeDB.getStockTypeID(stocktypename);

			ingredient_stock_list.clear();

			String selectQuery = "SELECT * FROM " + TABLE_STOCK + " WHERE "
					+ KEY_STOCK_TYPE_ID + " = '" + stocktypeid + "'";

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Stock stock = new Stock();
					// stock.stock_id = cursor.getInt(0);
					// stock.stock_type_id = cursor.getInt(1);
					stock.id = cursor.getInt(2);
					stock.quantity = cursor.getDouble(3);
					// stock.last_updated_date = (java.sql.Date) new
					// SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					// Locale.ENGLISH).parse(cursor.getString(4));
					// stock.last_updated_user_id = cursor.getInt(5);

					ingredient_stock_list.add(stock);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getAllPerishableStocks - success");
		} catch (Exception e) {
			Log.e("pos_error", "getAllPerishableStocks: " + e);
		}
		return ingredient_stock_list;
	}

	public int updateStock(String stocktypename, double quantity, int id,
			int userid) {
		int num = 0;
		int stocktypeid = 0;

		StockTypeDB stockTypeDB = new StockTypeDB(context);
		stockTypeDB.open();
		stocktypeid = stockTypeDB.getStockTypeID(stocktypename);

		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_QUANTITY, quantity);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_LAST_UPDATED_USER_ID, userid);
			values.put(KEY_IS_SYNCED, 0);

			String[] args = new String[] { String.valueOf(id),
					String.valueOf(stocktypeid) };
			num = db.update(TABLE_STOCK, values, KEY_ID + " =? AND "
					+ KEY_STOCK_TYPE_ID + " =? ", args);

			db.close();
			Log.d("pos", "updateStock - success");
			Log.d("pos", "updateStock: " + stocktypename + ": " + id);
		} catch (Exception e) {
			Log.e("pos_error", "updateStock: " + e);
		}
		return num;
	}

	public int addStock(List<Stock> list) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (Stock s : list) {
				String query = "INSERT OR REPLACE INTO " + TABLE_STOCK + " ( "
						+ KEY_STOCK_ID + "," + KEY_STOCK_TYPE_ID + "," + KEY_ID
						+ "," + KEY_QUANTITY + "," + KEY_LAST_UPDATED_DATE
						+ "," + KEY_LAST_UPDATED_USER_ID 
						+ "," + KEY_IS_SYNCED 
						+ ") VALUES ("
						+ s.stock_id + ", " + s.stock_type_id + " , " + s.id
						+ ", " + s.quantity + " , ?, " 
						+ s.last_updated_user_id + ", 1);";
				SQLiteStatement stmt = db.compileStatement(query);
				stmt.bindString(1, s.str_last_updated_date);
				stmt.execute();
			}

			db.close();
			num = 1;
			Log.d("pos_sync", "addStock - success");
		} catch (Exception e) {
			Log.e("pos_error", "addStock: " + e);
		}
		return num;
	}

	// TODO: delete after testing
	// --------------------------------------------------------------------------

	public int getStockCount() {
		int num = 0;
		try {
			String countQuery = "SELECT " + KEY_STOCK_ID + " FROM "
					+ TABLE_STOCK;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			num = cursor.getCount();

			cursor.close();
			db.close();
			Log.d("pos", "getStockCount: " + String.valueOf(num));
		} catch (Exception e) {
			Log.e("pos_error", "getStockCount: " + e);
		}
		return num;
	}

	public void tempAddStock() {
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = null;

			values = new ContentValues();
			values.put(KEY_STOCK_ID, 1);
			values.put(KEY_STOCK_TYPE_ID, 1);
			values.put(KEY_ID, 1);
			values.put(KEY_QUANTITY, 20);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_LAST_UPDATED_USER_ID, 1);
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_STOCK, null, values);

			values = new ContentValues();
			values.put(KEY_STOCK_ID, 2);
			values.put(KEY_STOCK_TYPE_ID, 1);
			values.put(KEY_ID, 2);
			values.put(KEY_QUANTITY, 10);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_LAST_UPDATED_USER_ID, 1);
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_STOCK, null, values);

			values = new ContentValues();
			values.put(KEY_STOCK_ID, 3);
			values.put(KEY_STOCK_TYPE_ID, 1);
			values.put(KEY_ID, 3);
			values.put(KEY_QUANTITY, 10);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_LAST_UPDATED_USER_ID, 1);
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_STOCK, null, values);

			values = new ContentValues();
			values.put(KEY_STOCK_ID, 4);
			values.put(KEY_STOCK_TYPE_ID, 1);
			values.put(KEY_ID, 4);
			values.put(KEY_QUANTITY, 10);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_LAST_UPDATED_USER_ID, 1);
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_STOCK, null, values);

			values = new ContentValues();
			values.put(KEY_STOCK_ID, 5);
			values.put(KEY_STOCK_TYPE_ID, 1);
			values.put(KEY_ID, 5);
			values.put(KEY_QUANTITY, 10);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_LAST_UPDATED_USER_ID, 1);
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_STOCK, null, values);

			values = new ContentValues();
			values.put(KEY_STOCK_ID, 6);
			values.put(KEY_STOCK_TYPE_ID, 1);
			values.put(KEY_ID, 6);
			values.put(KEY_QUANTITY, 10);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_LAST_UPDATED_USER_ID, 1);
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_STOCK, null, values);

			values = new ContentValues();
			values.put(KEY_STOCK_ID, 7);
			values.put(KEY_STOCK_TYPE_ID, 1);
			values.put(KEY_ID, 7);
			values.put(KEY_QUANTITY, 10);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_LAST_UPDATED_USER_ID, 1);
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_STOCK, null, values);

			db.close();
			Log.d("pos", "tempAddStock - success");
		} catch (Exception e) {
			Log.e("pos_error", "tempAddStock: " + e);
		}
	}
}
