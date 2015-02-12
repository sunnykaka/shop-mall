package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.Area;
import com.kariqu.tradecenter.domain.City;
import com.kariqu.tradecenter.domain.Province;
import com.kariqu.tradecenter.repository.LinkageRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

/**
 * 级联
 *
 * @author : Athens(刘杰)
 * @Date: 12-8-13
 * @Time: 下午6:14
 */
public class LinkageRepositoryImpl extends SqlMapClientDaoSupport implements LinkageRepository {

    public List<Province> getAllProvince() {
        return getSqlMapClientTemplate().queryForList("selectProvince");
    }

    public Province getProvinceByName(String name) {
        // 中国的省市名是没有重复的.
        List<Province> list = getSqlMapClientTemplate().queryForList("selectProvinceByName");
        if (list == null || list.size() == 0)
            return null;
        return list.get(0);
    }

    public List<City> getCityByProvinceCode(String provinceCode) {
        return getSqlMapClientTemplate().queryForList("selectCityByProvinceId", provinceCode);
    }

    public List<Area> getAreaByCityCode(String cityCode) {
        return getSqlMapClientTemplate().queryForList("selectAreaByCityId", cityCode);
    }

}
