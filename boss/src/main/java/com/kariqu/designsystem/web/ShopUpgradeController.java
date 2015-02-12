package com.kariqu.designsystem.web;

import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.service.ShopPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 店铺升级控制器：
 * 应用一个新模板
 * 升级到一个新的模板版本
 * User: Asion
 * Date: 11-8-9
 * Time: 下午1:28
 */
@Controller
public class ShopUpgradeController {

    @Autowired
    private ShopPageService shopPageService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;


    /**
     * 升级店铺模板,从管理的原始模板拷贝最新的css,js,style到编辑模式字段
     *
     * @param shopId
     * @return
     */
    @RequestMapping(value = "/design/shop/{shopId}/upgrade/shopTemplate", method = RequestMethod.GET)
    public String upgradeShopTemplate(@PathVariable("shopId") int shopId) {
        shopPageService.upgradeShopTemplate(shopId,false);
        return "redirect:/shop/" + shopId;
    }

    /**
     * 页面结构变化的升级
     * @param shopId
     * @return
     */
    @RequestMapping(value = "/design/shop/{shopId}/upgrade/shopTemplate/modifyConfig", method = RequestMethod.GET)
    public String upgradeShopTemplateByModifyConfig(@PathVariable("shopId") int shopId) {
        shopPageService.upgradeShopTemplate(shopId,true);
        return "redirect:/shop/" + shopId;
    }

    /**
     * 升级店铺页面，只能升级html内容，不能升级使得页面结构有任何改动的操作
     * @param pageId
     * @return
     */
    @RequestMapping(value = "/design/page/{pageId}/upgrade", method = RequestMethod.GET)
    public String upgradeShopPage(@PathVariable("pageId") long pageId) {
        shopPageService.upgradeShopPage(pageId,false);
        return "redirect:/page/" + pageId;
    }

    @RequestMapping(value = "/design/page/{pageId}/upgrade/modifyConfig", method = RequestMethod.GET)
    public String upgradeShopPageByModifyConfig(@PathVariable("pageId") long pageId) {
        shopPageService.upgradeShopPage(pageId,true);
        return "redirect:/page/" + pageId;
    }

    /**
     * 应用新模板
     *
     * @param shopId
     * @param templateVersionId
     * @return
     */

    //todo 需要改为POST提交，这里只是为了测试方便
    @RequestMapping(value = "/design/shop/{shopId}/applyNew/{templateVersionId}", method = RequestMethod.GET)
    public String applyNewVersion(@PathVariable("shopId") int shopId, @PathVariable("templateVersionId") int templateVersionId) {
        shopPageService.applyTemplateVersion(shopId, templateVersionId);
        return "redirect:" + urlBrokerFactory.getUrl("BuyHome").toString();
    }


    /**
     * 升级店铺到新版本
     *
     * @param shopId
     * @param templateVersionId
     * @return
     */
    //todo 需要改为POST提交，这里只是为了测试方便
    @RequestMapping(value = "/design/shop/{shopId}/upgrade/{templateVersionId}", method = RequestMethod.GET)
    public String upgradeNewVersion(@PathVariable("shopId") int shopId, @PathVariable("templateVersionId") int templateVersionId) {
        shopPageService.upgradeTemplateVersion(shopId, templateVersionId);
        return "redirect:" + urlBrokerFactory.getUrl("BuyHome").toString();
    }
}
