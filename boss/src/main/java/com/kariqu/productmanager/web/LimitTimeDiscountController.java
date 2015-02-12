package com.kariqu.productmanager.web;

import com.kariqu.common.DateUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.LimitedTimeDiscount;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.query.LimitedTimeDiscountQuery;
import com.kariqu.productcenter.service.LimitedTimeDiscountService;
import com.kariqu.productcenter.service.ProductActivityException;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productmanager.helper.LimitedTimeDiscountVo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Asion
 * Date: 13-4-2
 * Time: 下午2:08
 */
@Controller
public class LimitTimeDiscountController {

    private final Log logger = LogFactory.getLog(LimitTimeDiscountController.class);


    @Autowired
    private LimitedTimeDiscountService limitedTimeDiscountService;

    @Autowired
    private ProductService productService;


    @RequestMapping("product/limitTime/discount")
    public void productList(HttpServletResponse response, String productId, int start, int limit) throws IOException {
        List<LimitedTimeDiscountVo> list = new ArrayList<LimitedTimeDiscountVo>();
        LimitedTimeDiscountQuery query = new LimitedTimeDiscountQuery(start / limit + 1, limit);
        query.setProductId(NumberUtils.toInt(StringUtils.isBlank(productId) ? "" : productId.trim()));
        Page<LimitedTimeDiscount> limitedTimeDiscountPage = limitedTimeDiscountService.queryLimitedTimeDiscount(query);
        for (LimitedTimeDiscount limitedTimeDiscount : limitedTimeDiscountPage.getResult()) {
            LimitedTimeDiscountVo vo = new LimitedTimeDiscountVo();
            vo.setId(limitedTimeDiscount.getId());
            vo.setProductId(limitedTimeDiscount.getProductId());
            vo.setProductName(productService.getProductById(limitedTimeDiscount.getProductId()).getName());
            vo.setStartDate(DateUtils.formatDate(limitedTimeDiscount.getBeginDate(), DateUtils.DateFormatType.DATE_FORMAT_STR));
            vo.setEndDate(DateUtils.formatDate(limitedTimeDiscount.getEndDate(), DateUtils.DateFormatType.DATE_FORMAT_STR));
            if (limitedTimeDiscount.getDiscountType() == LimitedTimeDiscount.DiscountType.Ratio) {
                vo.setDiscountType("比例");
                NumberFormat nf = NumberFormat.getPercentInstance();
                vo.setDiscount(nf.format(new Double(limitedTimeDiscount.getDiscount()) / new Double(100)));
            } else {
                vo.setDiscountType("金额");
                vo.setDiscount(Money.getMoneyString(limitedTimeDiscount.getDiscount()) + "元");
            }
            list.add(vo);
        }
        new JsonResult(true).addData("totalCount", limitedTimeDiscountPage.getTotalCount()).addData("result", list).toJson(response);

    }

    @RequestMapping(value = "/product/limitTime/discount/add", method = RequestMethod.POST)
    @Permission("添加折扣设置")
    public void addProductLimitDiscount(int productId, LimitedTimeDiscount.DiscountType discountType, String discount,
                                        String start, String end, HttpServletResponse response) throws IOException {
        LimitedTimeDiscount model = new LimitedTimeDiscount();
        model.setProductId(productId);
        Date startDate = DateUtils.parseDate(start, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
        Date endDate = DateUtils.parseDate(end, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
        model.setBeginDate(startDate);
        model.setEndDate(endDate);
        model.setDiscountType(discountType);
        if (discountType == LimitedTimeDiscount.DiscountType.Ratio) {
            model.setDiscount(Integer.parseInt(discount));
        } else {
            model.setDiscount(Money.YuanToCent(discount));
        }
        try {
            limitedTimeDiscountService.createLimitedTimeDiscount(model);
        } catch (ProductActivityException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
            return;
        } catch (Exception e) {
            logger.error(e);
            new JsonResult(false, e.getMessage()).toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);

    }

    @RequestMapping(value = "/product/limitTime/discount/delete", method = RequestMethod.POST)
    @Permission("删除折扣设置")
    public void deleteProductLimitDiscount(long[] ids, HttpServletResponse response) throws IOException {
        for (long id : ids) {
            limitedTimeDiscountService.deleteLimitedTimeDiscount(id);
        }
        new JsonResult(true).toJson(response);
    }

}
