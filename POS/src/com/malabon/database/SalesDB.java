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

import com.malabon.function.NewID;
import com.malabon.object.Sale;

public class SalesDB {
	public static final String TABLE_SALES = "sales";

	public static final String KEY_SALES_ID = "sales_id";
	public static final String KEY_ORDER_TYPE_ID = "order_type_id";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_DATE = "date";
	public static final String KEY_IS_SYNCED = "is_synced";

	private DatabaseHelper DbHelper;
	private SQLiteDatabase db;
	private final Context context;

	private Sale sale;
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

	public SalesDB(Context ctx) {
		this.context = ctx;
	}

	public SalesDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public int newSale(Sale sale) {
		int num = 0;
		try {
			this.sale = sale;
			num = addSale();
			if (num > 0) {
				num = 0;
				int id = getMaxSaleID();
				addSaleProduct(id);
				if (!sale.customer.customer_id.equals(new NewID()
						.GetDefaultCustomerID()))
					addSaleCustomer(id);
				if (sale.discount != null)
					addSaleDiscount(id);
				addPayment(id);
				num = 1;
			}

		} catch (Exception e) {
			Log.e("pos_error", "newSale: " + e);
		}
		return num;
	}

	private int getMaxSaleID() {
		int num = 0;
		try {
			String countQuery = "SELECT MAX(" + KEY_SALES_ID
					+ ") AS maxID FROM " + TABLE_SALES;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);

			if (cursor != null) {
				cursor.moveToFirst();
				num = cursor.getInt(0);

				cursor.close();
			}
			db.close();
		} catch (Exception e) {
			Log.e("pos_error", "getMaxSaleID: " + e);
		}
		return num;
	}

	private int addSale() {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(KEY_ORDER_TYPE_ID, sale.orderType);
			values.put(KEY_USER_ID, sale.user);
			values.put(KEY_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_SALES, null, values);
			db.close();
			num = 1;
			Log.d("pos", "addSale - success");
		} catch (Exception e) {
			Log.e("pos_error", "addSale: " + e);
		}
		return num;
	}

	private void addSaleProduct(int id) {
		SalesProductDB salesProductDB = new SalesProductDB(context);
		salesProductDB.open();
		salesProductDB.addSaleProduct(sale, id);
	}

	private void addSaleCustomer(int id) {
		SalesCustomerDB salesCustomerDB = new SalesCustomerDB(context);
		salesCustomerDB.open();
		salesCustomerDB.addSaleCustomer(id, sale.customer.customer_id);
	}

	private void addSaleDiscount(int id) {
		SalesDiscountDB salesDiscountDB = new SalesDiscountDB(context);
		salesDiscountDB.open();
		salesDiscountDB.addSaleDiscount(id, sale.discount.discount_id);
	}

	private void addPayment(int id) {
		PaymentDB paymentDB = new PaymentDB(context);
		paymentDB.open();
		double totaldiscount;
		if (sale.discount != null)
			totaldiscount = sale.total * sale.discount.percentage;
		else
			totaldiscount = 0;
		paymentDB.addPayment(id, sale.user, sale.netTotal, sale.taxTotal, totaldiscount);
	}

	public ArrayList<Sale> getRowsForPush() {
		ArrayList<Sale> sync_sale_list = new ArrayList<Sale>();

		try {
			String selectQuery = "SELECT * FROM " + TABLE_SALES + " WHERE "
					+ KEY_IS_SYNCED + " = 0";

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Sale sale = new Sale();
					sale.sales_id = NewID.GetStringID(cursor.getInt(0));
					sale.orderType = cursor.getInt(1);
					sale.user = cursor.getInt(2);
					sale.strDate = cursor.getString(3);

					sync_sale_list.add(sale);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getRowsForPush SalesDB- success");
		} catch (Exception e) {
			Log.e("pos_error", "getRowsForPush SalesDB: " + e);
		}

		return sync_sale_list;
	}

	public int updateIsSynced(List<Integer> ids) {
		int num = 0;

		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (int id : ids) {
				ContentValues values = new ContentValues();
				values.put(KEY_IS_SYNCED, 1);

				String[] args = new String[] { String.valueOf(id) };
				num = db.update(TABLE_SALES, values, KEY_SALES_ID + " =?", args);
			}
			db.close();
			Log.d("pos", "updateIsSynced SalesDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "updateIsSynced SalesDB: " + e);
		}
		return num;
	}
}