package com.malabon.database;

public class DBTable {
	public String get_TABLE_CUSTOMER() {
		return "CREATE TABLE " + CustomerDB.TABLE_CUSTOMER + "("
				+ CustomerDB.KEY_CUSTOMER_ID
				+ " nvarchar(25) NOT NULL primary key, "
				+ CustomerDB.KEY_FIRST_NAME + " nvarchar(100) NOT NULL, "
				+ CustomerDB.KEY_LAST_NAME + " nvarchar(100) NOT NULL, "
				+ CustomerDB.KEY_ADDRESS + " nvarchar(2000) NOT NULL, "
				+ CustomerDB.KEY_ADDRESS_LANDMARK + " nvarchar(500), "
				+ CustomerDB.KEY_TEL_NO + " nvarchar(15), "
				+ CustomerDB.KEY_MOBILE_NO + " nvarchar(15), "
				+ CustomerDB.KEY_LAST_UPDATED_USER_ID + " INTEGER NOT NULL, "
				+ CustomerDB.KEY_LAST_UPDATED_DATE + " date NOT NULL, "
				+ CustomerDB.KEY_IS_SYNCED + " boolean NOT NULL);";
	}

	public String get_TABLE_DISCOUNT() {
		return "CREATE TABLE " + DiscountDB.TABLE_DISCOUNT + "("
				+ DiscountDB.KEY_DISCOUNT_ID
				+ " INTEGER NOT NULL PRIMARY KEY, " + DiscountDB.KEY_NAME
				+ " nvarchar(50) NOT NULL, " + DiscountDB.KEY_PERCENTAGE
				+ " double NOT NULL);";
	}

	public String get_TABLE_HISTORY_CLEAR_CACHE() {
		return "CREATE TABLE " + HistoryClearCacheDB.TABLE_HISTORY_CLEAR_CACHE
				+ "(" + HistoryClearCacheDB.KEY_ID
				+ " INTEGER NOT NULL PRIMARY KEY autoincrement, "
				+ HistoryClearCacheDB.KEY_DATE + " date NOT NULL, "
				+ HistoryClearCacheDB.KEY_USER_ID + " INTEGER NOT NULL, "
				+ HistoryClearCacheDB.KEY_IS_SYNCED + " boolean NOT NULL, "
				+ "foreign key (" + HistoryClearCacheDB.KEY_USER_ID
				+ ") references " + UserDB.TABLE_USER + "("
				+ UserDB.KEY_USER_ID + "));";
	}

	public String get_TABLE_HISTORY_SYNC() {
		return "CREATE TABLE " + HistorySyncDB.TABLE_HISTORY_SYNC + "("
				+ HistorySyncDB.KEY_ID
				+ " INTEGER NOT NULL PRIMARY KEY autoincrement, "
				+ HistorySyncDB.KEY_DATE + " date NOT NULL, "
				+ HistorySyncDB.KEY_USER_ID + " INTEGER NOT NULL, "
				+ HistorySyncDB.KEY_IS_MANUAL + " boolean NOT NULL, "
				+ HistorySyncDB.KEY_IS_SYNCED + " boolean NOT NULL, "
				+ "foreign key (" + HistorySyncDB.KEY_USER_ID + ") references "
				+ UserDB.TABLE_USER + "(" + UserDB.KEY_USER_ID + "));";
	}

	public String get_TABLE_INGREDIENT() {
		return "CREATE TABLE " + IngredientDB.TABLE_INGREDIENT + "("
				+ IngredientDB.KEY_INGREDIENT_ID
				+ " INTEGER NOT NULL PRIMARY KEY, " + IngredientDB.KEY_NAME
				+ " nvarchar(100) NOT NULL, " + IngredientDB.KEY_UNIT
				+ " nvarchar(10) NOT NULL);";
	}

