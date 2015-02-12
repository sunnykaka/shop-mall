/**
 * Created with JetBrains WebStorm.
 * Company: Ejs
 * Author: King
 * Date: 13-9-2
 * Time: 下午5:50
 * Information: 查看库存-View
 */

Ext.define('Supplier.view.Inventory', {
    extend: 'Ext.container.Container',
    alias: 'widget.inventory',
    id: 'comContainer',
    fixed: true,
    layout: 'border',
    initComponent: function () {

        var searchForm = Ext.create('Ext.form.Panel', {
            region: 'north',
            title: '查看库存',
            iconCls: 'icon-storage',
            layout: 'hbox',
            bodyPadding: 10,
            itemId: 'search',
            defaults: {
                xtype: 'combo',
                labelWidth: 60,
                width: 140,
                margin: '0 10 0 0'
            },
            items: [
                {
                    xtype: 'textfield', name: 'productCode', fieldLabel: '商品编号'
                },
                {
                    xtype: 'textfield', name: 'productName', fieldLabel: '商品名称'
                },
                {
                    xtype: 'textfield', name: 'barCode', fieldLabel: '条形码'
                },
                {
                    fieldLabel: '仓库', name: 'storageId', labelWidth: 35, valueField: 'id', displayField: 'name',
                    queryMode:      'local',
                    triggerAction:  'all',
                    forceSelection: true,
                    emptyText:      '请选择',
                    store: 'Storage'
                },
                {
                    xtype: 'button', text: '查询', width: 80, itemId: 'searchBtn'
                },
                {
                    xtype: 'button', text: '重置', width: 70, itemId: 'resetBtn', iconCls: 'icon-rewrite'
                },
                {
                    xtype: 'button', text: ' 导出Excel表', itemId: 'export', iconCls: 'icon-export', width: 105, hidden: true
                }
            ]
        });

        var statementGrid = Ext.create('Ext.grid.Panel', {
            region: 'center',
            store: 'Inventory',
            forceFit: true,
            itemId: 'grid',
            viewConfig: {
                enableTextSelection: true
            },
            columns: [
                { xtype: 'rownumberer', storable: false},
                { text: '品牌', dataIndex: 'brandName', width: 80},
                { text: '商品分类', dataIndex: 'categoryName', width: 60},
                { text: '商品编号', dataIndex: 'itemNo', width: 100},
                { text: '商品名称', dataIndex: 'productName', width: 200},
                { text: '商品属性', dataIndex: 'attribute', width: 150},
                { text: '条形码', dataIndex: 'barCode'},
                { text: '图片库存', dataIndex: 'stockQuantity'},
                { text: '理论库存', dataIndex: 'stockQuantity'},
                { text: '实物库存', dataIndex: 'stockQuantity'},
                { text: '库房名称', dataIndex: 'storageName'}
            ],
            bbar: Ext.create('Ext.PagingToolbar', {
                store: 'Inventory',
                displayInfo: true
            })
        });

        this.items = [searchForm, statementGrid];

        this.callParent(arguments);
    }
});
