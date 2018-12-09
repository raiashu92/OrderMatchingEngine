package com.akr.exch;

/**
 * Runnable task to spawn new threads
 * 
 * @author Akr
 *
 */

public class ThreadRequestExecutor implements Runnable{

	private final Order strOrder;
	private final MatchingEngine mEngine;
	
	public ThreadRequestExecutor(Order strOrder, MatchingEngine mEngine) {
		super();
		this.strOrder = strOrder;
		this.mEngine = mEngine;
	}

	@Override
	public void run() {
		
		try {
			if(strOrder.getMsgType().equals("C") || strOrder.getMsgType().equals("M")) {		//modify or cancel request
				System.out.println(" ***** Order modify/cancel request received. ***** ");
				mEngine.cancelOrModifyOrder(strOrder);
			} else {
				mEngine.matcher(strOrder);
			}
		} catch (Exception e) {
			System.out.println("Exception occured while in thread: " + Thread.currentThread().toString() 
					+ ", while processing order: " + strOrder.getOrderId());
			e.printStackTrace();
		}
	}

}
