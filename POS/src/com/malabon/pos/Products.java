package com.malabon.pos;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Products extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GridLayout grid = (GridLayout) findViewById(R.id.productsGrid);
		grid.setRowCount(1);
		grid.setColumnCount(1);

		LinearLayout ll = new LinearLayout(this, null,
				R.style.item_wrapper_even);

		TextView label = new TextView(this, null, R.style.h3_product);
		label.setText("Keytihelow");

		TextView price = new TextView(this, null, R.style.h3_price);
		price.setText("123.45");

		ll.addView(label, 0);
		ll.addView(price, 1);

		grid.addView(ll, new GridLayout.LayoutParams(GridLayout.spec(0),
				GridLayout.spec(0)));

		setContentView(grid);
	}
}
