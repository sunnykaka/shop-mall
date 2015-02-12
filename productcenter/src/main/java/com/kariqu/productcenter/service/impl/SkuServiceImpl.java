package com.kariqu.productcenter.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.model.Value;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.repository.ProductRepository;
import com.kariqu.productcenter.repository.StockKeepingUnitRepository;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.productcenter.service.SupplierQuery;
import com.kariqu.suppliercenter.domain.ProductStorage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 12-6-1
 * Time: 下午2:05
 */
public class SkuServiceImpl implements SkuService {

    @Autowired
    private StockKeepingUnitRepository stockKeepingUnitRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SkuStorageService skuStorageService;

    @Autowired
    private CategoryPropertyService categoryPropertyService;


    @Override
    public boolean minusSkuStock(long skuId, long productStorageId, int number) {
        return stockKeepingUnitRepository.minusSkuStock(skuId, productStorageId, number);
    }

    @Override
    public boolean addSkuStock(long skuId, long productStorageId, int number) {
        return stockKeepingUnitRepository.addSkuStock(skuId, productStorageId, number);
    }

    @Override
    public long createStockKeepingUnit(StockKeepingUnit stockKeepingUnit) {
        stockKeepingUnitRepository.createStockKeepingUnit(stockKeepingUnit);
        return stockKeepingUnit.getId();
    }

    @Override
    public StockKeepingUnit getStockKeepingUnit(long skuId) {
        return stockKeepingUnitRepository.getStockKeepingUnitById(skuId);
    }

    @Override
    public StockKeepingUnit getStockKeepingUnitWithStock(long skuId, int productStorageId) {
        StockKeepingUnit sku = stockKeepingUnitRepository.getStockKeepingUnitById(skuId);
        if (null == sku) {
            return null;
        }
        sku.setStockQuantity(stockKeepingUnitRepository.queryStockQuantity(skuId, productStorageId));
        sku.setTradeMaxNumber(stockKeepingUnitRepository.queryTradeMaxNumber(skuId, productStorageId));
        return sku;
    }

    @Override
    public List<StockKeepingUnit> querySKUByProductId(int productId) {
        return stockKeepingUnitRepository.querySKUByProductId(productId);
    }

    @Override
    public Page<StockKeepingUnit> querySKUByStorageId(int storageId, int pageNo, int pageSize) {
        return stockKeepingUnitRepository.querySKUByStorageId(storageId, pageNo, pageSize);
    }

