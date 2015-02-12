package com.kariqu.designcenter.domain.model.prototype;

import java.io.Serializable;

/**模板版本的状态
 * @author Tiger
 * @since 2011-4-7 下午11:50:04 
 * @version 1.0.0
 */
public enum VersionState implements Serializable{

    /*
     * 制作调试状态
     */
    debug,
    /*
     * 已经发布状态
     */
    released
}
