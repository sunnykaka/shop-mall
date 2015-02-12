package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.PageType;
import com.kariqu.designcenter.domain.model.shop.PageStatus;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopPageType;
import com.kariqu.designcenter.repository.ShopPageRepository;
import junit.framework.Assert;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

public class ShopPageRepositoryTest extends IbatisBaseSqlTest {

    @SpringBean("shopPageRepository")
    private ShopPageRepository shopPageRepository;

    @Test
    public void testShopPageRepository() {
        int shopId = 1;
        int shopTemplateId = 2;

        ShopPage sp = new ShopPage();
        sp.setName("test shop page");
        sp.setEditPageContent("edit page content");
        sp.setProdPageContent("prod page content");

        sp.setProdConfigContent("product config content");
        sp.setEditConfigContent("edit config content");
        sp.setShopId(shopId);
        sp.setShopTemplateId(3);
        sp.setShopPageType(ShopPageType.PROTOTYPE_PAGE);
        sp.setPageType(PageType.detail);
        sp.setPageStatus(PageStatus.NORMAL);

        ShopPage sp1 = new ShopPage();
        sp1.setName("test shop page");
        sp1.setEditPageContent("edit page content");
        sp1.setProdPageContent("prod page content");

        sp1.setProdConfigContent("product config content");
        sp1.setEditConfigContent("edit config content");
        sp1.setShopId(shopId);
        sp1.setShopTemplateId(shopTemplateId);
        sp1.setShopPageType(ShopPageType.PROTOTYPE_PAGE);
        sp1.setPageType(PageType.index);
        sp1.setPageStatus(PageStatus.NORMAL);

        shopPageRepository.createShopPage(sp);
        shopPageRepository.createShopPage(sp1);

        Assert.assertEquals(shopTemplateId, shopPageRepository.queryAllShopPages().size());
        Assert.assertEquals(shopTemplateId, shopPageRepository.queryShopPagesByShopId(shopId).size());
        Assert.assertEquals(shopTemplateId, shopPageRepository.queryIndexShopPage(shopId).getShopTemplateId());
        Assert.assertEquals(shopTemplateId, shopPageRepository.queryShopPageWithShopPageType(shopId, ShopPageType.PROTOTYPE_PAGE).size());
        Assert.assertEquals(ShopPageType.PROTOTYPE_PAGE, shopPageRepository.queryShopPageWithShopPageType(shopId, ShopPageType.PROTOTYPE_PAGE).get(0).getShopPageType());
        Assert.assertEquals(ShopPageType.PROTOTYPE_PAGE, shopPageRepository.queryDetailShopPage(shopId).getShopPageType());
        Assert.assertEquals(ShopPageType.PROTOTYPE_PAGE, shopPageRepository.queryIndexShopPage(shopId).getShopPageType());
        Assert.assertEquals(PageStatus.NORMAL, shopPageRepository.queryIndexShopPage(shopId).getPageStatus());
        sp1.setPageType(PageType.channel);
        shopPageRepository.updateShopPage(sp1);
        Assert.assertEquals(ShopPageType.PROTOTYPE_PAGE, shopPageRepository.queryChannelShopPage(shopId).getShopPageType());

        sp1.setName("tesagewgt shop page");
        sp1.setEditPageContent("edit pawgwegge content");
        sp1.setProdPageContent("prod pawegwgewge content");

        sp1.setProdConfigContent("productgewgewg config content");
        sp1.setEditConfigContent("edit cowgewgewgwegewgwenfig content");
        sp1.setShopId(100);
        sp1.setShopTemplateId(200);
        sp1.setShopPageType(ShopPageType.SYSTEM_PAGE);
        sp1.setPageType(PageType.detail);
        shopPageRepository.updateShopPage(sp1);

        shopPageRepository.deleteShopPageById(sp1.getId());
        shopPageRepository.deleteShopPageById(sp.getId());

        Assert.assertEquals(0, shopPageRepository.queryAllShopPages().size());

    }

}
