package com.malabon.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	public static final int DATABASE_VERSION = 155;
	public static final String DATABASE_NAME = "Android_POS1";
	private static DBTable table;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	private final Context context;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		this.DBHelper = new DatabaseHelper(this.context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				table = new DBTable();
				Log.d("pos", "creating tables");

				db.execSQL(table.get_TABLE_USER());
				db.execSQL(table.get_TABLE_USER_SALES_SUMMARY());
				db.execSQL(table.get_TABLE_LOG_USER_TIME_SHEET());

				db.execSQL(table.get_TABLE_PRODUCT_CATEGORY());
				db.execSQL(table.get_TABLE_PRODUCT());

				db.execSQL(table.get_TABLE_INGREDIENT());
				db.execSQL(table.get_TABLE_RECIPE());

				db.execSQL(table.get_TABLE_STOCK_TYPE());
				db.execSQL(table.get_TABLE_STOCK());

				db.execSQL(table.get_TABLE_LOG_CANCEL_PRODUCT());

				db.execSQL(table.get_TABLE_DISCOUNT());

				db.execSQL(table.get_TABLE_CUSTOMER());

				db.execSQL(table.get_TABLE_POS_SETTINGS());

				db.execSQL(table.get_TABLE_TABLE_RECEIPT_DETAIL());

				db.execSQL(table.get_TABLE_LOG_CASH());

				db.execSQL(table.get_TABLE_HISTORY_CLEAR_CACHE());
				db.execSQL(table.get_TABLE_HISTORY_SYNC());

				db.execSQL(table.get_TABLE_SALES());
				db.execSQL(table.get_TABLE_SALES_CUSTOMER());
				db.execSQL(table.get_TABLE_SALES_PRODUCT());
				db.execSQL(table.get_TABLE_SALES_DISCOUNT());
				db.execSQL(table.get_TABLE_PAYMENT());

				Log.d("pos", "tables created");

			} catch (Exception e) {
				Log.e("pos_error", "create_table: " + e);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + UserDB.TABLE_USER);
			db.execSQL("DROP TABLE IF EXISTS "
					+ LogUserTimeSheetDB.TABLE_LOG_USER_TIME_SHEET);
			db.execSQL("DROP TABLE IF EXISTS "
					+ UserSalesSummaryDB.TABLE_USER_SALES_SUMMARY);
			db.execSQL("DROP TABLE IF EXISTS " + ProductDB.TABLE_PRODUCT);
			db.execSQL("DROP TABLE IF EXISTS "
					+ ProductCategoryDB.TABLE_PRODUCT_CATEGORY);
			db.execSQL("DROP TABLE IF EXISTS " + IngredientDB.TABLE_INGREDIENT);
			db.execSQL("DROP TABLE IF EXISTS " + RecipeDB.TABLE_RECIPE);
			db.execSQL("DROP TABLE IF EXISTS " + StockTypeDB.TABLE_STOCK_TYPE);
			db.execSQL("DROP TABLE IF EXISTS " + StockDB.TABLE_STOCK);
			db.execSQL("DROP TABLE IF EXISTS "
					+ LogCancelProductDB.TABLE_LOG_CANCEL_PRODUCT);
			db.execSQL("DROP TABLE IF EXISTS " + DiscountDB.TABLE_DISCOUNT);
			db.execSQL("DROP TABLE IF EXISTS " + CustomerDB.TABLE_CUSTOMER);
			db.execSQL("DROP TABLE IF EXISTS "
					+ PosSettingsDB.TABLE_POS_SETTINGS);
			db.execSQL("DROP TABLE IF EXISTS "
					+ ReceiptDetailDB.TABLE_RECEIPT_DETAIL);
			db.execSQL("DROP TABLE IF EXISTS " + LogCashDB.TABLE_LOG_CASH);
			db.execSQL("DROP TABLE IF EXISTS "
					+ HistoryClearCacheDB.TABLE_HISTORY_CLEAR_CACHE);
			db.execSQL("DROP TABLE IF EXISTS "
					+ HistorySyncDB.TABLE_HISTORY_SYNC);
			db.execSQL("DROP TABLE IF EXISTS " + SalesDB.TABLE_SALES);
			db.execSQL("DROP TABLE IF EXISTS "
					+ SalesCustomerDB.TABLE_SALES_CUSTOMER);
			db.execSQL("DROP TABLE IF EXISTS "
					+ SalesProductDB.TABLE_SALES_PRODUCT);
			db.execSQL("DROP TABLE IF EXISTS "
					+ SalesDiscountDB.TABLE_SALES_DISCOUNT);
			db.execSQL("DROP TABLE IF EXISTS " + PaymentDB.TABLE_PAYMENT);
			onCreate(db);
		}
	}

	public DBAdapter open() throws SQLException {
		this.db = this.DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DBHelper.close();
	}
}