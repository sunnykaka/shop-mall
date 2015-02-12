package com.kariqu.productcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.*;

import java.util.Date;
import java.util.List;

/**
 * User: Asion
 * Date: 11-6-27
 * Time: 上午10:39
 */
public interface ProductRepository{

    /**
     * 判断一个类目是否发布了产品
     *
     * @param categoryId
     * @return
     */
    boolean existProduct(int categoryId);

    /**
     * 单独提供这个方法来修改商品的修改时间
     *
     * @param productId
     */
    void updateProductModifyTime(int productId);

    List<Product> queryProductsByCategoryId(int categoryId);

    Page<Product> queryProductsByCategoryIdByPage(int categoryId, int pageNo,int limit);

    Page<Product> queryProductsByCustomerIdByPage(int customerId, int pageNo,int limit);

    Page<Product> queryProductsByBrandIdByPage(int brandId, int pageNo,int limit);

    Page<Product> queryProductsByStoreIdByPage(int storeId, int pageNo,int limit);

    Page<Product> queryAllProductsByPage(int pageNo,int limit);

    void createRecommendProduct(int productId, int recommendProductId, RecommendType recommendType);

    void deleteRecommendProduct(int productId, int recommendProductId, RecommendType recommendType);

    List<Integer> getProductIds(int productId, RecommendType recommendType);


    /**
     * 模糊搜索
     *
     * @param search
     * @return
     */
    List<Product> queryProductByFuzzy(String search);

    void deleteRecommendByProductId(int byProductId);

    Page<Product> queryProductByFuzzyPage(String search, int pageNo,int limit);

    int queryProductAmountByProductCode(String productCode);

    List<Integer> queryDeletedProductFromGivingTime(Date date);

    void updateProductStrategy(int productId, StoreStrategy storeStrategy);

    void changeProductOnlineStatus(int productId, boolean online);

    /**
     * 收藏夹
     *
     * @param productCollect
     */
    void createProductCollect(ProductCollect productCollect);

    boolean hadProductCollect(ProductCollect productCollect);

    void deleteProductCollectById(int id, int userId);

    void deleteProductCollectByIds(int[] ids, int userId);

    void cancelProductCollect(int id, int userId);

    void cancelProductCollectByIds(int[] ids, int userId);

    int queryProductCollectNum(int userId);

    Page<ProductCollect> queryProductCollectByPage(int pageNo,int pageSize, int userId);

    /**
     * 按条件查询商家对应的商品编号
     *
     * @param customerId  商家编号
     * @param productCode 商品编码
     * @param productName 商品名, 模糊查询
     * @return
     */
    List<Integer> queryProductByCustomer(int customerId, String productCode, String productName);


    Page<Product> queryProductsByOnlineStatus(boolean online, Page<Product> page);

    List<Product> getProductByCode(String productCode);

    void createProduct(Product product);

    Product getProductById(int id);

    void updateProduct(Product product);

    void deleteProductById(int id);

    List<Product> queryAllProducts();

    int updateProductName(int id, String name);
}
