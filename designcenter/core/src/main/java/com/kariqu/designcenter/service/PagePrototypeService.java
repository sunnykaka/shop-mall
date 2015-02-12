package com.kariqu.designcenter.service;

import com.kariqu.designcenter.domain.model.prototype.PagePrototype;

import java.util.List;

/**
 * 页面原型服务
 *
 * @author Tiger
 * @version 1.0
 * @since 12-4-20 上午10:57
 */
public interface PagePrototypeService {

    /**
     * 根据页面ID查询页面原型
     *
     * @param id
     * @return
     */
    PagePrototype getPagePrototypeById(int id);

    List<PagePrototype> queryAllPagePrototype();

    void createPagePrototype(PagePrototype pagePrototype);

    void updatePagePrototype(PagePrototype pagePrototype);

    void deletePagePrototype(int id);

    /**
     * 发布页面原型，主要更改页面原型的状态以及生成页面配置文件
     * @param pagePrototypeId
     */
    void releasePagePrototypeId(int pagePrototypeId);


}
