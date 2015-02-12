package com.kariqu.productcenter.service;

import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-6-27
 * Time: 上午10:37
 */
public interface ProductService {

    /**
     * 获取商品基本信息(url, product 名称, picture 主图的最小地址, price)
     *
     * @param productId
     * @return
     */
    Map<String, Object> getProductMap(int productId);

    /**
     * 通知商品修改更新时间，以让搜索引擎感知
     *
     * @param productId
     */
    void notifyProductUpdate(int productId);

    /**
     * 判断一个类目是否发布了产品
     *
     * @param categoryId
     * @return
     */
    boolean existProduct(int categoryId);

    Map<String, Object> getProductMapWithproductInfo(int productId, int skuId);

    /**
     * 使商品上架
     *
     * @param productId
     */
    void makeProductOnline(int productId) throws Exception;


    /**
     * 使商品下架
     *
     * @param productId
     */
    void makeProductOffLine(int productId);


    int createProduct(Product product);

    /** 获取不包含 sku 信息的简单商品对象 */
    Product getSimpleProductById(int id);

    Product getProductById(int id);

    Product getProductByCode(String productCode);


    void updateProduct(Product product);

    void deleteProduct(int id);

    List<Product> queryProductsByCategoryId(int categoryId);

    Page<Product> queryProductsByOnlineStatus(boolean online, Page<Product> page);

    Page<Product> queryProductsByCategoryIdByPage(int categoryId, int pageNo, int limit);

    Page<Product> queryProductsByCustomerIdByPage(int customerId, int pageNo, int limit);

    Page<Product> queryProductsByBrandIdByPage(int brandId, int pageNo, int limit);

    Page<Product> queryProductsByStoreIdByPage(int storeId, int pageNo, int limit);


    Page<Product> queryAllProductsByPage(int pageNo, int limit);


    /**
     * 模糊搜索
     *
     * @param search
     * @return
     */
    List<Product> queryProductByFuzzy(String search);

    /**
     * 取全部商品图片
     *
     * @param productId
     * @return
     */
    PictureDesc getPictureDesc(int productId);

    List<String> getProductPictureUrl(int productId, int limit);

    /**
     * 获取 sku 主图, 设置了多个则获取第一个, 若未设置, 则获取商品主图
     *
     * @param skuId
     * @param productId
     * @return
     */
    ProductPicture getMainPictureBySKuId(long skuId, int productId);

    ProductPicture getProductPictureById(int id);

    /**
     * 查询sku的图片
     * @param skuId
     * @return
     */
    List<ProductPicture> getProductPictureBySkuId(long skuId);

    /*void updatePictureSkuId(int id, long skuId);*/

    void addProductPictureForSku(int[] ids, long skuId);

    void removeProductPictureForSku(int[] ids, long skuId);

    void updatePictureNumber(int id, int num);

    /**
     * 取商品最低价格
     *
     * @param productId
     * @return
     */
    String getProductPrice(int productId);

    /**
     * 商品是否有库存
     *
     * @param productId
     * @return
     */
    boolean getProductHasStock(int productId);

    void createProductPicture(ProductPicture productPicture);

    boolean existMainPicture(int productId);


    void createHtml(Html html);

    void updateHtml(Html html);

    HtmlDesc getHtmlDesc(int productId);

    void deleteHtmlDesc(int productId, String[] names);


    Recommend getRecommend(int productId);

    void createRecommendProduct(int productId, int recommendProductId, RecommendType recommendType);

    void deleteRecommendProduct(int productId, int recommendProductId, RecommendType recommendType);

    void createProductProperty(ProductProperty productProperty);

    void updateProductPropertyBrandJsonByPropertyType(String json, int productId, PropertyType propertyType);

    ProductProperty getProductPropertyByPropertyType(int productId, PropertyType propertyType);

    void deleteProductProperty(int productId);

    void deleteProductPropertyByPropertyType(int productId, PropertyType propertyType);

    void deleteRecommendBy(int byProductId);

    /**
     * 根据商品名字分页查询
     *
     * @param search
     * @param limit
     * @return
     */
    Page<Product> queryProductByFuzzyPage(String search, int pageNo, int limit);

    /**
     * 设置商品图片主图
     *
     * @param id
     */
    void setMainProductPicture(int id);

    /**
     * 设置商品图片的副图
     * @param id
     */
    void setMinorProductPicture(int id);

    /**
     * 判断是否有相同的商品编码
     *
     * @param productCode
     * @return
     */
    boolean existProductCode(String productCode);

    /**
     * 查询指定时间前被删除的商品ID
     *
     * @param date
     * @return
     */
    List<Integer> queryDeletedProductFromGivingTime(Date date);

    /**
     * 修改商品库存策略
     *
     * @param productId
     * @param storeStrategy
     */
    void updateProductStrategy(int productId, StoreStrategy storeStrategy);


    /**
     * 创建一个商品收藏
     *
     * @param productCollect
     */
    void createProductCollect(ProductCollect productCollect);

    /**
     * 删除一个商品收藏
     *
     * @param id
     */
    void deleteProductCollectById(int id, int userId);

    void deleteProductCollectByIds(int[] ids, int userId);

    void cancelProductCollect(int id, int userId);

    void cancelProductCollectByIds(int[] ids, int userId);

    /**
     * 分页查询商品收藏
     *
     * @param userId
     * @return
     */
    Page<ProductCollect> queryProductCollectByPage(int pageNo, int pageSize, int userId);

    Integer queryProductCollectNum(int userId);

    /**
     * 是否已经加入收藏夹
     *
     * @param productCollect
     * @return
     */
    boolean hadProductCollect(ProductCollect productCollect);

    String getMainProductPicture(int productId);

    String getMinorProductPicture(int productId);

    void deleteProductPictureById(int id);

    int deleteBrandNameForAllProduct();
}
