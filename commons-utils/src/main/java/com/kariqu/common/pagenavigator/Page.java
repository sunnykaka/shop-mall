package com.kariqu.common.pagenavigator;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象，表示一页数据和上下文信息
 *
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-29 上午12:42:41
 */
public class Page<T> extends BaseQuery {

    /**
     * 对象总数量
     */
    private int totalCount;

    /**
     * 每页的数据列表
     */
    private List<T> result = new ArrayList<T>();


    /**
     * 根据直接提供的记录开始位置创建分页对象
     *
     * @param start
     * @param limit
     * @param <T>
     * @return
     */
    public static <T> Page<T> createFromStart(int start, int limit) {
        return new Page<T>(start / limit + 1, limit);
    }


    public Page(int pageNo, int pageSize) {
        this.setPageNo(pageNo);
        this.setPageSize(pageSize);
    }

    public Page(int pageNo) {
        this.setPageNo(pageNo);
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public Page() {
        this.pageNo = DEFAULT_PAGE_NO;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    // 总页数，这个是根据totalcount和pageSize计算的
    public int getTotalPages() {
        if (totalCount == 0)
            return 0;

        int count = totalCount / pageSize;
        if (totalCount % pageSize > 0) {
            count++;
        }
        return count;
    }

    /**
     * 是否还有下一页.
     */
    public boolean isHasNext() {
        return (pageNo + 1 <= getTotalPages());
    }

    /**
     * 返回下页的页号,序号从1开始.
     */
    public int getNextPage() {
        if (isHasNext())
            return pageNo + 1;
        else
            return pageNo;
    }

    /**
     * 是否还有上一页.
     */
    public boolean isHasPre() {
        return (pageNo - 1 >= 1);
    }

    /**
     * 返回上页的页号,序号从1开始.
     */
    public int getPrePage() {
        if (isHasPre())
            return pageNo - 1;
        else
            return pageNo;
    }



    /**
     * @return the totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the result
     */
    public List<T> getResult() {
        if (result == null)
            return new ArrayList<T>();
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(List<T> result) {
        this.result = result;
    }

}
