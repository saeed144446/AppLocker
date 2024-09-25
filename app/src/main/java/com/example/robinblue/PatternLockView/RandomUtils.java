package com.example.robinblue.PatternLockView;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Random utilities.
 */
public class RandomUtils {

    private static final Random RANDOM = new Random();

    private RandomUtils() {
        throw new AssertionError("You can not instantiate this class. Use its static utility " +
                "methods instead");
    }

    /**
     * Generates a random integer
     */
    public static int randInt() {
        return RANDOM.nextInt((int) (System.nanoTime() % Integer.MAX_VALUE));
    }

    /**
     * Generates a random integer within {@code [0, max)}.
     *
     * @param max The maximum bound
     * @return A random integer
     */
    public static int randInt(int max) {
        return max > 0 ? randInt() % max : 0;
    }

    /**
     * Generates a random integer array which has length of {@code end - start},
     * and is filled by all values from {@code start} to {@code end - 1} in randomized orders.
     *
     * @param start The starting value
     * @param end   The ending value
     * @return The random integer array. If {@code end <= start}, an empty array is returned
     */
    public static int[] randIntArray(int start, int end) {
        if (end <= start) {
            return new int[0];
        }

        final List<Integer> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            values.add(i);
        }

        final int[] result = new int[values.size()];
        for (int i = 0; i < result.length; i++) {
            int k = randInt(values.size());
            result[i] = values.get(k);
            values.remove(k);
        }

        return result;
    }

    /**
     * Generates a random integer array which has length of {@code end},
     * and is filled by all values from {@code 0} to {@code end - 1} in randomized orders.
     *
     * @param end The ending value
     * @return The random integer array. If {@code end <= start}, an empty array is returned
     */
    public static int[] randIntArray(int end) {
        return randIntArray(0, end);
    }

}
