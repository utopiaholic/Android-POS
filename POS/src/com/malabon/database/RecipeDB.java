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

import com.malabon.object.Recipe;

public class RecipeDB {
	public static final String TABLE_RECIPE = "recipe";

	public static final String KEY_RECIPE_ID = "recipe_id";
	public static final String KEY_PRODUCT_ID = "product_id";
	public static final String KEY_INGREDIENT_ID = "ingredient_id";
	public static final String KEY_INGREDIENT_QUANTITY = "ingredient_quantity";

	private final ArrayList<Recipe> recipe_list = new ArrayList<Recipe>();

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

	public RecipeDB(Context ctx) {
		this.context = ctx;
	}

	public RecipeDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public ArrayList<Recipe> getAllRecipes() {
		try {
			recipe_list.clear();
			String selectQuery = "SELECT * FROM " + TABLE_RECIPE;

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					Recipe recipe = new Recipe();
					recipe.recipe_id = cursor.getInt(0);
					recipe.product_id = cursor.getInt(1);
					recipe.ingredient_id = cursor.getInt(2);
					recipe.ingredient_qty = cursor.getDouble(3);

					recipe_list.add(recipe);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			Log.d("pos", "getAllRecipes - success");
		} catch (Exception e) {
			Log.e("pos_error", "getAllRecipes: " + e);
		}
		return recipe_list;
	}

	public int addRecipe(List<Recipe> list) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();

			for (Recipe r : list) {
				String query = "INSERT OR REPLACE INTO " + TABLE_RECIPE + " ( "
						+ KEY_RECIPE_ID + "," + KEY_PRODUCT_ID + ","
						+ KEY_INGREDIENT_ID + "," + KEY_INGREDIENT_QUANTITY
						+ ") VALUES (" + r.recipe_id + ", " + r.product_id
						+ " , " + r.ingredient_id + ", " + r.ingredient_qty
						+ ");";
				db.execSQL(query);
			}

			db.close();
			num = 1;
			Log.d("pos_sync", "addRecipe - success");
		} catch (Exception e) {
			Log.e("pos_error", "addRecipe: " + e);
		}
		return num;
	}

	// TODO: delete after testing
	// --------------------------------------------------------------------------

	public int getRecipeCount() {
		int num = 0;
		try {
			String countQuery = "SELECT " + KEY_RECIPE_ID + " FROM "
					+ TABLE_RECIPE;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			num = cursor.getCount();

			cursor.close();
			db.close();
			Log.d("pos", "getRecipeCount: " + String.valueOf(num));
		} catch (Exception e) {
			Log.e("pos_error", "getRecipeCount: " + e);
		}
		return num;
	}

	public void tempAddRecipe() {
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = null;

			values = new ContentValues();
			values.put(KEY_RECIPE_ID, 1);
			values.put(KEY_PRODUCT_ID, 1); // pizza
			values.put(KEY_INGREDIENT_ID, 1); // tomato
			values.put(KEY_INGREDIENT_QUANTITY, 2);
			db.insert(TABLE_RECIPE, null, values);

			values = new ContentValues();
			values.put(KEY_RECIPE_ID, 2);
			values.put(KEY_PRODUCT_ID, 1); // pizza
			values.put(KEY_INGREDIENT_ID, 2); // bread
			values.put(KEY_INGREDIENT_QUANTITY, 2);
			db.insert(TABLE_RECIPE, null, values);

			values = new ContentValues();
			values.put(KEY_RECIPE_ID, 3);
			values.put(KEY_PRODUCT_ID, 2); // coke
			values.put(KEY_INGREDIENT_ID, 5); // coke
			values.put(KEY_INGREDIENT_QUANTITY, 1);
			db.insert(TABLE_RECIPE, null, values);

			values = new ContentValues();
			values.put(KEY_RECIPE_ID, 4);
			values.put(KEY_PRODUCT_ID, 3); // spaghetti
			values.put(KEY_INGREDIENT_ID, 3); // tomato
			values.put(KEY_INGREDIENT_QUANTITY, 2);
			db.insert(TABLE_RECIPE, null, values);

			values = new ContentValues();
			values.put(KEY_RECIPE_ID, 5);
			values.put(KEY_PRODUCT_ID, 3); // spaghetti
			values.put(KEY_INGREDIENT_ID, 3); // noodles
			values.put(KEY_INGREDIENT_QUANTITY, 1);
			db.insert(TABLE_RECIPE, null, values);

			values = new ContentValues();
			values.put(KEY_RECIPE_ID, 6);
			values.put(KEY_PRODUCT_ID, 4); // fries
			values.put(KEY_INGREDIENT_ID, 4); // potato
			values.put(KEY_INGREDIENT_QUANTITY, 4);
			db.insert(TABLE_RECIPE, null, values);

			values = new ContentValues();
			values.put(KEY_RECIPE_ID, 7);
			values.put(KEY_PRODUCT_ID, 5); // sprite
			values.put(KEY_INGREDIENT_ID, 6); // sprite
			values.put(KEY_INGREDIENT_QUANTITY, 1);
			db.insert(TABLE_RECIPE, null, values);

			values = new ContentValues();
			values.put(KEY_RECIPE_ID, 8);
			values.put(KEY_PRODUCT_ID, 6); // milk shake
			values.put(KEY_INGREDIENT_ID, 7); // milk
			values.put(KEY_INGREDIENT_QUANTITY, 1);
			db.insert(TABLE_RECIPE, null, values);

			db.close();
			Log.d("pos", "tempAddRecipe - success");
		} catch (Exception e) {
			Log.e("pos_error", "tempAddRecipe: " + e);
		}
	}
}
