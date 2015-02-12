/**
 * Created with JetBrains WebStorm.
 * Company: Ejs
 * Author: King
 * Date: 13-9-2
 * Time: 下午5:50
 * Information: 订单管理-View
 */

Ext.define('Supplier.view.OrderManage', {
    extend: 'Ext.container.Container',
    alias: 'widget.orderManage',
    id: 'orderManage',
    fixed: true,
    layout: 'border',
    initComponent: function () {

        var searchForm = Ext.create('Ext.form.Panel', {
            region: 'north',
            title: '订单列表',
            iconCls: 'icon-list',
            layout: 'hbox',
            bodyPadding: 10,
            id: 'orderListSearch',
            defaults: {
                xtype: 'combo',
                labelWidth: 60,
                hideEmptyLabel: true,
                width: 140,
                margin: '0 10 0 0'
            },
            tbar: {
                ui: 'footer',
                items: [
                    { text: '待处理', itemId: 'Confirm', belong: 'mainBtn', disabled: true},
                    '--》',
                    { text: '已打印', itemId: 'Print', belong: 'mainBtn'},
                    '--》',
                    { text: '待发货', itemId: 'Verify', belong: 'mainBtn'},
                    '--》',
                    { text: '物流状态', itemId: 'Success', belong: 'mainBtn'}
                ]
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
                    xtype: 'datefield', emptyText: '开始日期', width: 105, name: 'startDate', format: 'Y-m-d'
                },
                {
                    xtype: 'datefield', emptyText: '结束日期', width: 105, name: 'endDate', format: 'Y-m-d'
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
                        ['kuaijiesudi', '快捷'],
                        ['guotongkuaidi', '国通'],
                        ['lianbangkuaidi', '联邦'],
                        ['quanfengkuaidi', '全峰'],
                        ['suer', '速尔'],
                        ['tiantian', '天天'],
                        ['youshuwuliu', '优速'],
                        ['unknown', '未知']
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
                    fieldLabel: '订单状态', value: 'Confirm', name: 'orderState', id: 'order_state', hidden: true,
                    store: [
                        ['Confirm', '待处理'],
                        ['Print', '已打印'],
                        ['Verify', '待发货'],
                        ['Success', '物流状态']
                    ]
                },
                //分页数暂时不需要，等数据量大时加再开启
                /*{
                    fieldLabel: '单页数', value: '100', name: 'pageSize', id: 'page_size', labelWidth: 45,
                    store: [
                        ['100', '100'],
                        ['200', '200'],
                        ['300', '300'],
                        ['500', '500']
                    ]
                },*/
                {
                    xtype: 'button', text: '查询', width: 70, itemId: 'searchBtn', iconCls: 'icon-zoom'
                },
                {
                    xtype: 'button', text: '重置', width: 70, itemId: 'resetBtn', iconCls: 'icon-rewrite'
                }
            ]
        });

        var orderGrid = Ext.create('Ext.grid.Panel', {
            region: 'center',
            store: 'OrderManage',
            forceFit: false,
            id: 'orderListGrid',
            selType: 'checkboxmodel',
            viewConfig: {
                enableTextSelection: true,
                markDirty: false
            },
            plugins: new Ext.grid.plugin.CellEditing({
                pluginId: 'cellEdit',
                clicksToEdit: 2
            }),
            tbar: [
                { text: '订单汇总', iconCls: 'icon-sum', belong: 'Confirm', disabled: true, itemId: 'orderSummary'},
                { text: '打印物流单', iconCls: 'icon-printer', belong: 'Confirm', disabled: true, itemId: 'printPreview'},
                { text: '打印发货单', iconCls: 'icon-printer', belong: 'Confirm', disabled: true, itemId: 'printInvoice'},
                { xtype: 'numberfield', minValue: 0, fieldLabel: '生成物流编号基数', labelWidth: 120, width: 280,  belong: 'Confirm', disabled: true, id: 'batch_exp_num'},
                { text: '批量生成', iconCls: 'icon-control_play', belong: 'Confirm', disabled: true, itemId: 'generateSN'},
                { text: '确认打印', iconCls: 'icon-printer', belong: 'Confirm', disabled: true, itemId: 'printLogistics'},
                { text: '返回待处理', iconCls: 'icon-arrow_redo', belong: 'Print', hidden: true, disabled: true, itemId: 'goBack_1'},
                { text: '批量验货', iconCls: 'icon-list', belong: 'Print', hidden: true, disabled: true, itemId: 'batchInspection'},
                { text: '返回已打印', iconCls: 'icon-arrow_redo', belong: 'Verify', hidden: true, disabled: true, itemId: 'goBack_2'},
                { text: '确认发货', iconCls: 'icon-accept', belong: 'Verify', hidden: true, disabled: true, itemId: 'confirmationDelivery'},
                { text: '刷新', iconCls: 'icon-refresh', itemId: 'refresh'},
                { text: '是否显订单详情', iconCls: 'icon-refresh', itemId: 'isOrderItemShow', enableToggle: true,
                    pressed: true,
                    tooltip: '是否在底部显示选中订单的详细信息'},
                { text: '导出表格', iconCls: 'icon-sum', itemId: 'orderExport', belong: 'Success', initShow: true, hidden: true, disabled: false},
                Ext.create('Ext.container.Container', {
                    width: 10,
                    html: '<object id="LODOP1" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0>' +
                        '<embed id="LODOP_EM1" TYPE="application/x-print-lodop" width=0 height=0 PLUGINSPAGE="install_lodop32.exe"></embed>' +
                        '</object>'
                })
            ],
            columns: [
                { text: '订单编号', dataIndex: 'orderNo', width: 110},
                { text: '是否开发票', dataIndex: 'invoice', width: 80, renderer: function(value){
                    var result = (value === true) ? "开" : "否";
                    return result;
                }},
                { text: '物流编号', dataIndex: 'waybillNumber', width: 160, editor: {
                    allowBlank: false
                }},
                { text: '收货人', dataIndex: 'name', width: 100},
                { text: '买家姓名', dataIndex: 'userName', width: 100},
                { text: '订单状态', dataIndex: 'orderState', width: 100},
                { text: '配送方式', dataIndex: 'deliveryType', width: 100},
                { text: '仓库', dataIndex: 'storageName', width: 150},
                { text: '付款金额', dataIndex: 'totalPrice', width: 100},
                { text: '下单时间', dataIndex: 'createTime', width: 155},
                { text: '支付时间', dataIndex: 'payDate', width: 155},
                { text: '发货时间', dataIndex: 'sendTime', width:155},
                { text: '卖家留言', dataIndex: 'customerServiceRemark', width: 350},
                { text: '买家留言', dataIndex: 'userRemark', width: 350}
            ]
        });

        var orderItem = Ext.create('Ext.tab.Panel',{
            region: 'south',
            height: 170,
            hidden: true,
            id: 'orderItem',
            tabPosition: 'left',
            split: true,
            items: [
                {
                    title: '商品',
                    itemId: 'order',
                    overflowY: 'auto',
                    items: Ext.create('Ext.grid.Panel', {
                        store: 'GoodList',
                        forceFit: true,
                        itemId: 'goodGrid',
                        viewConfig: {
                            enableTextSelection: true
                        },
                        columns: [
                            { xtype: 'rownumberer', sortable: false},
                            { text: '商品编号', dataIndex: 'itemNo', width: 55},
                            { text: '商品名称', dataIndex: 'productName', width: 170},
                            { text: '商品属性', dataIndex: 'skuAttribute', width: 170},
                            { text: '条形码', dataIndex: 'barCode', width: 60},
                            { text: '类别', dataIndex: 'categoryName', width: 45},
                            { text: '单价', dataIndex: 'unitPrice', width: 45},
                            { text: '数量', dataIndex: 'shipmentNum', width: 40},
                            { text: '库存', dataIndex: 'stockQuantity', width: 40},
                            { text: '品牌', dataIndex: 'brandName', width: 70}
                        ]
                    })
                },
                {
                    title: '订单',
                    items: Ext.create('Ext.view.View', {
                        store: Ext.create('Ext.data.Store', {
                            extend: 'Ext.data.Store',
                            fields:['id','orderNo', 'createTime', 'location', 'payTime', 'deliveryType', 'totalPrice','name','payBank', 'mobile', 'cost','zipCode','userName'],
                            proxy: {
                                type: 'ajax',
                                url: '/orders',
                                //url: '/assets/js/data/OrderInfo.json',
                                reader: {
                                    type: 'json',
                                    successProperty: 'success',
                                    root: 'data.obj',
                                    messageProperty: 'message'
                                }
                            },
                            autoLoad: false
                        }),
                        tpl: new Ext.XTemplate(
                            '<tpl for=".">',
                                '<table class="mytable">',
                                    '<tr>',
                                        '<th>订单编号</th>',
                                        '<td>{orderNo}</td>',
                                        '<th>下单时间</th>',
                                        '<td>{createTime}</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<th>配送地区</th>',
                                        '<td>{location}</td>',
                                        '<th>付款时间</th>',
                                        '<td>{payTime}</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<th>配送方式</th>',
                                        '<td>{deliveryType}</td>',
                                        '<th>商品运费</th>',
                                        '<td>{cost}</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<th>收货人姓名</th>',
                                        '<td>{name}</td>',
                                        '<th>付款金额</th>',
                                        '<td>{totalPrice}</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<th>支付方式</th>',
                                        '<td>{payBank}</td>',
                                        '<th>邮政编码</th>',
                                        '<td>{zipCode}</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<th>联系电话</th>',
                                        '<td>{mobile}</td>',
                                        '<th>买家姓名</th>',
                                        '<td>{userName}</td>',
                                    '</tr>',
                                '</table>',
                            '</tpl>'
                        )
                    })
                },
                {
                    title: '发票',
                    items: Ext.create('Ext.view.View', {
                        store: Ext.create('Ext.data.Store', {
                            extend: 'Ext.data.Store',
                            fields:['invoiceType','invoiceTitle', 'companyName', 'invoiceContent'],
                            proxy: {
                                type: 'ajax',
                                url: '/orders',
                                //url: '/assets/js/data/Invoice.json',
                                reader: {
                                    type: 'json',
                                    successProperty: 'success',
                                    root: 'data.obj',
                                    messageProperty: 'message'
                                }
                            },
                            autoLoad: false
                        }),
                        tpl: new Ext.XTemplate(
                            '<tpl for=".">',
                                '<table class="mytable">',
                                    '<tr>',
                                        '<th>发票类型</th><td>{invoiceType}</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<th>发票抬头</th><td>{invoiceTitle}</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<th>单位名称</th><td>{companyName}</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<th>发票内容</th><td>{invoiceContent}</td>',
                                    '</tr>',
                                '</table>',
                            '</tpl>'
                        )
                    })
                }
            ]
        });

        this.items = [searchForm, orderGrid, orderItem];

        this.callParent(arguments);

        var map = new Ext.util.KeyMap({
            target: searchForm,
            key: Ext.EventObject.F1, // or Ext.EventObject.ENTER
            fn: function(){
                console.log('gasdfsdf');
            },
            scope: searchForm
        });
    }
});
