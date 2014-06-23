package com.malabon.sync;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.malabon.database.CustomerDB;
import com.malabon.database.IngredientDB;
import com.malabon.database.LogCancelProductDB;
import com.malabon.database.LogCashDB;
import com.malabon.database.LogUserTimeSheetDB;
import com.malabon.database.PaymentDB;
import com.malabon.database.PosSettingsDB;
import com.malabon.database.ProductCategoryDB;
import com.malabon.database.ProductDB;
import com.malabon.database.ReceiptDetailDB;
import com.malabon.database.RecipeDB;
import com.malabon.database.SalesCustomerDB;
import com.malabon.database.SalesDB;
import com.malabon.database.SalesDiscountDB;
import com.malabon.database.SalesProductDB;
import com.malabon.database.StockDB;
import com.malabon.database.StockTypeDB;
import com.malabon.database.UserDB;
import com.malabon.database.UserSalesSummaryDB;
import com.malabon.object.CancelledOrder;
import com.malabon.object.CashInOut;
import com.malabon.object.Customer;
import com.malabon.object.Ingredient;
import com.malabon.object.LogUserTime;
import com.malabon.object.PosSettings;
import com.malabon.object.Product;
import com.malabon.object.ProductCategory;
import com.malabon.object.ReceiptDetail;
import com.malabon.object.Recipe;
import com.malabon.object.Sale;
import com.malabon.object.Stock;
import com.malabon.object.StockType;
import com.malabon.object.Sync;
import com.malabon.object.User;
import com.malabon.object.UserSalesSummary;

public class Data {

	// ------GENERAL----------------------------------------------------------------------------------------

	private static final String TAG_BRANCH_ID = "branch_id";

	// ------PUSH----------------------------------------------------------------------------------------

	private static final String URL_PUSH_DEFAULT = "http://unboggleit.com/salesInv/webservice/sync_push_sis_default.php";
	private static final String URL_PUSH_CUSTOMER = "http://unboggleit.com/salesInv/webservice/sync_push_sis_customer.php";
	private static final String URL_PUSH_LOG_USER_TIME = "http://unboggleit.com/salesInv/webservice/sync_push_sis_log_user_time.php";
	
	public static JSONObject GetCredentials() {
		JSONObject jUser = new JSONObject();
		try {
			jUser.put("username", Sync.user.username);
			jUser.put("password", Sync.user.password);
		} catch (JSONException e) {
			Log.e("pos_error", "JSONObject: "
					+ "Could not parse malformed JSON");
		}
		return jUser;
	}

	private static List<Integer> syncedID;
	private static List<String> syncedStrID;

