package com.kariqu.designcenter.domain.model;



import java.io.Serializable;

/**模块实例参数，用于存储在装修店铺的时候，模块参数的存储
 * 当装修完毕之后，这些参数会被打包到PageStructure配置文件中，产品模式渲染就直接读取配置文件且被缓存
 * @author Tiger
 * @since 2011-4-5 下午04:41:16
 * @version 1.0.0
 */
public class ModuleInstanceParam implements Serializable {
    
    private static final long serialVersionUID = -1704539573203740291L;

    private long id;
    
    private String moduleInstanceId;
    
    private int modulePrototypeId;
    
    /**
     * 参数对应的pageId,当模块在头部或者尾部的时候pageId为0
     */
    private long pageId;
    
    private int shopId;

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数值
     */
    private String paramValue;


    /**
     * 参数类型
     */
    private ParamType paramType;
    
    public ModuleInstanceParam() {
        
    }

    public ModuleInstanceParam(String paramName, String paramValue) {
       this.paramName = paramName;
       this.paramValue = paramValue;
    }


    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the moduleInstanceId
     */
    public String getModuleInstanceId() {
        return moduleInstanceId;
    }

    /**
     * @param moduleInstanceId the moduleInstanceId to set
     */
    public void setModuleInstanceId(String moduleInstanceId) {
        this.moduleInstanceId = moduleInstanceId;
    }

    /**
     * @return the modulePrototypeId
     */
    public int getModulePrototypeId() {
        return modulePrototypeId;
    }

    /**
     * @param modulePrototypeId the modulePrototypeId to set
     */
    public void setModulePrototypeId(int modulePrototypeId) {
        this.modulePrototypeId = modulePrototypeId;
    }

    /**
     * @return the pageId
     */
    public long getPageId() {
        return pageId;
    }

    /**
     * @param pageId the pageId to set
     */
    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    /**
     * @return the shopId
     */
    public int getShopId() {
        return shopId;
    }

    /**
     * @param shopId the shopId to set
     */
    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    /**
     * @return the paramName
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * @param paramName the paramName to set
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /**
     * @return the paramValue
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * @param paramValue the paramValue to set
     */
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public ParamType getParamType() {
        return paramType;
    }

    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }
}
