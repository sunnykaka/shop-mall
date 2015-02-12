/**
 * User: Hualei Du
 * Date: 13-10-10
 * Time: 下午2:10
 */

Ext.define('Supplier.view.user.List', {
    extend: 'Ext.container.Container',
    alias: 'widget.userList',
    id: 'userList',
    fixed: true,
    layout: 'border',
    initComponent: function () {

        var searchForm = Ext.create('Ext.form.Panel', {
            region: 'north',
            title: '用户管理',
            iconCls: 'icon-user-manage',
            layout: 'hbox',
            bodyPadding: 10,
            id: 'userListSearch',
            defaults: {
                xtype: 'combo',
                labelWidth: 60,
                width: 220,
                margin: '0 10 0 0'
            },
            items: [
                {
                    xtype: 'textfield',
                    name: 'accountName',
                    fieldLabel: '用户名'
                },
                {
                    xtype: 'button',
                    text: '查询',
                    width: 80,
                    itemId: 'searchBtn'
                },
                {
                    xtype: 'button',
                    text: '添加用户',
                    width: 80,
                    itemId: 'addBtn'
                },
                {
                    xtype: 'button',
                    text: '删除已选用户',
                    width: 120,
                    itemId: 'deleteBtn',
                    disabled: true
                },
                {
                    xtype: 'button',
                    text: '刷新列表',
                    width: 80,
                    itemId: 'reloadBtn'
                }
            ]
        });

        var statementGrid = Ext.create('Ext.grid.Panel', {
            region: 'center',
            store: 'UserList',
            forceFit: true,
            id: 'userListGrid',
            selType: 'checkboxmodel',
            viewConfig: {
                enableTextSelection: true
            },
            columns: [
                {
                    text: '编号',
                    dataIndex: 'id',
                    width: 60
                },
                {
                    text: '用户名称',
                    dataIndex: 'accountName',
                    width: 90
                },
                {
                    text: '是否为主帐号',
                    dataIndex: 'mainAccount',
                    width: 120,
                    renderer: function (value) {
                        if (value) {
                            return '是';
                        }
                        return '否';
                    }
                },
                {
                    text: '是否激活',
                    dataIndex: 'normal',
                    width: 120,
                    renderer: function (value) {
                        if (value) {
                            return '是';
                        }
                        return '否';
                    }
                },
                {
                    text: '邮箱地址',
                    dataIndex: 'email',
                    width: 160
                }
            ]
        });

        this.items = [searchForm, statementGrid];

        this.callParent(arguments);
    }
});