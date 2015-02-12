package com.kariqu.accountcenter.domain;

/**
 *
 * User: Alec
 * Date: 12-9-5
 * Time: 下午5:06
 */
public class AccountQuery {
    private String userName;
    private int positionId;
    private int departmentId;
    private int pageNo = 1;
    private int start;
    private int limit;
    private String leaveOffice;
    private String deleteData;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLeaveOffice() {
        return leaveOffice;
    }

    public void setLeaveOffice(String leaveOffice) {
        this.leaveOffice = leaveOffice;
    }

    public String getDeleteData() {
        return deleteData;
    }

    public void setDeleteData(String deleteData) {
        this.deleteData = deleteData;
    }
}
