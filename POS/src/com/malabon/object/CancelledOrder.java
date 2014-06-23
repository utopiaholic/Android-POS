package com.malabon.object;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CancelledOrder implements Serializable {
	public int UserId;
	public Item CancelledItem;

	// sync
	public int id;
	public int product_id;
	public String str_date;
}
