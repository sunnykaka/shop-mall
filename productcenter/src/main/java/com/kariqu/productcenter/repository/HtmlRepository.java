package com.kariqu.productcenter.repository;

import com.kariqu.productcenter.domain.Html;

import java.util.List;

/**
 * User: Asion
 * Date: 11-9-6
 * Time: 下午2:02
 */
public interface HtmlRepository{

    void deleteHtmlByProductIdAndName(int productId, String name);

    List<Html> queryAllHtml();

    void deleteHtmlByProductId(int productId);

    void updateHtml(Html value);

    Html getByProductId(int productId);

    List<Html> queryByProductId(int productId);

    void createHtml(Html value);
}
