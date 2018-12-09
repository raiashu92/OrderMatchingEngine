package com.akr.exch;

import java.util.Comparator;
/**
 * <p>Matching algorithm used: Price/Time priority. </p>
 * 
 * comparator for sorting sell side order set 
 * 
 * @author Akr
 *
 */


public class SOComparator implements Comparator<Order> {

	@Override
	public int compare(Order order1, Order order2) {
		if(order1.getPrice().compareTo(order2.getPrice()) == 0) {            //price same
			return order1.getTime().compareTo(order2.getTime());
		} else {
			return order1.getPrice().compareTo(order2.getPrice());
		}
	}
}
