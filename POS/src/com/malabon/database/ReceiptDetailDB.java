package com.malabon.database;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.malabon.object.ReceiptDetail;

public class ReceiptDetailDB {
	public static final String TABLE_RECEIPT_DETAIL = "receipt_detail";

	public static final String KEY_RECEIPT_ID = "receipt_id";
	public static final String KEY_STORE_NAME = "store_name";
	public static final String KEY_OPERATED_BY = "operated_by";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_PERMIT_NO = "permit_no";
	public static final String KEY_TIN_NO = "tin_no";
	public static final String KEY_SERIAL_NO = "serial_no";
	public static final String KEY_ACCR_NO = "accr_no";
	public static final String KEY_MIN_NO = "min_no";
	public static final String KEY_MESSAGE = "message";

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

	public ReceiptDetailDB(Context ctx) {
		this.context = ctx;
	}

	public ReceiptDetailDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public ReceiptDetail getAllReceiptDetails() {
		ReceiptDetail receiptDetail = new ReceiptDetail();
		try {
			String selectQuery = "SELECT * FROM " + TABLE_RECEIPT_DETAIL
					+ " LIMIT 1";

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					receiptDetail.store_name = cursor.getString(1);
					receiptDetail.operated_by = cursor.getString(2);
					receiptDetail.address = cursor.getString(3);
					receiptDetail.permit_no = cursor.getString(4);
					receiptDetail.tin_no = cursor.getString(5);
					receiptDetail.serial_no = cursor.getString(6);
					receiptDetail.accr_no = cursor.getString(7);
					receiptDetail.min_no = cursor.getString(8);
					receiptDetail.message = cursor.getString(9);

				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getAllReceiptDetails - success");
		} catch (Exception e) {
			Log.e("pos_error", "getAllReceiptDetails: " + e);
		}
		return receiptDetail;
	}

	public int addAddReceiptDetail(List<ReceiptDetail> list) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (ReceiptDetail r : list) {
				String query = "INSERT OR REPLACE INTO " + TABLE_RECEIPT_DETAIL
						+ " ( " + KEY_STORE_NAME + "," + KEY_OPERATED_BY + ","
						+ KEY_ADDRESS + "," + KEY_PERMIT_NO + "," + KEY_TIN_NO
						+ "," + KEY_SERIAL_NO + "," + KEY_ACCR_NO + ","
						+ KEY_MIN_NO + "," + KEY_MESSAGE + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
				SQLiteStatement stmt = db.compileStatement(query);
				stmt.bindString(1, r.store_name);
				stmt.bindString(2, r.operated_by);
				stmt.bindString(3, r.address);
				stmt.bindString(4, r.permit_no);
				stmt.bindString(5, r.tin_no);
				stmt.bindString(6, r.serial_no);
				stmt.bindString(7, r.accr_no);
				stmt.bindString(8, r.min_no);
				stmt.bindString(9, r.message);
				stmt.execute();
			}

			db.close();
			num = 1;
			Log.d("pos_sync", "addAddReceiptDetail - success");
		} catch (Exception e) {
			Log.e("pos_error", "addAddReceiptDetail: " + e);
		}
		return num;
	}

	// TODO: delete after testing
	// --------------------------------------------------------------------------

	public int getReceiptDetailCount() {
		int num = 0;
		try {
			String countQuery = "SELECT " + KEY_RECEIPT_ID + " FROM "
					+ TABLE_RECEIPT_DETAIL;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			num = cursor.getCount();

			cursor.close();
			db.close();
			Log.d("pos", "getReceiptDetailCount: " + String.valueOf(num));
		} catch (Exception e) {
			Log.e("pos_error", "getReceiptDetailCount" + e);
		}
		return num;
	}

	public void tempAddReceiptDetails() {
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = null;

			values = new ContentValues();
			values.put(KEY_RECEIPT_ID, 3);
			values.put(KEY_STORE_NAME, "Pansit Malabon");
			values.put(KEY_OPERATED_BY, "Country Noodles");
			values.put(KEY_ADDRESS, "Daily Supermarket, Cubao, Quezon City");
			values.put(KEY_PERMIT_NO, "0516-027-839161-005");
			values.put(KEY_TIN_NO, "007-972-672-836");
			values.put(KEY_SERIAL_NO, "Z3THYL86");
			values.put(KEY_ACCR_NO, "43A-849362002-000382");
			values.put(KEY_MIN_NO, "826438227");
			values.put(KEY_MESSAGE, "Thank you. Come again.");
			db.insert(TABLE_RECEIPT_DETAIL, null, values);

			db.close();
			Log.d("pos", "tempAddReceiptDetails - success");
		} catch (Exception e) {
			Log.e("pos_error", "tempAddReceiptDetails: " + e);
		}
	}
}
