package com.kariqu.suppliersystem.orderManager.web;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.SupplierQuery;
import com.kariqu.suppliercenter.domain.*;
import com.kariqu.suppliersystem.common.PageInfo;
import com.kariqu.suppliersystem.orderManager.vo.OrderPrintInfo;
import com.kariqu.suppliersystem.orderManager.vo.PlatformOrder;
import com.kariqu.suppliersystem.orderManager.vo.ProductSku;

import java.text.SimpleDateFormat;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-12-18
 * Time: 上午10:34
 */
public abstract class BaseController extends PlatformController{

    protected OrderPrintInfo generateOrderPrintInfo(long orderId, int storageId) {
        OrderPrintInfo orderPrintInfo = new OrderPrintInfo();
        //客服修改的信息
        PlatformOrder platformOrder=getPlatformOrderById(orderId);
        ProductStorage productStorage = supplierService.queryProductStorageById(storageId);//发货信息
        orderPrintInfo.setAddress(platformOrder.getProvince().replaceAll(",", "") + platformOrder.getLocation());
        orderPrintInfo.setConsignee(platformOrder.getName());
        orderPrintInfo.setTelephone(platformOrder.getMobile());
        orderPrintInfo.setWaybillNumber(platformOrder.getWaybillNumber());
        String address = "";
        String consignor = "";
        String consignorTelephone = "";
        String company = "";
        if (productStorage.getAddress() != null) {
            address = productStorage.getAddress();
        }
        if (productStorage.getConsignor() != null) {
            consignor = productStorage.getConsignor();
        }
        if (productStorage.getTelephone() != null) {
            consignorTelephone = productStorage.getTelephone();
        }
        if (productStorage.getCompany() != null) {
            company = productStorage.getCompany();
        }
        orderPrintInfo.setCompany(company);
        orderPrintInfo.setConsignorAddress(address);
        orderPrintInfo.setConsignor(consignor);
        orderPrintInfo.setConsignorTelephone(consignorTelephone);
        orderPrintInfo.setZipCode(platformOrder.getZipCode());
        return orderPrintInfo;
    }




    /**
     * 根据sku对象获取sku的详细信息
     *
     * @param stockKeepingUnit
     * @return
     */
    protected ProductSku generateProductSku(StockKeepingUnit stockKeepingUnit) {
        ProductSku sku =productSkuDetail(stockKeepingUnit);
        SkuStorage skuStorage = skuStorageService.getSkuStorage(stockKeepingUnit.getId());
        if (skuStorage != null) {
            ProductStorage productStorage = supplierService.queryProductStorageById(skuStorage.getProductStorageId());
            sku.setStorageName(productStorage.getName());
        }
        return sku;
    }


    private ProductSku productSkuDetail(StockKeepingUnit stockKeepingUnit) {
        ProductSku sku = new ProductSku();
        sku.setSkuId(stockKeepingUnit.getId());
        Product product = productService.getProductById(stockKeepingUnit.getProductId());
        ProductCategory productCategory = productCategoryService.getProductCategoryById(product.getCategoryId());
        Brand brand = supplierService.queryBrandById(product.getBrandId());
        sku.setBrandName(brand.getName());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sku.setCreateTime((format.format(stockKeepingUnit.getCreateTime())));
        sku.setCategoryName(productCategory.getName());//类目名称，暂时没有
        SkuStorage skuStorage=skuStorageService.getSkuStorage(stockKeepingUnit.getId());
        if(skuStorage!=null){
            sku.setStockQuantity(skuStorage.getStockQuantity());
        }
        Money money = new Money();
        money.setCent(stockKeepingUnit.getPrice());
        sku.setSkuPrice(money.toString());
        sku.setProductName(product.getName());
        sku.setItemNo(stockKeepingUnit.getSkuCode());//(product.getProductCode());
        sku.setBarCode(stockKeepingUnit.getBarCode());
        sku.setAttribute(skuService.getSkuPropertyToString(stockKeepingUnit));
        return sku;
    }

    protected LogisticsPrintInfo queryLogisticsPrintInfo(int supplierId, String accountName, DeliveryInfo.DeliveryType name) {
        SupplierAccount supplierAccount = supplierService.querySupplierAccountByName(accountName, supplierId); //商家账号
        LogisticsPrintInfo logisticsPrintInfo= logisticsPrintInfoService.queryLogisticsPrintInfoByNameAndCustomerId(name, supplierAccount.getCustomerId());
        return logisticsPrintInfo;
    }

    protected SupplierQuery initSupplierQuery(SupplierQuery supplierQuery) {
        if (supplierQuery.getStoreId() == null) {
            supplierQuery.setStoreId(0);
        }
        if (supplierQuery.getStart() == null) {
            supplierQuery.setStart(0);
        }
        if (supplierQuery.getLimit() == 0) {
            supplierQuery.setLimit(15);
        }
        if (supplierQuery.getProductCode() == null) {
            supplierQuery.setProductCode("");
        }
        if (supplierQuery.getProductName() == null) {
            supplierQuery.setProductName("");
        }
        if (supplierQuery.getBarCode() == null) {
            supplierQuery.setBarCode("");
        }
        return supplierQuery;
    }

    protected void createSupplierLog(SupplierLog supplierLog, int supplierId) {
        supplierLog.setSupplierId(supplierId);
        supplierLogService.createSupplierLog(supplierLog);

    }

    protected PageInfo initPageInfo(PageInfo pageInfo) {
        int pageNo = pageInfo.getPageNo();
        int totalPage = pageInfo.getTotalPage();
        if ("nextPage".equals(pageInfo.getForwardType())) {
            pageInfo.setPageNo((pageNo + 1) > totalPage ? totalPage : pageNo + 1);
        } else if ("prePage".equals(pageInfo.getForwardType())) {
            pageInfo.setPageNo((pageNo - 1) > 1 ? pageNo - 1 : 1);
        } else if ("firstPage".equals(pageInfo.getForwardType())) {
            pageInfo.setPageNo(1);
        } else if ("endPage".equals(pageInfo.getForwardType())) {
            pageInfo.setPageNo(totalPage);
        }
        return pageInfo;
    }

    protected PageInfo generatePageInfo(PageInfo pageInfo, Page orderPage) {
        int totalCount = orderPage.getTotalCount();
        pageInfo.setPageNo(orderPage.getPageNo());
        pageInfo.setPageSize(orderPage.getPageSize());
        pageInfo.setTotalPage(((float) totalCount / pageInfo.getPageSize() > totalCount / pageInfo.getPageSize()) ? totalCount / pageInfo.getPageSize() + 1 : totalCount / pageInfo.getPageSize());
        pageInfo.setTotalCount(totalCount);
        return pageInfo;
    }
}
