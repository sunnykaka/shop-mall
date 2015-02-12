package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopPageType;
import com.kariqu.designcenter.repository.ShopPageRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-30 下午07:24:22
 */
public class ShopPageRepositoryImpl extends SqlMapClientDaoSupport implements ShopPageRepository {

    @Override
    public ShopPage queryIndexShopPage(int shopId) {
        return (ShopPage) this.getSqlMapClientTemplate().queryForObject("queryIndexShopPage", shopId);
    }

    @Override
    public List<ShopPage> queryShopPageWithShopPageType(int shopId, ShopPageType shopPageType) {
        Map param = new HashMap();
        param.put("shopId", shopId);
        param.put("shopPageType", shopPageType);
        return this.getSqlMapClientTemplate().queryForList("queryShopPageWithShopPageType", param);
    }

    @Override
    public List<ShopPage> queryShopPagesByShopId(int shopId) {
        return getSqlMapClientTemplate().queryForList("queryShopPagesByShopId", shopId);
    }

    @Override
    public void createShopPage(ShopPage shopPage) {
        this.getSqlMapClientTemplate().insert("insertShopPage", shopPage);
    }

    @Override
    public void deleteShopPageById(Long id) {
        this.getSqlMapClientTemplate().delete("deleteShopPage", id);
    }

    @Override
    public ShopPage getShopPageById(Long id) {
        return (ShopPage) this.getSqlMapClientTemplate().queryForObject("selectShopPage", id);
    }

    @Override
    public List<ShopPage> queryAllShopPages() {
        return this.getSqlMapClientTemplate().queryForList("selectAllShopPages");
    }

    @Override
    public void updateShopPage(ShopPage shopPage) {
        this.getSqlMapClientTemplate().update("updateShopPage", shopPage);
    }

    @Override
    public ShopPage querySearchListShopPage(int shopId) {
        return (ShopPage) this.getSqlMapClientTemplate().queryForObject("querySearchListShopPage", shopId);
    }

    @Override
    public ShopPage queryDetailShopPage(int shopId) {
        return (ShopPage) this.getSqlMapClientTemplate().queryForObject("queryDetailShopPage", shopId);
    }

    @Override
    public ShopPage queryDetailIntegralShopPage(int shopId) {
        return (ShopPage) this.getSqlMapClientTemplate().queryForObject("queryDetailIntegralShopPage", shopId);
    }

    @Override
    public ShopPage queryChannelShopPage(int shopId) {
        return (ShopPage) this.getSqlMapClientTemplate().queryForObject("queryChannelShopPage", shopId);
    }

    @Override
    public ShopPage queryMealSetShopPage(int shopId) {
        return (ShopPage) this.getSqlMapClientTemplate().queryForObject("queryMealSetShopPage", shopId);

    }
}
