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

import com.malabon.function.NewID;
import com.malabon.object.Customer;

public class CustomerDB {
	public static final String TABLE_CUSTOMER = "customer";

	public static final String KEY_CUSTOMER_ID = "customer_id";
	public static final String KEY_FIRST_NAME = "first_name";
	public static final String KEY_LAST_NAME = "last_name";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_ADDRESS_LANDMARK = "address_landmark";
	public static final String KEY_TEL_NO = "tel_no";
	public static final String KEY_MOBILE_NO = "mobile_no";
	public static final String KEY_LAST_UPDATED_USER_ID = "last_updated_user_id";
	public static final String KEY_LAST_UPDATED_DATE = "last_updated_date";
	public static final String KEY_IS_SYNCED = "is_synced";

	private final ArrayList<Customer> customer_list = new ArrayList<Customer>();

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

	public CustomerDB(Context ctx) {
		this.context = ctx;
	}

	public CustomerDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public String addCustomer(Customer customer, int userid) {
		String id = null;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();

			id = new NewID().GetCustomerID(context);
			values.put(KEY_CUSTOMER_ID, id);
			values.put(KEY_FIRST_NAME, customer.first_name);
			values.put(KEY_LAST_NAME, customer.last_name);
			values.put(KEY_ADDRESS, customer.address);
			values.put(KEY_ADDRESS_LANDMARK, customer.address_landmark);
			values.put(KEY_TEL_NO, customer.tel_no);
			values.put(KEY_MOBILE_NO, customer.mobile_no);
			values.put(KEY_LAST_UPDATED_USER_ID, userid);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);

