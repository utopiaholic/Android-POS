package com.malabon.function;

import android.content.Context;

import com.malabon.database.CustomerDB;
import com.malabon.database.DiscountDB;
import com.malabon.database.IngredientDB;
import com.malabon.database.PosSettingsDB;
import com.malabon.database.ProductCategoryDB;
import com.malabon.database.ProductDB;
import com.malabon.database.ReceiptDetailDB;
import com.malabon.database.RecipeDB;
import com.malabon.database.StockDB;
import com.malabon.database.StockTypeDB;
import com.malabon.database.UserDB;

public class LoadSampleData {
	public void AddTempData(Context context) {
		 AddUsers(context);
		 AddProductCategories(context);
		 AddProducts(context);
		 AddIngredients(context);
		 AddRecipes(context);
		 AddStockTypes(context);
		 AddStocks(context);
		AddDiscounts(context);
		 AddCustomers(context);
		 AddPosSettings(context);
		 AddReceiptDetails(context);
	}

	public void AddUsers(Context context) {
		UserDB userDB = new UserDB(context);
		userDB.open();

		if (userDB.getUserCount() == 0)
			userDB.tempAddUsers();
	}

	public void AddProductCategories(Context context) {
		ProductCategoryDB productCategoryDB = new ProductCategoryDB(context);
		productCategoryDB.open();

		if (productCategoryDB.getCategoryCount() == 0)
			productCategoryDB.tempAddCategories();
	}

	public void AddProducts(Context context) {
		ProductDB productDB = new ProductDB(context);
		productDB.open();

		if (productDB.getProductCount() == 0)
			productDB.tempAddProduct();
	}

	public void AddIngredients(Context context) {
		IngredientDB ingredientDB = new IngredientDB(context);
		ingredientDB.open();

		if (ingredientDB.getIngredientCount() == 0)
			ingredientDB.tempAddIngredient();
	}

	public void AddRecipes(Context context) {
		RecipeDB recipeDB = new RecipeDB(context);
		recipeDB.open();

		if (recipeDB.getRecipeCount() == 0)
			recipeDB.tempAddRecipe();
	}

	public void AddStockTypes(Context context) {
		StockTypeDB stockTypeDB = new StockTypeDB(context);
		stockTypeDB.open();

		if (stockTypeDB.getStockTypeCount() == 0)
			stockTypeDB.tempAddStockType();
	}

	public void AddStocks(Context context) {
		StockDB stockDB = new StockDB(context);
		stockDB.open();

		if (stockDB.getStockCount() == 0)
			stockDB.tempAddStock();
	}

	public void AddDiscounts(Context context) {
		DiscountDB discountDB = new DiscountDB(context);
		discountDB.open();

		if (discountDB.getDiscountCount() == 0)
			discountDB.tempAddDiscounts();
	}

	public void AddCustomers(Context context) {
		CustomerDB customerDB = new CustomerDB(context);
		customerDB.open();

		if (customerDB.getCustomerCount() == 0)
			customerDB.tempAddCustomers();
	}

	public void AddPosSettings(Context context) {
		PosSettingsDB posSettingsDB = new PosSettingsDB(context);
		posSettingsDB.open();

		if (posSettingsDB.getPosSettingCount() == 0)
			posSettingsDB.tempAddPosSettings();
	}

	public void AddReceiptDetails(Context context) {
		ReceiptDetailDB receiptDetailDB = new ReceiptDetailDB(context);
		receiptDetailDB.open();

		if (receiptDetailDB.getReceiptDetailCount() == 0)
			receiptDetailDB.tempAddReceiptDetails();
	}
}
