package com.kariqu.usercenter.domain;

/**
 * 用户等级规则
 * User: Asion
 * Date: 13-3-18
 * Time: 上午11:21
 */
public class UserGradeRule {

    /**
     * 等级
     */
    private UserGrade grade;

    /**
     * 等级的名称，比如金饭碗，瓷饭碗
     */
    private String name;

    /**
     * 等级的累计消费金额
     */
    private long totalExpense;


    /**
     * 等级的一次性消费金额
     */
    private long onceExpense;


    /**
     * 等级的评价比例
     */
    private double valuationRatio;


    /**
     * 等级图片地址
     */
    private String gradePic;


    /**
     * 等级描述
     */
    private String gradeDescription;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(long totalExpense) {
        this.totalExpense = totalExpense;
    }

    public long getOnceExpense() {
        return onceExpense;
    }

    public void setOnceExpense(long onceExpense) {
        this.onceExpense = onceExpense;
    }

    public double getValuationRatio() {
        return valuationRatio;
    }

    public void setValuationRatio(double valuationRatio) {
        this.valuationRatio = valuationRatio;
    }

    public UserGrade getGrade() {
        return grade;
    }

    public void setGrade(UserGrade grade) {
        this.grade = grade;
    }

    public String getGradePic() {
        return gradePic;
    }

    public void setGradePic(String gradePic) {
        this.gradePic = gradePic;
    }

    public String getGradeDescription() {
        return gradeDescription;
    }

    public void setGradeDescription(String gradeDescription) {
        this.gradeDescription = gradeDescription;
    }
}
