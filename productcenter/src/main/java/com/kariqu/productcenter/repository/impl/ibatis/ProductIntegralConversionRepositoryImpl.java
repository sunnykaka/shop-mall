package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductIntegralConversion;
import com.kariqu.productcenter.repository.ProductIntegralConversionRepository;
import com.kariqu.usercenter.domain.Currency;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kariqu.common.lib.Collections4.map;

/**
 * User: Json.zhu
 * Date: 13-12-31
 * Time: 下午1:45
 * 积分兑换
 */
public class ProductIntegralConversionRepositoryImpl extends SqlMapClientDaoSupport implements ProductIntegralConversionRepository {

    @Override
    public List<ProductIntegralConversion> selectAllProductIntegralConversionBySkuId(int skuId) {
        Map<String,Object> param = map(
                "skuId", skuId
        );

        return getSqlMapClientTemplate().queryForList("selectAllProductIntegralConversionBySkuId", param);
    }

    @Override
    public ProductIntegralConversion fetchIntegralConversionBySkuIdAndDaytime(int skuId, int productId, Date specificDate) {
        Map<String,Object> param = map(
                "skuId", skuId,
                "productId", productId,
                "specificDate", specificDate
        );

        return (ProductIntegralConversion) getSqlMapClientTemplate().queryForObject("fetchIntegralConversionBySkuIdAndDaytime", param);
    }

    @Override
    public List<ProductIntegralConversion> queryAllProductIntegralConversion() {
        return getSqlMapClientTemplate().queryForList("selectAllProductIntegralConversion");
    }

    @Override
    public ProductIntegralConversion queryProductIntegralConversionById(int id) {
        return (ProductIntegralConversion)getSqlMapClientTemplate().queryForObject("selectProductIntegralConversionById", id);
    }

    @Override
    public ProductIntegralConversion getProductIntegralConversionByGivingTime(long skuId, Date date) {
        Map param = new HashMap();
        param.put("skuId", skuId);
        param.put("specificDate", date);
        return (ProductIntegralConversion)getSqlMapClientTemplate().queryForObject("getProductIntegralConversionByGivingTime", param);
    }

    @Override
    public Page<ProductIntegralConversion> queryProductIntegralConversionByProductId(int start, int limit, int productId) {
        Page<ProductIntegralConversion> page = Page.createFromStart(start, limit);
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        param.put("productId", productId);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountProductIntegralConversionByProductId", param));
        page.setResult(getSqlMapClientTemplate().queryForList("selectProductIntegralConversionByProductId", param));

        return page;

    }

    @Override
    public List<ProductIntegralConversion> selectProductIntegralConversionByDate(Date curDate) {
        if (curDate == null) {
            curDate = DateUtils.getCurrentDate();
        }
        Map param = map(
                "curDate",DateUtils.formatDate(curDate, DateUtils.DateFormatType.DATE_FORMAT_STR)
        );

        return getSqlMapClientTemplate().queryForList("selectProductIntegralConversionByDate", param);
    }

    @Override
    public void createProductIntegralConversion(ProductIntegralConversion productIntegralConversion) {
        productIntegralConversion.setIntegralCount((int) Currency.CurrencyToIntegral(String.valueOf(productIntegralConversion.getIntegralCount())));//*100方便存储
        getSqlMapClientTemplate().insert("insertProductIntegralConversion", productIntegralConversion);
    }

    @Override
    public void deleteProductIntegralConversionById(int id) {
        getSqlMapClientTemplate().delete("deleteProductIntegralConversionById", id);
    }

    @Override
      public void updateProductIntegralConversionById(ProductIntegralConversion productIntegralConversion) {
        productIntegralConversion.setIntegralCount((int) Currency.CurrencyToIntegral(String.valueOf(productIntegralConversion.getIntegralCount())));
        getSqlMapClientTemplate().update("updateProductIntegralConversionById",productIntegralConversion);
    }
}
