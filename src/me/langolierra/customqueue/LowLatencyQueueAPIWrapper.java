package me.langolierra.customqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LowLatencyQueueAPIWrapper<T> implements ICustomQueue<T> {

    private ConcurrentLinkedQueue<T> queue;
    private volatile T tailElement;
    
	LowLatencyQueueAPIWrapper() {
		queue = new ConcurrentLinkedQueue<>();
		tailElement = null;
	}

	public void push(T element) {
		queue.add(element);
		tailElement = element;
	}

	public T back() {
		return tailElement;
	}

	public T pop() {
		T element = queue.poll();
		if (queue.isEmpty()) {
			tailElement = null;
		}
		return element;
	}

	public T front() {
		return queue.peek();
	}
	
	public int size() {
		return queue.size();
	}

	public boolean empty() {
		return queue.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Queue properties [ ");
		builder.append(queue);
		builder.append(" ]");
		return builder.toString();
	}

}
