package com.kariqu.designcenter.domain.open.api;

import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.designcenter.domain.open.module.*;
import com.kariqu.suppliercenter.domain.Brand;

import java.util.List;
import java.util.Map;

/**
 * 开放接口中的类目服务
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-5-3 下午08:44:08
 */
public interface CategoryService {

    /**
     * 返回某个类目的主推商品ID
     *
     * @param naCategoryId
     * @return
     */
    BasicProduct getCategoryMainRecommendProduct(int naCategoryId);

    /**
     * 获取直系父级类目
     *
     * @param naCategoryId
     * @return
     */
    NavigateCategory getParentCategory(int naCategoryId);

    /**
     * 查询指定类目的所有父级(不包括一级)类目
     *
     * @param naCategoryId 类目id
     * @return
     */
    List<NavigateCategory> categoryNameTree(int naCategoryId);

    /**
     * 查询指定类目的二级类目下的所有三级子类目(二级到三级)
     *
     * @param naCategoryId 三级类目
     * @param showAllThird 是否显示所有的三级类目
     * @param categorySort 是否将当前的类目放到最前面
     * @return
     */
    List<NavigateCategory> buildCategory(int naCategoryId, boolean showAllThird, boolean categorySort);

    /**
     * 传入一级类目ID进入频道数据
     *
     * @param naCategoryId
     * @return
     */
    CategoryChannelData buildCategoryChannelData(int naCategoryId);

    /**
     * 查询指定频道下的所有品牌
     *
     * @param channelId 频道Id
     * @return
     */
    List<BrandData> getBrandUrlByChannel(int channelId);

    /**
     * 查询品牌
     *
     * @param valueId
     * @return
     */
    Brand queryBrandById(int valueId);

    /**
     * 根据属性ID和值ID得到描述图片
     *
     * @param propertyId
     * @param valueId
     * @return
     */
    String getCategoryPropertyDetail(int propertyId, int valueId);


    /**
     * 是否是颜色属性
     * 因为颜色属性比较特殊，我们会经常用图片来显示
     *
     * @param propertyId
     * @return
     */
    boolean isColorProperty(int propertyId);


    /**
     * 查询出前台类目的可筛选属性和值
     *
     * @param navCategoryId
     * @return
     */
    CategorySearchData getNavigateCategorySearchableInfo(String navCategoryId);

    /**
     * 查询前台类目
     *
     * @param navCategoryId
     * @return
     */
    NavigateCategory queryNavigateCategoryById(int navCategoryId);

    NavigateCategory queryNavigateCategoryById(String navCategoryId);

}
