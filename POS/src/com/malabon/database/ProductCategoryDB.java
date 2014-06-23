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

import com.malabon.object.Category;
import com.malabon.object.ProductCategory;

public class ProductCategoryDB {
	public static final String TABLE_PRODUCT_CATEGORY = "product_category";

	public static final String KEY_CATEGORY_ID = "category_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_SORTORDER = "sortorder";

	private final ArrayList<Category> productcategory_list = new ArrayList<Category>();

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

	public ProductCategoryDB(Context ctx) {
		this.context = ctx;
	}

	public ProductCategoryDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public ArrayList<Category> getAllProductCategories() {
		try {
			productcategory_list.clear();
			String selectQuery = "SELECT * FROM " + TABLE_PRODUCT_CATEGORY;

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Category category = new Category();
					category.id = cursor.getInt(0);
					category.name = cursor.getString(1);
					category.sortorder = cursor.getInt(2);

					productcategory_list.add(category);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getAllProductCategories - success");
		} catch (Exception e) {
			Log.e("pos_error", "getAllProductCategories: " + e);
		}
		return productcategory_list;
	}

	public int addCategories(List<ProductCategory> list) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (ProductCategory pc : list) {
				String query = "INSERT OR REPLACE INTO "
						+ TABLE_PRODUCT_CATEGORY + " ( " + KEY_CATEGORY_ID
						+ "," + KEY_NAME + "," + KEY_SORTORDER + ") VALUES ("
						+ pc.category_id + ", ?,"
						+ pc.sortorder + ");";
				SQLiteStatement stmt = db.compileStatement(query);
				stmt.bindString(1, pc.name);
				stmt.execute();
			}

			db.close();
			num = 1;
			Log.d("pos_sync", "addCategories - success");
		} catch (Exception e) {
			Log.e("pos_error", "addCategories: " + e);
		}
		return num;
	}

	// TODO: delete after testing
	// --------------------------------------------------------------------------

	public int getCategoryCount() {
		int num = 0;
		try {
			String countQuery = "SELECT " + KEY_CATEGORY_ID + " FROM "
					+ TABLE_PRODUCT_CATEGORY;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			num = cursor.getCount();

			cursor.close();
			db.close();
			Log.d("pos", "getCategoryCount: " + String.valueOf(num));
		} catch (Exception e) {
			Log.e("pos_error", "getCategoryCount: " + e);
		}
		return num;
	}

	public void tempAddCategories() {
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = null;

			values = new ContentValues();
			values.put(KEY_CATEGORY_ID, 1);
			values.put(KEY_NAME, "Solid");
			values.put(KEY_SORTORDER, 1);
			db.insert(TABLE_PRODUCT_CATEGORY, null, values);

			values = new ContentValues();
			values.put(KEY_CATEGORY_ID, 2);
			values.put(KEY_NAME, "Liquid");
			values.put(KEY_SORTORDER, 2);
			db.insert(TABLE_PRODUCT_CATEGORY, null, values);

			db.close();
			Log.d("pos", "tempAddCategories - success");
		} catch (Exception e) {
			Log.e("pos_error", "tempAddCategories: " + e);
		}
	}
}
