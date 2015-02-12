package com.kariqu.designcenter.client.service;

import java.util.Map;

/**
 * 模块预览服务
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-26
 *        Time: 下午3:03
 */
public interface ModulePreviewService {


    String previewCommonModule(String name, Map<String, Object> context);

}
