package com.kariqu.productcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.SkuStockModifyException;
import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.SupplierQuery;

import java.util.List;

/**
 * User: Asion
 * Date: 11-9-20
 * Time: 上午9:53
 */
public interface StockKeepingUnitRepository {

    List<StockKeepingUnit> querySKUByProductId(int productId);

    void deleteSKUByProductId(int productId);

    boolean minusSkuStock(long skuId, long productStorageId, int number) throws SkuStockModifyException;

    boolean addSkuStock(long skuId, long productStorageId, int number);

    List<SkuStorage> queryStorageBySkuId(long skuId);

    void editPrice(long skuId, long price, long marketPrice);

    void editSkuStorage(SkuStorage skuStorage);

    void addSkuStorage(SkuStorage skuStorage);

    int queryStockQuantity(long skuId, int productStorageId);

    int queryTradeMaxNumber(long skuId, int productStorageId);

    void deleteSKUByIdList(String skuIdList);

    void deleteSkuStorageByProductId(int productId);

    void updateSkuState(int[] ids, StockKeepingUnit.SKUState skuState);

    public int queryAllStockQuantity(int productId);

    void editSkuBarCodeAndSkuCode(long skuId, String barCode, String skuCode);

    StockKeepingUnit querySKUByBarCode(String barCode);

    int queryCountSKUByBarCode(String barCode);

    /**
     * 查询指定仓库的所有 skuId
     *
     * @param productStorage 仓库
     * @return
     */
    List<Integer> querySkuIdByProductStorage(int productStorage);

    /**
     * 查询 sku 信息.
     *
     * @param customerId 商家Id
     * @param query      查询条件
     */
    Page<StockKeepingUnit> queryStockKeepingUnitByQuery(int customerId, SupplierQuery query);

    void createStockKeepingUnit(StockKeepingUnit stockKeepingUnit);

    StockKeepingUnit getStockKeepingUnitById(long id);

    void updateStockKeepingUnit(StockKeepingUnit entity);

    void deleteStockKeepingUnitById(long id);

    List<StockKeepingUnit> queryAllStockKeepingUnits();

    Page<StockKeepingUnit> querySKUByStorageId(int storageId, int pageNo, int pageSize);
}
