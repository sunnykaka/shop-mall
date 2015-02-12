package com.kariqu.suppliersystem.orderManager.web;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliersystem.orderManager.vo.PlatformOrder;
import com.kariqu.suppliersystem.orderManager.vo.PlatformOrderItem;
import com.kariqu.suppliersystem.supplierManager.vo.SessionUtils;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.service.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * orderDetail（订单明细）页面功能
 *
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-29
 * Time: 下午4:23
 */
@Controller
public class OrderDetailsController extends BaseController {


    private final Log logger = LogFactory.getLog(OrderDetailsController.class);

    /**
     * 跳转订单明细页面，生成订单明细列表
     *
     * @param model
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "supplier/orderDetail/OrderDetailsPage")
    public String OrderDetailsPage(Query query, Model model, HttpServletResponse response, HttpServletRequest request) {
        try {
            SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
            List productStorages = supplierService.queryProductStorageByCustomerId(supplierAccount.getCustomerId());
            List<PlatformOrder> platformOrderList= getPlatformOrderByQuery(query);
            List<PlatformOrder> platformOrders=new ArrayList<PlatformOrder>();
            for(PlatformOrder platformOrder:platformOrderList){
                List<PlatformOrderItem> platformOrderItems=getPlatformOrderItemByOrderId(platformOrder.getId());
                platformOrder.setPlatformOrderItemList(platformOrderItems);
                platformOrders.add(platformOrder);
            }
            model.addAttribute("orders",platformOrders);
            model.addAttribute("productStorages", productStorages);
            model.addAttribute("query", query);
            model.addAttribute("totalCount",tradeCenterSupplierClient.searchQuery(query,query.getCustomerId(), new Page<Order>(0, query.getLimit())).getTotalCount());
            return "orderDetails";

        } catch (Exception e) {
            model.addAttribute("msg", "获取订单列表详细出错");
            logger.error("获取订单列表详细出错", e);
            return "error";
        }

    }

}

