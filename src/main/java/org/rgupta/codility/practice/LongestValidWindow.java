package org.rgupta.codility.practice;

import java.util.HashMap;
import java.util.Map;

public class LongestValidWindow {

    public int solution(int[] A, int K) {

        int left = 0;
        int maxValidWindow = 0;
        Map<Integer, Integer> freqMap = new HashMap<>();
        for(int right = 0; right < A.length; right++) {
            freqMap.put(A[right], freqMap.getOrDefault(A[right], 0) + 1);
            while (freqMap.size() > K) {
                int count = freqMap.get(A[left]) -1 ;
                if (count == 0) {
                    freqMap.remove(A[left]);
                } else {
                    freqMap.put(A[left], count);
                }
                left++;
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

        int[] input2 = {5, 5, 1, 2, 1, 2, 3};
        int k2 = 2;
        int expectedRes2 = 4;
        int res2 = new LongestValidWindow().solution(input2, k2);
        System.out.println("expectedRes2 = " + expectedRes2);
        System.out.println("res2 = " + res2);

        int[] input3 = {7, 7, 7};
        int k3 = 1;
        int expectedResult3 = 3;
        int res3 = new LongestValidWindow().solution(input3 , k3);
        System.out.println("expectedResult3 = " + expectedResult3);
        System.out.println("res3 = " + res3);

        int[] input4 = {};
        int k4 = 1;
        int res4 = new LongestValidWindow().solution(input4 , k4);
        System.out.println("res4 = " + res4);

        int[] input5 = {42};
        int k5 = 1;
        int res5 = new LongestValidWindow().solution(input5 , k5);
        System.out.println("res5 = " + res5);
    }
}
