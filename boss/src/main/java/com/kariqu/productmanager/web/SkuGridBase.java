package com.kariqu.productmanager.web;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.productmanager.helper.StockPriceRecord;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.service.SupplierService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Asion
 * Date: 13-7-29
 * Time: 上午10:36
 */
public class SkuGridBase {

    private final Log logger = LogFactory.getLog(SkuGridBase.class);

    @Autowired
    protected SkuStorageService skuStorageService;

    @Autowired
    protected SupplierService supplierService;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected SkuService skuService;


    protected void buildStockPriceRecord(StockKeepingUnit stockKeepingUnit, StockPriceRecord stockPriceRecord) {
        Product product = productService.getProductById(stockKeepingUnit.getProductId());
        String skuProperties = stockKeepingUnit.getSkuPropertiesInDb();
        if (skuProperties != null) {
            stockPriceRecord.setSku(skuService.getSkuPropertyToString(stockKeepingUnit));
        }
        stockPriceRecord.setProductName(product.getName());
        stockPriceRecord.setSkuId(stockKeepingUnit.getId());
        stockPriceRecord.setPrice(Money.getMoneyString(stockKeepingUnit.getPrice()));
        stockPriceRecord.setMarketPrice(Money.getMoneyString(stockKeepingUnit.getMarketPrice()));
        stockPriceRecord.setValidStatus("NORMAL".equalsIgnoreCase(stockKeepingUnit.getSkuState().name()) ? "有效" : "无效");
        stockPriceRecord.setBarCode(stockKeepingUnit.getBarCode());
        stockPriceRecord.setSkuCode(stockKeepingUnit.getSkuCode());
        SkuStorage skuStorage = skuStorageService.getSkuStorage(stockKeepingUnit.getId());
        if (skuStorage != null) {
            int productStorageId = skuStorage.getProductStorageId();
            stockPriceRecord.setStoreId(productStorageId);
            stockPriceRecord.setTradeMaxNumber(skuStorage.getTradeMaxNumber());
            ProductStorage productStorage = supplierService.queryProductStorageById(productStorageId);
            if (productStorage != null) {
                stockPriceRecord.setStockQuantity(skuStorage.getStockQuantity());
                Supplier customer = supplierService.queryCustomerById(productStorage.getCustomerId());
                stockPriceRecord.setSkuLocation(customer.getName() + "的" + productStorage.getName());
            } else {
                logger.warn("发现一个仓库ID被删除了,ID是:" + productStorageId);
                stockPriceRecord.setStockQuantity(0);
                stockPriceRecord.setSkuLocation("所在仓库被删除");
            }

        }
    }

}
