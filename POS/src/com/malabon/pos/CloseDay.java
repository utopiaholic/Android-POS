package com.malabon.pos;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.malabon.object.Sync;

public class CloseDay extends Activity {

	DecimalFormat df = new DecimalFormat("0.00");

	EditText txtCounted, txtRegister, txtDeposit;
	TextView txtCashExpected, txtCashDifference;
	double cashExpected, cashDifference, counted, register, deposit;
	int user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_close_day);

		SharedPreferences prefs = this.getSharedPreferences("com.malabon.pos",
				Context.MODE_PRIVATE);
		user = prefs.getInt("user_id", -1);

		if (user == -1)
			finish();

		cashExpected = Sync.GetUserExpectedCash(user);
		counted = 0;
		register = 0;
		deposit = 0;
		Initialize();
	}

	public void Initialize() {
		txtCounted = (EditText) findViewById(R.id.cdTxtCounted);
		txtRegister = (EditText) findViewById(R.id.cdTxtRegister);
		txtDeposit = (EditText) findViewById(R.id.cdTxtDeposit);
		txtCashExpected = (TextView) findViewById(R.id.cdTxtCashExpected);
		txtCashDifference = (TextView) findViewById(R.id.cdTxtCashDifference);

		txtRegister.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				Double val = Double.parseDouble(s.toString());
				if ((Math.round(val * 100) / 100) == (Math
						.round(register * 100) / 100))
					return;
				register = val;
				deposit = counted - register;
				txtDeposit.setText(df.format(deposit));
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		txtDeposit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				Double val = Double.parseDouble(s.toString());
				if ((Math.round(val * 100) / 100) == (Math.round(deposit * 100) / 100))
					return;
				deposit = val;
				register = counted - deposit;
				txtRegister.setText(df.format(register));
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		SetAmounts();
	}

	private void SetAmounts() {
		txtCashExpected.setText(df.format(cashExpected));
		txtCounted.setText(df.format(counted));

		cashDifference = counted - cashExpected;

		txtCashDifference.setText(df.format(cashDifference));

		txtDeposit.setText(df.format(deposit));
		txtRegister.setText(df.format(register));
	}

	public void cancel(View view) {
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_CANCELED, resultIntent);
		finish();
	}

	public void endDay(View view) {
		if (Sync.Sales != null)
			Sync.AddUserSalesSummary(this, user, counted, cashExpected);
		else
			Sync.AddUserSalesSummary(this, user, 0, cashExpected);
		
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	public void clickAmount(View view) {
		int id = view.getId();
		double append = 0;
		switch (id) {
		case R.id.cdP1:
			append = 1;
			break;
		case R.id.cdP10:
			append = 10;
			break;
		case R.id.cdP100:
			append = 100;
			break;
		case R.id.cdP1000:
			append = 1000;
			break;
		case R.id.cdP20:
			append = 20;
			break;
		case R.id.cdP200:
			append = 200;
			break;
		case R.id.cdP25c:
			append = 0.25;
			break;
		case R.id.cdP5:
			append = 5;
			break;
		case R.id.cdP50:
			append = 50;
			break;
		case R.id.cdP500:
			append = 500;
			break;
		}
		counted += append;
		deposit += append;
		SetAmounts();
	}

	public void clickDigit(View view) {
		boolean negate = false;

		String amt = txtCounted.getText().toString();
		amt = amt.replace(".", ""); // remove decimal place
		int id = view.getId();
		switch (id) {
		case R.id.btn0n:
			amt += "0";
			break;
		case R.id.btn1n:
			amt += "1";
			break;
		case R.id.btn2n:
			amt += "2";
			break;
		case R.id.btn3n:
			amt += "3";
			break;
		case R.id.btn4n:
			amt += "4";
			break;
		case R.id.btn5n:
			amt += "5";
			break;
		case R.id.btn6n:
			amt += "6";
			break;
		case R.id.btn7n:
			amt += "7";
			break;
		case R.id.btn8n:
			amt += "8";
			break;
		case R.id.btn9n:
			amt += "9";
			break;
		case R.id.btnClr:
			amt = "000";
			break;
		case R.id.btnNeg:
			negate = true;
			break;
		}

		amt = amt.substring(0, amt.length() - 2) + "."
				+ amt.substring(amt.length() - 2, amt.length());

		counted = Double.parseDouble(amt);
		if (negate)
			counted = counted * (-1);
		deposit = counted - register;
		SetAmounts();
	}
}
