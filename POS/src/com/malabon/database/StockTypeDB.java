package com.malabon.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.malabon.object.StockType;

public class StockTypeDB {
	public static final String TABLE_STOCK_TYPE = "stock_type";

	public static final String KEY_STOCK_TYPE_ID = "stock_type_id";
	public static final String KEY_NAME = "name";

	private final ArrayList<StockType> stocktype_list = new ArrayList<StockType>();

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

	public StockTypeDB(Context ctx) {
		this.context = ctx;
	}

	public StockTypeDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public ArrayList<StockType> getAllStockTypes() {
		try {
			stocktype_list.clear();
			String selectQuery = "SELECT * FROM " + TABLE_STOCK_TYPE;

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					StockType stockType = new StockType();
					stockType.stock_type_id = cursor.getInt(0);
					stockType.name = cursor.getString(1);

					stocktype_list.add(stockType);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getAllStockTypes - success");
		} catch (Exception e) {
			Log.e("pos_error", "get_allstocktypes: " + e);
		}
		return stocktype_list;
	}

	public int getStockTypeID(String stocktypename) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery("SELECT " + KEY_STOCK_TYPE_ID
					+ " FROM " + TABLE_STOCK_TYPE + " WHERE " + KEY_NAME
					+ " = '" + stocktypename + "' COLLATE NOCASE", null);

			if (cursor != null) {
				cursor.moveToFirst();
				num = cursor.getInt(0);
			}
			cursor.close();
			db.close();

			Log.d("pos", "getStockTypeID - success");
		} catch (Exception e) {
			Log.e("pos_error", "getStockTypeID: " + e);
		}
		return num;
	}

	public int addStockType(List<StockType> list) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (StockType s : list) {
				String query = "INSERT OR REPLACE INTO " + TABLE_STOCK_TYPE
						+ " ( " + KEY_STOCK_TYPE_ID + "," + KEY_NAME
						+ ") VALUES (" + s.stock_type_id + ", ?);";
				SQLiteStatement stmt = db.compileStatement(query);
				stmt.bindString(1, s.name);
				stmt.execute();
			}

			db.close();
			num = 1;
			Log.d("pos_sync", "addStockType - success");
		} catch (Exception e) {
			Log.e("pos_error", "addStockType: " + e);
		}
		return num;
	}

	// TODO: delete after testing
	// --------------------------------------------------------------------------

	public int getStockTypeCount() {
		int num = 0;
		try {
			String countQuery = "SELECT " + KEY_STOCK_TYPE_ID + " FROM "
					+ TABLE_STOCK_TYPE;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			num = cursor.getCount();

			cursor.close();
			db.close();
			Log.d("pos", "getStockTypeCount: " + String.valueOf(num));
		} catch (Exception e) {
			Log.e("pos_error", "getStockTypeCount: " + e);
		}
		return num;
	}

	public void tempAddStockType() {
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = null;

			values = new ContentValues();
			values.put(KEY_STOCK_TYPE_ID, 1);
			values.put(KEY_NAME, "ingredient");
			db.insert(TABLE_STOCK_TYPE, null, values);

			values = new ContentValues();
			values.put(KEY_STOCK_TYPE_ID, 2);
			values.put(KEY_NAME, "supply");
			db.insert(TABLE_STOCK_TYPE, null, values);

			db.close();
			Log.d("pos", "tempAddStockType - success");
		} catch (Exception e) {
			Log.e("pos_error", "tempAddStockType: " + e);
		}
	}
}
