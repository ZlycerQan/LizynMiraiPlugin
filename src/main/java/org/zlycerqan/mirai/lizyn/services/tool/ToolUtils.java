package org.zlycerqan.mirai.lizyn.services.tool;

import java.util.HashSet;
import java.util.Set;

public class ToolUtils {

    public static int getRand(int l, int r) {
        return (int) (Math.random() * (r - l + 1)) + l;
    }

    public static void randomShuffle(int[] arr) {
        for (int i = 0; i < arr.length * 2; ++ i) {
            int p1 = getRand(0, arr.length - 1);
            int p2 = getRand(0, arr.length - 1);
            int dat = arr[p1];
            arr[p1] = arr[p2];
            arr[p2] = dat;
        }
    }

    public static int[] getRandList(int l, int r, boolean isOnly, int number) {
        int[] arr = new int[number];
        if (isOnly) {
            int L = r - l + 1;
            if (L > 10000) {
                Set<Integer> is = new HashSet<>();
                for (int i = 0; i < number; ++ i) {
                    int dat = getRand(l, r);
                    while (is.contains(dat)) {
                        dat = getRand(l, r);
                    }
                    is.add(dat);
                    arr[i] = dat;
                }
            } else {
                int[] rd = new int[L];
                for (int i = 0; i < L; ++ i) {
                    rd[i] = l + i;
                }
                randomShuffle(rd);
                System.arraycopy(rd, 0, arr, 0, number);
            }
        } else {
            for (int i = 0; i < number; ++ i) {
                arr[i] = getRand(l, r);
            }
        }
        return arr;
    }

    public static String randListToString(int[] arr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arr.length; ++ i) {
            stringBuilder.append(arr[i]);
            if (i != arr.length - 1) {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }

    public static int[] parseInterval(String s) {
        String[] nums = s.split(",");
        int[] a = new int[2];
        try {
            a[0] = Integer.parseInt(nums[0].substring(1));
            a[1] = Integer.parseInt(nums[1].substring(0, nums[1].length() - 1));
            if (a[1] < a[0]) {
                return null;
            }
            return a;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
