package com.kariqu.common.iptools.ip;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Alec
 * Date: 12-11-29
 * Time: 下午2:15
 * To change this template use File | Settings | File Templates.
 */
public class IPTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        //指定纯真数据库的文件名，所在文件夹
        IPSeeker ip=new IPSeeker();
        //获得地区
        IPLocation ipLocation= ip.getIPLocation("172.16.6.7");
        String address = ipLocation.getCountry();
        //获得类型
        String area  =ipLocation.getArea();
        System.out.println(area+address);

    }
}
