package org.rgupta.codility.practice;

import java.util.HashMap;
import java.util.Map;

public class DuplicateTransactionDetector {

	public int solution(int[] A, int K) {
		// Implement
		Map<Integer, Integer> indexMap = new HashMap<>();
		for (int i = 0; i < A.length; i++) {
			final Integer lastSeenIdx = indexMap.get(A[i]);
			if (lastSeenIdx != null && i - lastSeenIdx <= K) {
				return i;
			}
			indexMap.put(A[i], i);
		}
		return -1;
	}

	public static void main(String[] args) {

		int[] input1 = { 41, 12, 99, 12, 41 };
		int k = 2;
		int expectedResult = 3;
		final int result = new DuplicateTransactionDetector().solution(input1, k);
		System.out.println("expectedResult = " + expectedResult);
		System.out.println("result = " + result);
		int[] input2 = {};
		int k2 = 2;
		int expectedResult2 = -1;
		final int result2 = new DuplicateTransactionDetector().solution(input2, k2);
		System.out.println("expectedResult2 = " + expectedResult2);
		System.out.println("result2 = " + result2);
		int[] input3 = { 41, 12, 99, 13, 41 };
		int k3 = 6;
		int expectedResult3 = -1;
		final int result3 = new DuplicateTransactionDetector().solution(input3, k3);
		System.out.println("expectedResult3 = " + expectedResult3);
		System.out.println("result3 = " + result3);

	}
}
