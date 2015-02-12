/**
 * Created with JetBrains WebStorm.
 * Company: Ejs
 * Author: King
 * Date: 13-9-2
 * Time: 下午5:50
 * Information: 商家系统
 */

Ext.Loader.setConfig({enabled: true});
Ext.application({
    name: 'Supplier',
    appFolder: 'assets/js',
    controllers: ['Main', 'User'],
    launch: function () {
        //主框架
        Ext.create('Ext.container.Viewport', {
            id: 'mainViewPort',
            layout: 'fit',
            items: [{xtype: 'orderManage'}]
        });
        //右上角悬浮固定菜单
        Ext.create('Ext.container.Container', {
            renderTo: Ext.get('m_wrap'),
            items: [{xtype: 'mainMenu'}]
        })
    }
});