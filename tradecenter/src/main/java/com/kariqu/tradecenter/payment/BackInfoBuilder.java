package com.kariqu.tradecenter.payment;

import com.kariqu.tradecenter.domain.payment.TradeInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * User: amos.zhou
 * Date: 13-10-24
 * Time: 上午9:42
 */
public interface BackInfoBuilder {


    public abstract TradeInfo buildFromRequest(HttpServletRequest request);

    public Map<String, String> buildParam(HttpServletRequest request);
}
