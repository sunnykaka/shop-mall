/**
 * Created with JetBrains WebStorm.
 * Company: Ejs
 * Author: King
 * Date: 13-9-2
 * Time: 下午5:50
 * Information: 报表-View
 */

Ext.define('Supplier.view.Statement', {
    extend: 'Ext.container.Container',
    alias: 'widget.statement',
    id: 'comContainer',
    fixed: true,
    layout: 'border',
    initComponent: function () {

        var searchForm = Ext.create('Ext.form.Panel', {
            region: 'north',
            title: '报表',
            layout: 'hbox',
            iconCls: 'icon-chart',
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
                    fieldLabel: '仓库', name: 'storageId', labelWidth: 35, valueField: 'id', displayField: 'name',
                    queryMode: 'local',
                    triggerAction: 'all',
                    forceSelection: true,
                    emptyText: '请选择',
                    store: 'Storage'
                },
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

        var isNull = function(value){
            if (!value || value=="" || value=="null" || value==null) {
                return 0
            }else{
                return value;
            }
        }

        var statementGrid = Ext.create('Ext.grid.Panel', {
            region: 'center',
            store: 'Statement',
            forceFit: true,
            itemId: 'grid',
            viewConfig: {
                enableTextSelection: true
            },
            columns: [
                { xtype: 'rownumberer', storable: false},
                { text: '品牌', dataIndex: 'brandName', width: 60},
                { text: '商品名称', dataIndex: 'productName', width: 150},
                { text: '商品属性', dataIndex: 'attribute', width: 150},
                { text: '商品编号', dataIndex: 'itemNo', width: 80},
                { text: '条形码', dataIndex: 'barCode', width: 70},
                { text: '商品分类', dataIndex: 'categoryName', width: 50},
                { text: '订货数量', dataIndex: 'number', width: 40, renderer: isNull},
                { text: '销售数量', dataIndex: 'shipmentNum', width: 40, renderer: isNull},
                { text: '退货数量', dataIndex: 'backNum', width: 40, renderer: isNull},
                { text: '商品金额', dataIndex: 'commodityPrice', width: 40, renderer: isNull},
                { text: '销售金额', dataIndex: 'salePrice', width: 40, renderer: isNull},
                { text: '退款金额', dataIndex: 'backPrice', width: 40, renderer: isNull}
            ],
            bbar: Ext.create('Ext.PagingToolbar', {
                store: 'Statement',
                displayInfo: true,
                displayMsg: '当前 {0} - {1} 总计 {2}',
                emptyMsg: "无数据"
            })
        });

        this.items = [searchForm, statementGrid];

        this.callParent(arguments);
    }
});
