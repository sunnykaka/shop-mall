package com.kariqu.tradecenter.domain;

import java.util.List;

/**
 * 用来显示进度的实体(订单和退货均可).
 *
 * @author Athens(刘杰)
 * @since 1.0.0
 */
public class Progress {

    /**
     * 点亮的状态集
     */
    private List<ProgressDetail> lightenProgress;

    /**
     * 灰色的状态集
     */
    private List<String> greyProgress;

    /**
     * 点亮的状态集
     */
    public List<ProgressDetail> getLightenProgress() {
        return lightenProgress;
    }

    /**
     * 点亮的状态集
     */
    public void setLightenProgress(List<ProgressDetail> lightenProgress) {
        this.lightenProgress = lightenProgress;
    }

    /**
     * 灰色的状态集
     */
    public List<String> getGreyProgress() {
        return greyProgress;
    }

    /**
     * 灰色的状态集
     */
    public void setGreyProgress(List<String> greyProgress) {
        this.greyProgress = greyProgress;
    }

}