	public static JSONObject GetDataForPush(Context context, String tablename) {
		syncedID = new ArrayList<Integer>();
		syncedStrID = new ArrayList<String>();

		TablesForPush tbl = TablesForPush.valueOf(tablename);
		JSONObject jObj = new JSONObject();
		JSONObject jUser = GetCredentials();
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(jUser);

		// sales tables
		List<Sale> listdata = new ArrayList<Sale>();

		try {
			switch (tbl) {
			case sales:
				SalesDB salesDB = new SalesDB(context);
				salesDB.open();

				listdata = new ArrayList<Sale>();
				listdata = salesDB.getRowsForPush();

				for (Sale s : listdata) {
					syncedID.add(Integer.valueOf(s.sales_id
							.substring(s.sales_id.lastIndexOf("_") + 1)));

					JSONObject jRow = new JSONObject();

					jRow.put(SalesDB.KEY_SALES_ID, s.sales_id);
					jRow.put(TAG_BRANCH_ID, Sync.posSettings.branch_id);
					jRow.put(SalesDB.KEY_ORDER_TYPE_ID, s.orderType);
					jRow.put(SalesDB.KEY_USER_ID, s.user);
					jRow.put(SalesDB.KEY_DATE, s.strDate);

					jsonArray.put(jRow);
				}
				jObj.put(tablename, jsonArray);

				if (listdata.isEmpty())
					jObj = null;
				break;
			case sales_customer:
				SalesCustomerDB salesCustomerDB = new SalesCustomerDB(context);
				salesCustomerDB.open();

				listdata = new ArrayList<Sale>();
				listdata = salesCustomerDB.getRowsForPush();

				for (Sale s : listdata) {
					syncedID.add(s.id);

					JSONObject jRow = new JSONObject();

					jRow.put(SalesCustomerDB.KEY_SALES_ID, s.sales_id);
					jRow.put(SalesCustomerDB.KEY_CUSTOMER_ID, s.customer_id);

					jsonArray.put(jRow);
				}
				jObj.put(tablename, jsonArray);

				if (listdata.isEmpty())
					jObj = null;
				break;
			case sales_discount:
				SalesDiscountDB salesDiscountDB = new SalesDiscountDB(context);
				salesDiscountDB.open();

				listdata = new ArrayList<Sale>();
				listdata = salesDiscountDB.getRowsForPush();

				for (Sale s : listdata) {
					syncedID.add(s.id);

					JSONObject jRow = new JSONObject();

					jRow.put(SalesDiscountDB.KEY_SALES_ID, s.sales_id);
					jRow.put(SalesDiscountDB.KEY_DISCOUNT_ID, s.discount_id);

					jsonArray.put(jRow);
				}
				jObj.put(tablename, jsonArray);

				if (listdata.isEmpty())
					jObj = null;
				break;
			case sales_product:
				SalesProductDB salesProductDB = new SalesProductDB(context);
				salesProductDB.open();

				listdata = new ArrayList<Sale>();
				listdata = salesProductDB.getRowsForPush();

				for (Sale s : listdata) {
					syncedID.add(s.id);

					JSONObject jRow = new JSONObject();

					jRow.put(SalesProductDB.KEY_SALES_ID, s.sales_id);
					jRow.put(SalesProductDB.KEY_PRODUCT_ID, s.product_id);
					jRow.put(SalesProductDB.KEY_QUANTITY, s.product_quantity);

					jsonArray.put(jRow);
				}
				jObj.put(tablename, jsonArray);

				if (listdata.isEmpty())
					jObj = null;
				break;
			case payment:
				PaymentDB paymentDB = new PaymentDB(context);
				paymentDB.open();

				listdata = new ArrayList<Sale>();
				listdata = paymentDB.getRowsForPush();

				for (Sale s : listdata) {
					syncedID.add(Integer.valueOf(s.sales_id
							.substring(s.sales_id.lastIndexOf("_") + 1)));

					JSONObject jRow = new JSONObject();

					jRow.put(PaymentDB.KEY_PAYMENT_ID, s.payment_id);
					jRow.put(PaymentDB.KEY_SALES_ID, s.sales_id);
					jRow.put(PaymentDB.KEY_TOTAL_NET, s.netTotal);
					jRow.put(PaymentDB.KEY_TOTAL_TAX, s.taxTotal);
					jRow.put(PaymentDB.KEY_TOTAL_DISCOUNT, s.discount_amount);
					jRow.put(PaymentDB.KEY_RECEIPT_ID, s.receipt_id);
					jRow.put(PaymentDB.KEY_DATE, s.strDate);

					jsonArray.put(jRow);
				}
				jObj.put(tablename, jsonArray);

				if (listdata.isEmpty())
					jObj = null;
				break;
			case log_cancel_product:
				LogCancelProductDB logCancelProductDB = new LogCancelProductDB(
						context);
				logCancelProductDB.open();

				List<CancelledOrder> log_cancel_product_list = new ArrayList<CancelledOrder>();
				log_cancel_product_list = logCancelProductDB.getRowsForPush();

				for (CancelledOrder c : log_cancel_product_list) {
					syncedID.add(c.id);

					JSONObject jRow = new JSONObject();

					jRow.put(LogCancelProductDB.KEY_DATE, c.str_date);
					jRow.put(LogCancelProductDB.KEY_USER_ID, c.UserId);
					jRow.put(LogCancelProductDB.KEY_PRODUCT_ID, c.product_id);

					jsonArray.put(jRow);
				}
				jObj.put(tablename, jsonArray);

				if (log_cancel_product_list.isEmpty())
					jObj = null;
				break;
			case log_cash:
				LogCashDB logCashDB = new LogCashDB(context);
				logCashDB.open();

				List<CashInOut> log_cash_list = new ArrayList<CashInOut>();
				log_cash_list = logCashDB.getRowsForPush();

				for (CashInOut c : log_cash_list) {
					syncedID.add(c.id);

					JSONObject jRow = new JSONObject();
					jRow.put(TAG_BRANCH_ID, Sync.posSettings.branch_id);
					jRow.put(LogCashDB.KEY_IS_CASH_IN, c.is_cash_in);
					jRow.put(LogCashDB.KEY_AMOUNT, c.amount);
					jRow.put(LogCashDB.KEY_USER_ID, c.user_id);
					jRow.put(LogCashDB.KEY_DESCRIPTION, c.description);
					jRow.put(LogCashDB.KEY_DATE, c.str_date);

					jsonArray.put(jRow);
				}
				jObj.put(tablename, jsonArray);

				if (log_cash_list.isEmpty())
					jObj = null;
				break;
			case log_user_time:
				LogUserTimeSheetDB logUserTimeSheetDB = new LogUserTimeSheetDB(
						context);
				logUserTimeSheetDB.open();

				List<LogUserTime> log_user_time_sheet_list = new ArrayList<LogUserTime>();
				log_user_time_sheet_list = logUserTimeSheetDB.getRowsForPush();

				for (LogUserTime l : log_user_time_sheet_list) {
					syncedID.add(l.id);

					JSONObject jRow = new JSONObject();
					jRow.put(LogUserTimeSheetDB.KEY_USER_ID, l.user_id);
					jRow.put(LogUserTimeSheetDB.KEY_TIMEIN, l.str_timein);
					jRow.put(LogUserTimeSheetDB.KEY_TIMEIN_IMAGE, l.img_timein);
					jRow.put(LogUserTimeSheetDB.KEY_TIMEOUT, l.str_timeout);
					jRow.put(LogUserTimeSheetDB.KEY_TIMEIN_IMAGE, l.img_timeout);
					jRow.put(LogUserTimeSheetDB.KEY_SALES_SUMMARY_ID,
							l.sales_summary_id);

					jsonArray.put(jRow);
				}
				jObj.put(tablename, jsonArray);

				if (log_user_time_sheet_list.isEmpty())
					jObj = null;
				break;
			case user_image:
				break;
			case user_sales_summary:
				UserSalesSummaryDB userSalesSummaryDB = new UserSalesSummaryDB(
						context);
				userSalesSummaryDB.open();

				List<UserSalesSummary> user_sales_summary_list = new ArrayList<UserSalesSummary>();
				user_sales_summary_list = userSalesSummaryDB.getRowsForPush();

				for (UserSalesSummary u : user_sales_summary_list) {
					syncedStrID.add(u.sales_summary_id);

					JSONObject jRow = new JSONObject();
					jRow.put(UserSalesSummaryDB.KEY_SALES_SUMMARY_ID,
							u.sales_summary_id);
					jRow.put(UserSalesSummaryDB.KEY_CASH_TOTAL, u.cash_total);
					jRow.put(UserSalesSummaryDB.KEY_CASH_EXPECTED,
							u.cash_expected);

					jsonArray.put(jRow);
				}
				jObj.put(tablename, jsonArray);

				if (user_sales_summary_list.isEmpty())
					jObj = null;
				break;
			case history_clear_cache:
				break;
			case history_sync:
				break;
			case stock:
				break;
			case customer:
				CustomerDB customerDB = new CustomerDB(context);
				customerDB.open();

				List<Customer> customer_list = new ArrayList<Customer>();
				customer_list = customerDB.getRowsForPush();

				for (Customer c : customer_list) {
					syncedStrID.add(c.customer_id);

					JSONObject jRow = new JSONObject();
					jRow.put(CustomerDB.KEY_CUSTOMER_ID, c.customer_id);
					jRow.put(CustomerDB.KEY_FIRST_NAME, c.first_name);
					jRow.put(CustomerDB.KEY_LAST_NAME, c.last_name);
					jRow.put(CustomerDB.KEY_ADDRESS, c.address);
					jRow.put(CustomerDB.KEY_ADDRESS_LANDMARK,
							c.address_landmark);
					jRow.put(CustomerDB.KEY_TEL_NO, c.tel_no);
					jRow.put(CustomerDB.KEY_MOBILE_NO, c.mobile_no);
					jRow.put(CustomerDB.KEY_LAST_UPDATED_USER_ID,
							c.last_updated_user_id);
					jRow.put(CustomerDB.KEY_LAST_UPDATED_DATE,
							c.str_last_updated_date);

					jsonArray.put(jRow);
				}
				jObj.put(tablename, jsonArray);

				if (customer_list.isEmpty())
					jObj = null;
				break;
			default:
				break;
			}
		} catch (Throwable t) {
			Log.e("pos_error", "JSONObject: "
					+ "Could not parse malformed JSON");
		}
		return jObj;
	}

