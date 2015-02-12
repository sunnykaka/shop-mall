package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.repository.ProductRepository;
import com.kariqu.productcenter.repository.StockKeepingUnitRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-9-20
 * Time: 上午9:59
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class StockKeepingUnitRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("stockKeepingUnitRepository")
    private StockKeepingUnitRepository stockKeepingUnitRepository;

    @SpringBean("productRepository")
    private ProductRepository productRepository;


    @Test
    public void testStockKeepingUnitRepository() throws InterruptedException {
        StockKeepingUnit stockKeepingUnit = new StockKeepingUnit();
        stockKeepingUnit.setProductId(1);
        stockKeepingUnit.setPrice(1);
        stockKeepingUnit.setSkuPropertiesInDb("12884901892,12884901892");

        StockKeepingUnit stockKeepingUnit2 = new StockKeepingUnit();
        stockKeepingUnit2.setProductId(1);
        stockKeepingUnit2.setPrice(1);
        stockKeepingUnit2.setSkuPropertiesInDb("12884901892,12884901892");

        stockKeepingUnitRepository.createStockKeepingUnit(stockKeepingUnit);

        stockKeepingUnitRepository.editSkuBarCodeAndSkuCode(stockKeepingUnit.getId(), "123", "123");
        assertEquals("123", stockKeepingUnitRepository.querySKUByBarCode("123").getBarCode());


        stockKeepingUnitRepository.createStockKeepingUnit(stockKeepingUnit2);

        Thread.sleep(1000);
        stockKeepingUnit.setPrice(222);
        stockKeepingUnit.setSkuState(StockKeepingUnit.SKUState.REMOVED);


        assertEquals(2, stockKeepingUnitRepository.querySKUByProductId(1).size());

        assertEquals(2, stockKeepingUnitRepository.queryAllStockKeepingUnits().size());
        stockKeepingUnitRepository.createStockKeepingUnit(stockKeepingUnit);

        assertEquals(3, stockKeepingUnitRepository.queryAllStockKeepingUnits().size());

        SkuStorage skuStorage = new SkuStorage();
        skuStorage.setSkuId(1);
        skuStorage.setProductStorageId(1);
        skuStorage.setStockQuantity(1);
        stockKeepingUnitRepository.addSkuStorage(skuStorage);
        SkuStorage skuStorage2 = new SkuStorage();
        skuStorage2.setSkuId(2);
        skuStorage2.setProductStorageId(1);
        skuStorage2.setStockQuantity(1);
        stockKeepingUnitRepository.addSkuStorage(skuStorage2);

        /*库存数量断言*/
        assertEquals(true, stockKeepingUnitRepository.addSkuStock(1, 1, 1));
        assertEquals(true, stockKeepingUnitRepository.minusSkuStock(1, 1, 1));

        //测试skuId为1的有一个库位
        assertEquals(1, stockKeepingUnitRepository.queryStorageBySkuId(1).size());
        //测试编辑sku 库位、数量
        SkuStorage skuStorage3 = new SkuStorage();
        skuStorage3.setSkuId(1);
        skuStorage3.setProductStorageId(1);
        skuStorage3.setStockQuantity(10);
        stockKeepingUnitRepository.editSkuStorage(skuStorage3);

        assertEquals(2, stockKeepingUnitRepository.querySKUByStorageId(1, 1, 10).getTotalCount());

        assertEquals("123", stockKeepingUnitRepository.querySKUByStorageId(1, 1, 10).getResult().get(0).getSkuCode());


        //测试sku库存数量
        int stockQuantity = stockKeepingUnitRepository.queryStockQuantity(1, 1);
        assertEquals(10, stockQuantity);

        StockKeepingUnit sku = stockKeepingUnitRepository.getStockKeepingUnitById(1L);
        sku.setStockQuantity(stockQuantity);
        assertEquals(1, sku.getProductId());
        assertEquals(10, sku.getStockQuantity());
        int[] ids = {1, 2, 3};

        stockKeepingUnitRepository.updateSkuState(ids, StockKeepingUnit.SKUState.REMOVED);
        stockKeepingUnitRepository.deleteSKUByIdList("1,2,3,");

        stockKeepingUnitRepository.deleteStockKeepingUnitById(1l);
        stockKeepingUnitRepository.deleteSKUByProductId(1);
    }


}
