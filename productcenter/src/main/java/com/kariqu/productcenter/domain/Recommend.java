package com.kariqu.productcenter.domain;


import java.util.Collections;
import java.util.List;

/**
 * 商品推荐的内存模型
 * User: Asion
 * Date: 11-9-6
 * Time: 上午11:05
 */
public class Recommend {

    /**
     * 哪个商品的推荐
     */
    private int productId;

    private List<Integer> accessoryProducts = Collections.emptyList(); //配件

    private List<Integer> composeProducts = Collections.emptyList();  //组合

    private List<Integer> categoryProducts = Collections.emptyList(); //同类

    /**
     * 根据推荐类型得到商品列表ID
     *
     * @param recommendType
     * @return
     */
    public List<Integer> getProductList(RecommendType recommendType) {
        if (recommendType == RecommendType.ACCESSORY) {
            return accessoryProducts;
        } else if (recommendType == RecommendType.COMPOSE) {
            return composeProducts;
        } else if (recommendType == RecommendType.CATEGORY) {
            return categoryProducts;
        } else {
            return Collections.emptyList();
        }
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setAccessoryProducts(List<Integer> accessoryProducts) {
        this.accessoryProducts = accessoryProducts;
    }

    public void setComposeProducts(List<Integer> composeProducts) {
        this.composeProducts = composeProducts;
    }

    public void setCategoryProducts(List<Integer> categoryProducts) {
        this.categoryProducts = categoryProducts;
    }
}
