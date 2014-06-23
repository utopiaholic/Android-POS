package com.malabon.object;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Payment implements Serializable {
	public double balance;
	public double cash;
	private double change;

	public double getChange() {
		change = cash - balance;
		return change;
	}

	public double getCash(double amount) {
		cash += amount;
		return cash;
	}

	public Boolean confirmPayment() {
		if (change >= 0 && balance > 0)
			return true;
		else
			return false;
	}
}
