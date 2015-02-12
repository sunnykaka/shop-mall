package com.kariqu.productcenter.repository;

import com.kariqu.productcenter.domain.ProductPicture;

import java.util.List;

/**
 * User: Asion
 * Date: 11-6-28
 * Time: 下午4:27
 */
public interface ProductPictureRepository {


    ProductPicture getProductPictureById(int id);


    /**
     * 查询商品的主图
     *
     * @param productId
     * @return
     */
    ProductPicture getMainProductPictureByProductId(int productId);

    /** 商品副图 */
    ProductPicture getMinorProductPictureByProductId(int productId);

    void makeProductPictureAsMain(ProductPicture productPicture);


    void deleteProductPictureById(int id);

    void createProductPicture(ProductPicture productPicture);

    List<ProductPicture> queryProductPicturesByProductId(int productId);

    List<String> queryProductPictureUrlByProductId(int productId, int limit);

    void deleteProductPictureByProductId(int productId);

    List<ProductPicture> queryAllProductPictures();

    List<ProductPicture> queryProductPicturesBySkuId(String skuId);

    void makeProductPictureAsMinor(ProductPicture productPicture);

    void updateProductPictureSkuId(int id, String skuId);

    void updateProductPictureNum(int id, int num);
}
