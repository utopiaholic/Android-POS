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

public class PaymentDB {
	public static final String TABLE_PAYMENT = "payment";
	
	public static final String KEY_PAYMENT_ID = "payment_id";
	public static final String KEY_SALES_ID = "sales_id";
	public static final String KEY_TOTAL_NET = "total_net";
	public static final String KEY_TOTAL_TAX = "total_tax";
	public static final String KEY_TOTAL_DISCOUNT = "total_discount";
	public static final String KEY_RECEIPT_ID = "receipt_id";
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

	public PaymentDB(Context ctx) {
		this.context = ctx;
	}

	public PaymentDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public void addPayment(int salesid, int userid,
			double totalnet, double totaltax, double totaldiscount) {
		try {
			String receiptid = new NewID().GetSalesSummaryID(
					String.valueOf(userid), String.valueOf(salesid));

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_SALES_ID, salesid);
			values.put(KEY_TOTAL_NET, totalnet);
			values.put(KEY_TOTAL_TAX, totaltax);
			values.put(KEY_TOTAL_DISCOUNT, totaldiscount);
			values.put(KEY_RECEIPT_ID, receiptid);
			values.put(KEY_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_PAYMENT, null, values);

			db.close();
			Log.d("pos", "addPayment - success");
		} catch (Exception e) {
			Log.e("pos_error", "addPayment: " + e);
		}
	}

	public ArrayList<Sale> getRowsForPush() {
		ArrayList<Sale> sync_payment_list = new ArrayList<Sale>();

		try {
			String selectQuery = "SELECT * FROM " + TABLE_PAYMENT + " WHERE "
					+ KEY_IS_SYNCED + " = 0";

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Sale sale = new Sale();
					sale.payment_id = NewID.GetStringID(cursor.getInt(0));
					sale.sales_id = NewID.GetStringID(cursor.getInt(1));
					sale.netTotal = cursor.getDouble(2);
					sale.taxTotal = cursor.getDouble(3);
					sale.discount_amount = cursor.getDouble(4);
					sale.receipt_id = cursor.getString(5);
					sale.strDate = cursor.getString(6);

					sync_payment_list.add(sale);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getRowsForPush PaymentDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "getRowsForPush PaymentDB: " + e);
		}

		return sync_payment_list;
	}

	public int updateIsSynced(List<Integer> ids) {
		int num = 0;

		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (int id : ids) {
				ContentValues values = new ContentValues();
				values.put(KEY_IS_SYNCED, 1);

				String[] args = new String[] { String.valueOf(id) };
				num = db.update(TABLE_PAYMENT, values, KEY_PAYMENT_ID + " =?",
						args);
			}
			db.close();
			Log.d("pos", "updateIsSynced PaymentDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "updateIsSynced PaymentDB: " + e);
		}
		return num;
	}
}
