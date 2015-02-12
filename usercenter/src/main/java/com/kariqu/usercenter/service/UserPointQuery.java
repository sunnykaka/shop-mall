package com.kariqu.usercenter.service;

import com.kariqu.common.pagenavigator.BaseQuery;
import com.kariqu.usercenter.domain.UserPoint;

/**
 * User: Asion
 * Date: 13-3-18
 * Time: 上午10:34
 */
public class UserPointQuery extends BaseQuery {

    private int userId;

    private UserPoint.PointType type;

    /**
     * 查询某个用户的所有积分
     *
     * @param userId
     * @return
     */
    public static UserPointQuery where(int userId) {
        UserPointQuery query = new UserPointQuery();
        query.setUserId(userId);
        return query;
    }

    /**
     * 查询某个用户的所有积分
     *
     * @param userId
     * @return
     */
    public static UserPointQuery where(int userId, int size) {
        UserPointQuery query = new UserPointQuery();
        query.setUserId(userId);
        query.setPageSize(size);
        return query;
    }

    /**
     * 查询某个用户的某个类型的积分
     *
     * @param pointType
     * @return
     */
    public UserPointQuery and(UserPoint.PointType pointType) {
        this.setType(pointType);
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public UserPoint.PointType getType() {
        return type;
    }

    public void setType(UserPoint.PointType type) {
        this.type = type;
    }
}
