package com.malabon.object;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Ingredient implements Serializable {
	public int id;
	public String name;
	public double availableQty;
	public String unit;
}
