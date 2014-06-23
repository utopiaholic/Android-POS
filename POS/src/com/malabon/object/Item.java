package com.malabon.object;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Item implements Serializable {
	public int id;
	public String name;
	public double price;
	public String unit;
	public int quantity;
	public int category_id;
	public int availableQty;
	public int sortorder;
	//public Boolean can_be_taken_out;
}