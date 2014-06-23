package com.malabon.pos;

import java.io.Serializable;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.malabon.object.Item;
import com.malabon.object.Sale;

public class EditOrders extends Activity {
	Sale sale = null;
	DecimalFormat df = new DecimalFormat("0.00");
	static final int PAYMENT_REQUEST = 14;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_orders);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
		// Get data via the key
		sale = (Sale) extras.get("Sale_EditOrders");
		if (sale != null) {
			// do something with the data
			// display
			loadDisplay();
		}
	}

	public void loadDisplay() {
		TextView customerName = (TextView) findViewById(R.id.editOrdersCustomerName);
		customerName.setText(sale.customer.first_name + " "
				+ sale.customer.last_name);

		int count = 0;
		TableLayout table = (TableLayout) findViewById(R.id.tableLayout);
		table.removeAllViews();
		for (Item item : sale.items) {
			LayoutInflater vi = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			TableRow newRow = (TableRow) vi.inflate(
					R.layout.product_edit_orders_row, null);

			TextView temp = (TextView) newRow.findViewById(R.id.cartItemName);
			temp.setText(item.name);
			temp = (TextView) newRow.findViewById(R.id.cartItemPrice);
			temp.setText(df.format(item.price));
			temp = (TextView) newRow.findViewById(R.id.cartItemQty);
			temp.setText(String.valueOf(item.quantity)); // "1");

			ImageButton deleteButton = (ImageButton) newRow
					.findViewById(R.id.cartBtnDelete);
			deleteButton.setTag(count);
			deleteButton.setOnClickListener(deleteClick);
			ImageButton moreQtyButton = (ImageButton) newRow
					.findViewById(R.id.cartMoreQty);
			moreQtyButton.setTag(count);
			moreQtyButton.setOnClickListener(moreQtyClick);
			ImageButton lessQtyButton = (ImageButton) newRow
					.findViewById(R.id.cartLessQty);
			lessQtyButton.setTag(count);
			lessQtyButton.setOnClickListener(lessQtyClick);

			count++;

			table.addView(newRow, 0);
		}

		updateTotals();
		return;
	}

	private void updateTotals() {
		sale.computeTotal();

		TextView temp = (TextView) findViewById(R.id.editOrdersTotal);
		temp.setText(df.format(sale.total));
		temp = (TextView) findViewById(R.id.editOrdersNetTotal);
		temp.setText(df.format(sale.netTotal));
		temp = (TextView) findViewById(R.id.editOrdersTaxTotal);
		temp.setText(df.format(sale.taxTotal));
	}

	public OnClickListener deleteClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			ImageButton deleteButton = (ImageButton) arg0;
			int index = (Integer) deleteButton.getTag();
			Item item = sale.items.get(index);
			sale.items.remove(index);
			sale.deletedItems.add(item);
			loadDisplay();
		}
	};

	public OnClickListener moreQtyClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			addMinusQty(arg0, 1);
		}
	};

	public OnClickListener lessQtyClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			addMinusQty(arg0, -1);
		}
	};

	private void addMinusQty(View arg0, int number) {

		ImageButton qtyButton = (ImageButton) arg0;
		int index = (Integer) qtyButton.getTag();
		Item item = sale.items.get(index);

		if (item.availableQty == 0 && number > 0) {
			showToast("Out of stock!");
			return;
		}

		TableRow newRow = (TableRow) arg0.getParent().getParent();
		TextView editText = (TextView) newRow.findViewById(R.id.cartItemQty);
		int num = Integer.parseInt(editText.getText().toString()) + number;
		if (num > 0)
			editText.setText(String.valueOf(num));

		item.quantity = Integer.parseInt(editText.getText().toString());
		item.availableQty -= number;

		updateTotals();
	}

	public void pay(View view) {
		Intent intent = new Intent(this, PaymentActivity.class);
		intent.putExtra("Sale_Payment", sale);
		startActivityForResult(intent, PAYMENT_REQUEST);
	}

	public void close(View view) {
		Intent resultIntent = new Intent();
		Bundle b = new Bundle();
		b.putSerializable("Sale_EditOrders", (Serializable) sale);
		resultIntent.putExtras(b);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
			case (PAYMENT_REQUEST): {
				if (resultCode == Activity.RESULT_FIRST_USER) {
					Intent resultIntent = new Intent();
					setResult(Activity.RESULT_FIRST_USER, resultIntent);
					finish();
				}
			}
		}
	}
}
