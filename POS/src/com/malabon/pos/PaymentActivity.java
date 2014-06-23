package com.malabon.pos;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.malabon.object.Discount;
import com.malabon.object.Payment;
import com.malabon.object.Sale;
import com.malabon.object.Sync;

public class PaymentActivity extends Activity {

	static final int ENTER_CASH = 15;

	TextView tvPaymentTotal, tvPaymentCash, tvPaymentChange;
	String orderType;
	double cash = 0.00;
	Payment objPayment;
	DecimalFormat df = new DecimalFormat("0.00");
	Sale sale;
	Spinner cmb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_payment);

		Initialize();
		InitDiscounts();
		setAmounts(cash);
	}

	private void Initialize() {

		View rbTakeOut = findViewById(R.id.rbTakeOut);
		rbTakeOut.setEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
		// Get data via the key
		sale = (Sale) extras.get("Sale_Payment");
		if (sale != null) {
			TextView txtCustomerName = (TextView) findViewById(R.id.paymentCustomerName);
			txtCustomerName.setText(sale.customer.first_name + " "
					+ sale.customer.last_name);

			sale.computeTotal();
			objPayment = new Payment();
			objPayment.balance = Double.parseDouble(df.format(sale.total));
		}

		/*
		 * for (Item item : sale.items) { if (!item.can_be_taken_out) {
		 * rbTakeOut.setEnabled(false);
		 * showToast("Products marked with (***) cannot be taken out"); break; }
		 * }
		 */

		// objPayment.cash = objPayment.getCash(cash);
	}

	private void InitDiscounts() {

		if (Sync.Discounts == null)
			Sync.GetDiscounts(this);

		cmb = (Spinner) findViewById(R.id.cmbDiscounts);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add("Select Discount");
		for (Discount d : Sync.Discounts)
			adapter.add(d.name);
		cmb.setAdapter(adapter);

		cmb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				String sel = cmb.getSelectedItem().toString();

				if (sel == "Select Discount") {
					sale.discount = null;
					setDiscount();
					return;
				}

				for (Discount d : Sync.Discounts) {
					if (d.name == sel) {
						sale.discount = d;
						double maxTotal = Sync.GetMaxTotal(getApplicationContext());
						//Check if senior discount
						if(d.discount_id == 1){
							sale.computeSeniorTotal(maxTotal);
						} else {
							sale.computeTotal();
						}

						objPayment.balance = Double.parseDouble(df
								.format(sale.total));
						setBalance();
						setDiscount();
						setChange();
						break;
					}
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				sale.discount = null;
			}
		});
	}

	private void setAmounts(double amount) {
		setBalance();

		TextView txtCash = (TextView) findViewById(R.id.paymentCash);
		txtCash.setText(df.format(objPayment.getCash(amount)));

		setChange();
	}

	private void setBalance() {
		TextView txtBalTotal = (TextView) findViewById(R.id.balTotal);
		txtBalTotal.setText(df.format(objPayment.balance));
	}

	private void setDiscount() {
		double discTotal = 0;
		if (sale.discount != null)
			discTotal = sale.discount.amountPhp * -1;

		TextView txtDiscountTotal = (TextView) findViewById(R.id.discTotal);
		txtDiscountTotal.setText(df.format(discTotal));
	}

	private void setChange() {
		TextView txtChange = (TextView) findViewById(R.id.paymentChange);
		txtChange.setText(df.format(objPayment.getChange()));
	}

	public void clickNumber(View view) {
		switch (view.getId()) {
		case R.id.btn1000:
			setAmounts(1000);
			break;
		case R.id.btn500:
			setAmounts(500);
			break;
		case R.id.btn100:
			setAmounts(100);
			break;
		case R.id.btn50:
			setAmounts(50);
			break;
		case R.id.btn20:
			setAmounts(20);
			break;
		case R.id.btn10:
			setAmounts(10);
			break;
		case R.id.btn5:
			setAmounts(5);
			break;
		case R.id.btn1:
			setAmounts(1);
			break;
		}
	}

	public void enterCash(View view) {
		Intent intent = new Intent(this, AddPayment.class);
		startActivityForResult(intent, ENTER_CASH);
	}

	public void exactCash(View view) {
		objPayment.cash = 0;
		cash = sale.total;
		setAmounts(cash);
	}

	public void cancel(View view) {
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_CANCELED, resultIntent);
		finish();
	}

	public void confirm(View view) {
		confirmPayment();
	}

	private void confirmPayment() {
		if (objPayment.confirmPayment() && IsOrderTypeSelected()) {

			commitSale();

			Intent resultIntent = new Intent();
			Sync.lastSale = sale;
			setResult(Activity.RESULT_FIRST_USER, resultIntent);
			finish();
		} else if (!objPayment.confirmPayment() && IsOrderTypeSelected()) {
			showToast("Settle balance");
		} else if (objPayment.confirmPayment() && !IsOrderTypeSelected()) {
			showToast("Select order type");
		} else {
			showToast("Settle balance and select order type");
		}
	}

	private void commitSale() {
		sale.cash = objPayment.cash;

		RadioGroup rgOrderType = (RadioGroup) findViewById(R.id.rgOrderType);
		int selectedId = rgOrderType.getCheckedRadioButtonId();
		switch (selectedId) {
		case R.id.rbDineIn:
			sale.orderType = 1;
			break;
		case R.id.rbTakeOut:
			sale.orderType = 2;
			break;
		case R.id.rbDelivery:
			sale.orderType = 3;
			break;
		}

		Sync.AddSale(this, sale);
		Sync.RefreshInventory(this);
	}

	private boolean IsOrderTypeSelected() {
		boolean isSelected = false;
		RadioGroup rgOrderType = (RadioGroup) findViewById(R.id.rgOrderType);
		int selectedId = rgOrderType.getCheckedRadioButtonId();
		if (selectedId > 0) {
			isSelected = true;
			orderType = (String) ((RadioButton) findViewById(selectedId))
					.getText();
		}
		return isSelected;
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case (ENTER_CASH): {
			if (resultCode == Activity.RESULT_OK) {
				String amt = intent.getStringExtra("cash");
				cash = Double.parseDouble(amt);
				setAmounts(cash);
			}
		}
		}
	}
}
