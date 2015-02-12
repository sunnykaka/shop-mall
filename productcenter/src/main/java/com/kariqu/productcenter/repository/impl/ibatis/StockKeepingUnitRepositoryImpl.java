package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.SkuProperty;
import com.kariqu.productcenter.domain.SkuStockModifyException;
import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.repository.StockKeepingUnitRepository;
import com.kariqu.productcenter.service.SupplierQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.*;

/**
 * User: Asion
 * Date: 11-9-20
 * Time: 上午9:53
 */
public class StockKeepingUnitRepositoryImpl extends SqlMapClientDaoSupport implements StockKeepingUnitRepository {

    private static Log logger = LogFactory.getLog(StockKeepingUnitRepositoryImpl.class);

    @Override
    public void createStockKeepingUnit(StockKeepingUnit stockKeepingUnit) {
        getSqlMapClientTemplate().insert("insertStockKeepingUnit", stockKeepingUnit);
    }

    @Override
    public StockKeepingUnit getStockKeepingUnitById(long id) {
        StockKeepingUnit stockKeepingUnit = (StockKeepingUnit) getSqlMapClientTemplate().queryForObject("selectStockKeepingUnit", id);
        if (stockKeepingUnit != null) {
            loadSkuProperty(stockKeepingUnit);
        } else {
            logger.warn("发现被删除了的SKU,id是:" + id);
        }
        return stockKeepingUnit;
    }


    private void loadSkuProperty(StockKeepingUnit stockKeepingUnit) {
        List<SkuProperty> skuProperties = new LinkedList<SkuProperty>();
        String skuPropertiesInDb = stockKeepingUnit.getSkuPropertiesInDb(); //把数据库中的字符串pidvid列表转换为Sku属性的List
        if (StringUtils.isNotEmpty(skuPropertiesInDb)) {
            String[] pidvid = skuPropertiesInDb.split(",");
            for (String s : pidvid) {
                PropertyValueUtil.PV pv = PropertyValueUtil.parseLongToPidVid(Long.valueOf(s));
                SkuProperty skuProperty = new SkuProperty();
                skuProperty.setSkuId(stockKeepingUnit.getId());
                skuProperty.setPropertyId(pv.pid);
                skuProperty.setValueId(pv.vid);
                skuProperties.add(skuProperty);
            }
        }
        stockKeepingUnit.setSkuProperties(skuProperties);
    }

    @Override
    public List<StockKeepingUnit> querySKUByProductId(int productId) {
        List<StockKeepingUnit> skus = getSqlMapClientTemplate().queryForList("querySKUByProductId", productId);
        for (StockKeepingUnit sku : skus) {
            loadSkuProperty(sku);
        }
        return skus;
    }

    @Override
    public void deleteSKUByProductId(int productId) {
        getSqlMapClientTemplate().delete("deleteSKUByProductId", productId);
    }

    @Override
    public boolean minusSkuStock(final long skuId, final long productStorageId, final int number) throws SkuStockModifyException {
        try {
            int rowsEffected = getSqlMapClientTemplate().update("minusSkuStock", new HashMap() {{
                put("skuId", skuId);
                put("productStorageId", productStorageId);
                put("number", number);
            }});
            return rowsEffected == 1;
        } catch (DataIntegrityViolationException e) {
            logger.error("减少库存出现异常", e);
            throw new SkuStockModifyException("减少库存出现异常", e);
        } catch (Exception e) {
            logger.error("减少库存出现异常", e);
            throw new RuntimeException("减少库存出现异常", e);
        }
    }

    @Override
    public boolean addSkuStock(final long skuId, final long productStorageId, final int number) {
        try {
            int rowsEffected = getSqlMapClientTemplate().update("addSkuStock", new HashMap() {{
                put("skuId", skuId);
                put("productStorageId", productStorageId);
                put("number", number);
            }});
            return rowsEffected == 1;
        } catch (DataIntegrityViolationException e) {
            logger.error("增加库存出现异常", e);
            throw new SkuStockModifyException("增加库存出现异常", e);
        } catch (Exception e) {
            logger.error("增加库存出现异常", e);
            throw new RuntimeException("增加库存出现异常", e);
        }
    }

    @Override
    public void updateStockKeepingUnit(StockKeepingUnit entity) {
        getSqlMapClientTemplate().update("updateStockKeepingUnit", entity);
    }

    /*编辑库位Id、库存数量*/
    //Todo 以后变为一个sku对应多个库位
    @Override
    public void editSkuStorage(SkuStorage skuStorage) {
        /* Map map = new HashMap();
       Date date = new Date();
       map.put("skuId", skuStorage.getSkuId());
       map.put("productStorageId", skuStorage.getProductStorageId());
       map.put("stockQuantity", skuStorage.getStockQuantity());
       getSqlMapClientTemplate().update("updateSkuStorage", map);*/
        /*版本1，一个sku只有一个库位*/
        getSqlMapClientTemplate().delete("deleteSkuStorage", skuStorage.getSkuId());
        getSqlMapClientTemplate().insert("insertSkuStorage", skuStorage);

    }

    /*编辑价格*/
    @Override
    public void editPrice(long skuId, long price, long marketPrice) {
        Map map = new HashMap();
        Date date = new Date();
        map.put("id", skuId);
        map.put("price", price);
        map.put("marketPrice", marketPrice);
        getSqlMapClientTemplate().update("updateSkuPrice", map);
    }

