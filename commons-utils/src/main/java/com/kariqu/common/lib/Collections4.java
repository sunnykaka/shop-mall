package com.kariqu.common.lib;

import java.util.*;

/**
 * 方便构造数组, list, map
 * Created by Canal.wen on 2014/6/27 14:22.
 */
public class Collections4 {
    public static <E> boolean isEmpty(Collection<E> collection) {
        if (collection == null) {
            return true;
        }

        if (collection.size() < 1) {
            return true;
        }

        return false;
    }

    public static <E> boolean isNotEmpty(Collection<E> collection) {
        return !isEmpty(collection);
    }

    /**
     * Create array from values.
     * @param values values to create array from.
     * @return array filled with values from arguments.
     */
    public static <T> T[] arr(T... values) {
        return values;
    }

    /**
     * Create array from values.
     * @param values values to create array from.
     * @return array filled with values from arguments.
     */
    public static <T> T[] array(T... values) {
        return values;
    }

    /**
     * Creates a set from values.
     *
     * @param ts values for a set.
     * @return set filled with values.
     */
    public static <T> Set<T> set(T... ts) {
        return new HashSet<T>(Arrays.asList(ts));
    }

    /**
     * Create a map from keys and values.
     *
     * @param keysAndValues list of key value pairs. The number of items must be even.
     * The argument at index 0 is a key of the value at index 1, argument at index 2 is key of value at index 3, and so on.
     * @return Map filled with keys and values.
     */
    @SuppressWarnings("unchecked")
    public static <T, K> Map<T, K> map(Object... keysAndValues) {
        if (keysAndValues.length % 2 != 0) throw new IllegalArgumentException("number of arguments must be even");

        Map<T, K> result = new HashMap<T, K>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            result.put((T) keysAndValues[i], (K) keysAndValues[i + 1]);
        }
        return result;
    }

    /**
     * Create a list from values.
     *
     * @param values values to create a list from.
     * @return list with values.
     */
    public static <T> List<T> li(T... values) {
        return list(values);
    }

    /**
     * Create a list from values.
     *
     * @param values values to create a list from.
     * @return list with values.
     */
    public static <T> List<T> list(T... values) {
        List result = new ArrayList<T>();
        for (T value : values) {
            result.add(value);
        }
        return result;
    }
}
