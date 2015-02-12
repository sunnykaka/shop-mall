package com.kariqu.buyer.web.helper;

import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.service.ProductActivityService;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.service.SupplierService;
import com.kariqu.tradecenter.domain.OrderItem;
import com.kariqu.tradecenter.domain.TradeItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 订单拆分帮助类
 * User: Asion
 * Date: 13-1-15
 * Time: 下午1:13
 */
public class OrderSplitHelper {

    private static final Log logger = LogFactory.getLog(OrderSplitHelper.class);


    @Autowired
    private SkuStorageService skuStorageService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductActivityService productActivityService;


    /**
     * 按照仓库拆分订单
     *
     * @param orderItemList
     * @return
     */
    public Map<ProductStorage, List<OrderItem>> buildSplitOrder(List<OrderItem> orderItemList) {

        Map<ProductStorage, List<OrderItem>> orderItemMap = new LinkedHashMap<ProductStorage, List<OrderItem>>();
        for (OrderItem orderItem : orderItemList) {
            ProductStorage productStorage = skuStorageService.getConcretionStorage(orderItem.getSkuId());
            if (null != productStorage) {
                orderItem.setStorageId(productStorage.getId());
                StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnit(orderItem.getSkuId());
                int productId = stockKeepingUnit.getProductId();
                Product product = productService.getProductById(productId);
                String customerName = supplierService.queryCustomerById(productStorage.getCustomerId()).getName();
                productStorage.setCustomerName(customerName);

                orderItem.setCategoryId(product.getCategoryId());
                orderItem.setCustomerId(product.getCustomerId());
                orderItem.setBrandId(product.getBrandId());
                orderItem.setProductId(product.getId());

                List<OrderItem> tradeItemViewList = orderItemMap.get(productStorage);
                if (tradeItemViewList == null) {
                    tradeItemViewList = new LinkedList<OrderItem>();
                    tradeItemViewList.add(orderItem);
                    orderItemMap.put(productStorage, tradeItemViewList);
                } else {
                    tradeItemViewList.add(orderItem);
                }
            } else {
                logger.warn("拆分订单时发现仓库被删除,skuId:" + orderItem.getSkuId());
            }
        }
        return orderItemMap;
    }


    /**
     * 构建拆分数据用于显示
     *
     * @return
     */
    public Map<ProductStorage, List<TradeItemView>> buildSplitOrderForView(List<OrderItem> orderItemList) {
        Map<ProductStorage, List<TradeItemView>> orderItemMap = new LinkedHashMap<ProductStorage, List<TradeItemView>>();
        Map<ProductStorage, List<OrderItem>> productStorageListMap = this.buildSplitOrder(orderItemList);
        for (Map.Entry<ProductStorage, List<OrderItem>> productStorageListEntry : productStorageListMap.entrySet()) {
            ProductStorage key = productStorageListEntry.getKey();
            List<OrderItem> value = productStorageListEntry.getValue();
            List<TradeItemView> tradeItemList = new ArrayList<TradeItemView>(value.size());
            for (OrderItem orderItem : value) {
                tradeItemList.add(convertToView(orderItem));
            }
            orderItemMap.put(key, tradeItemList);
        }
        return orderItemMap;
    }


    /**
     * 把购买项和显示适配
     *
     * @param item
     * @return
     */

    public TradeItemView convertToView(TradeItem item) {
        ProductStorage concretionStorage = skuStorageService.getConcretionStorage(item.getSkuId());
        StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnitWithStock(item.getSkuId(), concretionStorage.getId());
        TradeItemView tradeItemView = new TradeItemView();
        tradeItemView.setNumber(item.getNumber());
        tradeItemView.setCustomer(supplierService.queryCustomerById(concretionStorage.getCustomerId()).getName());
        tradeItemView.setSkuId(item.getSkuId());
        tradeItemView.setLimit(stockKeepingUnit.getTradeMaxNumber());
        int productId = stockKeepingUnit.getProductId();
        tradeItemView.setProductId(productId);
        Product product = productService.getProductById(productId);
        ProductPicture productPicture = productService.getMainPictureBySKuId(stockKeepingUnit.getId(), stockKeepingUnit.getProductId());
        tradeItemView.setImageUrl(productPicture == null ? "" : productPicture.getPictureUrl());
        SkuPriceDetail skuPriceDetail = productActivityService.getSkuMarketingPrice(stockKeepingUnit);
        tradeItemView.setOriginalPrice(skuPriceDetail.getOriginalPrice());
        tradeItemView.setPrice(skuPriceDetail.getSellPrice());
        tradeItemView.setTotalPrice(new Money(skuPriceDetail.getSellPrice()).multiply(item.getNumber()).toString());
        tradeItemView.setHasStock(stockKeepingUnit.getStockQuantity() > 0);
        tradeItemView.setSkuPv(skuService.getSkuPropertyToString(stockKeepingUnit));
        tradeItemView.setProductName(product.getName());
        return tradeItemView;
    }


}
