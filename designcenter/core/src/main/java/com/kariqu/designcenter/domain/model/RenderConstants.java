package com.kariqu.designcenter.domain.model;

/**
 * @author Tiger
 * @version 1.0
 * @since 2010-12-20 下午03:31:03
 */
public interface RenderConstants {

    /*
    默认一个店铺的ID，代表我们官方店铺
     */
    int shopId = 1;

    /**
     * PageStructure 渲染上下文的key
     */
    String PAGE_STRUCTURE = "page_structure";
    /**
     * RenderContext 在渲染上下文的key
     */
    String RENDER_CONTEXT = "render_context";

    /**
     * 渲染区域
     */
    String RENDER_AREA = "render_area";


    /**
     * 渲染模式
     */
    String RENDER_MOD = "render_mod";

    /**
     * 头部
     */
    String HEAD = "header";
    /**
     * 页面BODY
     */
    String BODY = "body";
    /**
     * 页尾
     */
    String FOOT = "footer";

    /**
     * 模块参数引用
     */
    String MODULE_PARAM_REF = "_PARAM";

    /**
     * 默认区域的名称，在默认区域的模块是不能删除的
     */
    String FIXED_REGION_NAME = "_fixedRegion";

    /**
     * 全局CSSkey
     */
    String GLOBAL_CSS_KEY = "global_css_key";

    /**
     * 全局的JS key
     */
    String GLOBAL_JS_KEY = "global_js_key";

    /**
     * 风格key
     */
    String STYLE_KEY = "style_key";

    /**
     * 下上下文中店铺的ID key
     */
    String SHOP_ID_CONTEXT_KEY = "shopId";

    /** 下上下文中店铺的 css ID key */
    String CSS_ID_CONTEXT_KEY = "shopCssId";

    /**
     * 产品上下文key
     */
    String PRODUCT_CONTEXT_KEY = "_product";

    /**
     * sku在上下文中的key
     */
    String SKU_CONTEXT_KEY = "_sku";


    /**
     * 缓存版本
     */
    String CACHE_VERSION = "cache_version";


    /**
     * 模块是否及时渲染
     */
    String IS_CACHEABLE = "isCacheable";


    /**
     * 模块是否可以被删除
     */
    String IS_DELETE = "isDelete";


    /**
     * 模块是否可以被编辑
     */
    String IS_EDIT = "isEdit";


    /**
     * 初始化缓存版本
     */
    String INIT_CACHE_VERSION = "1.0.0";


    /**
     * Url Broker，传递给模板用于调用统一的URL
     */
    String URL_Broker = "urlBroker";


    String IMG_RESOLVER = "imgResolver";


    /**
     * 大于等于这个值的ID是模板模块的ID
     */
    int COMMON_MODULE_ID_THRESHOLD = 10000;


    /**
     * 模块toolbar
     */
    String TOOL_BAR = "TOOLBAR";

    /**
     * 渲染区域，分为头，尾，主题部分
     *
     * @author Tiger
     * @version 1.0
     * @since 2011-4-4 下午08:58:33
     */
    public enum RenderArea {
        head, foot, body;

        boolean equals(RenderArea other) {
            return this.toString().equals(other.toString());
        }
    }

    /**
     * 渲染模式
     */
    public enum RenderMod {
        debug,
        design,
        preview,
        product
    }
}