	public String get_TABLE_LOG_CANCEL_PRODUCT() {
		return "CREATE TABLE " + LogCancelProductDB.TABLE_LOG_CANCEL_PRODUCT
				+ "(" + LogCancelProductDB.KEY_ID
				+ " INTEGER NOT NULL PRIMARY KEY autoincrement, "
				+ LogCancelProductDB.KEY_DATE + " date NOT NULL, "
				+ LogCancelProductDB.KEY_USER_ID + " INTEGER NOT NULL, "
				+ LogCancelProductDB.KEY_PRODUCT_ID + " INTEGER NOT NULL, "
				+ LogUserTimeSheetDB.KEY_SALES_SUMMARY_ID + " nvarchar(25), "
				+ LogUserTimeSheetDB.KEY_IS_SYNCED + " boolean NOT NULL, "
				+ "foreign key (" + LogCancelProductDB.KEY_USER_ID
				+ ") references " + UserDB.TABLE_USER + "("
				+ UserDB.KEY_USER_ID + "), " + "foreign key ("
				+ LogCancelProductDB.KEY_PRODUCT_ID + ") references "
				+ ProductDB.TABLE_PRODUCT + "(" + ProductDB.KEY_PRODUCT_ID
				+ "));";
	}

	public String get_TABLE_LOG_CASH() {
		return "CREATE TABLE " + LogCashDB.TABLE_LOG_CASH + "( "
				+ LogCashDB.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ LogCashDB.KEY_IS_CASH_IN + " boolean NOT NULL, "
				+ LogCashDB.KEY_AMOUNT + " double NOT NULL, "
				+ LogCashDB.KEY_USER_ID + " INTEGER NOT NULL, "
				+ LogCashDB.KEY_DESCRIPTION + " nvarchar(500), "
				+ LogCashDB.KEY_DATE + " date NOT NULL, "
				+ LogCashDB.KEY_IS_SYNCED + " boolean NOT NULL, "
				+ "foreign key (" + LogCashDB.KEY_USER_ID + ") references "
				+ UserDB.TABLE_USER + "(" + UserDB.KEY_USER_ID + "));";
	}

	public String get_TABLE_LOG_USER_TIME_SHEET() {
		return "CREATE TABLE " + LogUserTimeSheetDB.TABLE_LOG_USER_TIME_SHEET
				+ "(" + LogUserTimeSheetDB.KEY_ID
				+ " INTEGER NOT NULL PRIMARY KEY autoincrement, "
				+ LogUserTimeSheetDB.KEY_USER_ID + " INTEGER NOT NULL, "
				+ LogUserTimeSheetDB.KEY_TIMEIN + " date, "
				+ LogUserTimeSheetDB.KEY_TIMEIN_IMAGE + " blob, "
				+ LogUserTimeSheetDB.KEY_TIMEOUT + " date, "
				+ LogUserTimeSheetDB.KEY_TIMEOUT_IMAGE + " blob, "
				+ LogUserTimeSheetDB.KEY_SALES_SUMMARY_ID + " nvarchar(25), "
				+ LogUserTimeSheetDB.KEY_IS_SYNCED + " boolean NOT NULL, "
				+ "foreign key (" + LogUserTimeSheetDB.KEY_USER_ID
				+ ") references " + UserDB.TABLE_USER + "("
				+ UserDB.KEY_USER_ID + "), " + "foreign key ("
				+ LogUserTimeSheetDB.KEY_SALES_SUMMARY_ID + ") references "
				+ UserSalesSummaryDB.TABLE_USER_SALES_SUMMARY + "("
				+ UserSalesSummaryDB.KEY_SALES_SUMMARY_ID + "));";
	}

