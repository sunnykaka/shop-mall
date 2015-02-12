package com.kariqu.tradecenter.service;

import com.kariqu.tradecenter.domain.Area;
import com.kariqu.tradecenter.domain.City;
import com.kariqu.tradecenter.domain.Province;

import java.util.List;

/**
 * 省市区县, 三级联动.
 *
 * @author : Athens(刘杰)
 * @Date: 12-8-13
 * @Time: 下午6:08
 */
public interface LinkageService {

    /**
     * 获取所有省市信息.
     *
     * @return
     */
    List<Province> getAllProvince();

    /**
     * 根据省市名查询对应的信息.
     *
     * @param name 城市名. 如 北京市, 河北省等
     * @return
     */
    Province getProvinceByName(String name);

    /**
     * 获取所有省市下的所有地市信息.
     *
     * @param provinceCode 省市编码
     * @return
     */
    List<City> getCityByProvinceCode(String provinceCode);

    /**
     * 获取所有地市下的所有区县信息.
     *
     * @param cityCode 地市编码
     * @return
     */
    List<Area> getAreaByCityCode(String cityCode);

}
