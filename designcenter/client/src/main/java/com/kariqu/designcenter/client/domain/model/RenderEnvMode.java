package com.kariqu.designcenter.client.domain.model;

/**
 * 渲染环境模式
 * 
 * @author Tiger
 * @since 2011-1-8 下午04:01:20
 * @version 1.0
 */
public enum RenderEnvMode {
    
    /**
     * DEBUG模式，用于在线制作模板
     */
    ENV_DEBUG,
    
    /**
     * 编辑模式，用户页面装修时的渲染
     */
    ENV_EDIT,
    
    /**
     * 生产环境模式，用户正常的渲染
     */
    ENV_PRODUCTION,
    
    /**
     * 预览模式，用于装修好页面以后预览渲染
     */
    ENV_PREVIEW,

    /**
     * 初始化渲染,用于生成模板页面的PageStructure.xml使用
     */
    ENV_INIT

}