	public String get_TABLE_POS_SETTINGS() {
		return "CREATE TABLE " + PosSettingsDB.TABLE_POS_SETTINGS + "("
				+ PosSettingsDB.KEY_ID + " INTEGER NOT NULL PRIMARY KEY, "
				+ PosSettingsDB.KEY_BRANCH_ID + " INTEGER NOT NULL, "
				+ PosSettingsDB.KEY_BRANCH_NAME + " nvarchar(100) NOT NULL, "
				+ PosSettingsDB.KEY_IS_MANUAL + " boolean NOT NULL, "
				+ PosSettingsDB.KEY_SYNC_FREQUENCY + " INTEGER NULL, "
				+ PosSettingsDB.KEY_SYNC_TIME + " nvarchar(10) NULL, "
				+ PosSettingsDB.KEY_CLEAR_FREQUENCY + " INTEGER NOT NULL);";
	}

	public String get_TABLE_PRODUCT() {
		return "CREATE TABLE " + ProductDB.TABLE_PRODUCT + "("
				+ ProductDB.KEY_PRODUCT_ID + " INTEGER NOT NULL PRIMARY KEY, "
				+ ProductDB.KEY_NAME + " nvarchar(100) NOT NULL, "
				+ ProductDB.KEY_PRICE + " double NOT NULL, "
				+ ProductDB.KEY_CATEGORY_ID + " INTEGER NOT NULL, "
				+ ProductDB.KEY_SORTORDER
				+ " INTEGER NOT NULL, "
				// + ProductDB.KEY_CAN_BE_TAKEN_OUT + " boolean NOT NULL, "
				+ "foreign key (" + ProductDB.KEY_CATEGORY_ID + ") references "
				+ ProductCategoryDB.TABLE_PRODUCT_CATEGORY + "("
				+ ProductCategoryDB.KEY_CATEGORY_ID + "));";
	}

	public String get_TABLE_PRODUCT_CATEGORY() {
		return "CREATE TABLE " + ProductCategoryDB.TABLE_PRODUCT_CATEGORY + "("
				+ ProductCategoryDB.KEY_CATEGORY_ID
				+ " INTEGER NOT NULL PRIMARY KEY, "
				+ ProductCategoryDB.KEY_NAME + " nvarchar(100) NOT NULL, "
				+ ProductCategoryDB.KEY_SORTORDER + " INTEGER NOT NULL);";
	}

	public String get_TABLE_TABLE_RECEIPT_DETAIL() {
		return "CREATE TABLE " + ReceiptDetailDB.TABLE_RECEIPT_DETAIL + "("
				+ ReceiptDetailDB.KEY_RECEIPT_ID
				+ " INTEGER NOT NULL PRIMARY KEY, "
				+ ReceiptDetailDB.KEY_STORE_NAME + " nvarchar(200) NOT NULL, "
				+ ReceiptDetailDB.KEY_OPERATED_BY + " nvarchar(200) NOT NULL, "
				+ ReceiptDetailDB.KEY_ADDRESS + " nvarchar(2000) NOT NULL, "
				+ ReceiptDetailDB.KEY_PERMIT_NO + " nvarchar(50), "
				+ ReceiptDetailDB.KEY_TIN_NO + " nvarchar(50), "
				+ ReceiptDetailDB.KEY_SERIAL_NO + " nvarchar(50), "
				+ ReceiptDetailDB.KEY_ACCR_NO + " nvarchar(50), "
				+ ReceiptDetailDB.KEY_MIN_NO + " nvarchar(50), "
				+ ReceiptDetailDB.KEY_MESSAGE + " nvarchar(500));";
	}

	public String get_TABLE_RECIPE() {
		return "CREATE TABLE " + RecipeDB.TABLE_RECIPE + "("
				+ RecipeDB.KEY_RECIPE_ID + " INTEGER NOT NULL PRIMARY KEY, "
				+ RecipeDB.KEY_PRODUCT_ID + " INTEGER NOT NULL, "
				+ RecipeDB.KEY_INGREDIENT_ID + " INTEGER NOT NULL, "
				+ RecipeDB.KEY_INGREDIENT_QUANTITY + " double NOT NULL, "
				+ "foreign key (" + RecipeDB.KEY_PRODUCT_ID + ") references "
				+ ProductDB.TABLE_PRODUCT + "(" + ProductDB.KEY_PRODUCT_ID
				+ "));";
	}

