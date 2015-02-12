package com.kariqu.buyer.web.controller.myinfo;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.service.CouponQuery;
import com.kariqu.tradecenter.service.CouponService;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * User: kyle
 * Date: 13-1-16
 * Time: 下午7:11
 */
@Controller
@PageTitle("我的现金券")
public class MyCouponController  {

    @Autowired
    private CouponService couponService;


    @RenderHeaderFooter
    @RequestMapping(value = "/my/coupons")
    public String forwardMyCoupon(String status, String pageSize, String pageNo,HttpServletRequest request, Model model) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        int page = NumberUtils.toInt(pageNo, 1);
        if (page <= 0) page = 1;
        int size = NumberUtils.toInt(pageSize, 5);
        if (size <= 0 || size > 50) size = 5;
        //构建BuildCouponQuery 现金券的查询条件
        BuildCouponQuery buildCouponQuery = new BuildCouponQuery(status, sessionUserInfo, page, size).invoke();
        CouponQuery couponQuery = buildCouponQuery.getCouponQuery();
        Coupon.CouponUsed cu = buildCouponQuery.getCu();

        Page<Coupon> couponPage = couponService.queryCoupon(couponQuery);
        model.addAttribute("couponPage", couponPage);
        model.addAttribute("couponPageBar", PageProcessor.process(couponPage));
        model.addAttribute("currentStatus", cu);

        model.addAttribute("contentVm", "myinfo/myCoupon.vm");
        return "myinfo/myInfoLayout";
    }


    private Page<Coupon> queryCouponsByStatus() {
        return null;
    }




    private List<Coupon> filterCouponsByStatus(int userId, final String status) {
        List<Coupon> coupons = couponService.queryCouponByUserId(userId);
        Iterable<Coupon> usedIter = Iterables.filter(coupons, new Predicate<Coupon>() {
            @Override
            public boolean apply(Coupon input) {
                if (Coupon.CouponUsed.Used.name().equalsIgnoreCase(status)) {

                    return input.isUsed() ;

                } else if (Coupon.CouponUsed.Unused.name().equalsIgnoreCase(status)) {
                    return !input.isUsed() && !input.isExpire();

                } else if (Coupon.CouponUsed.Overdue.name().equalsIgnoreCase(status)) {
                    return !input.isUsed() && input.isExpire();
                }
                return !input.isUsed();
            }
        });
        return Lists.newArrayList(usedIter);
    }


    private class BuildCouponQuery {
        private String status;
        private SessionUserInfo sessionUserInfo;
        private int page;
        private int size;
        private Coupon.CouponUsed cu;
        private CouponQuery couponQuery;

        public BuildCouponQuery(String status, SessionUserInfo sessionUserInfo, int page, int size) {
            this.status = status;
            this.sessionUserInfo = sessionUserInfo;
            this.page = page;
            this.size = size;
        }

        public Coupon.CouponUsed getCu() {
            return cu;
        }

        public CouponQuery getCouponQuery() {
            return couponQuery;
        }

        public BuildCouponQuery invoke() {
            try{
                cu = Enum.valueOf(Coupon.CouponUsed.class, status);
            }   catch (Exception e){
                // 如果传入的现金券状态为空或为非法字符，则默认为unused-未使用状态
                cu = Coupon.CouponUsed.Unused;
            }
            couponQuery = CouponQuery.where(sessionUserInfo.getId(), cu);
            couponQuery.setPageNo(page);
            couponQuery.setPageSize(size);
            return this;
        }
    }
}
