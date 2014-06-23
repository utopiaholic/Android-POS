package com.malabon.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.malabon.function.NewID;

@SuppressWarnings("serial")
public class Sale implements Serializable {

	public List<Item> items = new ArrayList<Item>();
	public List<Item> deletedItems = new ArrayList<Item>();
	public double total;
	public double taxTotal;
	public double netTotal;
	public double cash;
	public Customer customer;
	public int user;

	public int orderType;
	public Discount discount;
	
	//sync
	public int id;
	public int product_id;
	public int product_quantity;
	public String sales_id;
	public String strDate;
	public String payment_id;
	public String receipt_id;
	public String customer_id;
	public int discount_id;
	public double discount_amount;
	private double maxtotal;
	
	public void computeTotal() {
		total = getSubtotal();		
		
		double totalDiscount = 0;
		if (discount != null) {
			totalDiscount = getTotalDiscount();
			discount.amountPhp = totalDiscount;
		}
		
		total = total - totalDiscount;
		netTotal = total * 0.88;
		taxTotal = total * 0.12;
	}
	
	public void computeSeniorTotal(double maxTotal) {
		total = getSubtotal();	
		netTotal = total * 0.88;
		taxTotal = total * 0.12;
		maxtotal = maxTotal;
		
		double totalDiscount = 0;
		if (discount != null) {
			totalDiscount = getTotalDiscount();
			discount.amountPhp = totalDiscount;
		}

		total = total - totalDiscount;
	}
	
	public double getTotalDiscount(){
		double discountTotal = 0;
		
		//compute senior discount
		if(discount.discount_id == 1 && this.maxtotal != 0){
			
			if(total < maxtotal) {
				discountTotal = total * 0.88 * discount.percentage;
			} else {
			    discountTotal = maxtotal * 0.88 *discount.percentage;
			}
			
		} else {
			discountTotal = total * discount.percentage;
		}
		
		return discountTotal;
	}
	
	public double getSubtotal(){
		double subtotal = 0;
		for (Item item : items) {
			subtotal += (item.price * item.quantity);
		}
		return subtotal;
	}
	
	public double getChange(){
		return cash - total;
	}

	public void setDefaultCustomer() {
		this.customer = new Customer();
		customer.customer_id = new NewID().GetDefaultCustomerID();
		customer.first_name = "Default";
		customer.last_name = "Customer";
	}
}
