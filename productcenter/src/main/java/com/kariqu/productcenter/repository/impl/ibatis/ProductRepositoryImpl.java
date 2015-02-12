package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.ProductCollect;
import com.kariqu.productcenter.domain.RecommendType;
import com.kariqu.productcenter.domain.StoreStrategy;
import com.kariqu.productcenter.repository.ProductRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-6-27
 * Time: 上午10:43
 */
public class ProductRepositoryImpl extends SqlMapClientDaoSupport implements ProductRepository {

    @Override
    public void createProduct(Product product) {
        getSqlMapClientTemplate().insert("insertProduct", product);
    }

    @Override
    public Product getProductById(int id) {
        return (Product) getSqlMapClientTemplate().queryForObject("selectProduct", id);
    }

    @Override
    public void updateProduct(Product product) {
        getSqlMapClientTemplate().update("updateProduct", product);
    }

    @Override
    public void deleteProductById(int id) {
        getSqlMapClientTemplate().delete("deleteProduct", id);
    }

    @Override
    public List<Product> queryAllProducts() {
        return getSqlMapClientTemplate().queryForList("selectAllProducts");
    }

    @Override
    public int updateProductName(int id, String name) {
        Map param = new HashMap();
        param.put("id", id);
        param.put("name", name);
        return getSqlMapClientTemplate().update("updateProductName", param);
    }

    public List<Integer> queryProductByCustomer(int customerId, String productCode, String productName) {
        Map map = new HashMap();
        map.put("customerId", customerId);
        map.put("productCode", productCode);
        map.put("name", productName);
        return getSqlMapClientTemplate().queryForList("selectProductByCustomer", map);
    }

    @Override
    public List<Product> queryProductsByCategoryId(int categoryId) {
        return getSqlMapClientTemplate().queryForList("queryProductsByCategoryId", categoryId);
    }

    @Override
    public Page<Product> queryProductsByCategoryIdByPage(int categoryId, int pageNo, int limit) {
        Map param = new HashMap();
        param.put("categoryId", categoryId);
        Page<Product> page = new Page<Product>(pageNo, limit);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Product> result = getSqlMapClientTemplate().queryForList("queryProductsByCategoryIdByPage", param);
        page.setResult(result);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForProductByCategoryId", categoryId));
        return page;
    }

    @Override
    public Page<Product> queryProductsByCustomerIdByPage(int customerId, int pageNo, int limit) {
        Map param = new HashMap();
        param.put("customerId", customerId);
        Page<Product> page = new Page<Product>(pageNo, limit);

        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Product> result = getSqlMapClientTemplate().queryForList("queryProductsByCustomerIdByPage", param);
        page.setResult(result);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForProductByCustomerId", customerId));
        return page;
    }

    @Override
    public Page<Product> queryProductsByBrandIdByPage(int brandId, int pageNo, int limit) {
        Map param = new HashMap();
        param.put("brandId", brandId);
        Page<Product> page = new Page<Product>(pageNo, limit);

        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Product> result = getSqlMapClientTemplate().queryForList("queryProductsByBrandIdByPage", param);
        page.setResult(result);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForProductByBrandId", brandId));
        return page;
    }

    @Override
    public Page<Product> queryProductsByStoreIdByPage(int storeId, int pageNo, int limit) {
        Map param = new HashMap();
        param.put("storeId", storeId);
        Page<Product> page = new Page<Product>(pageNo, limit);

        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Product> result = getSqlMapClientTemplate().queryForList("queryProductsByStoreIdByPage", param);
        page.setResult(result);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForProductByStoreId", storeId));
        return page;
    }

    @Override
    public Page<Product> queryAllProductsByPage(int pageNo, int limit) {
        Map param = new HashMap();
        Page<Product> page = new Page<Product>(pageNo, limit);

        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Product> result = getSqlMapClientTemplate().queryForList("queryAllProductsByPage", param);
        page.setResult(result);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForProduct"));
        return page;
    }

    @Override
    public boolean existProduct(int categoryId) {
        Product product = (Product) getSqlMapClientTemplate().queryForObject("selectOneProduct", categoryId);
        return product == null ? false : true;
    }

    @Override
    public void updateProductModifyTime(int productId) {
        getSqlMapClientTemplate().update("updateProductModifyTime", productId);
    }

    @Override
    public void createRecommendProduct(int productId, int recommendProductId, RecommendType recommendType) {
        Map map = new HashMap();
        map.put("productId", productId);
        map.put("recommendProductId", recommendProductId);
        map.put("recommendType", recommendType);
        getSqlMapClientTemplate().insert("insertRecommendProduct", map);
    }

    @Override
    public void deleteRecommendProduct(int productId, int recommendProductId, RecommendType recommendType) {
        Map map = new HashMap();
        map.put("productId", productId);
        map.put("recommendProductId", recommendProductId);
        map.put("recommendType", recommendType);
        getSqlMapClientTemplate().delete("deleteRecommendProduct", map);
    }