    /**
     * 查询有库存信息的sku
     *
     * @param productId
     * @return
     */
    @Override
    public List<StockKeepingUnit> queryProductSkuWithStock(int productId) {
        List<StockKeepingUnit> stockKeepingUnits = querySKUByProductId(productId);
        if (null == stockKeepingUnits || stockKeepingUnits.size() == 0) {
            throw new RuntimeException("商品(" + productId + ")没有sku!");
        }
        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnits) {
            if (stockKeepingUnit.getSkuState() == StockKeepingUnit.SKUState.NORMAL) {
                ProductStorage concretionStorage = skuStorageService.getConcretionStorage(stockKeepingUnit.getId());
                if (concretionStorage != null) {
                    stockKeepingUnit.setStockQuantity(stockKeepingUnitRepository.queryStockQuantity(stockKeepingUnit.getId(), concretionStorage.getId()));
                    stockKeepingUnit.setTradeMaxNumber(stockKeepingUnitRepository.queryTradeMaxNumber(stockKeepingUnit.getId(), concretionStorage.getId()));
                }
            }
        }
        return stockKeepingUnits;
    }

    @Override
    public List<StockKeepingUnit> queryValidSkuForSell(int productId) {
        List<StockKeepingUnit> stockKeepingUnitList = queryProductSkuWithStock(productId);
        if (stockKeepingUnitList == null) return null;

        Iterator<StockKeepingUnit> iterator = stockKeepingUnitList.iterator();
        while (iterator.hasNext()) {
            StockKeepingUnit stockKeepingUnit = iterator.next();
            if (stockKeepingUnit.getSkuState() == StockKeepingUnit.SKUState.REMOVED) {
                iterator.remove();
            }
        }

        //如果没有有效sku，直接抛出异常
        if (stockKeepingUnitList.size() == 0) {
            throw new RuntimeException("商品无有效的 sku!");
        }
        return stockKeepingUnitList;
    }

    @Override
    public void updateStockKeepingUnit(StockKeepingUnit stockKeepingUnit) {
        stockKeepingUnitRepository.updateStockKeepingUnit(stockKeepingUnit);
    }

    @Override
    public void editSkuPrice(long skuId, long price, long marketPrice) {
        stockKeepingUnitRepository.editPrice(skuId, price, marketPrice);
    }

    @Transactional
    public void editSkuStorage(SkuStorage skuStorage) {
        stockKeepingUnitRepository.editSkuStorage(skuStorage);
    }

    @Override
    public void deleteSKUByProductId(int productId) {
        stockKeepingUnitRepository.deleteSkuStorageByProductId(productId);
        stockKeepingUnitRepository.deleteSKUByProductId(productId);
    }

    @Override
    public boolean hasMultiSku(int productId) {
        List<StockKeepingUnit> stockKeepingUnits = stockKeepingUnitRepository.querySKUByProductId(productId);
        if (null == stockKeepingUnits || stockKeepingUnits.size() == 0) {
            throw new RuntimeException("商品(" + productId + ")没有sku!");
        }
        if (stockKeepingUnits.size() > 1) {//如果有多个sku，那么肯定有多值
            return true;
        } else { //如果sku属性大于零，则是多值
            StockKeepingUnit stockKeepingUnit = stockKeepingUnits.get(0);
            if (stockKeepingUnit.getSkuProperties().size() > 0) {
                return true;
            } else {
                return false;//没有销售属性
            }
        }
    }

    public void createSkuStorage(SkuStorage skuStorage) {
        stockKeepingUnitRepository.addSkuStorage(skuStorage);
    }

    public void deleteSku(long id) {
        stockKeepingUnitRepository.deleteStockKeepingUnitById(id);
    }

    public void deleteSkuByIdList(String id) {
        stockKeepingUnitRepository.deleteSKUByIdList(id);
    }

    @Override
    public void updateSkuState(int[] ids, StockKeepingUnit.SKUState skuState) {
        for (int id : ids) {
            StockKeepingUnit stockKeepingUnit = getStockKeepingUnit(id);
            if (stockKeepingUnit.getPrice() == 0) {
                throw new RuntimeException("价格不合法");
            }
            SkuStorage skuStorage = skuStorageService.getSkuStorage(id);
            if (skuStorage == null) {
                throw new RuntimeException("SKU没有指定库位");
            }
        }
        stockKeepingUnitRepository.updateSkuState(ids, skuState);
    }

    @Override
    public String getSkuPropertyToString(StockKeepingUnit stockKeepingUnit) {
        Joiner.MapJoiner joiner = Joiner.on(" ").withKeyValueSeparator(":");
        return joiner.join(getSkuPropertyNameAndValueNamePair(stockKeepingUnit));
    }

    private Map<String, String> getSkuPropertyNameAndValueNamePair(StockKeepingUnit stockKeepingUnit) {
        Map<String, String> pairMap = Maps.newHashMap();
        String skuProperties = stockKeepingUnit.getSkuPropertiesInDb();
        if (StringUtils.isNotEmpty(skuProperties) && skuProperties.split(",").length > 0) {
            for (String pv : skuProperties.split(",")) {
                PropertyValueUtil.PV pvResult = PropertyValueUtil.parseLongToPidVid(Long.valueOf(pv));
                Property property = categoryPropertyService.getPropertyById(pvResult.pid);
                Value value = categoryPropertyService.getValueById(pvResult.vid);
                pairMap.put(property.getName(), value.getValueName());
            }
        }
        return pairMap;
    }

    @Override
    public String getSkuPropertyValueToStr(StockKeepingUnit sku) {
        Map<String, String> map = getSkuPropertyNameAndValueNamePair(sku);
        StringBuilder sbd = new StringBuilder();
        for (String value : map.values()) {
            sbd.append(value).append(" ");
        }
        if (sbd.length() > 0) sbd.delete(sbd.length() - 1, sbd.length());
        return sbd.toString();
    }

    @Override
    public void editSkuBarCodeAndSkuCode(long skuId, String barCode, String skuCode) {
        stockKeepingUnitRepository.editSkuBarCodeAndSkuCode(skuId, barCode, skuCode);
    }

    @Override
    public boolean hadSKUByBarCode(String barCode) {
        return stockKeepingUnitRepository.queryCountSKUByBarCode(barCode) > 0;
    }

    @Override
    public Page<StockKeepingUnit> searchSkuBySupplier(int customerId, SupplierQuery query) {
        return stockKeepingUnitRepository.queryStockKeepingUnitByQuery(customerId, query);
    }

    @Override
    public boolean isSkuUsable(long skuId) {
        boolean flag = false;
        //sku所属商品是否下架
        StockKeepingUnit sku = stockKeepingUnitRepository.getStockKeepingUnitById(skuId);
        if (null != sku && sku.canBuy()) {
            Product product = productRepository.getProductById(sku.getProductId());
            if (product.isOnline()) {
                flag = true;
            }
        }
        //sku是否有库存
        if (flag) {
            SkuStorage skuStorage = skuStorageService.getSkuStorage(skuId);
            if (null != skuStorage) {
                return skuStorage.getStockQuantity() > 0;
            }
            return false;
        }

        return flag;
    }

    @Override
    public int getSumStockQuantityByProductId(int productId) {
        return stockKeepingUnitRepository.queryAllStockQuantity(productId);
    }
}