    /*编辑条形码 编号*/
    @Override
    public void editSkuBarCodeAndSkuCode(long skuId, String barCode, String skuCode) {
        Map map = new HashMap();
        Date date = new Date();
        map.put("id", skuId);
        map.put("barCode", barCode);
        map.put("skuCode", skuCode);
        getSqlMapClientTemplate().update("updateSkuBarCode", map);
    }

    @Override
    public void deleteStockKeepingUnitById(long id) {
        getSqlMapClientTemplate().delete("deleteStockKeepingUnit", id);
        /*删除库位*/
        getSqlMapClientTemplate().delete("deleteSkuStorage", id);
    }

    //根据id 字符串 删除sku及库位
    @Override
    public void deleteSKUByIdList(String skuIdList) {
        String[] skuId = skuIdList.split(",");
        if (skuId.length > 0) {
            getSqlMapClientTemplate().delete("deleteStockKeepingUnitByIdList", skuId);
            getSqlMapClientTemplate().delete("deleteSkuStorageByIdList", skuId);
        }

    }

    @Override
    public List<StockKeepingUnit> queryAllStockKeepingUnits() {
        List<StockKeepingUnit> skus = getSqlMapClientTemplate().queryForList("selectAllStockKeepingUnits");
        for (StockKeepingUnit sku : skus) {
            loadSkuProperty(sku);
        }
        return skus;
    }

    @Override
    public Page<StockKeepingUnit> querySKUByStorageId(int storageId, int pageNo, int pageSize) {
        Map param = new HashMap();
        param.put("storageId", storageId);
        Page<StockKeepingUnit> page = new Page<StockKeepingUnit>();
        page.setPageSize(pageSize);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("querySKUCountByStorageId", param));
        if (pageNo > page.getTotalPages()) {
            return new Page<StockKeepingUnit>(pageNo, pageSize);
        }

        page.setPageNo(pageNo);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<StockKeepingUnit> skuList = getSqlMapClientTemplate().queryForList("querySKUByStorageId", param);
        page.setResult(skuList);

        return page;
    }

    @Override
    public List<SkuStorage> queryStorageBySkuId(long skuId) {
        List<SkuStorage> storageId = getSqlMapClientTemplate().queryForList("queryStorageBySkuId", skuId);
        return storageId;
    }

    @Override
    public int queryStockQuantity(long skuId, int productStorageId) {
        Map map = new HashMap();
        map.put("skuId", skuId);
        map.put("productStorageId", productStorageId);
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject("queryStockQuantity", map).toString());
    }

    @Override
    public int queryTradeMaxNumber(long skuId, int productStorageId) {
        Map map = new HashMap();
        map.put("skuId", skuId);
        map.put("productStorageId", productStorageId);
        return Integer.parseInt(getSqlMapClientTemplate().queryForObject("queryTradeMaxNumber", map).toString());
    }

    @Override
    public void addSkuStorage(SkuStorage skuStorage) {
        getSqlMapClientTemplate().insert("insertSkuStorage", skuStorage);
    }

    @Override
    public void deleteSkuStorageByProductId(int productId) {
        getSqlMapClientTemplate().delete("deleteSkuStorageByProductId", productId);
    }

    @Override
    public void updateSkuState(int[] ids, StockKeepingUnit.SKUState skuState) {
        if (ids.length > 0) {
            Map map = new HashMap();
            map.put("skuState", skuState);
            map.put("skuIds", ids);
            getSqlMapClientTemplate().update("updateSkuState", map);
        }
    }


    @Override
    public int queryAllStockQuantity(int productId) {
        //这里的是该商品所有 有效的sku库存 和
        return (Integer) getSqlMapClientTemplate().queryForObject("queryAllStockQuantity", productId);
    }

    @Override
    public StockKeepingUnit querySKUByBarCode(String barCode) {
        return (StockKeepingUnit) getSqlMapClientTemplate().queryForObject("querySKUByBarCode", barCode);
    }


    @Override
    public int queryCountSKUByBarCode(String barCode) {
        return (Integer) getSqlMapClientTemplate().queryForObject("queryCountSKUByBarCode", barCode);
    }

    public List<Integer> querySkuIdByProductStorage(int productStorage) {
        return getSqlMapClientTemplate().queryForList("querySkuByProductStorage", productStorage);
    }

    public Page<StockKeepingUnit> queryStockKeepingUnitByQuery(int customerId, SupplierQuery query) {
        Map map = new HashMap();
        map.put("customerId", customerId);
        map.put("productCode", query.getProductCode());
        map.put("productName", query.getProductName());
        map.put("productStorageId", query.getStoreId());
        map.put("barCode", query.getBarCode());
        map.put("dateType", query.getDateType());
        map.put("startDate", query.getStartDate());
        map.put("endDate", query.getEndDate());
        map.put("start", query.getStart());
        map.put("limit", query.getLimit());
        List<StockKeepingUnit> list = getSqlMapClientTemplate().queryForList("queryStockKeepingUnitByQuery", map);
        Page<StockKeepingUnit>  page = new Page<StockKeepingUnit>();
        page.setResult(list);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("queryStockKeepingUnitCountByQuery", map));
        return page;
    }

}
