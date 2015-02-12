package com.kariqu.designcenter.client.service;

import com.kariqu.designcenter.service.Result;

/**
 * 页面装修服务
 *
 * @author Tiger
 * @version 1.0
 * @since 12-5-7 下午3:35
 */
public interface PageDesignService {

    /**
     * 头部增加模块
     *
     * @param prototypeId    原型ID
     * @param shopTemplateId 店铺模板id
     * @param regionName     坑名称
     * @return
     */
    Result addModuleToHead(int prototypeId, int shopTemplateId, String regionName);

    /**
     * 尾部增加模块
     *
     * @param prototypeId    原型ID
     * @param shopTemplateId 店铺模板id
     * @param regionName     坑名称
     * @return
     */
    Result addModuleToFoot(int prototypeId, int shopTemplateId, String regionName);

    /**
     * body增加模块
     *
     * @param prototypeId 原型ID
     * @param pageId      店铺模板id
     * @param regionName  坑名称
     * @return
     */
    Result addModuleToBody(int prototypeId, long pageId, String regionName);

    /**
     * 头部删除模块
     *
     * @param moduleInstanceId
     * @param shopTemplateId
     * @param regionName
     * @return
     */
    Result deleteModuleFromHead(String moduleInstanceId, int shopTemplateId, String regionName);


    /**
     * 尾部删除模块
     *
     * @param moduleInstanceId
     * @param shopTemplateId
     * @param regionName
     * @return
     */
    Result deleteModuleFromFoot(String moduleInstanceId, int shopTemplateId, String regionName);

    /**
     * 删除body部分模块
     *
     * @param moduleInstanceId
     * @param pageId
     * @param regionName
     * @return
     */
    Result deleteModuleFromBody(String moduleInstanceId, long pageId, String regionName);

    /**
     * 移动头部模块
     *
     * @param moduleInstanceId
     * @param shopTemplateId
     * @param regionName
     * @param direction        up表示上移，down表示下移
     * @return
     */
    Result moveModuleOfHead(String moduleInstanceId, int shopTemplateId, String regionName, String direction);

    /**
     * 移动尾部模块
     *
     * @param moduleInstanceId
     * @param shopTemplateId
     * @param regionName
     * @param direction        up表示上移，down表示下移
     * @return
     */
    Result moveModuleOfFoot(String moduleInstanceId, int shopTemplateId, String regionName, String direction);

    /**
     * 移动body部分的模块
     *
     * @param moduleInstanceId
     * @param pageId
     * @param regionName
     * @param direction
     * @return
     */
    Result moveModuleOfBody(String moduleInstanceId, long pageId, String regionName, String direction);
}