	public String get_TABLE_STOCK() {
		return "CREATE TABLE " + StockDB.TABLE_STOCK + "("
				+ StockDB.KEY_STOCK_ID + " INTEGER NOT NULL PRIMARY KEY, "
				+ StockDB.KEY_STOCK_TYPE_ID + " INTEGER NOT NULL, "
				+ StockDB.KEY_ID + " INTEGER NOT NULL, " + StockDB.KEY_QUANTITY
				+ " double NOT NULL, " + StockDB.KEY_LAST_UPDATED_DATE
				+ " date NOT NULL, " + StockDB.KEY_LAST_UPDATED_USER_ID
				+ " INTEGER NOT NULL, " + StockDB.KEY_IS_SYNCED
				+ " boolean NOT NULL, " + "foreign key ("
				+ StockDB.KEY_STOCK_TYPE_ID + ") references "
				+ UserDB.TABLE_USER + "(" + StockTypeDB.KEY_STOCK_TYPE_ID
				+ "), " + "foreign key (" + StockDB.KEY_LAST_UPDATED_USER_ID
				+ ") references " + UserSalesSummaryDB.TABLE_USER_SALES_SUMMARY
				+ "(" + UserDB.KEY_USER_ID + "));";
	}

	public String get_TABLE_STOCK_TYPE() {
		return "CREATE TABLE " + StockTypeDB.TABLE_STOCK_TYPE + "("
				+ StockTypeDB.KEY_STOCK_TYPE_ID
				+ " INTEGER NOT NULL PRIMARY KEY, " + StockTypeDB.KEY_NAME
				+ " nvarchar(100) NOT NULL);";
	}

	public String get_TABLE_USER() {
		return "CREATE TABLE " + UserDB.TABLE_USER + "(" + UserDB.KEY_USER_ID
				+ " INTEGER NOT NULL PRIMARY KEY, " + UserDB.KEY_USERNAME
				+ " nvarchar(20) NOT NULL, " + UserDB.KEY_PASSWORD
				+ " nvarchar(20) NOT NULL, " + UserDB.KEY_IS_ADMIN
				+ " boolean NOT NULL);";
	}

	public String get_TABLE_USER_SALES_SUMMARY() {
		return "CREATE TABLE " + UserSalesSummaryDB.TABLE_USER_SALES_SUMMARY
				+ "(" + UserSalesSummaryDB.KEY_SALES_SUMMARY_ID
				+ " nvarchar(25) NOT NULL PRIMARY KEY, "
				+ UserSalesSummaryDB.KEY_CASH_TOTAL + " double NOT NULL, "
				+ UserSalesSummaryDB.KEY_CASH_EXPECTED + " double NOT NULL, "
				+ UserSalesSummaryDB.KEY_IS_SYNCED + " boolean NOT NULL);";
	}

	// ---------------payment----------------

	public String get_TABLE_SALES() {
		return "CREATE TABLE " + SalesDB.TABLE_SALES + "("
				+ SalesDB.KEY_SALES_ID
				+ " INTEGER NOT NULL PRIMARY KEY autoincrement, "
				+ SalesDB.KEY_ORDER_TYPE_ID + " INTEGER NOT NULL, "
				+ SalesDB.KEY_USER_ID + " INTEGER NOT NULL, "
				+ SalesDB.KEY_DATE + " date NOT NULL, " + SalesDB.KEY_IS_SYNCED
				+ " boolean NOT NULL);";
	}

