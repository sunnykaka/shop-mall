package com.kariqu.suppliersystem.orderManager.web;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.SupplierQuery;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliersystem.common.JsonResult;
import com.kariqu.suppliersystem.orderManager.vo.ProductSku;
import com.kariqu.suppliersystem.supplierManager.vo.SessionUtils;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.OrderItem;
import com.kariqu.tradecenter.service.Query;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * productStock(商品库存)界面功能
 *
 * @author:Wendy
 * @since:1.0.0 Date: 12-12-12
 * Time: 下午3:46
 */
@Controller
@RequestMapping("/product")
public class ProductController extends BaseController {


    private final Log logger = LogFactory.getLog(ProductController.class);


    /**
     * 商家库存查询
     * @param supplierQuery
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/stock")
    public void queryStockKeepingUnit(SupplierQuery supplierQuery, HttpServletRequest request,HttpServletResponse response) throws IOException {
        try {
            SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
            supplierQuery = initSupplierQuery(supplierQuery);
            Page<StockKeepingUnit> stockKeepingUnitPage = skuService.searchSkuBySupplier(supplierAccount.getCustomerId(), supplierQuery);
            List<ProductSku> productSkuList = new ArrayList<ProductSku>();
            for (StockKeepingUnit stockKeepingUnit : stockKeepingUnitPage.getResult()) {
                StockKeepingUnit sku = skuService.getStockKeepingUnit(stockKeepingUnit.getId());
                productSkuList.add(generateProductSku(sku));
            }
            new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_LIST,productSkuList).addData(JsonResult.RESULT_TYPE_TOTAL_COUNT, stockKeepingUnitPage.getTotalCount()).toJson(response);
        } catch (Exception e) {
            logger.error("获取sku库存信息失败", e);
            new JsonResult(JsonResult.FAILURE,"获取sku库存信息失败").toJson(response);
        }
    }


    /**
     *生成订单商品统计列表(页面主菜单--报表）
     *
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "/report")
    public void productReportPage(Query query, SupplierQuery supplierQuery, HttpServletResponse response, HttpServletRequest request) throws IOException {
        SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
        supplierQuery = initSupplierQuery(supplierQuery);

        //用结束时间进行查询
        if (StringUtils.isNotEmpty(query.getStartDate())) {
            query.setDateType("endDate");
        }
        //创建 时间开始查询
        if (StringUtils.isNotEmpty(supplierQuery.getStartDate()) || StringUtils.isNotEmpty(supplierQuery.getEndDate())) {
            supplierQuery.setDateType("create");
        }
        Page<StockKeepingUnit> stockKeepingUnitPage = skuService.searchSkuBySupplier(supplierAccount.getCustomerId(), supplierQuery);

        List<ProductSku> productSkuList = getProductSkusList(query, supplierAccount);
        List<ProductSku> productSkus = new ArrayList<ProductSku>();
        String totalPrice = assembSkuInfo(stockKeepingUnitPage, productSkus, productSkuList);
        new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_LIST,productSkus).addData(JsonResult.RESULT_TYPE_TOTAL_COUNT,stockKeepingUnitPage.getTotalCount()).addData("totalPrice",totalPrice).toJson(response);

    }

    /**
     * 组装Sku信息
     * @param stockKeepingUnitPage
     * @param productSkus
     * @param productSkuList
     * @return
     */
    private String assembSkuInfo(Page<StockKeepingUnit> stockKeepingUnitPage, List<ProductSku> productSkus, List<ProductSku> productSkuList) {
        String totalPrice = "0";
        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnitPage.getResult()) {
            StockKeepingUnit sku = skuService.getStockKeepingUnit(stockKeepingUnit.getId());
            ProductSku productSku = generateProductSku(sku);
            for (ProductSku newProductSku : productSkuList) {        //订单里面的sku信息
                Money money = new Money(productSku.getSkuPrice());
                productSku.setCommodityPrice(money.multiply(newProductSku.getShipmentNum()).toString());
                if (newProductSku.getSkuId() == productSku.getSkuId()) {
                    productSku.setNumber(newProductSku.getNumber());
                    productSku.setShipmentNum(newProductSku.getShipmentNum());
                    productSku.setUnitPrice(newProductSku.getUnitPrice());
                    productSku.setSalePrice(newProductSku.getSalePrice());
                    break;
                }
            }
            if (productSku.getSalePrice() != null) {
                Money money = new Money(productSku.getSalePrice());
                Money moneyPrice = new Money(totalPrice);
                totalPrice = (money.add(moneyPrice)).toString();
            }
            productSkus.add(productSku);
        }
        return totalPrice;
    }

    /**
     * 查询prodcutSkuList
     * @param query
     * @param supplierAccount
     * @return
     */
    private List<ProductSku> getProductSkusList(Query query, SupplierAccount supplierAccount) {
        List<ProductSku> productSkuList = new ArrayList<ProductSku>();
        List<Order> orderList = tradeCenterSupplierClient.searchQueryList(query, supplierAccount.getCustomerId());
        /*获取订单存在的sku以及sku的订货数量，退货数量*/
        for (Order order : orderList) {
            List<OrderItem> orderItems = tradeCenterSupplierClient.queryOrderItemWithoutBackingNumberByOrderId(order.getId());
            for (OrderItem orderItem : orderItems) {
                if (productSkuList.size() > 0 && orderItem.getShipmentNum() > 0) {
                    boolean has = false;
                    //判断sku是在集合中存在，存在改变购买数量，移除集合前面的那个值 重新add
                    for (int j = 0; j < productSkuList.size(); j++) {
                        if (productSkuList.get(j).getSkuId() == orderItem.getSkuId()) {
                            ProductSku newProductSku = new ProductSku();
                            has = true;
                            newProductSku.setSkuId(productSkuList.get(j).getSkuId());
                            //订货数量
                            newProductSku.setNumber(productSkuList.get(j).getNumber() + orderItem.getNumber());
                            //销售数量
                            newProductSku.setShipmentNum(productSkuList.get(j).getShipmentNum() + orderItem.getShipmentNum());
                            newProductSku.setUnitPrice(productSkuList.get(j).getUnitPrice());
                            Money money = new Money(orderItem.getUnitPriceByMoney());
                            Money saleMoney = new Money(productSkuList.get(j).getSalePrice());
                            newProductSku.setSalePrice((saleMoney.add(money.multiply(orderItem.getShipmentNum()))).toString());
                            productSkuList.remove(j);
                            productSkuList.add(newProductSku);
                        }
                    }
                    if (!has) {
                        ProductSku productSku = generateSku(orderItem);
                        productSkuList.add(productSku);
                    }
                } else {
                    ProductSku productSku = generateSku(orderItem);
                    productSkuList.add(productSku);
                }
            }
        }
        return productSkuList;
    }


    private ProductSku generateSku(OrderItem orderItem) {
        ProductSku productSku = new ProductSku();
        productSku.setSkuId(orderItem.getSkuId());
        //订货数量
        productSku.setNumber(orderItem.getNumber());
        //销售数量
        productSku.setShipmentNum(orderItem.getShipmentNum());
        productSku.setUnitPrice(orderItem.getUnitPriceByMoney());
        Money money = new Money(orderItem.getUnitPriceByMoney());
        productSku.setSalePrice(money.multiply(orderItem.getShipmentNum()).toString());
        return productSku;
    }
}
