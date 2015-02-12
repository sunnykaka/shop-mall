package com.kariqu.tradecenter.service.impl;

import com.kariqu.tradecenter.domain.Area;
import com.kariqu.tradecenter.domain.City;
import com.kariqu.tradecenter.domain.Province;
import com.kariqu.tradecenter.repository.LinkageRepository;
import com.kariqu.tradecenter.service.LinkageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author : Athens(刘杰)
 * @Date: 12-8-13
 * @Time: 下午6:14
 */
public class LinkageServiceImpl implements LinkageService {

    @Autowired
    private LinkageRepository linkageRepository;

    public List<Province> getAllProvince() {
        return linkageRepository.getAllProvince();
    }

    public Province getProvinceByName(String name) {
        return linkageRepository.getProvinceByName(name);
    }

    public List<City> getCityByProvinceCode(String provinceCode) {
        return linkageRepository.getCityByProvinceCode(provinceCode);
    }

    public List<Area> getAreaByCityCode(String cityCode) {
        return linkageRepository.getAreaByCityCode(cityCode);
    }
}