	public String get_TABLE_SALES_CUSTOMER() {
		return "CREATE TABLE " + SalesCustomerDB.TABLE_SALES_CUSTOMER + "("
				+ SalesCustomerDB.KEY_ID
				+ " INTEGER NOT NULL PRIMARY KEY autoincrement, "
				+ SalesCustomerDB.KEY_SALES_ID + " INTEGER NOT NULL, "
				+ SalesCustomerDB.KEY_CUSTOMER_ID + " nvarchar(25) NOT NULL, "
				+ SalesCustomerDB.KEY_IS_SYNCED + " boolean NOT NULL, "
				+ "foreign key (" + SalesCustomerDB.KEY_SALES_ID
				+ ") references " + SalesDB.TABLE_SALES + "("
				+ SalesDB.KEY_SALES_ID + "), " + "foreign key ("
				+ SalesCustomerDB.KEY_CUSTOMER_ID + ") references "
				+ CustomerDB.TABLE_CUSTOMER + "(" + CustomerDB.KEY_CUSTOMER_ID
				+ "));";
	}

	public String get_TABLE_SALES_PRODUCT() {
		return "CREATE TABLE " + SalesProductDB.TABLE_SALES_PRODUCT + "("
				+ SalesProductDB.KEY_ID
				+ " INTEGER NOT NULL PRIMARY KEY autoincrement, "
				+ SalesProductDB.KEY_SALES_ID + " INTEGER NOT NULL, "
				+ SalesProductDB.KEY_PRODUCT_ID + " INTEGER NOT NULL, "
				+ SalesProductDB.KEY_QUANTITY + " INTEGER NOT NULL, "
				+ SalesProductDB.KEY_IS_SYNCED + " boolean NOT NULL, "
				+ "foreign key (" + SalesProductDB.KEY_SALES_ID
				+ ") references " + SalesDB.TABLE_SALES + "("
				+ SalesDB.KEY_SALES_ID + "), " + "foreign key ("
				+ SalesProductDB.KEY_PRODUCT_ID + ") references "
				+ ProductDB.TABLE_PRODUCT + "(" + ProductDB.KEY_PRODUCT_ID
				+ "));";
	}

	public String get_TABLE_SALES_DISCOUNT() {
		return "CREATE TABLE " + SalesDiscountDB.TABLE_SALES_DISCOUNT + "("
				+ SalesDiscountDB.KEY_ID
				+ " INTEGER NOT NULL PRIMARY KEY autoincrement, "
				+ SalesDiscountDB.KEY_SALES_ID + " INTEGER NOT NULL, "
				+ SalesDiscountDB.KEY_DISCOUNT_ID + " INTEGER NOT NULL, "
				+ SalesDiscountDB.KEY_IS_SYNCED + " boolean NOT NULL, "
				+ "foreign key (" + SalesDiscountDB.KEY_SALES_ID
				+ ") references " + SalesDB.TABLE_SALES + "("
				+ SalesDB.KEY_SALES_ID + "), " + "foreign key ("
				+ SalesDiscountDB.KEY_DISCOUNT_ID + ") references "
				+ DiscountDB.TABLE_DISCOUNT + "(" + DiscountDB.KEY_DISCOUNT_ID
				+ "));";
	}

	public String get_TABLE_PAYMENT() {
		return "CREATE TABLE " + PaymentDB.TABLE_PAYMENT + "("
				+ PaymentDB.KEY_PAYMENT_ID
				+ " INTEGER NOT NULL PRIMARY KEY autoincrement, "
				+ PaymentDB.KEY_SALES_ID + " INTEGER NOT NULL, "
				+ PaymentDB.KEY_TOTAL_NET + " double NOT NULL, "
				+ PaymentDB.KEY_TOTAL_TAX + " double NOT NULL, "
				+ PaymentDB.KEY_TOTAL_DISCOUNT + " double NOT NULL, "
				+ PaymentDB.KEY_RECEIPT_ID + " nvarchar(200) NOT NULL, "
				+ PaymentDB.KEY_DATE + " date NOT NULL, "
				+ PaymentDB.KEY_IS_SYNCED + " boolean NOT NULL, "
				+ "foreign key (" + PaymentDB.KEY_SALES_ID + ") references "
				+ SalesDB.TABLE_SALES + "(" + SalesDB.KEY_SALES_ID + "));";
	}
}
