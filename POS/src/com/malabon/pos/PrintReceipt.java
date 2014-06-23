package com.malabon.pos;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.malabon.object.Item;
import com.malabon.object.ReceiptDetail;
import com.malabon.object.Sync;

public class PrintReceipt extends Activity {

	String cashier;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
	DecimalFormat df = new DecimalFormat("0.00");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_receipt);

		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			finish();
			return;
		}

		cashier = extras.getString("cashier");
		Initialize();
	}

	private void Initialize() {
		ReceiptDetail rd = new ReceiptDetail();
		rd = Sync.receiptDetail;

		TextView temp = (TextView) findViewById(R.id.rcptStoreName);
		temp.setText(rd.store_name);
		temp = (TextView) findViewById(R.id.rcptOperation);
		temp.setText(temp.getText() + rd.operated_by);
		temp = (TextView) findViewById(R.id.rcptAddress);
		temp.setText(rd.address);
		temp = (TextView) findViewById(R.id.rcptPermit);
		temp.setText(temp.getText() + rd.permit_no);
		temp = (TextView) findViewById(R.id.rcptTin);
		temp.setText(temp.getText() + rd.tin_no);
		temp = (TextView) findViewById(R.id.rcptSerial);
		temp.setText(temp.getText() + rd.serial_no);
		temp = (TextView) findViewById(R.id.rcptAccr);
		temp.setText(temp.getText() + rd.accr_no);
		temp = (TextView) findViewById(R.id.rcptMin);
		temp.setText(temp.getText() + rd.min_no);

		temp = (TextView) findViewById(R.id.rcptTxnType);
		switch (Sync.lastSale.orderType) {
		case 1:
			temp.setText("Dine-in");
			break;
		case 2:
			temp.setText("Take-out");
			break;
		case 3:
			temp.setText("Delivery");
			break;
		}

		temp = (TextView) findViewById(R.id.rcptDate);
		temp.setText(dateFormat.format(new Date()));
		temp = (TextView) findViewById(R.id.rcptCashier);
		temp.setText("Cashier: " + cashier);
		temp = (TextView) findViewById(R.id.rcptCustomer);
		temp.setText("Customer: " + Sync.lastSale.customer.first_name + " "
				+ Sync.lastSale.customer.last_name);

		temp = (TextView) findViewById(R.id.rcptSubtotalAmt);
		temp.setText(df.format(Sync.lastSale.getSubtotal()));
		temp = (TextView) findViewById(R.id.rcptNetTotalAmt);
		temp.setText(df.format(Sync.lastSale.netTotal));
		temp = (TextView) findViewById(R.id.rcptTaxTotalAmt);
		temp.setText(df.format(Sync.lastSale.taxTotal));
		temp = (TextView) findViewById(R.id.rcptDiscountTotalAmt);
		temp.setText(df.format(Sync.lastSale.getTotalDiscount() * -1));
		temp = (TextView) findViewById(R.id.rcptTotalAmt);
		temp.setText(df.format(Sync.lastSale.total));
		temp = (TextView) findViewById(R.id.rcptCashAmt);
		temp.setText(df.format(Sync.lastSale.cash));
		temp = (TextView) findViewById(R.id.rcptChangeAmt);
		temp.setText(df.format(Sync.lastSale.getChange()));

		temp = (TextView) findViewById(R.id.rcptMessage);
		temp.setText(rd.message);

		InflateItems();
	}

	private void InflateItems() {
		TableLayout table = (TableLayout) findViewById(R.id.rcptItemsTable);
		table.removeAllViews();
		for (Item item : Sync.lastSale.items) {
			LayoutInflater vi = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			TableRow newRow = (TableRow) vi.inflate(R.layout.receipt_item_row,
					null);

			TextView temp = (TextView) newRow.findViewById(R.id.riQty);
			temp.setText(String.valueOf(item.quantity));
			temp = (TextView) newRow.findViewById(R.id.riProduct);
			temp.setText(item.name);
			temp = (TextView) newRow.findViewById(R.id.riPrice);
			temp.setText(df.format(item.price * item.quantity));

			table.addView(newRow, 0);
		}
	}

	public void print(View v) {
		showToast("Print");
	}

	public void cancel(View v) {
		finish();
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}
}
