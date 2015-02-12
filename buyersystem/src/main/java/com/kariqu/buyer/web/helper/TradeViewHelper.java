package com.kariqu.buyer.web.helper;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.ProductPicture;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.*;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.service.SupplierService;
import com.kariqu.tradecenter.domain.CartItem;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.TradeItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 交易过程表现层的视图适配帮助类
 * User: Asion
 * Date: 12-7-17
 * Time: 下午3:13
 */

public class TradeViewHelper {

    private static final Log logger = LogFactory.getLog(TradeViewHelper.class);

    @Autowired
    private SkuStorageService skuStorageService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductActivityService productActivityService;


    /**
     * 检查对某个sku的购买量，和库存进行对比
     * 返回错误信息列表
     *
     * @param cartItemList
     */
    public List<String> checkTradeItem(List<? extends TradeItem> cartItemList) {
        List<String> errorMsg = new LinkedList<String>();
        for (TradeItem cartItem : cartItemList) {
            ProductStorage concretionStorage = skuStorageService.getConcretionStorage(cartItem.getSkuId());
            if (null != concretionStorage) {
                StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnitWithStock(cartItem.getSkuId(), concretionStorage.getId());
                if (stockKeepingUnit.getStockQuantity() < cartItem.getNumber()) {
                    errorMsg.add(productService.getProductById(stockKeepingUnit.getProductId()).getName() + "库存只有" + stockKeepingUnit.getStockQuantity() + "个");
                }
            }
        }
        return errorMsg;
    }


    /**
     * 适配成json
     *
     * @param cartItemList
     * @return
     */
    public List<JsonSku> adaptJsonSku(List<CartItem> cartItemList) {
        List<JsonSku> jsonSkuList = new ArrayList<JsonSku>(cartItemList.size());
        for (CartItem cartItem : cartItemList) {
            JsonSku jsonSku = new JsonSku();
            jsonSku.setSkuId(cartItem.getSkuId());
            jsonSku.setNumber(cartItem.getNumber());
            StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnit(cartItem.getSkuId());
            if (null != stockKeepingUnit) {
                Product product = productService.getProductById(stockKeepingUnit.getProductId());
                if (null != product && product.isOnline()) {
                    jsonSku.setProductId(product.getId());
                    ProductPicture productPicture = productService.getMainPictureBySKuId(stockKeepingUnit.getId(), stockKeepingUnit.getProductId());
                    if (null != productPicture) {
                        jsonSku.setImgUrl(ProductPictureResolver.getMinSizeImgUrl(productPicture.getPictureUrl()));
                        if (null != product.getName()) {
                            jsonSku.setName(product.getName().replaceAll("\"", "").replaceAll("'", ""));
                        }
                        jsonSku.setPrice(productActivityService.getSkuMarketingPrice(stockKeepingUnit).getSellPrice());
                        jsonSkuList.add(jsonSku);
                    }
                }
            } else {
                logger.warn("发现被删除了的SKU,SKU ID是" + cartItem.getSkuId());
            }
        }
        return jsonSkuList;
    }


    /**
     * 适配订单列表的显示
     *
     * @param orderPage
     * @return
     */
    public Page<OrderView> OrderView(Page<Order> orderPage) {
        Page<OrderView> orderViewPage = new Page<OrderView>();
        List<OrderView> orderViewList = new ArrayList<OrderView>();
        for (Order order : orderPage.getResult()) {
            OrderView orderView = new OrderView();
            orderView.setOrder(order);
            /**
             * 加入商家名称、发货仓库
             */
            Supplier customer = supplierService.queryCustomerById(order.getCustomerId());
            orderView.setCustomer(customer != null ? customer.getName() : "已删除");
            ProductStorage productStorage = supplierService.queryProductStorageById(order.getStorageId());
            orderView.setStoreName(productStorage != null ? productStorage.getName() : "已删除");

            orderViewList.add(orderView);
        }
        orderViewPage.setResult(orderViewList);
        orderViewPage.setPageSize(orderPage.getPageSize());
        orderViewPage.setPageNo(orderPage.getPageNo());
        orderViewPage.setTotalCount(orderPage.getTotalCount());
        return orderViewPage;
    }


}
