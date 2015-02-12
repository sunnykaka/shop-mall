package com.kariqu.designcenter.client.domain.model;

import java.util.List;
import java.util.Map;

/**
 * 渲染上下文,渲染模块和坑部分可以在模板的渲染指令中回调
 * User: Asion
 * Date: 11-12-15
 * Time: 下午2:25
 */
public interface RenderContext {

    /**
     * 执行渲染，得到渲染页面
     *
     * @return
     */
    String render();


    /**
     * 获取渲染结果参数，这些参数用户向调用方返回信息
     * 比如资源文件路径等等
     *
     * @return
     */
    Map<String, String> getResultParams();


    /**
     * 渲染模板模块
     *
     * @param name
     * @param domId
     * @return
     */
    String renderTemplateModule(String name, int domId);

    /**
     * 渲染公共模块
     *
     * @param name
     * @param domId
     * @return
     */
    String renderCommonModule(String name, String version, int domId);


    /**
     * 渲染坑,坑的名称在头，尾，以及页面body不能重复，不同的页面body可以有相同的坑名称
     *
     * @param regionName
     * @return
     */
    String renderRegion(String regionName, List<ModuleInfo> moduleInfoList);


}
