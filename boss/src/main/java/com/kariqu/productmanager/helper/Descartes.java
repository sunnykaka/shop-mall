package com.kariqu.productmanager.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * 算pidvid笛卡尔积的类，通过状态记忆来计算
 * 设A,B为集合，用A中元素为第一元素，B中元素为第二元素构成的有序对，所有这样的有序对组成的集合叫做A与B的笛卡尔积，记作AxB.
 * 笛卡尔积的符号化为：
 * 　AxB={<x,y>|x∈A∧y∈B}
 * 　例如，A={a,b},B={0,1,2},则
 * 　AxB={<a,o>,<a,1>,<a,2>,<b,0>,<b,1>,<b,2>,}
 * 　BxA={<0,a>,<0,b>,<1,a>,<1,b>,<2,a>,<2,b>}
 * 　笛卡尔积的运算性质
 * 　1.对任意集合A，根据定义有
 * 　AxΦ =Φ ，Φ xA=Φ
 * 　2.一般地说，笛卡尔积运算不满足交换律，即
 * 　AxB≠BxA(当A≠Φ ∧B≠Φ∧A≠B时)
 * User: Asion
 * Date: 11-9-26
 * Time: 下午2:37
 */
public final class Descartes {

    private List<List<Long>> finalSet = new LinkedList<List<Long>>(); //笛卡尔积

    private List<List<Long>> newStates = new LinkedList<List<Long>>(); //状态集合，被用来计算笛卡尔积的中间状态

    private boolean abort = false; //遇空终止

    public final Descartes compute(List<Long> inputSet) {
        if (abort) {
            return this;
        }
        //空集防御
        if (inputSet == null || inputSet.size() == 0) {
            finalSet = new LinkedList<List<Long>>();//空集
            abort = true;
            return this;
        }
        if (finalSet.size() == 0) {//如果finalSet为空，则把输入的集合当做finalSet
            for (Long element : inputSet) {
                List<Long> set = new LinkedList<Long>();
                set.add(element);
                finalSet.add(set);
            }
        } else {
            for (List<Long> originalSet : finalSet) {
                for (Long set : inputSet) {
                    List<Long> newState = new LinkedList<Long>();
                    newState.addAll(originalSet); //把初始中的每个集合拿出来和输入的集合的每个元素组合成一个新的集合
                    newState.add(set);
                    newStates.add(newState);
                }
            }
            if (newStates.size() > 0) {
                finalSet = newStates;
                newStates = new LinkedList<List<Long>>();
            }
        }
        return this;
    }

    public final List<List<Long>> getResult() {
        return finalSet;
    }


    public static void main(String[] args) {
        Descartes descartes = new Descartes();
        List<Long> list =  new LinkedList<Long>();
        list.add(2l);
        list.add(3l);
        descartes.compute(list);
        descartes.compute(list);
        descartes.compute(list);
        System.out.println(descartes.getResult());
    }

}
