package org.rgupta.dsa.sorting;

import java.util.Arrays;

public final class MergeSort {

    public static void main(String[] args) {
        int[] input = {3, 4, 2, 1, 6};
        int[] expected = {1, 2, 3, 4, 6};

        int[] res = new MergeSort().mergeSort(input);
        System.out.println("res = " + Arrays.toString(res));
        System.out.println("expected = " + Arrays.toString(expected));
        boolean success = Arrays.equals(res , expected);
        System.out.println("success = " + success);
    }


    int[] mergeSort(int[] arr) {

        mergeSort(arr, 0, arr.length - 1);
        return arr;
    }

    void mergeSort(int[] arr, int l, int r) {
        if (l >= r) {
            return;
        }
        int mid = l + (r - l) / 2;
        mergeSort(arr, l, mid);
        mergeSort(arr, mid + 1, r);
        merge(arr, l, mid, r);
    }

    void merge(int[] arr, int l, int mid, int r) {

        int length1 = mid - l + 1;
        int length2 = r - mid;

        int[] left = new int[length1];
        int[] right = new int[length2];

        for (int i = 0; i < length1; i++) {
            left[i] = arr[l + i];
        }

        for (int i = 0; i < length2; i++) {
            right[i] = arr[mid + 1 + i];
        }

        int leftIdx = 0;
        int rightIdx = 0;
        int k = l;

        while (leftIdx < length1 && rightIdx < length2) {
            if (left[leftIdx] <= right[rightIdx]) {
                arr[k++] = left[leftIdx++];
            } else {
                arr[k++] = right[rightIdx++];
            }
        }

        while (leftIdx < length1) {
            arr[k++] = left[leftIdx++];
        }

        while (rightIdx < length2) {
            arr[k++] = right[rightIdx++];
        }
    }

}