	public static void UpdateIsSynced(Context context, String tablename) {
		TablesForPush tbl = TablesForPush.valueOf(tablename);
		int num = 0;

		if (syncedID != null || syncedStrID != null) {
			try {
				switch (tbl) {
				case sales:
					SalesDB salesDB = new SalesDB(context);
					salesDB.open();
					num = salesDB.updateIsSynced(syncedID);
					break;
				case sales_customer:
					SalesCustomerDB salesCustomerDB = new SalesCustomerDB(
							context);
					salesCustomerDB.open();
					num = salesCustomerDB.updateIsSynced(syncedID);
					break;
				case sales_discount:
					SalesDiscountDB salesDiscountDB = new SalesDiscountDB(
							context);
					salesDiscountDB.open();
					num = salesDiscountDB.updateIsSynced(syncedID);
					break;
				case sales_product:
					SalesProductDB salesProductDB = new SalesProductDB(context);
					salesProductDB.open();
					num = salesProductDB.updateIsSynced(syncedID);
					break;
				case payment:
					PaymentDB paymentDB = new PaymentDB(context);
					paymentDB.open();
					num = paymentDB.updateIsSynced(syncedID);
					break;
				case log_cancel_product:
					LogCancelProductDB logCancelProductDB = new LogCancelProductDB(
							context);
					logCancelProductDB.open();
					num = logCancelProductDB.updateIsSynced(syncedID);
					break;
				case log_cash:
					LogCashDB logCashDB = new LogCashDB(context);
					logCashDB.open();
					num = logCashDB.updateIsSynced(syncedID);
					break;
				case log_user_time:
					LogUserTimeSheetDB logUserTimeSheetDB = new LogUserTimeSheetDB(
							context);
					logUserTimeSheetDB.open();
					num = logUserTimeSheetDB.updateIsSynced(syncedID);
					break;
				case user_image:
					break;
				case user_sales_summary:
					UserSalesSummaryDB userSalesSummaryDB = new UserSalesSummaryDB(
							context);
					userSalesSummaryDB.open();
					num = userSalesSummaryDB.updateIsSynced(syncedStrID);
					break;
				case history_clear_cache:
					break;
				case history_sync:
					break;
				case stock:
					break;
				case customer:
					CustomerDB customerDB = new CustomerDB(context);
					customerDB.open();
					num = customerDB.updateIsSynced(syncedStrID);
					break;
				default:
					break;
				}
			} catch (Throwable t) {
				Log.e("pos_error", t.toString());
			}
		}
	}

