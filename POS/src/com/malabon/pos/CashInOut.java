package com.malabon.pos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.malabon.object.Sync;

public class CashInOut extends Activity {

	EditText txtCash, txtCashDescription;
	static final int REQUEST_CASH_IN = 98;
	static final int REQUEST_CASH_OUT = 99;
	int user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cash_in_out);

		SharedPreferences prefs = this.getSharedPreferences("com.malabon.pos",
				Context.MODE_PRIVATE);
		user = prefs.getInt("user_id", -1);

		txtCash = (EditText) findViewById(R.id.txtCashInOut);
		txtCashDescription = (EditText) findViewById(R.id.txtCashDescription);
	}

	public void close(View view) {
		finish();
	}

	public void cashOut(View view) {
		cashInOutAuthorization(REQUEST_CASH_OUT);
	}

	public void cashIn(View view) {
		cashInOutAuthorization(REQUEST_CASH_IN);
	}

	private void addCashInOut(int iscashin) {
		double amount = Double.parseDouble(txtCash.getText().toString().trim());
		double cashonhand = Sync.GetUserExpectedCash(user);

		if ((iscashin == 0 && amount <= cashonhand) || (iscashin == 1)) {
			boolean isSuccess = Sync.AddCashInOut(this, iscashin, amount,
					txtCashDescription.getText().toString().trim());

			if (isSuccess) {
				showToast("Transaction successful");
				finish();
			} else
				showToast("Transaction unsuccessful. Please try again.");
		} else {
			showToast("Transaction failed. Cash out amount is more than cash on hand.");
		}
	}

	private void cashInOutAuthorization(int request) {
		if (txtCash.getText().toString().length() > 0) {
			Intent intent = new Intent(this, Login.class);
			SharedPreferences prefs = this.getSharedPreferences(
					"com.malabon.pos", Context.MODE_PRIVATE);
			prefs.edit().putInt("RequestType", 5).commit();
			this.startActivityForResult(intent, request);
		}
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CASH_IN)
				addCashInOut(1);
			if (requestCode == REQUEST_CASH_OUT)
				addCashInOut(0);
		}
	}
}