			db.insert(TABLE_CUSTOMER, null, values);
			db.close();
			Log.d("pos", "addCustomer - success");
		} catch (Exception e) {
			Log.e("pos_error", "addCustomer: " + e);
		}
		return id;
	}

	public ArrayList<Customer> getAllCustomers() {
		try {
			customer_list.clear();
			String selectQuery = "SELECT * FROM " + TABLE_CUSTOMER
					+ " ORDER BY " + KEY_LAST_NAME;

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Customer customer = new Customer();
					customer.customer_id = cursor.getString(0);
					customer.first_name = cursor.getString(1);
					customer.last_name = cursor.getString(2);
					customer.address = cursor.getString(3);
					customer.address_landmark = cursor.getString(4);
					customer.tel_no = cursor.getString(5);
					customer.mobile_no = cursor.getString(6);

					customer_list.add(customer);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getAllCustomers - success");
		} catch (Exception e) {
			Log.e("pos_error", "getAllCustomers: " + e);
		}
		return customer_list;
	}

	public int updateCustomer(Customer customer, int userid) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_FIRST_NAME, customer.first_name);
			values.put(KEY_LAST_NAME, customer.last_name);
			values.put(KEY_ADDRESS, customer.address);
			values.put(KEY_ADDRESS_LANDMARK, customer.address_landmark);
			values.put(KEY_TEL_NO, customer.tel_no);
			values.put(KEY_MOBILE_NO, customer.mobile_no);
			values.put(KEY_LAST_UPDATED_USER_ID, userid);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);

			num = db.update(TABLE_CUSTOMER, values, KEY_CUSTOMER_ID + " = ?",
					new String[] { String.valueOf(customer.customer_id) });
			db.close();
			Log.d("pos", "updateCustomer - success");
		} catch (Exception e) {
			Log.e("pos_error", "updateCustomer: " + e);
		}
		return num;
	}

	public int ifExistsTelNo(String telno) {
		int num = 0;
		try {
			String countQuery = "SELECT " + KEY_CUSTOMER_ID + " FROM "
					+ TABLE_CUSTOMER + " WHERE " + KEY_TEL_NO + " = " + telno;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			num = cursor.getCount();

			cursor.close();
			db.close();
			Log.d("pos", "ifExistsTelNo: " + String.valueOf(num));
		} catch (Exception e) {
			Log.e("pos_error", "ifExistsTelNo: " + e);
		}
		return num;
	}

	public int addCustomer(List<Customer> list) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (Customer c : list) {
				String query = "INSERT OR REPLACE INTO " + TABLE_CUSTOMER
						+ " ( " + KEY_CUSTOMER_ID + "," + KEY_FIRST_NAME + ","
						+ KEY_LAST_NAME + "," + KEY_ADDRESS + ","
						+ KEY_ADDRESS_LANDMARK + "," + KEY_TEL_NO + ","
						+ KEY_MOBILE_NO + "," + KEY_LAST_UPDATED_USER_ID + ","
						+ KEY_LAST_UPDATED_DATE + "," + KEY_IS_SYNCED
						+ ") VALUES (?,?,?,?,?,?,?, '" 
						+ c.last_updated_user_id
						+ "' ,?, 1);";
				SQLiteStatement stmt = db.compileStatement(query);
				stmt.bindString(1, c.customer_id);
				stmt.bindString(2, c.first_name);
				stmt.bindString(3, c.last_name);
				stmt.bindString(4, c.address);
				stmt.bindString(5, c.address_landmark);
				stmt.bindString(6, c.tel_no);
				stmt.bindString(7, c.mobile_no);
				stmt.bindString(8, c.str_last_updated_date);
				stmt.execute();
			}

			db.close();
			num = 1;
			Log.d("pos_sync", "addCustomer - success");
		} catch (Exception e) {
			Log.e("pos_error", "addCustomer: " + e);
		}
		return num;
	}

	public ArrayList<Customer> getRowsForPush() {
		ArrayList<Customer> sync_customer_list = new ArrayList<Customer>();

		try {
			String selectQuery = "SELECT * FROM " + TABLE_CUSTOMER + " WHERE "
					+ KEY_IS_SYNCED + " = 0";

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Customer customer = new Customer();
					customer.customer_id = cursor.getString(0);
					customer.first_name = cursor.getString(1);
					customer.last_name = cursor.getString(2);
					customer.address = cursor.getString(3);
					customer.address_landmark = cursor.getString(4);
					customer.tel_no = cursor.getString(5);
					customer.mobile_no = cursor.getString(6);
					customer.last_updated_user_id = cursor.getInt(7);
					customer.str_last_updated_date = cursor.getString(8);

					sync_customer_list.add(customer);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getRowsForPush CustomerDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "getRowsForPush CustomerDB: " + e);
		}

		return sync_customer_list;
	}

	public int updateIsSynced(List<String> ids) {
		int num = 0;

		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (String id : ids) {
				ContentValues values = new ContentValues();
				values.put(KEY_IS_SYNCED, 1);

				String[] args = new String[] { String.valueOf(id) };
				num = db.update(TABLE_CUSTOMER, values,
						KEY_CUSTOMER_ID + " =?", args);
			}
			db.close();
			Log.d("pos", "updateIsSynced CustomerDB - success");
		} catch (Exception e) {
			Log.e("pos_error", "updateIsSynced CustomerDB: " + e);
		}
		return num;
	}

	// TODO: delete after testing
	// --------------------------------------------------------------------------

	public int getCustomerCount() {
		int num = 0;
		try {
			String countQuery = "SELECT " + KEY_CUSTOMER_ID + " FROM "
					+ TABLE_CUSTOMER;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			num = cursor.getCount();

			cursor.close();
			db.close();
			Log.d("pos", "getCustomerCount: " + String.valueOf(num));
		} catch (Exception e) {
			Log.e("pos_error", "getCustomerCount: " + e);
		}
		return num;
	}

	public void tempAddCustomers() {
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = null;

			values = new ContentValues();
			values.put(KEY_CUSTOMER_ID, "1_1");
			values.put(KEY_FIRST_NAME, "Noble");
			values.put(KEY_LAST_NAME, "Hodge");
			values.put(KEY_ADDRESS, "Ap #708-5317 Arcu. St.");
			values.put(KEY_ADDRESS_LANDMARK, "Enim Mauris Quis LLC");
			values.put(KEY_TEL_NO, "09751846044");
			values.put(KEY_MOBILE_NO, "9089519");
			values.put(KEY_LAST_UPDATED_USER_ID, 7);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_CUSTOMER, null, values);

			values = new ContentValues();
			values.put(KEY_CUSTOMER_ID, "1_2");
			values.put(KEY_FIRST_NAME, "Jeremy");
			values.put(KEY_LAST_NAME, "Gibson");
			values.put(KEY_ADDRESS, "P.O. Box 402, 8731 Vitae, Street");
			values.put(KEY_ADDRESS_LANDMARK, "Ornare Tortor Institute");
			values.put(KEY_TEL_NO, "74634208");
			values.put(KEY_MOBILE_NO, "09734289950");
			values.put(KEY_LAST_UPDATED_USER_ID, 7);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_CUSTOMER, null, values);

			values = new ContentValues();
			values.put(KEY_CUSTOMER_ID, "2_1");
			values.put(KEY_FIRST_NAME, "Evan");
			values.put(KEY_LAST_NAME, "Mcdowell");
			values.put(KEY_ADDRESS, "487-7041 Neque St.");
			values.put(KEY_ADDRESS_LANDMARK, "In Faucibus Orci Industries");
			values.put(KEY_TEL_NO, "5225500");
			values.put(KEY_MOBILE_NO, "09096149581");
			values.put(KEY_LAST_UPDATED_USER_ID, 7);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_CUSTOMER, null, values);

			values = new ContentValues();
			values.put(KEY_CUSTOMER_ID, "1_3");
			values.put(KEY_FIRST_NAME, "Magee");
			values.put(KEY_LAST_NAME, "Merrill");
			values.put(KEY_ADDRESS, "2722 Diam Ave");
			values.put(KEY_ADDRESS_LANDMARK,
					"Sagittis Placerat Cras Associates");
			values.put(KEY_TEL_NO, "6484307");
			values.put(KEY_MOBILE_NO, "09456244540");
			values.put(KEY_LAST_UPDATED_USER_ID, 7);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_CUSTOMER, null, values);

			values = new ContentValues();
			values.put(KEY_CUSTOMER_ID, "1_4");
			values.put(KEY_FIRST_NAME, "Hilel");
			values.put(KEY_LAST_NAME, "Christensen");
			values.put(KEY_ADDRESS, "P.O. Box 325, 160 Et Rd.");
			values.put(KEY_ADDRESS_LANDMARK, "Adipiscing Elit Etiam Corp.");
			values.put(KEY_TEL_NO, "3499793");
			values.put(KEY_MOBILE_NO, "09740522018");
			values.put(KEY_LAST_UPDATED_USER_ID, 7);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_CUSTOMER, null, values);

			values = new ContentValues();
			values.put(KEY_CUSTOMER_ID, "2_2");
			values.put(KEY_FIRST_NAME, "Kaye");
			values.put(KEY_LAST_NAME, "Palmer");
			values.put(KEY_ADDRESS, "P.O. Box 365, 4958 Orci, Road");
			values.put(KEY_ADDRESS_LANDMARK, "Erat Eget Company");
			values.put(KEY_TEL_NO, "7812327");
			values.put(KEY_MOBILE_NO, "09274836213");
			values.put(KEY_LAST_UPDATED_USER_ID, 7);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_CUSTOMER, null, values);

			values = new ContentValues();
			values.put(KEY_CUSTOMER_ID, "1_5");
			values.put(KEY_FIRST_NAME, "Mark");
			values.put(KEY_LAST_NAME, "Foster");
			values.put(KEY_ADDRESS, "Ante Maecenas Mi Corporation");
			values.put(KEY_ADDRESS_LANDMARK, "P.O. Box 234, 6597 Mi Street");
			values.put(KEY_TEL_NO, "6267504");
			values.put(KEY_MOBILE_NO, "09619654610");
			values.put(KEY_LAST_UPDATED_USER_ID, 7);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_CUSTOMER, null, values);

			values = new ContentValues();
			values.put(KEY_CUSTOMER_ID, "2_3");
			values.put(KEY_FIRST_NAME, "Germaine");
			values.put(KEY_LAST_NAME, "Lynch");
			values.put(KEY_ADDRESS, "P.O. Box 559, 5084 Praesent Avenue");
			values.put(KEY_ADDRESS_LANDMARK, "Phasellus Corporation");
			values.put(KEY_TEL_NO, "4546833");
			values.put(KEY_MOBILE_NO, "09231631996");
			values.put(KEY_LAST_UPDATED_USER_ID, 7);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_CUSTOMER, null, values);

			values = new ContentValues();
			values.put(KEY_CUSTOMER_ID, "1_6");
			values.put(KEY_FIRST_NAME, "Clare");
			values.put(KEY_LAST_NAME, "Mitchell");
			values.put(KEY_ADDRESS, "3890 Dui. Road");
			values.put(KEY_ADDRESS_LANDMARK, "Aliquet Corp.");
			values.put(KEY_TEL_NO, "7207638");
			values.put(KEY_MOBILE_NO, "09921536448");
			values.put(KEY_LAST_UPDATED_USER_ID, 7);
			values.put(KEY_LAST_UPDATED_DATE, formatter.format(new Date()));
			values.put(KEY_IS_SYNCED, 0);
			db.insert(TABLE_CUSTOMER, null, values);

			db.close();
			Log.d("pos", "tempAddCustomers - success");
		} catch (Exception e) {
			Log.e("pos_error", "tempAddCustomers: " + e);
		}
	}
}
