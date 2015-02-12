package com.kariqu.productmanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Promotion;
import com.kariqu.productcenter.domain.PromotionTopic;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.PromotionService;
import com.kariqu.productmanager.helper.ProductPidVid;
import com.kariqu.productmanager.helper.PromotionQuery;
import com.kariqu.productmanager.helper.PromotionTopicQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Baron.Zhang
 * Date: 2014/10/15
 * Time: 17:38
 */
@Controller
public class PromotionController {

    private static final Log logger = LogFactory.getLog(PromotionController.class);

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/promotion/new", method = RequestMethod.POST)
    @Permission("发布活动商品")
    public void createPromotion(Promotion promotion, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            promotionService.createPromotion(promotion);
        } catch (Exception e) {
            logger.error("活动管理的发布活动商品异常：" + e);
            new JsonResult(false, "发布活动商品出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/promotion/delete/{id}", method = RequestMethod.POST)
    @Permission("删除活动商品")
    public void deletePromotion(@PathVariable("id") int id, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            promotionService.deletePromotion(id);
        } catch (Exception e) {
            logger.error("活动管理的删除活动商品异常：" + e);
            new JsonResult(false, "删除活动商品出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/promotion/delete", method = RequestMethod.POST)
    @Permission("批量删除活动商品")
    public void deletePromotions(@RequestParam int[] ids, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            promotionService.deletePromotions(ids);
        } catch (Exception e) {
            logger.error("活动管理的批量删除活动商品异常：" + e);
            new JsonResult(false, "批量删除活动商品出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/promotion/update", method = RequestMethod.POST)
    @Permission("更新活动商品")
    public void updatePromotion(Promotion promotion, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            promotionService.updatePromotion(promotion);
        } catch (Exception e) {
            logger.error("活动管理的更新活动商品异常：" + e);
            new JsonResult(false, "更新活动商品出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/promotion/{id}", method = RequestMethod.POST)
    @Permission("获取活动商品详情")
    public void getPromotion(@PathVariable("id") int id, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Promotion promotion;
        try {
            promotion = promotionService.getPromotion(id);
        } catch (Exception e) {
            logger.error("活动管理的获取活动商品详情异常：" + e);
            new JsonResult(false, "获取活动商品详情出错").toJson(response);
            return;
        }
        new JsonResult(true).addData("obj",promotion).toJson(response);
    }

    @RequestMapping(value = "/promotion/query", method = RequestMethod.POST)
    @Permission("获取活动商品")
    public void queryPromotions(PromotionQuery promotionQuery, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Page<Promotion> page = null;
        try {
            page = promotionService.queryPromotionsByTopicId(promotionQuery.getTopicId(),promotionQuery.getStart(),promotionQuery.getLimit());
        } catch (Exception e) {
            logger.error("活动管理的获取活动商品异常：" + e);
            new JsonResult(false, "获取活动商品出错").toJson(response);
            return;
        }
        new JsonResult(true).addData("totalCount", page.getTotalCount()).addData("result", page.getResult()).toJson(response);
    }



    @RequestMapping(value = "/promotiontopic/new", method = RequestMethod.POST)
    @Permission("发布活动")
    public void createPromotionTopic(PromotionTopic promotionTopic, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            promotionService.createPromotionTopic(promotionTopic);
        } catch (Exception e) {
            logger.error("活动管理的发布活动异常：" + e);
            new JsonResult(false, "发布活动出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/promotiontopic/delete/{id}", method = RequestMethod.POST)
    @Permission("删除活动")
    public void deletePromotionTopic(@PathVariable("id") int id, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            promotionService.deletePromotionTopic(id);
        } catch (Exception e) {
            logger.error("活动管理的删除活动异常：" + e);
            new JsonResult(false, "删除活动出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/promotiontopic/delete", method = RequestMethod.POST)
    @Permission("批量删除活动")
    public void deletePromotionTopics(@RequestParam int[] ids, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            promotionService.deletePromotionTopics(ids);
        } catch (Exception e) {
            logger.error("活动管理的批量删除活动异常：" + e);
            new JsonResult(false, "删除活动出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/promotiontopic/update", method = RequestMethod.POST)
    @Permission("更新活动")
    public void updatePromotionTopic(PromotionTopic promotionTopic, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            promotionService.updatePromotionTopic(promotionTopic);
        } catch (Exception e) {
            logger.error("活动管理的更新活动异常：" + e);
            new JsonResult(false, "更新活动出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/promotiontopic/{id}", method = RequestMethod.POST)
    @Permission("获取活动详情")
    public void getPromotionTopic(@PathVariable("id") int id, HttpServletResponse response, HttpServletRequest request) throws IOException {
        PromotionTopic promotionTopic;
        try {
            promotionTopic = promotionService.getPromotionTopic(id);
        } catch (Exception e) {
            logger.error("活动管理的获取活动详情异常：" + e);
            new JsonResult(false, "获取活动详情出错").toJson(response);
            return;
        }
        new JsonResult(true).addData("obj",promotionTopic).toJson(response);
    }

    @RequestMapping(value = "/promotiontopic/query", method = RequestMethod.POST)
    @Permission("获取活动")
    public void queryPromotionTopics(PromotionTopicQuery promotionTopicQuery, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Page<PromotionTopic> page = null;
        try {
            page = promotionService.queryPromotionTopics(promotionTopicQuery.getName(), promotionTopicQuery.getTopic(),promotionTopicQuery.getStart(), promotionTopicQuery.getLimit());
        } catch (Exception e) {
            logger.error("活动管理的获取活动异常：" + e);
            new JsonResult(false, "获取活动出错").toJson(response);
            return;
        }
        new JsonResult(true).addData("totalCount", page.getTotalCount()).addData("result", page.getResult()).toJson(response);
    }


}