    @Override
    public List<Integer> getProductIds(int productId, RecommendType recommendType) {
        Map map = new HashMap();
        map.put("productId", productId);
        map.put("type", recommendType);
        return getSqlMapClientTemplate().queryForList("selectRecommendByType", map);
    }


    @Override
    public List<Product> queryProductByFuzzy(String search) {
        StringBuffer sb = new StringBuffer();
        sb.append("%");
        sb.append(search);
        sb.append("%");
        return getSqlMapClientTemplate().queryForList("queryProductByFuzzy", sb.toString());
    }

    @Override
    public void deleteRecommendByProductId(int byProductId) {
        getSqlMapClientTemplate().delete("deleteRecommendBy", byProductId);
    }

    @Override
    public Page<Product> queryProductByFuzzyPage(String search, int pageNo, int limit) {
        StringBuffer sb = new StringBuffer();
        sb.append("%");
        sb.append(search);
        sb.append("%");
        Map param = new HashMap();
        param.put("search", sb.toString());
        Page<Product> page = new Page<Product>(pageNo, limit);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Product> result = getSqlMapClientTemplate().queryForList("queryProductByFuzzyPage", param);
        page.setResult(result);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountByFuzzyPage", param));
        return page;
    }

    @Override
    public int queryProductAmountByProductCode(String productCode) {
        return (Integer) this.getSqlMapClientTemplate().queryForObject("queryProductAmountByProductCode", productCode);
    }

    @Override
    public List<Integer> queryDeletedProductFromGivingTime(Date date) {
        return this.getSqlMapClientTemplate().queryForList("queryDeletedProductFromGivingTime", date);
    }


    @Override
    public void updateProductStrategy(int productId, StoreStrategy storeStrategy) {
        Map param = new HashMap();
        param.put("storeStrategy", storeStrategy);
        param.put("id", productId);
        this.getSqlMapClientTemplate().update("updateProductStrategy", param);
    }

    @Override
    public void changeProductOnlineStatus(int productId, boolean online) {
        Map param = new HashMap();
        param.put("productId", productId);
        param.put("online", online);
        if (online) {
            Date onlineData = new Date();
            param.put("onlineTime", onlineData);
            param.put("onLineTimeLong", onlineData.getTime());
        } else {
            param.put("offLineTime", new Date());
        }
        this.getSqlMapClientTemplate().update("changeProductOnlineStatus", param);
    }

    @Override
    public void createProductCollect(ProductCollect productCollect) {
        getSqlMapClientTemplate().insert("insertProductCollect", productCollect);
    }

    @Override
    public boolean hadProductCollect(ProductCollect productCollect) {
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject("CheckProductCollectNum", productCollect).toString()) > 0;
    }

    @Override
    public void deleteProductCollectById(int id, int userId) {
        Map map = new HashMap();
        map.put("id", id);
        map.put("userId", userId);
        getSqlMapClientTemplate().update("deleteProductCollectById", map);
    }

    @Override
    public void deleteProductCollectByIds(int[] ids, int userId) {
        Map map = new HashMap();
        map.put("ids", ids);
        map.put("userId", userId);
        getSqlMapClientTemplate().update("deleteProductCollectByIds", map);
    }

    @Override
    public Page<ProductCollect> queryProductCollectByPage(int pageNo, int pageSize, int userId) {
        Page<ProductCollect> page = new Page<ProductCollect>(pageNo, pageSize);
        Map param = new HashMap();
        param.put("userId", userId);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<ProductCollect> productCollectList = getSqlMapClientTemplate().queryForList("selectProductCollectByUserId", param);
        page.setResult(productCollectList);
        page.setTotalCount(queryProductCollectNum(userId));
        return page;
    }

    @Override
    public Page<Product> queryProductsByOnlineStatus(boolean online, Page<Product> page) {
        Map param = new HashMap();
        param.put("online", online);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Product> productList = getSqlMapClientTemplate().queryForList("selectProductByOnlineStatus", param);
        page.setResult(productList);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectProductCountByOnlineStatus", online));
        return page;
    }

    @Override
    public List<Product> getProductByCode(String productCode) {
        return getSqlMapClientTemplate().queryForList("getProductByCode", productCode);
    }

    @Override
    public void cancelProductCollect(int id, int userId) {
        Map map = new HashMap();
        map.put("id", id);
        map.put("userId", userId);
        getSqlMapClientTemplate().update("cancelProductCollect", map);
    }

    @Override
    public void cancelProductCollectByIds(int[] ids, int userId) {
        Map map = new HashMap();
        map.put("ids", ids);
        map.put("userId", userId);
        getSqlMapClientTemplate().update("cancelProductCollectByIds", map);
    }

    @Override
    public int queryProductCollectNum(int userId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountProductCollect", userId);
    }

}
