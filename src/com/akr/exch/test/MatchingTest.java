package com.akr.exch.test;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import com.akr.exch.MatchingEngine;
import com.akr.exch.Order;
import com.akr.exch.OrderBook;

public class MatchingTest {
	@Test
	public void matchingTest() throws Exception {
		Order order = new Order(1, 100, 20.15f, "BARC.L", 1, "D", "09:06");
		Order o2 = new Order(1, 200, 20.15f, "BARC.L",2, "D", "09:09");
		Order o1 = new Order(1, 200, 20.20f, "BARC.L",3, "D", "09:08");
		Order o4 = new Order(2, 200, 20.30f, "BARC.L",4, "D", "09:05");
		Order o6 = new Order(2, 100, 20.30f, "BARC.L",5, "D", "09:01");
		Order o5 = new Order(2, 100, 20.25f, "BARC.L",6, "D", "09:03");
		
		Order o7 = new Order(1, 250, 20.35f, "BARC.L",7, "D", "09:10");
		
		Order o8 = new Order(1, 200, 20.15f, "BARC.L",2, "C", "09:09"); //only the side and orderId required
		Order o9 = new Order(2, 150, 20.20f, "BARC.L",4, "M", "09:07");
		
		MatchingEngine me = new MatchingEngine();
		ConcurrentHashMap<String, OrderBook> tradeBook;
		tradeBook = me.getTradeBook();
		String symbol = order.getSymbol();
		
		if(!tradeBook.containsKey(symbol)) {
			System.out.println(Thread.currentThread().toString() + ": Creating new key in tradeBook");
			OrderBook newOrderBook = new OrderBook();
			
			if(order.getSide() == 1) {
				newOrderBook.getBuySet().add(order);
			} else if (order.getSide() == 2) {
				newOrderBook.getSellSet().add(order);
			} else {
				System.out.println("Invalid order status. Valid values: 1=Buy 2=Sell");
				throw new Exception("Invalid order side");
			}
			
			tradeBook.put(symbol, newOrderBook);
			
			System.out.println("TradeBook size: " + tradeBook.size() + "\nNew symbol put in the tradeBook:");
			System.out.println(order);
			
		}
		try {
			me.matcher(o1);
			me.matcher(o2);
			me.matcher(order);
			me.matcher(o4);
			me.matcher(o5);
			me.matcher(o6);
			
			Thread.sleep(3000);
			me.matcher(o7);
			me.cancelOrModifyOrder(o8);
			me.cancelOrModifyOrder(o9);
			
			Thread.sleep(7000);
			me.printExecutedOrders();
			me.printTradeBook();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
