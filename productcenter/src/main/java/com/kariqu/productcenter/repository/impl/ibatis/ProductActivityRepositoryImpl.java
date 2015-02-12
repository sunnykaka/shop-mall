package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.ProductActivity;
import com.kariqu.productcenter.domain.ProductActivityType;
import com.kariqu.productcenter.repository.ProductActivityRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Asion
 * Date: 13-4-1
 * Time: 下午4:41
 */
public class ProductActivityRepositoryImpl extends SqlMapClientDaoSupport implements ProductActivityRepository {

    @Override
    public void createProductActivity(ProductActivity productActivity) {
        getSqlMapClientTemplate().insert("insertProductActivity", productActivity);
    }

    @Override
    public void updateProductActivity(ProductActivity productActivity) {
        getSqlMapClientTemplate().update("updateProductActivity", productActivity);
    }

    @Override
    public Integer getActivityCountWrapperGivingDate(int productId, Date dateInterval) {
        Map param = new HashMap();
        param.put("productId", productId);
        param.put("dateInterval", dateInterval);
        return (Integer) getSqlMapClientTemplate().queryForObject("getActivityCountWrapperGivingDate", param);
    }

    @Override
    public Integer getActivityCountWrapperGivingDateExceptSelf(int productId, Date dateInterval, int id) {
        Map param = new HashMap();
        param.put("productId", productId);
        param.put("dateInterval", dateInterval);
        param.put("id", id);
        return (Integer) getSqlMapClientTemplate().queryForObject("getActivityCountWrapperGivingDateExceptSelf", param);
    }

    @Override
    public ProductActivity getProductActivityByGivingTime(int productId, Date specificDate) {
        Map param = new HashMap();
        param.put("productId", productId);
        param.put("specificDate", specificDate);
        return (ProductActivity) getSqlMapClientTemplate().queryForObject("getProductActivityByGivingTime", param);
    }

    @Override
    public ProductActivity getProductActivityByActivityIdAndType(int activityId, ProductActivityType activityType) {
        Map param = new HashMap();
        param.put("activityId", activityId);
        param.put("activityType", activityType);
        return (ProductActivity) getSqlMapClientTemplate().queryForObject("getProductActivityByActivityIdAndType", param);
    }

    @Override
    public Integer getCountOfProductJoinActivityAfterCurrentTime(int productId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountOfProductJoinActivityAfterCurrentTime", productId);
    }

    @Override
    public void deleteProductActivity(int productId, long activityId) {
        Map param = new HashMap();
        param.put("productId", productId);
        param.put("activityId", activityId);
        getSqlMapClientTemplate().delete("deleteProductActivity", param);
    }

    @Override
    public Integer getActivityCountInDateRange(int productId, Date start, Date end) {
        Map param = new HashMap();
        param.put("productId", productId);
        param.put("start", start);
        param.put("end", end);
        return (Integer) getSqlMapClientTemplate().queryForObject("getActivityCountInDateRange", param);
    }

    @Override
    public Integer getActivityCountInDateRangeExceptSelf(int productId, Date start, Date end, int id) {
        Map param = new HashMap();
        param.put("productId", productId);
        param.put("start", start);
        param.put("end", end);
        param.put("id", id);
        return (Integer) getSqlMapClientTemplate().queryForObject("getActivityCountInDateRangeExceptSelf", param);
    }

    @Override
    public Integer getUserBuyCountForIntegralAndSuperConversionBySkuId(Long skuId, Date startDate) {
        Map param = new HashMap();
        param.put("skuId", skuId);
        param.put("specificDate", startDate);
        return (Integer) getSqlMapClientTemplate().queryForObject("getUserBuyCountForIntegralAndSuperConversionBySkuId", param);
    }
}
