package com.malabon.sync;

import java.util.ArrayList;
import java.util.List;

public class TableToSync {
	private static List<String> listTablePosToServer;
	private static List<String> listTableServerToPos;

	// Insert to Server
	public static List<String> GetTablePosToServer() {
		if (listTablePosToServer == null) {
			listTablePosToServer = new ArrayList<String>();

			listTablePosToServer.add("sales");
			listTablePosToServer.add("sales_customer");
			listTablePosToServer.add("sales_discount");
			listTablePosToServer.add("sales_product");
			listTablePosToServer.add("payment");
			listTablePosToServer.add("log_cancel_product");
			listTablePosToServer.add("log_cash");
			listTablePosToServer.add("log_user_time");
			// listTablePosToServer.add("user_image");
			listTablePosToServer.add("user_sales_summary");
			// listTablePosToServer.add("history_clear_cache");
			// listTablePosToServer.add("history_sync");

			// sync 2-way
			// listTablePosToServer.add("stock");
			listTablePosToServer.add("customer");
		}

		return listTablePosToServer;
	}

	// Insert, Update, Delete from POS
	public static List<String> GetTableServerToPos() {
		if (listTableServerToPos == null) {
			listTableServerToPos = new ArrayList<String>();

			listTableServerToPos.add("pos_settings");
			listTableServerToPos.add("user");
			listTableServerToPos.add("product_category");
			listTableServerToPos.add("product");
			listTableServerToPos.add("ingredient");
			listTableServerToPos.add("recipe");
			listTableServerToPos.add("discount");
			listTableServerToPos.add("stock_type");
			listTableServerToPos.add("receipt_detail");

			// sync 2-way
			listTableServerToPos.add("stock");
			listTableServerToPos.add("customer");
		} 

		return listTableServerToPos;
	}
}
