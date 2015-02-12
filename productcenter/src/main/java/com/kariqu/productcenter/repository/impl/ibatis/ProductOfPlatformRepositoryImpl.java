package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.ProductOfPlatform;
import com.kariqu.productcenter.repository.ProductOfPlatformRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Alec
 * Date: 13-10-17
 * Time: 上午10:00
 */
public class ProductOfPlatformRepositoryImpl extends SqlMapClientDaoSupport implements ProductOfPlatformRepository {
    @Override
    public void addProductToPlatform(ProductOfPlatform product) {
        this.getSqlMapClientTemplate().insert("addProductToPlatform", product);
    }

    @Override
    public ProductOfPlatform queryProductOfPlatform(int productId, ProductOfPlatform.Platform platform) {
        Map param = new HashMap();
        param.put("productId", productId);
        param.put("platform", platform);
        return (ProductOfPlatform)this.getSqlMapClientTemplate().queryForObject("queryProductOfPlatform", param);
    }
}
