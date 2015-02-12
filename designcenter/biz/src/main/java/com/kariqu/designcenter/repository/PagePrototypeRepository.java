package com.kariqu.designcenter.repository;

import com.kariqu.designcenter.domain.model.prototype.PagePrototype;

import java.util.List;

/**
 * User: Asion
 * Date: 12-4-20
 * Time: 上午10:35
 */
public interface PagePrototypeRepository {

    void deletePagePrototypeById(int id);

    PagePrototype getPagePrototypeById(int id);

    List<PagePrototype> queryAllPagePrototype();

    void createPagePrototype(PagePrototype pagePrototype);

    void updatePagePrototype(PagePrototype pagePrototype);
}
