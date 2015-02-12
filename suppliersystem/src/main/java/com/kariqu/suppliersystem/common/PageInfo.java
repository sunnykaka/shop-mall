package com.kariqu.suppliersystem.common;
/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-12-5
 * Time: 下午2:37
 */
public class PageInfo {
    private int pageSize=15;
    private int pageNo=0;
    private int totalPage;
    private int totalCount;
    private String forwardType;

    public String getForwardType() {
        return forwardType;
    }

    public void setForwardType(String forwardType) {
        this.forwardType = forwardType;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getQuery() {
        return "";
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
