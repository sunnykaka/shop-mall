package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductSuperConversion;
import com.kariqu.productcenter.repository.ProductSuperConversionRepository;
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
 * 商品超值兑换表
 */
public class ProductSuperConversionRepositoryImpl extends SqlMapClientDaoSupport implements ProductSuperConversionRepository {

    @Override
    public List<ProductSuperConversion> selectAllProductSuperConversionBySkuId(int skuId) {
        Map<String, Object> param = map(
                "skuId", skuId
        );

        return getSqlMapClientTemplate().queryForList("selectAllProductSuperConversionBySkuId", param);
    }

    @Override
    public ProductSuperConversion fetchSuperConversionBySkuIdAndDaytime(int skuId, int productId, Date specificDate) {
        Map<String,Object> param = map(
                "skuId", skuId,
                "productId", productId,
                "specificDate", specificDate
        );

        return (ProductSuperConversion) getSqlMapClientTemplate().queryForObject("fetchSuperConversionBySkuIdAndDaytime", param);
    }

    @Override
    public List<ProductSuperConversion> queryAllProductSuperConversion(){
        return getSqlMapClientTemplate().queryForList("selectAllProductSuperConversion");
    }

    @Override
    public ProductSuperConversion queryProductSuperConversionById(int id) {
        return (ProductSuperConversion)getSqlMapClientTemplate().queryForObject("selectProductSuperConversionById", id);
    }

    @Override
    public ProductSuperConversion getProductSuperConversionByGivingTime(long skuId, Date specificDate) {
        Map param = new HashMap();
        param.put("skuId", skuId);
        param.put("specificDate", specificDate);
        return (ProductSuperConversion)getSqlMapClientTemplate().queryForObject("getProductSuperConversionByGivingTime", param);
    }

    @Override
    public Page<ProductSuperConversion> queryProductSuperConversionByProductId(int start, int limit, int productId) {
        Page<ProductSuperConversion> page = Page.createFromStart(start, limit);
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        param.put("productId", productId);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountProductSuperConversionByProductId", param));
        page.setResult(getSqlMapClientTemplate().queryForList("selectProductSuperConversionByProductId", param));
        return page;
    }

    @Override
    public List<ProductSuperConversion> selectSuperConversionByDate(Date curDate) {
        if (curDate == null) {
            curDate = DateUtils.getCurrentDate();
        }
        Map param = map(
                "curDate",DateUtils.formatDate(curDate, DateUtils.DateFormatType.DATE_FORMAT_STR)
        );

        return getSqlMapClientTemplate().queryForList("selectSuperConversionByDate", param);

    }

    @Override
    public void createProductSuperConversion(ProductSuperConversion productSuperConversion) {
        productSuperConversion.setIntegralCount((int) Currency.CurrencyToIntegral(String.valueOf(productSuperConversion.getIntegralCount())));//*100, 方便存储
        getSqlMapClientTemplate().insert("insertProductSuperConversion", productSuperConversion);
    }

    @Override
    public void deleteProductSuperConversionById(int id) {
        getSqlMapClientTemplate().delete("deleteProductSuperConversionById", id);
    }

    @Override
    public void updateProductSuperConversionById(ProductSuperConversion productSuperConversion) {
        productSuperConversion.setIntegralCount((int) Currency.CurrencyToIntegral(String.valueOf(productSuperConversion.getIntegralCount())));//*100, 方便存储
        getSqlMapClientTemplate().update("updateProductSuperConversionById", productSuperConversion);
    }
}
