package com.kariqu.productcenter.domain;

import org.junit.Test;

/**
 * User: Asion
 * Date: 11-9-22
 * Time: 下午12:41
 */
public class MoneyTest {

    @Test
    public void test() {
        Money money1 = new Money("1");
        Money money2 = new Money("1");
        Money add = money1.add(money2);
        System.out.println(add);

        System.out.println(Money.getMoneyString(10));

    }
}
