package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.ProductPicture;
import com.kariqu.productcenter.repository.ProductPictureRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-6-28
 * Time: 下午4:28
 */
public class ProductPictureRepositoryImpl extends SqlMapClientDaoSupport implements ProductPictureRepository {

    @Override
    public ProductPicture getProductPictureById(int id) {
        return (ProductPicture) getSqlMapClientTemplate().queryForObject("getProductPictureById", id);
    }

    @Override
    public void createProductPicture(ProductPicture productPicture) {
        getSqlMapClientTemplate().insert("insertProductPicture", productPicture);
    }

    @Override
    public List<ProductPicture> queryProductPicturesByProductId(int productId) {
        return getSqlMapClientTemplate().queryForList("queryProductPictureByProductId", productId);
    }

    @Override
    public List<String> queryProductPictureUrlByProductId(int productId, int limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("productId", productId);
        map.put("limit", limit);
        return getSqlMapClientTemplate().queryForList("queryProductPictureUrlByProductId", map);
    }

    @Override
    public void deleteProductPictureByProductId(int productId) {
        getSqlMapClientTemplate().delete("deleteProductPicture", productId);
    }

    @Override
    public List<ProductPicture> queryAllProductPictures() {
        return getSqlMapClientTemplate().queryForList("selectAllProductPictures");
    }

    @Override
    public List<ProductPicture> queryProductPicturesBySkuId(String skuId) {
        return getSqlMapClientTemplate().queryForList("queryProductPicturesBySkuId", skuId);
    }

    @Override
    public void updateProductPictureSkuId(int id, String skuId) {
        Map params = new HashMap();
        params.put("id", id);
        params.put("skuId", skuId);
        getSqlMapClientTemplate().update("updateProductPictureSkuId", params);
    }

    @Override
    public void updateProductPictureNum(int id, int num) {
        Map params = new HashMap();
        params.put("id", id);
        params.put("number", num);
        getSqlMapClientTemplate().update("updateProductPictureNum", params);
    }

    @Override
    public ProductPicture getMainProductPictureByProductId(int productId) {
        return (ProductPicture) getSqlMapClientTemplate().queryForObject("getMainProductPicture", productId);
    }

    @Override
    public ProductPicture getMinorProductPictureByProductId(int productId) {
        return (ProductPicture) getSqlMapClientTemplate().queryForObject("getMinorProductPicture", productId);
    }

    @Override
    public void makeProductPictureAsMain(ProductPicture productPicture) {
        getSqlMapClientTemplate().update("updateAnotherPicToNotMain", productPicture);
        getSqlMapClientTemplate().update("makeProductPictureAsMain", productPicture);
    }

    @Override
    public void makeProductPictureAsMinor(ProductPicture productPicture) {
        getSqlMapClientTemplate().update("updateAnotherPicToNotMinor", productPicture);
        getSqlMapClientTemplate().update("makeProductPictureAsMinor", productPicture);
    }

    @Override
    public void deleteProductPictureById(int id) {
        getSqlMapClientTemplate().delete("deleteProductPictureById", id);
    }
}
