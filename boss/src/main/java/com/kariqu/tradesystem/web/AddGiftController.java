package com.kariqu.tradesystem.web;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.common.JsonResult;
import com.kariqu.login.SessionUtils;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.tradecenter.client.TradeCenterBossClient;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.OrderItem;
import com.kariqu.tradecenter.domain.OrderState;
import com.kariqu.tradesystem.helper.OrderGift;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * User: amos.zhou
 * Date: 13-10-28
 * Time: 下午2:45
 */
@Controller
@RequestMapping("order")
public class AddGiftController {

    private final Log logger = LogFactory.getLog(AddGiftController.class);

    @Autowired
    private TradeCenterBossClient tradeCenterBossClient;

    @Autowired
    private SkuStorageService skuStorageService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ProductService productService;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 给赠品
     *
     * @param orderId  订单ID
     * @param skuId    赠品的SKU
     * @param number   赠品数量
     * @param session
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/gift", method = RequestMethod.POST)
    public void addGift(long orderId, Integer skuId, Integer number, HttpSession session, HttpServletResponse response) throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("赠送物品的订单号为：" + orderId + "---赠送物品的skuId为：" + skuId + "---赠送物品的数量为：" + number);
        }
        Order order = tradeCenterBossClient.queryOrderById(orderId);

        if (!order.getOrderState().canAddGift()) {
            new JsonResult(false, "只有未确定的订单才能赠送物品").toJson(response);
            return;
        }

        ProductStorage storage = skuStorageService.getConcretionStorage(skuId);
        logger.info("赠品所在的仓库ID：" + storage.getId());
        List<OrderItem> itemList = order.getOrderItemList();
        for (OrderItem item : itemList) {
            if (item.getStorageId() != storage.getId()) {
                logger.info("订单商品，所在的仓库ID：" + item.getStorageId());
                logger.info("赠送物品必与订单中的商品在同一仓库");
                new JsonResult(false, "赠送物品必与订单中的商品在同一仓库").toJson(response);
                return;
            }

            if (item.getSkuId() == skuId) {
                logger.info("赠送物品不能与订单项中的商品一样，如果需要，请直接修改订单项商品的数量");
                new JsonResult(false, "赠送物品不能与订单项中的商品一样，如果需要，请直接修改订单项商品的数量").toJson(response);
                return;
            }
        }

        Account account = SessionUtils.getLoginAccount(session);
        Integer productId = skuService.getStockKeepingUnit(skuId).getProductId();
        String productName = productService.getProductById(productId).getName();

        Object[] params = {orderId, skuId, account.getId(), number, productName, productId};
        jdbcTemplate.update("insert into t_order_gift (order_id,sku_id,date,operate_user_id,number,product_name,product_id) values (?,?,Now(),?,?,?,?)", params);
        new JsonResult(true, "赠送物品成功").toJson(response);
    }


    @RequestMapping(value = "/{orderId}/gift")
    public void getGiftList(@PathVariable("orderId") long orderId, HttpServletResponse response) throws IOException {
        String sql = " select * from t_order_gift where order_id = ? ";
        List<OrderGift> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper(OrderGift.class), orderId);
        new JsonResult(true).addData("list", result).toJson(response);
    }

    @RequestMapping(value = "/gift/delete")
    public void deleteGift(long []ids, HttpServletResponse response) throws IOException {
        String sql = "delete from t_order_gift where id= ?";
        for (long id : ids) {
            jdbcTemplate.update(sql, id);
        }
        new JsonResult(true).toJson(response);
    }
}
