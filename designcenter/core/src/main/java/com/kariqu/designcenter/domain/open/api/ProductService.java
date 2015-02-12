package com.kariqu.designcenter.domain.open.api;

import com.kariqu.designcenter.domain.open.module.BasicProduct;
import com.kariqu.designcenter.domain.open.module.Product;

import java.util.List;
import java.util.Map;

/**
 * @author Tiger
 * @version 1.0.0
 * @since 2011-5-3 下午08:36:28
 */
public interface ProductService {

    /**
     * 根据商品id 获取商品的相关信息
     *
     * @param productIds 用逗号分隔的商品id
     * @return
     */
    List<Map<String, Object>> getProductMap(String productIds);

    /**
     * 构建基本的商品
     *
     * @param id
     * @return
     */
    BasicProduct buildBasicProduct(int id);

    /**
     * 根据商品中心的商品ID构建open api的商品
     *
     * @param productId
     * @return
     */
    Product buildOpenProduct(int productId);


    /**
     * 根据一个商品ID和推荐类型查询这个商品的推荐商品列表
     *
     * @param productId
     * @param recommendType
     * @return
     */
    List<BasicProduct> queryProductRecommend(int productId, String recommendType);


    /**
     * 根据商品ID返回商品的关键属性对
     *
     * @param productId
     * @return
     */
    List<Product.PropertyValuePair> queryKeyPropertyPair(int productId);


    /**
     * 返回指定数量的属性对，如果max=-1则表示无限制
     *
     * @param productId
     * @param max
     * @return
     */
    List<Product.PropertyValuePair> queryKeyPropertyPair(int productId, int max);

    /**
     * 根据一个商品对象构建open api的商品对象
     *
     * @param product
     * @return
     */
    Product buildOpenProduct(com.kariqu.productcenter.domain.Product product);


}
