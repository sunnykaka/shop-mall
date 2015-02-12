package com.kariqu.usercenter;

import com.kariqu.usercenter.domain.UserGrade;

/**
 * User: Asion
 * Date: 13-3-22
 * Time: 上午10:57
 */
public class UserGradeRuleView {

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
    private String totalExpense;


    /**
     * 等级的一次性消费金额
     */
    private String onceExpense;


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

    public UserGrade getGrade() {
        return grade;
    }

    public void setGrade(UserGrade grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(String totalExpense) {
        this.totalExpense = totalExpense;
    }

    public String getOnceExpense() {
        return onceExpense;
    }

    public void setOnceExpense(String onceExpense) {
        this.onceExpense = onceExpense;
    }

    public double getValuationRatio() {
        return valuationRatio;
    }

    public void setValuationRatio(double valuationRatio) {
        this.valuationRatio = valuationRatio;
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
