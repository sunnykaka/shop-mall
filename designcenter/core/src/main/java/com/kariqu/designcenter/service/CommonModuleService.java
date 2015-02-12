package com.kariqu.designcenter.service;

import com.kariqu.designcenter.domain.model.prototype.CommonModule;

import java.util.List;

/**
 * 公共模块即系统模块的服务
 *
 * @author Tiger
 * @version 1.0
 * @since 2011-4-4 下午09:58:49
 */
public interface CommonModuleService {

    /**
     * 发布公共模块
     *
     * @param id
     */
    void releaseCommonModule(int id);

    /**
     * 创建模块的时候如果发现配置文件有异常，配置内容将被置空
     *
     * @param commonModule
     * @return
     */
    int createCommonModule(CommonModule commonModule);

    void updateCommonModule(CommonModule commonModule);

    CommonModule getCommonModuleById(int id);

    void deleteCommonModule(int id);

    List<CommonModule> queryAllCommonModule();

    /**
     * 判断是否重名
     *
     * @param name
     * @return
     */
    boolean existCommonModule(String name);

    /**
     * 根据名称和版本号查询公共模块
     *
     * @param name
     * @param version
     * @return
     */
    CommonModule queryCommonModuleByNameAndVersion(String name, String version);


    /**
     * 根据名字和版本得到模块用于渲染
     * 会启用开发模式配置
     *
     * @param name
     * @param version
     * @return
     */
    CommonModule getCommonModuleForRendering(String name, String version);


    /**
     * 查询所有的公共模块
     *
     * @return
     */
    List<CommonModule> queryAllGlobalCommonModule();
}
