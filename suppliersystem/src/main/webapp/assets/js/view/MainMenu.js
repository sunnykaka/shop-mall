/**
 * Created with JetBrains WebStorm.
 * Company: Ejs
 * Author: King
 * Date: 13-9-2
 * Time: 下午5:50
 * Information: 右上角悬浮固定菜单-View
 */

Ext.define('Supplier.view.MainMenu', {
    extend: 'Ext.toolbar.Toolbar',
    xtype: 'basic-toolbar',
    alias: 'widget.mainMenu',
    id: 'mainMenu',
    border: false,
    style: 'background:none;padding:5px;',
    initComponent: function () {
        this.items = [
            {
                xtype: 'button', itemId: 'examineGood', text: '验货', width: 70, margin: '0 10 0 0', iconCls: 'icon-cart'
            },
            {
                xtype: 'button',
                text: '功能菜单',
                itemId: 'menuSelectWrap',
                width: 120,
                iconCls: 'icon-menu',
                menu: Ext.create('Ext.menu.Menu', {
                    showSeparator: false,
                    border: false,
                    width: 160,
                    id: 'menuSelect',
                    items: [
                        { text: '订单列表', iconCls: 'icon-list', itemId: 'orderManage'},
                        { text: '报表', iconCls: 'icon-chart', itemId: 'statement'},
                        { text: '历史订单', iconCls: 'icon-previous', itemId: 'orderList', hidden: true},
                        { text: '查看库存', iconCls: 'icon-storage', itemId: 'inventory'},
                        { text: '物流设计', iconCls: 'icon-design', itemId: 'logistics'},
                        { text: '用户日志', iconCls: 'icon-user-log', itemId: 'userLog', id: 'menuUserLog', hidden: true},
                        { text: '用户管理', iconCls: 'icon-user-manage', itemId: 'userList', id: 'menuUserList', hidden: true}
                    ]
                })
            },
            {
                xtype: 'button', itemId: 'modifyPassword', text: '修改密码', width: 90, margin: '0 10 0 0', iconCls: 'icon-password'
            },
            {
                xtype: 'button', itemId: 'logout', text: '退出', width: 70, margin: '0 10 0 0', iconCls: 'icon-logout'
            }
        ];

        this.callParent(arguments);
    }
});
