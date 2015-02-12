/**
 * User: Hualei Du
 * Date: 13-10-11
 * Time: 上午9:59
 */
Ext.define('Supplier.view.user.Log', {
    extend: 'Ext.container.Container',
    alias: 'widget.userLog',
    id: 'userLog',
    fixed: true,
    layout: 'border',
    initComponent: function () {

        var searchForm = Ext.create('Ext.form.Panel', {
            region: 'north',
            title: '用户日志',
            iconCls: 'icon-user-log',
            layout: 'hbox',
            bodyPadding: 10,
            id: 'userLogSearch',
            defaults: {
                xtype: 'combo',
                labelWidth: 60,
                width: 220,
                margin: '0 10 0 0'
            },
            items: [
                {
                    xtype: 'textfield',
                    name: 'content',
                    fieldLabel: '内容'
                },
                {
                    xtype: 'button',
                    text: '查询',
                    width: 80,
                    itemId: 'searchBtn'
                },
                {
                    xtype: 'button',
                    text: '删除已选日志',
                    width: 120,
                    itemId: 'deleteBtn',
                    disabled: true
                }
            ]
        });

        var statementGrid = Ext.create('Ext.grid.Panel', {
            region: 'center',
            store: 'UserLog',
            forceFit: true,
            id: 'userLogGrid',
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
                    text: '操作人',
                    dataIndex: 'operator',
                    width: 90
                },
                {
                    text: '日期',
                    dataIndex: 'date',
                    width: 120
                },
                {
                    text: 'ip地址',
                    dataIndex: 'ip',
                    width: 120
                },
                {
                    text: '主题',
                    dataIndex: 'title',
                    width: 160
                },
                {
                    text: '内容',
                    dataIndex: 'content',
                    width: 260
                }
            ],
            bbar: Ext.create('Ext.PagingToolbar', {
                store: 'UserLog',
                displayInfo: true
            })
        });

        this.items = [searchForm, statementGrid];

        this.callParent(arguments);
    }
});