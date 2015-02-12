package com.kariqu.tradecenter.domain;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import java.util.*;

public class RotaryLotteryTest extends UnitilsJUnit4 {

    private static final int NUMBER = 10000;

    /**
     * 测试轮盘
     */
    @Test
    public void testRotary() throws Exception {
        // 抽奖
        int[] array = new int[] {5, 45, 55, 75, 150, 320, 550, 800, 1000, 1500, 2000, 3500};
        Map<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();

        int randMax = count(array);
        Arrays.sort(array);
        int number;
        Integer key, value;

        Random random = new Random();
        for (int i = 0; i < NUMBER; i++) {
            number = random.nextInt(randMax);

            key = calculate(number, array);
            value = map.get(key);
            map.put(key, (value == null) ? 1 : value + 1);
            //System.out.println("当前数 " + number + " 在 " + key + " 区间中!");
        }
        System.out.println(map);
    }

    int count(int[] array) {
        int count = 0;
        for (int i : array) {
            count += i;
        }
        return count;
    }

    int calculate(int number, int[] array) {
        int min, max = 0, len = array.length;
        for (int i = 0; i < len; i++) {
            min = (i == 0) ? 0 : array[i - 1] + 1;
            max += array[i];

            if (rangeInDefined(number, min, max))
                return array[i];
        }
        return 0;
    }

    boolean rangeInDefined(int current, int min, int max) {
        return current >= min && current <= max;
    }

}
