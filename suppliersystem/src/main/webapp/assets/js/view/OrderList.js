/**
 * Created with JetBrains WebStorm.
 * Company: Ejs
 * Author: King
 * Date: 13-9-2
 * Time: 下午5:50
 * Information: 历史订单-View
 */

Ext.define('Supplier.view.OrderList', {
    extend: 'Ext.container.Container',
    alias: 'widget.orderList',
    id: 'comContainer',
    fixed: true,
    layout: 'border',
    initComponent: function () {

        var searchForm = Ext.create('Ext.form.Panel', {
            region: 'north',
            title: '历史订单',
            iconCls: 'icon-previous',
            layout: 'hbox',
            bodyPadding: 10,
            id: 'search',
            defaults: {
                xtype: 'combo',
                labelWidth: 60,
                width: 140,
                margin: '0 10 0 0'
            },
            items: [
                {
                    fieldLabel: '日期类型',  value: 'date', name: 'dateType', width: 180,
                    store: [
                        ['date', '订单下单日期'],
                        ['endDate', '订单结束日期']
                    ]
                },
                {
                    xtype: 'datefield', fieldLabel: '开始日期', width: 170, name: 'startDate', format: 'Y-m-d'
                },
                {
                    xtype: 'datefield', fieldLabel: '结束日期', width: 170, name: 'endDate', format: 'Y-m-d'
                },
                {
                    fieldLabel: '快递公司', value: '', name: 'deliveryType',
                    store: [
                        ['', '请选择'],
                        ['shunfeng', '顺丰'],
                        ['yunda', '韵达'],
                        ['zhaijisong', '宅急送'],
                        ['ems', 'EMS'],
                        ['yuantong', '圆通'],
                        ['shentong', '申通'],
                        ['zhongtong', '中通'],
                        ['huitongkuaidi', '汇通'],
                        ['quanritongkuaidi', '全日通'],
                        ['kuaijiesudi', '快捷']
                    ]
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
                    fieldLabel: '关键字搜索', value: 'consignee', name: 'queryOption', labelWidth: 75, margin: '0 5 0 0', width: 160,
                    store: [
                        ['consignee', '收货人'],
                        ['userName', '用户姓名'],
                        ['orderNo', '订单编号'],
                        ['expressNo', '物流单号']
                    ]
                },
                {
                    xtype: 'textfield', name: 'queryValue', width: 100
                },
                {
                    xtype: 'button', text: '查询', width: 70, itemId: 'searchBtn', iconCls: 'icon-zoom'
                },
                {
                    xtype: 'button', text: '重置', width: 70, itemId: 'resetBtn', iconCls: 'icon-rewrite'
                },
                {
                    xtype: 'button', text: ' 导出Excel表', itemId: 'export', iconCls: 'icon-export', width: 105, hidden: true
                }
            ]
        });

        var orderGrid = Ext.create('Ext.grid.Panel', {
            region: 'center',
            store: 'OrderList',
            itemId: 'grid',
            viewConfig: {
                enableTextSelection: true
            },
            bbar: Ext.create('Ext.PagingToolbar', {
                store: 'OrderList',
                displayInfo: true
            }),
            columns: [
                {xtype: 'rownumberer', sortable: false},
                { text: '品牌', dataIndex: 'brand'},
                { text: '订单编号', dataIndex: 'order_num'},
                { text: '订单状态', dataIndex: 'order_state'},
                { text: '商品名称', dataIndex: 'good_name', width: 150},
                { text: '商品属性', dataIndex: 'good_attr', width: 150},
                { text: '订单数量', dataIndex: 'order_amount'},
                { text: '单价', dataIndex: 'good_price'},
                { text: '订单金额', dataIndex: 'order_price'},
                { text: '买家ID', dataIndex: 'buyer_id'},
                { text: '收货人姓名', dataIndex: 'receiver'},
                { text: '联系电话', dataIndex: 'phone'},
                { text: '收货地址', dataIndex: 'address'},
                { text: '快递公司', dataIndex: 'expressage'},
                { text: '快递单号', dataIndex: 'logistics_num'},
                { text: '发货数量', dataIndex: 'give_amount'},
                { text: '退货数量', dataIndex: 'return_amount'},
                { text: '买家备注', dataIndex: 'buery_message'},
                { text: '卖家留言', dataIndex: 'seller_note'},
                { text: '订货日期', dataIndex: 'order_date'},
                { text: '付款日期', dataIndex: 'deal_date'},
                { text: '审核日期', dataIndex: 'check_date'},
                { text: '打印日期', dataIndex: 'print_date'},
                { text: '验货日期', dataIndex: 'examine_date'},
                { text: '发货日期', dataIndex: 'give_date'},
                { text: '签收时间', dataIndex: 'receiver_date'},
                { text: '审核员', dataIndex: 'checker'},
                { text: '打单员', dataIndex: 'printer'},
                { text: '验货员', dataIndex: 'examiner'},
                { text: '发货员', dataIndex: 'giver'},
                { text: '所属网站', dataIndex: 'site'}
            ]
        });

        this.items = [searchForm, orderGrid];

        this.callParent(arguments);
    }
});
