package com.malabon.object;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Recipe implements Serializable {
	public int recipe_id;
	public int product_id;
	public int ingredient_id;
	public double ingredient_qty;
}
