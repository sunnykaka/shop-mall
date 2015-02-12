package com.kariqu.omcenter.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.om.domain.Seo;
import com.kariqu.om.domain.SeoType;
import com.kariqu.om.service.SeoService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SEO推广优化信息设置
 * User: Alec
 * Date: 13-10-9
 * Time: 下午5:46
 */
@Controller
@RequestMapping("/seo")
public class SeoController {
    private final Log logger = LogFactory.getLog(SeoController.class);
    @Autowired
    private SeoService seoService;

    @RequestMapping(value = "/query")
    public void querySeo(String seoObjectId, SeoType seoType, HttpServletResponse response) throws IOException {
        Seo seo = seoService.querySeoByObjIdAndType(seoObjectId, seoType);
        if (null == seo) {
            seo = new Seo();
        }
        new JsonResult(true).addData("id", seo.getId()).addData("seoObjectId", seo.getSeoObjectId()).addData("seoType", seo.getSeoType()).addData("title", seo.getTitle()).addData("description", seo.getDescription()).addData("keywords", seo.getKeywords()).toJson(response);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Permission("修改SEO设置信息")
    public void updateSeo(Seo seo, HttpServletResponse response) throws IOException {
        try {
            if (seo.getId() == 0) {
                seoService.insertSeo(seo);
            } else {
                seoService.updateSeo(seo);
            }
        } catch (Exception e) {
            logger.error("修改SEO设置信息失败:" + seo.toString(), e);
            new JsonResult(false, "编辑失败").toJson(response);
            return;
        }

        new JsonResult(true).toJson(response);
    }
}
