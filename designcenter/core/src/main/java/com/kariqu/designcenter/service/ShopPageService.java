package com.kariqu.designcenter.service;

import com.kariqu.designcenter.domain.model.PageType;
import com.kariqu.designcenter.domain.model.shop.ShopPage;

import java.util.List;


/**
 * 店铺页面服务
 *
 * @author tiger
 * @version 1.0
 * @since 2010-12-22 下午08:02:20
 */
public interface ShopPageService {

    /**
     * 用一个模板版本初始化真实的店铺
     * 如果用于初始化的版本没有发布则抛出异常，终止程序
     * 首先构建店铺模板
     * 1，店铺模板有编辑模式和产品模式，用这两种模式来对装修界面和用户所看到的界面进行隔离
     * 2，将模板版本的头尾结构文件用真实的实例ID替换
     * 3，将头尾内容和头尾结构文件set到店铺模板的产品和编辑模式字段
     * 4，用模板页面生成店铺页面，初始化编辑模式字段
     * 5，设置店铺页面的产品内容，设置产品化配置
     * 6，用真实实例ID替换店铺页面编辑模式配置和产品模式配置的domId
     * 7，用真实的头尾配置，店铺ID，页面ID，真实的页面配置初始化数据库中的实例参数表用于用户装修
     * 8，清除掉店铺页面编辑模式配置文件中的参数，因为没有作用，可以节省磁盘空间
     *
     * @param shopId
     * @param templateVersionId
     */
    void initShopPages(int shopId, int templateVersionId);


    /**
     * 使用页面原型初始化店铺头部
     *
     * @param pagePrototypeId
     * @param shopId
     * @return
     */
    Result initShopHeadWithHeadPagePrototype(int pagePrototypeId, int shopId);

    /**
     * 使用页面原型初始化店铺尾部
     *
     * @param pagePrototypeId
     * @param shopId
     * @return
     */
    Result initShopFootWithFootPagePrototype(int pagePrototypeId, int shopId);

    /**
     * 使用页面原型初始化店铺页面
     *
     * @param pagePrototypeId
     * @param shopId
     * @param pageType
     * @return
     */
    Result initShopPageWithPagePrototype(int pagePrototypeId, int shopId, PageType pageType, String name);

    /**
     * 应用页面原型到店铺页面头部
     *
     * @param pagePrototypeId
     * @param shopId
     * @return
     */
    Result applyPagePrototypeToHead(int pagePrototypeId, int shopId);

    /**
     * 应用页面原型到店铺尾部
     *
     * @param pagePrototypeId
     * @param shopId
     * @return
     */
    Result applyPagePrototypeToFoot(int pagePrototypeId, int shopId);

    /**
     * 应用页面原型到店铺页面
     *
     * @param pagePrototypeId
     * @param shopPageId
     * @return
     */
    Result applyPagePrototypeToShopPage(int pagePrototypeId, long shopPageId);


    /**
     * 根据modifyConfig判断是否重度升级店铺模板,升级改动是在原来模板上，升级过程需要复制头尾的内容，全局的js,css,style，如果页面结构发生改动
     * 删除原来的实例参数，重新实例化，然后将店铺模板的编辑模式内容全部更新
     *
     * @param shopId
     * @param modifyConfig
     */
    void upgradeShopTemplate(int shopId, boolean modifyConfig);


    /**
     * 根据modifyConfig判断是否重度升级，如果为假则只复制html内容，为真则需要删除原来店铺页面的参数，重新实例化参数
     * 将店铺页面的编辑模式页面结构设置为新的值
     *
     * @param pageId
     * @param modifyConfig
     */
    void upgradeShopPage(long pageId, boolean modifyConfig);


    /**
     * 应用模板的某个版本到店铺,此方法需要在一个事务中运行
     * 1. 首先查询店铺模板对象，并用模板版本对象的头尾配置文件以及头尾代码文件更新店铺模板对象的编辑模式字段
     * 2. 删除所有已经存在的类型为other的页面，并创建新的other类型的页面
     * 3. 更新所有非other类型的页面，不需要更新prod模式的下的页面代码文件和配置文件
     * 4. 实例化页面参数
     * 5. 实例化模板头尾的参数
     *
     * @param templateVersionId
     */
    void applyTemplateVersion(int shopId, int templateVersionId);

    /**
     * 升级模板，升级模板仅仅能做如下事情：
     * 1. 修改全局CSS和JS
     * 2. 修改页面内容，模块内容
     * 不能做如下事情：
     * 1 不能做所有使得用户PageStrucure变化的东西
     * 2 不能使用户的装修数据库有改动
     *
     * @param shopId
     * @param templateVersionId
     */
    void upgradeTemplateVersion(int shopId, int templateVersionId);


    /**
     * 发布装修完的店铺页面 (只会发布店铺的三个主要页面)
     * 1. 设置店铺模板的所有产品化字段，比如头尾内容，配置文件，风格
     * 2. 根据数据库中的参数构建PageStructure对象
     * 3. 设置店铺页面的产品化内容和产品化配置
     * 3. 将头尾，body以及所有参数全部构建成一个xml文件
     * 4. 清除页面缓存以及模板头尾缓存
     *
     * @param shopId
     */
    public void publishShopPages(int shopId);


    /**
     * 单独发布店铺页面
     *
     * @param pageId
     */
    public void publishShopPage(long pageId);


    public ShopPage getShopPageById(long id);

    public ShopPage queryIndexShopPage(int shopId);

    public ShopPage querySearchListShopPage(int shopId);

    public ShopPage queryDetailShopPage(int shopId);

    ShopPage queryDetailIntegralShopPage(int shopId);

    public ShopPage queryChannelShopPage(int shopId);

    public ShopPage queryMealSetShopPage(int shopId);



    public List<ShopPage> queryShopPagesByShopId(int shopId);

    public long createShopPage(ShopPage shopPage);

    public void updateShopPage(ShopPage shopPage);

    public void deleteShopPage(long id);

    public List<ShopPage> queryAllShopPages();

}
