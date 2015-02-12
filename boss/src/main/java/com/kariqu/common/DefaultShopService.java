package com.kariqu.common;

import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.service.ShopTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 由于我们目前只有一个官方店铺，为了不用每次在客户端和后台传递店铺ID和店主ID
 * 使用一个缺省的官方店铺服务
 * <p/>
 * 这个服务是boss后台，所以在发现数据库中没有官方账户和店铺的时候要首先初始化出来
 * User: Asion
 * Date: 12-5-10
 * Time: 上午9:55
 */
@Component
public class DefaultShopService {

    @Autowired
    private ShopTemplateService shopTemplateService;

    /**
     * 得到易居尚店铺模板
     *
     * @return
     */
    public ShopTemplate getDefaultShopTemplate() {
        return shopTemplateService.getShopTemplateByShopId(RenderConstants.shopId);
    }

}
