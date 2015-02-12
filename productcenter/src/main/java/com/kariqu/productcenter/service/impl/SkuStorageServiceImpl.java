package com.kariqu.productcenter.service.impl;

import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.repository.StockKeepingUnitRepository;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Asion
 * Date: 12-6-26
 * Time: 下午12:47
 */
public class SkuStorageServiceImpl implements SkuStorageService {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private StockKeepingUnitRepository stockKeepingUnitRepository;


    @Override
    public ProductStorage getConcretionStorage(long skuId, String location) {
        SkuStorage skuStorage = getSkuStorage(skuId);
        if(null == skuStorage){
            return null;
        }
        return supplierService.queryProductStorageById(skuStorage.getProductStorageId());
    }

    @Override
    public ProductStorage getConcretionStorage(long skuId) {
        return getConcretionStorage(skuId, null);
    }

    @Override
    public SkuStorage getSkuStorage(long skuId) {
        List<SkuStorage> skuStorages = stockKeepingUnitRepository.queryStorageBySkuId(skuId);
        if (skuStorages.size() == 0) {
            return null;
        }
        //一个sku可能有多个库位，但是这里值取出第一个，以后会根据物流信息定位仓库
        return skuStorages.get(0);
    }
}
