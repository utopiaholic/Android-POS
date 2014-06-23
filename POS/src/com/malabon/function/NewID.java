package com.malabon.function;

import android.content.Context;

import com.malabon.database.CustomerDB;
import com.malabon.object.Customer;
import com.malabon.object.Sync;

public class NewID {

	private int count;
	private static int branchCustomerIDcount = 0;

	public String GetDefaultCustomerID() {
		return AppendBranchID(0);
	}

	private static String GetBranchID() {
		if (Sync.posSettings != null)
			return String.valueOf(Sync.posSettings.branch_id);
		else
			return "";
	}

	private int getBranchCustomerCount() {
		String branchID = GetBranchID();
		if (branchCustomerIDcount == 0) {
			for (Customer c : Sync.Customers) {
				if (c.customer_id.charAt(0) == branchID.charAt(0)) {
					branchCustomerIDcount++;
				}
			}
		} else {
			branchCustomerIDcount++;
		}
		return branchCustomerIDcount;
	}

	public String GetCustomerID(Context context) {
		CustomerDB customerDB = new CustomerDB(context);
		customerDB.open();

		count = getBranchCustomerCount() + 1;

		return AppendBranchID(count);
	}

	public String GetSalesSummaryID(String userString, String dateString) {
		String branchID = GetBranchID();
		return branchID + userString + dateString;
	}

	public String GetReceiptID(String userString, String salesString) {
		String branchID = GetBranchID();
		return branchID + userString + salesString;
	}

	public String AppendBranchID(int count) {
		String branchID = GetBranchID();
		return branchID + "_" + count;
	}

	public static String GetStringID(int intid) {
		String branchID = GetBranchID();
		return branchID + "_" + String.valueOf(intid);
	}
}
