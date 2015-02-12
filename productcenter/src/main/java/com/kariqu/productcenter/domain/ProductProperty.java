package com.kariqu.productcenter.domain;

import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.categorycenter.domain.util.PidVidJsonUtil;

import java.util.List;

/**
 * 商品属性
 * User: Asion
 * Date: 11-11-8
 * Time: 上午11:24
 */
public class ProductProperty {

    private int productId;

    //商品的pidvid json串，表示商品的属性，按属性在类目上的优先级排序
    // '{"singlePidVidMap":{"1":4294977296,"4":17179879190,"5":21474846488},"pidvid":[4294977296,17179879190,21474846488],"multiPidVidMap":{}}'
    private String json;

    /**
     * 属性类型
     */
    private PropertyType propertyType;


    /**
     * 读取pidvid的个数
     *
     * @return
     */
    public int getPidVidCount() {
        List<Long> longs = PidVidJsonUtil.restore(json).getPidvid();
        return longs.size();
    }


    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }
}
