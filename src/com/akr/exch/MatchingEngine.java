package com.akr.exch;

import java.util.*;
import java.util.concurrent.*;

/**
 * Contains matching, modify, cancel logic.
 * 
 * @author Akr
 *
 */

public class MatchingEngine {
	final ConcurrentHashMap<String, OrderBook> tradeBook;
	final CopyOnWriteArrayList<Order> executedOrders;
	
	public MatchingEngine() {
		super();
		System.out.println(Thread.currentThread().toString() + ": MatchingEngine's constructor called");
		tradeBook = new ConcurrentHashMap<String, OrderBook>();
		executedOrders = new CopyOnWriteArrayList<Order>();
	}

	public ConcurrentHashMap<String, OrderBook> getTradeBook() {
		return tradeBook;
	}

	public CopyOnWriteArrayList<Order> getExecutedOrders() {
		return executedOrders;
	}

	public void matcher(Order newOrder)  {
		String symbol = newOrder.getSymbol();
		System.out.println("--*--");
		System.out.println(Thread.currentThread().toString() + ": order- " + newOrder);
		
		
		//symbol already exists, first fill the order and then if left qty then put in book
		OrderBook curOrderBook = tradeBook.get(symbol);
		ConcurrentSkipListSet<Order> matchedPortion = null;
		
		if(newOrder.getSide() == 1) {
			//new order is on buy side
			matchedPortion = (ConcurrentSkipListSet<Order>) curOrderBook.getSellSet().headSet(newOrder, true);
		} else if (newOrder.getSide() == 2) {
			//new order is on sell side
			matchedPortion  = (ConcurrentSkipListSet<Order>) curOrderBook.getBuySet().headSet(newOrder, true);
		}
		
		
		
		if(matchedPortion.size() > 0){
			
			System.out.println(Thread.currentThread().toString() + ": There are " + matchedPortion.size() + " matching orders to fill the request.");
			Iterator<Order> itr = matchedPortion.iterator();
			while(itr.hasNext()) {
				Order order = itr.next();
				int remaining = order.getQuantity() - order.getFilledQty();
				int newOrderQtyLeft = newOrder.getQuantity() - newOrder.getFilledQty();
				if (newOrderQtyLeft >= remaining) {    				 //equals can also be added here ?
					//available quantity < bid quantity
					newOrder.setFilledQty(newOrder.getFilledQty() + remaining);
					order.setFilledQty(order.getQuantity());
					executedOrders.add(order);
					if(newOrderQtyLeft == remaining) {
						executedOrders.add(newOrder);
					}
					itr.remove();
				} else {
					//available quantity > bid quantity
					newOrder.setFilledQty(newOrder.getQuantity());
					order.setFilledQty(order.getFilledQty() + newOrderQtyLeft);
					executedOrders.add(newOrder);
					break;
				}
			}
		}
		
		
		//order partially filled or no match
	
		if ((newOrder.getQuantity() - newOrder.getFilledQty()) != 0) {	
		System.out.println(Thread.currentThread().toString() + ": There are no matching orders/order partially filled. Adding the current order to the order book ...");
		
		if(newOrder.getSide() == 1) {
			curOrderBook.getBuySet().add(newOrder);
		} else if (newOrder.getSide() == 2) {
			curOrderBook.getSellSet().add(newOrder);
		}
		
		System.out.println(Thread.currentThread().toString() + " : " + newOrder);
		}
		
		System.out.println(Thread.currentThread().toString() + ": Executed orders: " + executedOrders.size());
		System.out.println("--*--\n");
		
	}
	
	
	public void printExecutedOrders() {
		System.out.println("********************************************");
		System.out.println("Printing the executed orders ...");
		executedOrders.forEach( o -> System.out.println(o));
		System.out.println("********************************************");
	}
	
	public void printTradeBook() {
		System.out.println("********************************************");
		System.out.println("Printing the tradeBook ...");
		tradeBook.forEach((symb, ordb) -> {
			System.out.println("symbol: " + symb);
			System.out.println("buyset: " + ordb.getBuySet());
			System.out.println("sellset: " + ordb.getSellSet());
		});
		System.out.println("********************************************");
	}
	
	public void cancelOrModifyOrder(Order clOrder) throws InterruptedException  {
		
		if(tradeBook.containsKey(clOrder.getSymbol())) {
			OrderBook clOrderBook = tradeBook.get(clOrder.getSymbol());
			boolean orderExecuted = false; 
			for (Order ord : executedOrders) {
				if(ord.getOrderId() == clOrder.getOrderId())
					orderExecuted = true;
			}
			if(orderExecuted) {
				System.out.println(Thread.currentThread().toString() + ": OrderId: " + clOrder.getOrderId() + " already executed, cannot cancel/modify.");
			} else {
				ConcurrentSkipListSet<Order> candidateList = null;
				if (clOrder.getSide() == 1) {
					candidateList = clOrderBook.getBuySet();
				} else if (clOrder.getSide() == 2) {
					candidateList = clOrderBook.getSellSet();
				}
				Iterator<Order> itr = candidateList.iterator();
				Order locatedOrder;
				while(itr.hasNext()) {
					locatedOrder = itr.next();
					if(locatedOrder.getOrderId() == clOrder.getOrderId()) {
						itr.remove();
						if(clOrder.getMsgType().equals("C")) {
							System.out.println(Thread.currentThread().toString() + ": Cancelling the orderId " + clOrder.getOrderId());
						} else if (clOrder.getMsgType().equals("M")) {
							System.out.println(Thread.currentThread().toString() + ": Modifying the orderId " + clOrder.getOrderId());
							clOrder.setFilledQty(locatedOrder.getFilledQty());
							//Thread.sleep(1000);
							matcher(clOrder);
						}
						break;
					}
				}
			}
			
		} else {
			System.out.println(Thread.currentThread().toString() + ": No such order exists. Cannot cancel/modify: " + clOrder.getOrderId());
		}
	}
	
}
