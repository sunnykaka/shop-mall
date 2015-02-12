package com.kariqu.productcenter.domain;

import java.util.List;

/**
 * 对商品进行图片描述
 * User: Asion
 * Date: 11-9-6
 * Time: 上午10:56
 */
public class PictureDesc {
    public static final String defaultPictureUrl = "http://img07.yijushang.com/images/none.jpg";

    private int productId;

    private List<ProductPicture> pictures;

    /**
     * 得到主图，如果商品没有图片，使用一个默认的
     *
     * @return
     */
    public ProductPicture getMainPicture() {
        ProductPicture main = null;
        for (ProductPicture picture : pictures) {
            if (picture.isMainPic()) {
                main = picture;
                break;
            }
        }
        if (main == null) {
            main = new ProductPicture();
            main.setName("not found");
            main.setMainPic(true);
            main.setPictureUrl(defaultPictureUrl);
        }
        return main;
    }

    public List<ProductPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<ProductPicture> pictures) {
        this.pictures = pictures;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
