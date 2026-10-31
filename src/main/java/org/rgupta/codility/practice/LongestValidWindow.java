package org.rgupta.codility.practice;

import java.util.HashMap;
import java.util.Map;

public class LongestValidWindow {

    public int solution(int[] A, int K) {

        System.out.println("begining of loop");
        int left = 0, right = 0;
        int maxValidWindow = 0;
        Map<Integer, Integer> freqMap = new HashMap<>();
        freqMap.put(A[left], 1);
        while (right < A.length - 1) {
            right++;
            freqMap.put(A[right], freqMap.getOrDefault(A[right], 0) + 1);
            while (freqMap.size() > K) {
                int count = freqMap.get(A[left]);
                int newCount = count - 1;
                if (newCount == 0) {
                    freqMap.remove(A[left]);
                } else {
                    freqMap.put(A[left], newCount);
                }
                left ++;
            }
            maxValidWindow = Math.max(maxValidWindow, right - left + 1);
        }

        return maxValidWindow;
    }


    public static void main(String[] args) {
        int[] input1 = {1, 2, 1, 3, 4, 2, 3};
        int expectedResult = 3;
        int K = 2;
        int res = new LongestValidWindow().solution(input1, K);
        System.out.println("expectedResult = " + expectedResult);
        System.out.println("res = " + res);

        int[] input2 = {5,5,1,2,1,2,3};
        int k2 = 2;
        int expectedRes2 = 4;
        int res2 = new LongestValidWindow().solution(input2 , k2);
        System.out.println("expectedRes2 = " + expectedRes2);
        System.out.println("res2 = " + res2);
    }
}
