package com.kariqu.accountcenter.domain;


/**
 * 公司的职位
 * User: Asion
 * Date: 12-3-12
 * Time: 上午10:50
 */
public class Position {
    
    private int id;

    /**
     * 职位级别
     */
    private Level level;

    /**
     * 职位名称
     */
    private String positionName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
