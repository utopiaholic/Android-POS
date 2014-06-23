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

import com.malabon.object.Ingredient;

public class IngredientDB {
	public static final String TABLE_INGREDIENT = "ingredient";

	public static final String KEY_INGREDIENT_ID = "ingredient_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_UNIT = "unit";

	private final ArrayList<Ingredient> ingredient_list = new ArrayList<Ingredient>();

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

	public IngredientDB(Context ctx) {
		this.context = ctx;
	}

	public IngredientDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public ArrayList<Ingredient> getAllIngredients() {
		try {
			ingredient_list.clear();
			String selectQuery = "SELECT * FROM " + TABLE_INGREDIENT;

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Ingredient ingredient = new Ingredient();
					ingredient.id = cursor.getInt(0);
					ingredient.name = cursor.getString(1);
					ingredient.unit = cursor.getString(2);

					ingredient_list.add(ingredient);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getAllIngredients - success");
		} catch (Exception e) {
			Log.e("pos_error", "getAllIngredients: " + e);
		}
		return ingredient_list;
	}

	public int addIngredient(List<Ingredient> list) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (Ingredient i : list) {
				String query = "INSERT OR REPLACE INTO " + TABLE_INGREDIENT
						+ " ( " + KEY_INGREDIENT_ID + "," + KEY_NAME + ","
						+ KEY_UNIT + ") VALUES (" + i.id + ", ?, ?);";
				SQLiteStatement stmt = db.compileStatement(query);
				stmt.bindString(1, i.name);
				stmt.bindString(2, i.unit);
				stmt.execute();
			}

			db.close();
			num = 1;
			Log.d("pos_sync", "addIngredient - success");
		} catch (Exception e) {
			Log.e("pos_error", "addIngredient: " + e);
		}
		return num;
	}

	// TODO: delete after testing
	// --------------------------------------------------------------------------

	public int getIngredientCount() {
		int num = 0;
		try {
			String countQuery = "SELECT " + KEY_INGREDIENT_ID + " FROM "
					+ TABLE_INGREDIENT;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			num = cursor.getCount();

			cursor.close();
			db.close();
			Log.d("pos", "getIngredientCount: " + String.valueOf(num));
		} catch (Exception e) {
			Log.e("pos_error", "getIngredientCount: " + e);
		}
		return num;
	}

	public void tempAddIngredient() {
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = null;

			values = new ContentValues();
			values.put(KEY_INGREDIENT_ID, 1);
			values.put(KEY_NAME, "Tomato");
			values.put(KEY_UNIT, "kilo");
			db.insert(TABLE_INGREDIENT, null, values);

			values = new ContentValues();
			values.put(KEY_INGREDIENT_ID, 2);
			values.put(KEY_NAME, "Bread");
			values.put(KEY_UNIT, "kilo");
			db.insert(TABLE_INGREDIENT, null, values);

			values = new ContentValues();
			values.put(KEY_INGREDIENT_ID, 3);
			values.put(KEY_NAME, "Noodles");
			values.put(KEY_UNIT, "kilo");
			db.insert(TABLE_INGREDIENT, null, values);

			values = new ContentValues();
			values.put(KEY_INGREDIENT_ID, 4);
			values.put(KEY_NAME, "Potato");
			values.put(KEY_UNIT, "kilo");
			db.insert(TABLE_INGREDIENT, null, values);

			values = new ContentValues();
			values.put(KEY_INGREDIENT_ID, 5);
			values.put(KEY_NAME, "Coke");
			values.put(KEY_UNIT, "kilo");
			db.insert(TABLE_INGREDIENT, null, values);

			values = new ContentValues();
			values.put(KEY_INGREDIENT_ID, 6);
			values.put(KEY_NAME, "Sprite");
			values.put(KEY_UNIT, "kilo");
			db.insert(TABLE_INGREDIENT, null, values);

			values = new ContentValues();
			values.put(KEY_INGREDIENT_ID, 7);
			values.put(KEY_NAME, "Milk");
			values.put(KEY_UNIT, "kilo");
			db.insert(TABLE_INGREDIENT, null, values);

			db.close();
			Log.d("pos", "tempAddIngredient - success");
		} catch (Exception e) {
			Log.e("pos_error", "tempAddIngredient: " + e);
		}
	}
}
