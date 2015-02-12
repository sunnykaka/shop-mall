package com.kariqu.cmscenter.domain;

/**
 * 内容,用富文本编辑器编辑
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-23
 *        Time: 上午11:08
 */
public class Content {

    private int id;

    private String title;

    private String content;

    /**
     * 访问url
     */
    private String url;

    private int categoryId;

    private int templateId;

    private int priority;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }
}
