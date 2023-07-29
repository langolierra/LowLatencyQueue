package me.langolierra.customqueue;

import java.util.concurrent.atomic.AtomicInteger;

public class LowLatencyQueue<T> implements ICustomQueue<T> {

	AtomicInteger frontIndex;
	AtomicInteger backIndex;

	ArrayBucketFactory bucketFactory;
	int bucketLength = 10000;

	LowLatencyQueue() {
		frontIndex = new AtomicInteger();
		backIndex = new AtomicInteger();

		bucketFactory = new ArrayBucketFactory(bucketLength);
	}

	public void push(T element) {
		setElement(backIndex.getAndIncrement(), element);
	}

	public T back() {
		return !empty() ? getElement(backIndex.get() - 1) : null;
	}

	public T pop() {
		int front = frontIndex.getPlain();
		if (backIndex.getAcquire() > front) {
			int wits = frontIndex.compareAndExchange(front, front + 1);
			return (wits == front) ? popElement(front, wits) : pop();
		}
		return null;
	}

	public T front() {
		return !empty() ? getElement(frontIndex.get()) : null;
	}

	@SuppressWarnings("unchecked")
	private T popElement(int front, int wits) {
		Object[] bucket = bucketFactory.getBucket(front);
		int indexInBucket = front % bucketLength;
		try {
			return (T) bucket[indexInBucket];
		} finally {
			bucket[indexInBucket] = null;
			bucketFactory.tryCleanBucket(front);
		}
	}

	@SuppressWarnings("unchecked")
	private T getElement(int front) {
		Object[] bucket = bucketFactory.getBucket(front);
		int indexInBucket = front % bucketLength;

		return (T) bucket[indexInBucket];
	}

	private void setElement(int back, T element) {
		Object[] bucket = bucketFactory.getBucket(back);
		int indexInBucket = back % bucketLength;

		bucket[indexInBucket] = element;
	}

	public int size() {
		return backIndex.getAcquire() - frontIndex.getAcquire();
	}

	public boolean empty() {
		return size() == 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Queue properties [ ");

		builder.append("frontIndex:").append(frontIndex).append("; ");
		builder.append("backIndex:").append(backIndex).append("; ");
		builder.append("size:").append(size()).append("; ");
		builder.append("empty:").append(empty()).append("; ");

		builder.append("]");
		return builder.toString();
	}

}
