package me.langolierra.customqueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.langolierra.customqueue.ICustomQueue;
import me.langolierra.customqueue.LowLatencyQueue;

class QueueContractTest {

	ICustomQueue<String> q;

	@BeforeEach
	void createAndFill() {
		System.out.println("\n\n------------------ @BeforeEach ------------");
		q = new LowLatencyQueue<>();
		q.push("first");
		q.push("second");
		q.push("third");
		q.push("fourth");
		q.push("fifth");
	}

	@Test
	void testPop() {
		System.out.println("------------------ TEST ------------");
		assertEquals("first", q.front(), "Front in not working");
		assertEquals("fifth", q.back(), "Back in not working");

		q.pop();
		System.out.println("pop: " + q);
		assertEquals("second", q.front(), "Pop in not working");
		assertEquals("fifth", q.back(), "Back in not working");

		q.pop();
		System.out.println("pop: " + q);
		assertEquals("third", q.front(), "Pop in not working");
		assertEquals("fifth", q.back(), "Back in not working");

		q.pop();
		System.out.println("pop: " + q);
		assertEquals("fourth", q.front(), "Pop in not working");
		assertEquals("fifth", q.back(), "Back in not working");

		q.pop();
		System.out.println("pop: " + q);
		assertEquals("fifth", q.front(), "Pop in not working");
		assertEquals("fifth", q.back(), "Back in not working");

		q.pop();
		System.out.println("pop: " + q);
		assertNull(q.front(), "Front of last in not working");
		assertNull(q.back(), "Back in not working");

	}

	@Test
	void testEmpty() {
		System.out.println("------------------ TEST ------------");
		assertFalse(q.empty(), "Should not be empty");
		q.pop();
		q.pop();
		q.pop();
		q.pop();
		q.pop();
		assertTrue(q.empty(), "Should be empty");
		System.out.println("pop: " + q);
	}

	@Test
	void testZize() {
		System.out.println("------------------ TEST ------------");
		assertEquals(5, q.size());
		q.pop();

		assertEquals(4, q.size());
		q.pop();

		assertEquals(3, q.size());
		q.pop();

		assertEquals(2, q.size());
		q.pop();

		assertEquals(1, q.size());
		q.pop();

		assertEquals(0, q.size());

		q.pop();
		q.pop();
		q.pop();
		q.pop();
		q.pop();
		q.pop();
		q.pop();

		System.out.println("pop: " + q);
	}
}
