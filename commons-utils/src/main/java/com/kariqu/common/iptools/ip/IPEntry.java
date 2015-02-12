package com.kariqu.common.iptools.ip;

/**
 * Created with IntelliJ IDEA.
 * User: Alec
 * Date: 12-11-29
 * Time: 下午2:00
 * To change this template use File | Settings | File Templates.
 */
public class IPEntry {
    public String beginIp;
    public String endIp;
    public String country;
    public String area;

    public IPEntry() {
        this.beginIp = (this.endIp = this.country = this.area = "");
    }
}
