package com.kariqu.searchengine.repository;

import java.util.List;

/**
 * Created by play.liu on 2014/7/1.
 * Description:
 */
public interface BrandRespository {
    /**
     * 获取所有的品牌名
     * @return 返回所有的品牌名称
     */
    public List<String> loadAllBrandName();

}
