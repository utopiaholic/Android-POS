package com.malabon.pos;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.malabon.object.Discount;
import com.malabon.object.Sync;

public class ReceiptDiscount extends Activity {

	DecimalFormat df = new DecimalFormat("0.00");
	EditText txtPercent, txtPhp;
	Spinner cmb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_receipt_discount);
		InitDiscounts();
	}

	private void InitDiscounts() {
		txtPercent = (EditText) findViewById(R.id.txtDiscountPercent);
		txtPhp = (EditText) findViewById(R.id.txtDiscountPhp);

		if (Sync.Discounts == null)
			Sync.GetDiscounts(this);

		cmb = (Spinner) findViewById(R.id.cmbDiscounts);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add("(None)");
		for (Discount d : Sync.Discounts)
			adapter.add(d.name);
		cmb.setAdapter(adapter);

		cmb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				String sel = cmb.getSelectedItem().toString();

				if (sel == "(None)") {
					txtPercent.setEnabled(true);
					txtPhp.setEnabled(true);
					return;
				}

				txtPercent.setEnabled(false);
				txtPhp.setEnabled(false);

				for (Discount d : Sync.Discounts) {
					if (d.name == sel) {
						txtPercent.setText(df.format(d.percentage * 100));
						txtPhp.setText("");
						break;
					}
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				txtPercent.setEnabled(true);
				txtPhp.setEnabled(true);
			}
		});
	}

	public void confirm(View view) {
		EditText percent = (EditText) findViewById(R.id.txtDiscountPercent);
		EditText php = (EditText) findViewById(R.id.txtDiscountPhp);
		Float floatPercent = 0f;
		Float floatPhp = 0f;

		if (!percent.getText().toString().isEmpty()) {
			floatPercent = Float.parseFloat(percent.getText().toString());
			floatPercent /= 100;
		}

		if (!php.getText().toString().isEmpty()) {
			floatPhp = Float.parseFloat(php.getText().toString());
		}

		SharedPreferences prefs = this.getSharedPreferences("com.malabon.pos",
				Context.MODE_PRIVATE);
		prefs.edit().putFloat("discountPercent", floatPercent).commit();
		prefs.edit().putFloat("discountPhp", floatPhp).commit();
		prefs.edit().putBoolean("doNewSale", false).commit();

		setResult(Activity.RESULT_OK);
		finish();
	}

	public void cancel(View view) {
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

}
