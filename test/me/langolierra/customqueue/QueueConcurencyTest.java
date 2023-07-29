package me.langolierra.customqueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueueConcurencyTest {

	ICustomQueue<String> q;
	List<String> pushed = Collections.synchronizedList(new ArrayList<>());
	List<String> popped = Collections.synchronizedList(new ArrayList<>());

	@BeforeEach
	void createAndFill() {
		System.out.println("\n\n------------------ @BeforeEach ------------");
		//q = new LowLatencyQueueAPIWrapper<>();
		q = new LowLatencyQueue<>();
	}

	@Test
	void testMix() throws InterruptedException {
		System.out.println("------------------ TEST ------------");

		Random r = new Random();
		ExecutorService executor = Executors.newFixedThreadPool(200);
		LocalTime start = LocalTime.now();
		
        List<Future<?>> futureList = new ArrayList<>();
		
		Stream.generate(() -> new Runnable() {
			public void run() {
				
				for (int i = 0; i < 1000; i++) {

					String objectT = Thread.currentThread().getName() + "-" + Math.abs(r.nextLong());
					q.push(objectT);
					pushed.add(objectT);
					//System.out.println("push  <<< " + objectT + "; back:"  + q.back()+ "; front:"  + q.front());
					
					try {
						Thread.sleep(r.nextInt(5));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).limit(100).map(executor::submit).forEach(futureList::add);;

	
		Stream.generate(() -> new Runnable() {
			public void run() {
				for (int i = 0; i < 1100; i++) {
					try {
						Thread.sleep(r.nextInt(10));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					String poppedT = q.pop();
					if (poppedT != null) {
						popped.add(poppedT);
						//System.out.println("pop  <<< " + poppedT  + "; size:" + q.size() + "; t1me:" + (LocalTime.now().toSecondOfDay() - start.toSecondOfDay()));
					} 
				}
			}
		}).limit(100).map(executor::submit).forEach(futureList::add);
	
		while(futureList.stream().anyMatch(t -> !t.isDone())) {
	        Thread.sleep(1000);
		}
		
		System.out.println("queue: " + q + "; pushed: " + pushed.size()+ "; popped: " + popped.size() + "; time: " + (LocalTime.now().toSecondOfDay() - start.toSecondOfDay()));

		assertEquals(0, q.size());
		assertEquals(pushed.size(), popped.size());

	}
}
