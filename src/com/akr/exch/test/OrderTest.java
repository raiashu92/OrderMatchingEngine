package com.akr.exch.test;

import java.util.*;

import org.junit.Test;

import com.akr.exch.SOComparator;
import com.akr.exch.BOComparator;
import com.akr.exch.Order;

public class OrderTest {

	@Test
	public void test() {
		Order o3 = new Order(1, 100, 20.15f, "BARC.L", 1, "D", "09:06");
		Order o2 = new Order(1, 200, 20.15f, "BARC.L",2, "D", "09:09");
		Order o1 = new Order(1, 200, 20.20f, "BARC.L",3, "D", "09:10");
		Order o4 = new Order(2, 200, 20.30f, "BARC.L",4, "D", "09:05");
		Order o6 = new Order(2, 100, 20.30f, "BARC.L",5, "D", "09:01");
		Order o5 = new Order(2, 100, 20.25f, "BARC.L",6, "D", "09:03");
		
		Order o7 = new Order(1, 250, 20.3f, "BARC.L",7, "D", "09:10");
		
		Order o8 = new Order(1, 200, 20.25f, "BARC.L",9, "C", "09:09"); //only the side and orderId required
		Order o9 = new Order(2, 150, 20.15f, "BARC.L",8, "D", "09:10");
		
		TreeSet<Order> sellS = new TreeSet<>(new SOComparator());
		sellS.add(o4);
		sellS.add(o5);
		sellS.add(o6);
		//set.add(o9);
		System.out.println("sell side set:");
		sellS.forEach(order -> System.out.println(order));
		
		TreeSet<Order> buyS = new TreeSet<>(new BOComparator());
		buyS.add(o1);
		buyS.add(o2);
		buyS.add(o3);
		buyS.add(o8);
		//ts.add(o7);
		System.out.println("buy side set:");
		buyS.forEach(order -> System.out.println(order));
		
		System.out.println("New Buy order of " + o7.getPrice() + " headset from sellset");
		sellS.headSet(o7,true).forEach(o -> System.out.println(o));
		
		/*System.out.println("New Sell order of " + o9.getPrice() + " tailSet from buyset");
		ts.tailSet(o9,true).forEach(o -> System.out.println(o));*/
		
		System.out.println("New Sell order " + o9.getPrice() + " headset from buyset");
		buyS.headSet(o9,true).forEach(o -> System.out.println(o));
		
			
		
	}

}
