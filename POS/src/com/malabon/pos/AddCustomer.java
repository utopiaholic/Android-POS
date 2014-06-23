package com.malabon.pos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.malabon.object.Customer;
import com.malabon.object.Sync;

public class AddCustomer extends Activity {
	EditText textFirstName, textLastName, textAddress, textAddressLandMark,
			textTelNo, textMobileNo;
	LinearLayout add_view, update_view;
	String valid_number = null, valid_name = null;
	String CUSTOMER_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_customer);

		if (Sync.Customers == null)
			Sync.GetCustomers(this);

		set_Add_Update_Screen();
		String called_from = getIntent().getStringExtra("called");

		if (called_from.equalsIgnoreCase("add")) {
			add_view.setVisibility(View.VISIBLE);
			update_view.setVisibility(View.GONE);
		} else {
			update_view.setVisibility(View.VISIBLE);
			add_view.setVisibility(View.GONE);
			CUSTOMER_ID = getIntent().getStringExtra("CUSTOMER_ID");

			Customer c = new Customer();
			for (Customer tmp : Sync.Customers) {
				if (tmp.customer_id.equals(CUSTOMER_ID)) {
					c = tmp;
					break;
				}
			}

			textFirstName.setText(c.first_name);
			textLastName.setText(c.last_name);
			textAddress.setText(c.address);
			textAddressLandMark.setText(c.address_landmark);
			textTelNo.setText(c.tel_no);
			textMobileNo.setText(c.mobile_no);
		}
	}

	public void addCustomer(View view) {
		if ((Is_Valid_Name(textFirstName) == true)
				&& (Is_Valid_Name(textLastName) == true)
				&& (textAddress != null)
				&& ((Is_Valid__Number(7, textTelNo) == true) || (Is_Valid__Number(
						11, textMobileNo) == true))

		) {
			Customer c = new Customer();
			c.first_name = textFirstName.getText().toString();
			c.last_name = textLastName.getText().toString();
			c.address = textAddress.getText().toString();
			c.address_landmark = textAddressLandMark.getText().toString();
			c.tel_no = textTelNo.getText().toString();
			c.mobile_no = textMobileNo.getText().toString();

			int result = Sync.AddCustomer(this, c);
			switch (result) {
			case 1:
				showToast("Data inserted successfully");
				break;
			case 2:
				showToast("User's contact number already exists");
				break;
			default:
				showToast("Data insertion unsuccessful");
				break;
			}
			cancel(null);
		} else {
			showToast("Complete all required fields.");
		}
	}

	public void updateCustomer(View view) {
		if ((Is_Valid_Name(textFirstName) == true)
				&& (Is_Valid_Name(textLastName) == true)
				&& (textAddress != null)
				&& ((Is_Valid__Number(7, textTelNo) == true) || (Is_Valid__Number(
						11, textMobileNo) == true))

		) {
			Customer c = new Customer();
			c.customer_id = CUSTOMER_ID;
			c.first_name = textFirstName.getText().toString();
			c.last_name = textLastName.getText().toString();
			c.address = textAddress.getText().toString();
			c.address_landmark = textAddressLandMark.getText().toString();
			c.tel_no = textTelNo.getText().toString();
			c.mobile_no = textMobileNo.getText().toString();

			if (Sync.UpdateCustomer(this, c) > 0)
				showToast("Data updated successfully");
			else
				showToast("Data update unsuccessful");
			cancel(null);
		} else {
			showToast("Complete all required fields.");
		}
	}

	public void cancel(View view) {
		Intent view_user = new Intent();
		setResult(Activity.RESULT_OK, view_user);
		finish();
	}

	public void set_Add_Update_Screen() {
		textFirstName = (EditText) findViewById(R.id.textFirstName);
		textLastName = (EditText) findViewById(R.id.textLastName);
		textAddress = (EditText) findViewById(R.id.textAddress);
		textAddressLandMark = (EditText) findViewById(R.id.textAddressLandMark);
		textTelNo = (EditText) findViewById(R.id.textTelNo);
		textMobileNo = (EditText) findViewById(R.id.textMobileNo);

		add_view = (LinearLayout) findViewById(R.id.add_view);
		update_view = (LinearLayout) findViewById(R.id.update_view);

		add_view.setVisibility(View.GONE);
		update_view.setVisibility(View.GONE);
	}

	public Boolean Is_Valid__Number(int MinLen, EditText edt)
			throws NumberFormatException {
		Boolean isValid = true;
		if (edt.getText().toString().length() <= 0) {
			edt.setError("Numbers Only");
			isValid = false;
		} else if (edt.getText().toString().length() < MinLen) {
			edt.setError("Minimum length: " + MinLen);
			isValid = false;
		} else {
			isValid = true;
		}
		return isValid;
	}

	public Boolean Is_Valid_Name(EditText edt) throws NumberFormatException {
		Boolean isValid = true;
		if (edt.getText().toString().length() <= 0) {
			edt.setError("Alphabets Only");
			isValid = false;
		} else if (!edt.getText().toString().matches("[a-zA-Z ]+")) {
			edt.setError("Alphabets Only");
			isValid = false;
		} else {
			isValid = true;
		}
		return isValid;
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}
}
