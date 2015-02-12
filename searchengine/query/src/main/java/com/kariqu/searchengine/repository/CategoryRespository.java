package com.kariqu.searchengine.repository;

import java.util.List;
import java.util.Map;

/**
 * Created by play.liu on 2014/7/1.
 * Description:
 */
public interface CategoryRespository {
    /**
     * 查询所有的类目名称
     * @return 类目名称集合
     */
     public List<String> loadAllCategoryName();
    public List<String> loadAllCategoryBrandMapping();

}