	public static String GetUrlForPush(String tablename) {
		TablesForPush tbl = TablesForPush.valueOf(tablename);
		String url = "";

		switch (tbl) {
		case customer:
			url = URL_PUSH_CUSTOMER;
			break;
		case log_user_time:
			url = URL_PUSH_LOG_USER_TIME;
			break;
		default:
			url = URL_PUSH_DEFAULT;
			break;
		}
		return url;
	}

	public enum TablesForPush {
		sales, sales_customer, sales_discount, sales_product, payment, log_cancel_product, log_cash, log_user_time, user_image, user_sales_summary, history_clear_cache, history_sync,

		// both
		stock, customer
	}

	// ------PULL----------------------------------------------------------------------------------------

	private static final String URL_PULL_DEFAULT = "http://unboggleit.com/salesInv/webservice/sync_pull_sis_default.php";
	private static final String URL_PULL_USER = "http://unboggleit.com/salesInv/webservice/sync_pull_sis_user.php";
	private static final String URL_PULL_POS_SETTINGS = "http://unboggleit.com/salesInv/webservice/sync_pull_sis_possettings.php";
	private static final String URL_PULL_RECEIPT_DETAIL = "http://unboggleit.com/salesInv/webservice/sync_pull_sis_receiptdetail.php";

	public static List<NameValuePair> GetDataForPull(Context context,
			String tablename) {
		List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
		nvp.add(new BasicNameValuePair("username", "admin"));
		nvp.add(new BasicNameValuePair("password", "@dmin"));
		nvp.add(new BasicNameValuePair("tablename", tablename));

		// nvp.add(new BasicNameValuePair("sqlcondition",
		// " where UserName = 'gela'")); //where last_updated >
		// last_sync_hisotry_date

		String condition = "";
		String address = "";

		//TODO: uncomment after testing
		//gela's virtual mac is D0:B8:DA:3C:69:BD
		try {
			//WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			//address = wimanager.getConnectionInfo().getMacAddress();
			address = "temp_mac_address";
		} catch (Exception e) {
			//Log.e("pos_error", "GetMacAddress: " + e.toString());
		}

		TablesForPull tbl = TablesForPull.valueOf(tablename);
		try {
			switch (tbl) {
			case pos_settings:
				condition = " WHERE p.mac_address = '" + address + "'";
				break;
			case stock:
				condition = " WHERE branch_id = (SELECT branch_id FROM pos_settings WHERE mac_address = '"
						+ address + "' )";
				break;
			case receipt_detail:
				condition = " WHERE p.mac_address = '" + address + "'";
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.e("pos_error", "GetDataForPull: " + e.toString());
		}

		nvp.add(new BasicNameValuePair("sqlcondition", condition));
		return nvp;
	}

	public static void AddUpdatePosData(Context context, JSONArray jArray,
			String tablename) {
		try {
			TablesForPull tbl = TablesForPull.valueOf(tablename);
			int num = 0;

			switch (tbl) {
			case pos_settings:
				ArrayList<PosSettings> posSettings_list = new ArrayList<PosSettings>();

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject c = jArray.getJSONObject(i);

					PosSettings posSettings = new PosSettings();
					posSettings.branch_id = c
							.getInt(PosSettingsDB.KEY_BRANCH_ID);
					posSettings.branch_name = c
							.getString(PosSettingsDB.KEY_BRANCH_NAME);
					posSettings.is_manual = c
							.getInt(PosSettingsDB.KEY_IS_MANUAL) > 0;
					if (!posSettings.is_manual) {
						posSettings.sync_frequency = c
								.getInt(PosSettingsDB.KEY_SYNC_FREQUENCY);
						posSettings.str_sync_time = c
								.getString(PosSettingsDB.KEY_SYNC_TIME);
					}
					posSettings.clear_frequency = c
							.getInt(PosSettingsDB.KEY_CLEAR_FREQUENCY);
					posSettings_list.add(posSettings);
				}

				if (!posSettings_list.isEmpty()) {
					PosSettingsDB posSettingsDB = new PosSettingsDB(context);
					posSettingsDB.open();
					num = posSettingsDB.addPosSetting(posSettings_list);
				}
				break;
			case user:
				ArrayList<User> user_list = new ArrayList<User>();

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject c = jArray.getJSONObject(i);

					User user = new User();
					user.user_id = c.getInt(UserDB.KEY_USER_ID);
					user.username = c.getString(UserDB.KEY_USERNAME);
					user.password = c.getString(UserDB.KEY_PASSWORD);
					user.is_admin = c.getInt(UserDB.KEY_IS_ADMIN) > 0;
					user_list.add(user);
				}

				if (!user_list.isEmpty()) {
					UserDB userDB = new UserDB(context);
					userDB.open();
					num = userDB.adduser(user_list);
				}
				break;
			case product_category:
				ArrayList<ProductCategory> product_category_list = new ArrayList<ProductCategory>();

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject c = jArray.getJSONObject(i);

					ProductCategory productCategory = new ProductCategory();
					productCategory.category_id = c
							.getInt(ProductCategoryDB.KEY_CATEGORY_ID);
					productCategory.name = c
							.getString(ProductCategoryDB.KEY_NAME);
					productCategory.sortorder = c
							.getInt(ProductCategoryDB.KEY_SORTORDER);
					product_category_list.add(productCategory);
				}

				if (!product_category_list.isEmpty()) {
					ProductCategoryDB productCategoryDB = new ProductCategoryDB(
							context);
					productCategoryDB.open();
					num = productCategoryDB
							.addCategories(product_category_list);
				}
				break;
			case product:
				ArrayList<Product> product_list = new ArrayList<Product>();

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject c = jArray.getJSONObject(i);

					Product product = new Product();
					product.product_id = c.getInt(ProductDB.KEY_PRODUCT_ID);
					product.name = c.getString(ProductDB.KEY_NAME);
					product.price = c.getDouble(ProductDB.KEY_PRICE);
					product.category_id = c.getInt(ProductDB.KEY_CATEGORY_ID);
					product.sortorder = c.getInt(ProductDB.KEY_SORTORDER);
					product_list.add(product);
				}

