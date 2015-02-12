package com.kariqu.common.pagenavigator;

/**
 * User: Asion
 * Date: 13-3-7
 * Time: 下午6:04
 */
public abstract class BaseQuery {

    static int DEFAULT_PAGE_SIZE = 15;

    static int DEFAULT_PAGE_NO = 1;

    /**
     * 第几页
     */
    protected int pageNo = DEFAULT_PAGE_NO;

    /**
     * 每页显示对象个数
     */
    protected int pageSize = DEFAULT_PAGE_SIZE;


    // 每页的第一条记录在结果集中的位置
    public int getPageFirst() {
        return ((pageNo - 1) * pageSize);
    }

    //和上面方法一样
    public int getStart() {
        return getPageFirst();
    }

    //和getPageSize一样
    public int getLimit() {
        return pageSize;
    }


    /**
     * @return the pageNo
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * @param pageNo the pageNo to set
     */
    public void setPageNo(int pageNo) {
        if (pageNo > 0) {
            this.pageNo = pageNo;
        }
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        }
    }
}
