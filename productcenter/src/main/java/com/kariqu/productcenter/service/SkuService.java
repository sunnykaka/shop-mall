package com.kariqu.productcenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.domain.StockKeepingUnit;

import java.util.List;
import java.util.Map;

/**
 * SKU服务
 * User: Asion
 * Date: 12-6-1
 * Time: 下午2:03
 */
public interface SkuService {

    /**
     * 开始更新库存，这里必须保证数据一致性
     * 不能出现不一致情况，直接用数据库的更新机制来更新库存，成功返回true，失败返回false
     *
     * @param skuId
     * @param number
     * @return
     */
    boolean minusSkuStock(long skuId, long productStorageId, int number);


    /**
     * 向一个sku增加库存
     *
     * @param skuId
     * @param number
     * @return
     */
    boolean addSkuStock(long skuId, long productStorageId, int number);

    /**
     * 保存SKU对象
     *
     * @param stockKeepingUnit
     * @return
     */
    long createStockKeepingUnit(StockKeepingUnit stockKeepingUnit);

    /**
     * 根据SKU ID查询 SKU，返回的SKU没有库存信息
     *
     * @param skuId
     * @return
     */
    StockKeepingUnit getStockKeepingUnit(long skuId);

    /**
     * 返回有库存信息的SKU对象
     *
     * @param skuId
     * @param productStorageId
     * @return
     */
    StockKeepingUnit getStockKeepingUnitWithStock(long skuId, int productStorageId);


    /**
     * 查询一个商品的所有SKU
     *
     * @param productId
     * @return
     */
    List<StockKeepingUnit> querySKUByProductId(int productId);

    /**
     * 查询一个仓库的SKU
     * @param storageId
     * @return
     */
    Page<StockKeepingUnit> querySKUByStorageId(int storageId, int pageNo, int pageSize);


    /**
     * 查询有库存信息的sku
     *
     * @param productId
     * @return
     */
    List<StockKeepingUnit> queryProductSkuWithStock(int productId);


    /**
     * 查询有效 sku用来销售
     *
     * @param productId
     * @return
     */
    List<StockKeepingUnit> queryValidSkuForSell(int productId);


    /**
     * 更新SKU
     *
     * @param stockKeepingUnit
     */
    void updateStockKeepingUnit(StockKeepingUnit stockKeepingUnit);

    /**
     * 编辑SKU价格
     *
     * @param skuId
     * @param price
     */
    void editSkuPrice(long skuId, long price, long marketPrice);

    /**
     * 编辑SKU对应库位号、库存数量
     *
     * @param skuStorage
     */
    void editSkuStorage(SkuStorage skuStorage);

    /**
     * 根据商品ID删除SKU
     *
     * @param productId
     */
    void deleteSKUByProductId(int productId);


    /**
     * 判断商品是否有多SKU,如果商品没有销售属性或者只是单值都会返回false
     * 因为如果没有销售属性和只是单值，在详情页是不会显示的，只有多值才具备商品不确定性，需要筛选价格和库存
     */

    boolean hasMultiSku(int productId);

    void createSkuStorage(SkuStorage skuStorage);

    void deleteSku(long id);

    void deleteSkuByIdList(String id);

    void updateSkuState(int[] ids, StockKeepingUnit.SKUState skuState);

    /**
     * 得到SKU属性说明：颜色：红色 尺寸：32寸
     *
     * @param stockKeepingUnit
     * @return
     */
    String getSkuPropertyToString(StockKeepingUnit stockKeepingUnit);

    /**
     * 得到 sku 属性说明: 红色 32寸
     * @param sku
     * @return
     */
    String getSkuPropertyValueToStr(StockKeepingUnit sku);

    /**
     * 编辑sku 条形码和编号
     *
     * @param skuId
     * @param barCode
     */
    void editSkuBarCodeAndSkuCode(long skuId, String barCode, String skuCode);

    boolean hadSKUByBarCode(String barCode);

    public Page<StockKeepingUnit> searchSkuBySupplier(int customerId, SupplierQuery query);

    /**
     * 判断sku是否失效
     *
     * @param skuId
     * @return
     */
    boolean isSkuUsable(long skuId);

    /**
     * 得到某个商品的库存总和
     *
     * @param productId
     * @return
     */
    int getSumStockQuantityByProductId(int productId);


}
