package com.akr.exch;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main executor class.
 * 
 * message format:= symbol|side|qty|price|msgType|orderId|time
 * 		
 * 		- Side: 1=Buy and 2=Sell
 * 		- time: Timestamp is optional, if not provided, code auto generates.
 * 		- orderId: to be provided only if it is a modify/cancel request.
 * 		- msgType: message type. D= new order, M= modify request, C= cancel request
 * 		- qty: order quantity
 * 
 * @author Akr
 *
 */

public class MainRequestExecutor {

	public static void main(String[] args) throws Exception {
		ScheduledExecutorService execService = Executors.newScheduledThreadPool(4);
		System.out.println("*****  Starting the Order matching engine ...  *****\n\n");

		String[] lines = {
				"BAC.N|1|100|20.15|D|0|09:06",
				"BAC.N|2|200|20.3|D|0|09:05",
				"BAC.N|2|100|20.25|D|0|09:03",
				"BAC.N|2|100|20.3|D|0|09:01",
				"BAC.N|1|200|20.2|D|0|09:08",
				"BAC.N|1|200|20.15|D|0|09:09",
				"BAC.N|1|250|20.35|D|0|09:10",
				"JPM.N|2|20|325|D|0|00",
				"JPM.N|1|15|290|D|0|00",
				"JPM.N|2|10|315|D|0|00",
				"JPM.N|1|25|280|D|0|00",
				"JPM.N|2|15|320|D|0|00",
				"JPM.N|2|10|300|D|0|00",
				"JPM.N|1|5|295|D|0|00",
				"JPM.N|1|30|265|D|0|00",
				"JPM.N|2|17|285|D|0|00",
				"BAC.N|1|200|20.15|C|6|09:12",
				"JPM.N|1|5|295|C|14|00",
				"BAC.N|2|250|20.2|M|2|09:14",
				};
		
		int orderId = 0;
		ConcurrentHashMap<String, OrderBook> tradeBook;
		CopyOnWriteArrayList<Order> executedOrders;
		
		MatchingEngine mEngine = new MatchingEngine();
		tradeBook = mEngine.getTradeBook();
		
		for (String line : lines) {
			
			Order order = getOrder(line, ++orderId);
			
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
			
			ThreadRequestExecutor tre = new ThreadRequestExecutor(order, mEngine);
			execService.submit(tre);
		}
					
		/*try {
			Thread.sleep(15000);
		} catch (InterruptedException e1) {
			
			e1.printStackTrace();
		}*/
		
		execService.shutdown();
		if (!execService.awaitTermination(20000L, TimeUnit.MILLISECONDS)) { 
            System.out.println("ExecutorService didn't terminate in the specified time."); 
            List<Runnable> droppedTasks = execService.shutdownNow(); 
            System.out.println("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed."); 
        }
		
		mEngine.printExecutedOrders();
		mEngine.printTradeBook();
		
		
		System.out.println("\n\n*****  Finished execution, exiting now ...  *****");
	}
	
	public static Order getOrder(String line, int orderId) {
		String[] parts = line.split("\\|");
		
		Order order = null;
		
		if((parts[4].equals("C") || parts[4].equals("M")) && !parts[5].equals("0")) {
			order = new Order(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Float.parseFloat(parts[3]), parts[0], Integer.parseInt(parts[5]), parts[4], parts[6]);
		} else {
			order = new Order(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Float.parseFloat(parts[3]), parts[0], orderId, parts[4], parts[6]);
		}
		
		return order;
	}

}
