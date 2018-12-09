package com.akr.exch;

import java.time.LocalTime;
import java.util.Random;

/**
 * Contains an order - buy or sell
 * 
 * @author Akr
 *
 */

public class Order {
	private int side;				//1 is Buy and 2 is Sell
	private int quantity;
	private Float price;
	private String symbol;
	private String time;            //timestamp
	private int filledQty;			//fill received
	private int orderId;
	private String msgType;			//new order=D, modify order=M, cancel order=C
	Random rand = new Random();
	int Low = 1000;
	int High = 9999;
	
	//time optional
	public Order(int side, int quantity, Float price, String symbol, int orderId, String msgType, String time) {
		super();
		this.side = side;
		this.quantity = quantity;
		this.price = price;
		this.symbol = symbol;
		this.orderId = orderId;
		this.msgType = msgType;
		
		if(time.equals("00")) {
			LocalTime lt = LocalTime.now();
			this.time = lt.toString() + rand.nextInt(High-Low)+ Low;
		} else {
			this.time = time;
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		System.out.println(" in equals");
	  if (this == obj) {
		  System.out.println("equals called");
	    return true;
	  }
	  if (obj == null) {
	    return false;
	  }
	  if (getClass() != obj.getClass()) {
	    return false;
	  }

	  Order that = (Order) obj;
	  return (this.side == that.side) &&
	         (this.price.equals(that.price)) &&
	         (this.time.equals(that.time)) &&
	         (this.orderId == that.orderId);
	}
	
	public String getMsgType() {
		return msgType;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getFilledQty() {
		return filledQty;
	}

	public void setFilledQty(int filledQty) {
		this.filledQty = filledQty;
	}

	public int getSide() {
		return side;
	}

	public int getQuantity() {
		return quantity;
	}

	public Float getPrice() {
		return price;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getTime() {
		return time;
	}

	public int getOrderId() {
		return orderId;
	}

	@Override
	public String toString() {
		return "orderId=" + orderId + ", symbol=" + symbol + ", side=" + side + ", quantity=" + quantity + ", price=" + price + 
				", time=" + time + ", filledQty=" + filledQty;
	}
		
}
