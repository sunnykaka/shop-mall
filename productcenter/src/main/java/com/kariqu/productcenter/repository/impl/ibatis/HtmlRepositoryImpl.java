package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.Html;
import com.kariqu.productcenter.repository.HtmlRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-9-8
 * Time: 上午10:39
 */
public class HtmlRepositoryImpl extends SqlMapClientDaoSupport implements HtmlRepository {
    @Override
    public void createHtml(Html value) {
        getSqlMapClientTemplate().insert("insertHtml", value);
    }


    @Override
    public List<Html> queryByProductId(int productId) {
        return getSqlMapClientTemplate().queryForList("queryHtmlByProductId", productId);
    }


    @Override
    public Html getByProductId(int productId) {
        return (Html) getSqlMapClientTemplate().queryForObject(
                "queryHtmlByProductId", productId);    }

    @Override
    public void updateHtml(Html value) {
        getSqlMapClientTemplate().update("updateHtml", value);
    }

    @Override
    public void deleteHtmlByProductId(int productId) {
        getSqlMapClientTemplate().delete("deleteHtml", productId);
    }

    @Override
    public List<Html> queryAllHtml() {
        return getSqlMapClientTemplate().queryForList("selectAllHtml");
    }

    @Override
    public void deleteHtmlByProductIdAndName(int productId, String name) {
        Map map = new HashMap();
        map.put("productId", productId);
        map.put("name", name);
        getSqlMapClientTemplate().delete("deleteHtmlByProductIdAndName", map);
    }
}
