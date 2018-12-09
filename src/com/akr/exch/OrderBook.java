package com.akr.exch;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Creates a book for each symbol.
 * This stored as value in the TradeBook hashmap
 * 
 * @author Akr
 *
 */

public class OrderBook {
	private final ConcurrentSkipListSet<Order> buySet;
	private final ConcurrentSkipListSet<Order> sellSet;
	
	public OrderBook() {
		super();
		System.out.println(Thread.currentThread().toString() + ": OrderBook's constructor called");
		buySet = new ConcurrentSkipListSet<Order>(new BOComparator());
		sellSet = new ConcurrentSkipListSet<Order>(new SOComparator());
	}

	public ConcurrentSkipListSet<Order> getBuySet() {
		return buySet;
	}

	public ConcurrentSkipListSet<Order> getSellSet() {
		return sellSet;
	}

	public String combinedSize() {
		return ("BuySet: " + buySet.size() + ", SellSet: " + sellSet.size());
	}
	
	
}
