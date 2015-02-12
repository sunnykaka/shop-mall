package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.repository.ShopTemplateRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

/**
 * @author Asion
 * @since 2011-4-30 下午07:23:39
 * @version 1.0.0
 */
public class ShopTemplateRepositoryImpl extends SqlMapClientDaoSupport implements ShopTemplateRepository {

    @Override
    public ShopTemplate getShopTemplateByShopId(int shopId) {
        return (ShopTemplate) this.getSqlMapClientTemplate().queryForObject("getShopTemplateByShopId", shopId);
    }

    @Override
    public void createShopTemplate(ShopTemplate shopTemplate) {
        this.getSqlMapClientTemplate().insert("insertShopTemplate",shopTemplate);
    }

    @Override
    public void deleteShopTemplateById(int id) {
        this.getSqlMapClientTemplate().delete("deleteShopTemplate",id);
    }

    @Override
    public ShopTemplate getShopTemplateById(int id) {
        return (ShopTemplate) getSqlMapClientTemplate().queryForObject("selectShopTemplate", id);
    }

    @Override
    public List<ShopTemplate> queryAllShopTemplates() {
        return this.getSqlMapClientTemplate().queryForList("selectAllShopTemplates");
    }

    @Override
    public void updateShopTemplate(ShopTemplate shopTemplate) {
        this.getSqlMapClientTemplate().update("updateShopTemplate", shopTemplate);
    }

}
