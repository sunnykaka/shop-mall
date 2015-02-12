package com.kariqu.session.config;

import com.kariqu.session.StoreType;

/**
 *  数据entry的配置
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-17 下午7:13
 */
public class SessionConfigEntry {

    /**
     * the name which is used in the application.
     */
    private String name;
    /**
     * the key means the store key,for example cookie key,this key always
     * is a unmeaningful name
     */
    private String key;

    private StoreType storeType;

    private String domain;

    private String path;

    /**
     * combined cookie's key
     */
    private String combineKey;

    private int lifeCycle;

    private boolean httpOnly;

    /**
     * 表示此属性是只读的，程序不能修改只读属性，只读属性由session框架生成，比如
     * sessionId
     */
    private boolean readOnly;

    private boolean commbine;

    private boolean encrypt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public StoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(StoreType storeType) {
        this.storeType = storeType;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(int lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getCombineKey() {
        return combineKey;
    }

    public void setCombineKey(String combineKey) {
        this.combineKey = combineKey;
    }

    public boolean isCommbine() {
        return commbine;
    }

    public void setCommbine(boolean commbine) {
        this.commbine = commbine;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }
}


