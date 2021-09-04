package org.zlycerqan.mirai.lizyn.services.score.connection.utils;

import java.util.Random;

public class PermutationGenerator {

    public static int[] generatorPermutation(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; ++ i) {
            arr[i] = i;
        }
        return arr;
    }

    public static int[] generatorRandomPermutation(int n) {
        Random random = new Random();
        int[] arr = generatorPermutation(n);
        for (int i = 0; i < n; ++ i) {
            int t = arr[i];
            int p = random.nextInt(n);
            arr[i] =  arr[p];
            arr[p] = t;
        }
        return arr;
    }

}
