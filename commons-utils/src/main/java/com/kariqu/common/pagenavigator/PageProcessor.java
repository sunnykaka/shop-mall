package com.kariqu.common.pagenavigator;

/**
 * 分页条处理类,让分页条和分页对象分离
 * @author Asion
 *
 */

public class PageProcessor {

    private static final int THRESHOLDFORELLIPSIS = 5;

    public static <T> PageBar process(Page<T> page) {
        int totalPage = page.getTotalPages();
        int currentPage = page.getPageNo();
        PageBar cssPageBar = new PageBar();
        cssPageBar.setLinkNums(linkNums(totalPage, currentPage));
        return cssPageBar;
    }

    private static int[] linkNums(int totalPage, int currentPage) {
        if (totalPage < currentPage)
            return new int[0];

        int[] pre = new int[0];
        int[] next = new int[0];
        if (totalPage > 0) {
            if (currentPage <= THRESHOLDFORELLIPSIS) {
                pre = new int[currentPage];
                for (int i = 0; i < pre.length; i++) {
                    pre[i] = i + 1;
                }
            } else if (currentPage == totalPage) {
                pre = new int[THRESHOLDFORELLIPSIS + 1];
                pre[0] = 1;
                pre[1] = 2;
                pre[2] = -1;
                pre[3] = currentPage - 2;
                pre[4] = currentPage - 1;
                pre[5] = currentPage;
            } else {
                pre = new int[THRESHOLDFORELLIPSIS];
                pre[0] = 1;
                pre[1] = 2;
                pre[2] = -1;
                pre[3] = currentPage - 1;
                pre[4] = currentPage;
            }
            if ((totalPage - currentPage) <= (THRESHOLDFORELLIPSIS - 1)) {
                next = new int[totalPage - currentPage];
                for (int i = 0; i < next.length; i++) {
                    next[i] = currentPage + i + 1;
                }
            } else if (currentPage == 1) {
                next = new int[THRESHOLDFORELLIPSIS];
                next[0] = 2;
                next[1] = 3;
                next[2] = -1;
                next[3] = totalPage - 1;
                next[4] = totalPage;
            } else {
                next = new int[THRESHOLDFORELLIPSIS - 1];
                next[0] = currentPage + 1;
                next[1] = -1;
                next[2] = totalPage - 1;
                next[3] = totalPage;
            }
        }

        int[] linkNums = new int[pre.length + next.length];

        int index = 0;
        for (int num : pre) {
            linkNums[index++] = num;
        }
        for (int num : next) {
            linkNums[index++] = num;
        }

        return linkNums;
    }

}
