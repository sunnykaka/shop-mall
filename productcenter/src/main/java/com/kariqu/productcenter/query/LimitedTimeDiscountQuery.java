package com.kariqu.productcenter.query;

import com.kariqu.common.pagenavigator.BaseQuery;

/**
 * @author Athens(刘杰)
 * @Time 2013-04-03 11:47
 * @since 1.0.0
 */
public class LimitedTimeDiscountQuery extends BaseQuery {

    private int productId;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public LimitedTimeDiscountQuery() {}

    public LimitedTimeDiscountQuery(int pageNo, int pageSize) {
        super.pageNo = pageNo;
        super.pageSize = pageSize;
    }

}
