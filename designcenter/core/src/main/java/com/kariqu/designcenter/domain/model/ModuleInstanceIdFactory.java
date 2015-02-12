package com.kariqu.designcenter.domain.model;

/**
 * 因为一个模块可以在同一个模板添加几次，因此需要区分模块名称相同的模块参数，此时需要根据domId来生成
 * 模块位置，模块domId，店铺ID，页面ID可保证实例出来的字符窜是唯一的
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-21 下午04:32:26
 */
public class ModuleInstanceIdFactory {

    public static final String HEAD = "100";

    public static final String FOOT = "200";

    public static final String BODY = "300";

    public static final String GLOBAL_GRANULARITY = "400";

    private static final String SPLIT = "_";

    public static String createModuleIdOfHead(int shopId, String domId) {
        StringBuilder result = new StringBuilder();
        result.append(HEAD).append(SPLIT).append(shopId).append(SPLIT).append(domId);
        return result.toString();
    }

    public static String createModuleIdOfBody(long pageId, String domId, int shopId) {
        StringBuilder result = new StringBuilder();
        result.append(BODY).append(SPLIT).append(shopId).append(SPLIT).append(pageId).append(SPLIT).append(domId);
        return result.toString();
    }

    public static String createModuleIdOfFoot(int shopId, String domId) {
        StringBuilder result = new StringBuilder();
        result.append(FOOT).append(SPLIT).append(shopId).append(SPLIT).append(domId);
        return result.toString();
    }

    public static String createModuleIdOfGlobalGranularity(int shopId, int prototypeId) {
        StringBuilder result = new StringBuilder();
        result.append(GLOBAL_GRANULARITY).append(SPLIT).append(shopId).append(SPLIT).append(prototypeId);
        return result.toString();
    }

    public static ModuleInstanceParamDetail analyzeModuleInstanceParam(String moduleInstanceId) {
        String[] paramParts = moduleInstanceId.split(SPLIT);
        String sign = paramParts[0];
        if (HEAD.equals(sign)) {
            return new ModuleInstanceParamDetail(true, false, false, false, Integer.valueOf(paramParts[1]), null, null);
        }

        if (FOOT.equals(sign)) {
            return new ModuleInstanceParamDetail(false, false, true, false, Integer.valueOf(paramParts[1]), null, null);
        }

        if (BODY.equals(sign)) {
            return new ModuleInstanceParamDetail(false, true, false, false, Integer.valueOf(paramParts[1]), Integer.valueOf(paramParts[2]), null);
        }

        if (GLOBAL_GRANULARITY.equals(sign)) {
            return new ModuleInstanceParamDetail(false, false, false, true, Integer.valueOf(paramParts[1]), null, Integer.valueOf(paramParts[2]));
        }
        return null;


    }
}