				if (!product_list.isEmpty()) {
					ProductDB productDB = new ProductDB(context);
					productDB.open();
					num = productDB.addProduct(product_list);
				}
				break;
			case ingredient:
				ArrayList<Ingredient> ingredient_list = new ArrayList<Ingredient>();

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject c = jArray.getJSONObject(i);

					Ingredient ingredient = new Ingredient();
					ingredient.id = c.getInt(IngredientDB.KEY_INGREDIENT_ID);
					ingredient.name = c.getString(IngredientDB.KEY_NAME);
					ingredient.unit = c.getString(IngredientDB.KEY_UNIT);
					ingredient_list.add(ingredient);
				}

				if (!ingredient_list.isEmpty()) {
					IngredientDB ingredientDB = new IngredientDB(context);
					ingredientDB.open();
					num = ingredientDB.addIngredient(ingredient_list);
				}
				break;
			case recipe:
				ArrayList<Recipe> recipe_list = new ArrayList<Recipe>();

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject c = jArray.getJSONObject(i);

					Recipe recipe = new Recipe();
					recipe.recipe_id = c.getInt(RecipeDB.KEY_RECIPE_ID);
					recipe.product_id = c.getInt(RecipeDB.KEY_PRODUCT_ID);
					recipe.ingredient_id = c.getInt(RecipeDB.KEY_INGREDIENT_ID);
					recipe.ingredient_qty = c
							.getDouble(RecipeDB.KEY_INGREDIENT_QUANTITY);
					recipe_list.add(recipe);
				}

				if (!recipe_list.isEmpty()) {
					RecipeDB recipeDB = new RecipeDB(context);
					recipeDB.open();
					num = recipeDB.addRecipe(recipe_list);
				}
				break;
			case discount:
				break;
			case stock_type:
				ArrayList<StockType> stocktype_list = new ArrayList<StockType>();

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject c = jArray.getJSONObject(i);

					StockType stockType = new StockType();
					stockType.stock_type_id = c
							.getInt(StockTypeDB.KEY_STOCK_TYPE_ID);
					stockType.name = c.getString(StockTypeDB.KEY_NAME);
					stocktype_list.add(stockType);
				}

				if (!stocktype_list.isEmpty()) {
					StockTypeDB stockTypeDB = new StockTypeDB(context);
					stockTypeDB.open();
					num = stockTypeDB.addStockType(stocktype_list);
				}
				break;
			case receipt_detail:
				ArrayList<ReceiptDetail> receiptdetail_list = new ArrayList<ReceiptDetail>();

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject c = jArray.getJSONObject(i);

					ReceiptDetail receiptDetail = new ReceiptDetail();
					receiptDetail.store_name = c
							.getString(ReceiptDetailDB.KEY_STORE_NAME);
					receiptDetail.operated_by = c
							.getString(ReceiptDetailDB.KEY_OPERATED_BY);
					receiptDetail.address = c
							.getString(ReceiptDetailDB.KEY_ADDRESS);
					receiptDetail.permit_no = c
							.getString(ReceiptDetailDB.KEY_PERMIT_NO);
					receiptDetail.tin_no = c
							.getString(ReceiptDetailDB.KEY_TIN_NO);
					receiptDetail.serial_no = c
							.getString(ReceiptDetailDB.KEY_SERIAL_NO);
					receiptDetail.accr_no = c
							.getString(ReceiptDetailDB.KEY_ACCR_NO);
					receiptDetail.min_no = c
							.getString(ReceiptDetailDB.KEY_MIN_NO);
					receiptDetail.message = c
							.getString(ReceiptDetailDB.KEY_MESSAGE);
					receiptdetail_list.add(receiptDetail);
				}

				if (!receiptdetail_list.isEmpty()) {
					ReceiptDetailDB receiptDetailDB = new ReceiptDetailDB(
							context);
					receiptDetailDB.open();
					num = receiptDetailDB
							.addAddReceiptDetail(receiptdetail_list);
				}
				break;
			case stock:
				ArrayList<Stock> stock_list = new ArrayList<Stock>();

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject c = jArray.getJSONObject(i);

					Stock stock = new Stock();
					stock.stock_id = c.getInt(StockDB.KEY_STOCK_ID);
					stock.stock_type_id = c.getInt(StockDB.KEY_STOCK_TYPE_ID);
					stock.id = c.getInt(StockDB.KEY_ID);
					stock.quantity = c.getDouble(StockDB.KEY_QUANTITY);
					stock.str_last_updated_date = c
							.getString(StockDB.KEY_LAST_UPDATED_DATE);
					stock.last_updated_user_id = c
							.getInt(StockDB.KEY_LAST_UPDATED_USER_ID);
					stock_list.add(stock);
				}

				if (!stock_list.isEmpty()) {
					StockDB stockDB = new StockDB(context);
					stockDB.open();
					num = stockDB.addStock(stock_list);
				}
				break;
			case customer:
				ArrayList<Customer> customer_list = new ArrayList<Customer>();

				for (int i = 0; i < jArray.length(); i++) {
					JSONObject c = jArray.getJSONObject(i);

					Customer customer = new Customer();
					customer.customer_id = c
							.getString(CustomerDB.KEY_CUSTOMER_ID);
					customer.first_name = c
							.getString(CustomerDB.KEY_FIRST_NAME);
					customer.last_name = c.getString(CustomerDB.KEY_LAST_NAME);
					customer.address = c.getString(CustomerDB.KEY_ADDRESS);
					customer.address_landmark = c
							.getString(CustomerDB.KEY_ADDRESS_LANDMARK);
					customer.tel_no = c.getString(CustomerDB.KEY_TEL_NO);
					customer.mobile_no = c.getString(CustomerDB.KEY_MOBILE_NO);
					customer.last_updated_user_id = c
							.getInt(CustomerDB.KEY_LAST_UPDATED_USER_ID);
					customer.str_last_updated_date = c
							.getString(CustomerDB.KEY_LAST_UPDATED_DATE);
					customer_list.add(customer);
				}

				if (!customer_list.isEmpty()) {
					CustomerDB customerDB = new CustomerDB(context);
					customerDB.open();
					num = customerDB.addCustomer(customer_list);
				}
				break;
			default:
				break;
			}
		} catch (Throwable t) {
			Log.e("pos_error", "AddUpdateDeletePosData: " + t.toString());
		}
	}

	public static String GetUrlForPull(String tablename) {
		TablesForPull tbl = TablesForPull.valueOf(tablename);
		String url = "";

		switch (tbl) {
		case user:
			url = URL_PULL_USER;
			break;
		case pos_settings:
			url = URL_PULL_POS_SETTINGS;
			break;
		case receipt_detail:
			url = URL_PULL_RECEIPT_DETAIL;
			break;
		default:
			url = URL_PULL_DEFAULT;
			break;
		}
		return url;
	}

	public enum TablesForPull {
		pos_settings, user, product, product_category, ingredient, recipe, discount, stock_type, receipt_detail,

		// both
		stock, customer
	}
}
