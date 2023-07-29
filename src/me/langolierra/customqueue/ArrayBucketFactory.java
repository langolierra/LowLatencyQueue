package me.langolierra.customqueue;

import java.util.Arrays;
import java.util.HashMap;

public class ArrayBucketFactory {
	private final int bucketAgeAhead = 4;
	private final int bucketLength;

	private volatile HashMap<Integer, Object[]> innerStructure;

	ArrayBucketFactory(int bucketLength) {
		this.bucketLength = bucketLength;
		this.innerStructure = new HashMap<>();

		for (int i = 0; i < bucketAgeAhead; i++) {
			createBucket(Integer.valueOf(i));
		}
	}

	public Object[] getBucket(int index) {
		Integer bucketNumber = detectBucket(index);

		if (index % bucketLength == 0) {
			Integer bucketNumberAhead = Integer.valueOf(bucketNumber.intValue() + bucketAgeAhead);
			if (!innerStructure.containsKey(bucketNumberAhead)) {
				createBucket(bucketNumberAhead);
			}
		}
		return innerStructure.get(bucketNumber);
	}

	public void tryCleanBucket(int index) {
		Integer bucketNumber = detectBucket(index);
		if (index % bucketLength == 0) {
			Integer bucketNumberToClean = Integer.valueOf(bucketNumber.intValue() - bucketAgeAhead);
			if (innerStructure.containsKey(bucketNumberToClean)) {
				innerStructure.remove(bucketNumberToClean);
			}
		}
	}

	private void createBucket(Integer bucketNumber) {
		innerStructure.put(bucketNumber, Arrays.copyOf(new Object[] {}, bucketLength));
	}

	private Integer detectBucket(int index) {
		return Integer.valueOf(index / bucketLength);
	}
}
