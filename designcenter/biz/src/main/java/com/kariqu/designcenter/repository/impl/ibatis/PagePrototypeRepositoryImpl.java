package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.prototype.PagePrototype;
import com.kariqu.designcenter.repository.PagePrototypeRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

/**
 * User: Asion
 * Date: 12-4-20
 * Time: 上午10:36
 */
public class PagePrototypeRepositoryImpl extends SqlMapClientDaoSupport implements PagePrototypeRepository {

    @Override
    public void createPagePrototype(PagePrototype pagePrototype) {
        getSqlMapClientTemplate().insert("insertPagePrototype", pagePrototype);
    }

    @Override
    public PagePrototype getPagePrototypeById(int id) {
        return (PagePrototype) getSqlMapClientTemplate().queryForObject("selectPagePrototype", id);
    }

    @Override
    public void updatePagePrototype(PagePrototype pagePrototype) {
        getSqlMapClientTemplate().update("updatePagePrototype", pagePrototype);
    }

    @Override
    public void deletePagePrototypeById(int id) {
        getSqlMapClientTemplate().delete("deletePagePrototype", id);
    }

    @Override
    public List<PagePrototype> queryAllPagePrototype() {
        return getSqlMapClientTemplate().queryForList("selectAllPagePrototypes");
    }

}
