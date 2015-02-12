
Desktop.TradeWindow = Ext.extend(Ext.app.Module, {

    id: 'Trade-win',

    title: '交易记录',

    //创建安全管理的窗口
    createWindow: function () {
        var store = createJsonStore('trade/search', [
            { name: 'tradeNo', type: 'String' },
            { name: 'outerTradeNo', type: 'String' },
            { name: 'orderNo', type: 'String' },
            { name: 'bizType', type: 'String' },
            { name: 'payTotal', type: 'String' },
            { name: 'payType', type: 'String' },
            { name: 'tradeDate', type: 'String' }
        ]);

        store.load({
            params: {
                start: 0,
                limit: 11
            }
        });

        var orderGrid = new Ext.grid.GridPanel({
            id: 'orderList',
            store: store,
            autoScroll: true,
            height: 365,
            loadMask: true,
            columns: [
                { header: "交易号", width: 120, sortable: true, dataIndex: 'tradeNo' },
                { header: "支付平台流水号", width: 140, sortable: true, dataIndex: 'outerTradeNo' },
                { header: "订单号", width: 140, sortable: true, dataIndex: 'orderNo' },
                { header: "成交方式", width: 80, sortable: true, dataIndex: 'bizType', renderer: function (value) {
                    return (value == 'order') ? "订单支付" : ((value == 'coupon') ? "购买现金券" : "");
                } },
                { header: "金额", width: 75, sortable: true, dataIndex: 'payTotal' },
                { header: "支付方式", width: 120, sortable: true, dataIndex: 'payType', renderer: function(value) {
                    return (value == 'defaultbank') ? "银行卡" : value;
                } },
                { header: "交易时间", width: 160, sortable: true, dataIndex: 'tradeDate' }
            ],
            bbar: new Ext.PagingToolbar({
                pageSize: 11,
                store: store,
                displayInfo: true
            })
        });

        var searchOrder = function () {
            store.proxy = new Ext.data.HttpProxy({url: 'trade/search'});
            var startDate = searchForm.getForm().findField('startDate').getValue();
            var endDate = searchForm.getForm().findField('endDate').getValue();

            var bizType = searchForm.getForm().findField('bizType').getValue();
            var payType = searchForm.getForm().findField('payType').getValue();

            var orderNo = searchForm.getForm().findField('orderNo').getValue();
            var tradeNo = searchForm.getForm().findField('tradeNo').getValue();

            var myStore = orderGrid.getStore();
            myStore.baseParams = {
                startDate: startDate,
                endDate: endDate,
                bizType: bizType,
                payType: payType,
                orderNo: orderNo,
                tradeNo: tradeNo
            };
            myStore.load({
                params: {
                    start: 0,
                    limit: 11,
                    startDate: startDate,
                    endDate: endDate,
                    bizType: bizType,
                    payType: payType,
                    orderNo: orderNo,
                    tradeNo: tradeNo
                }
            });
        };

        var exportOrder = function () {
            var bizType = searchForm.getForm().findField('bizType').getValue();
            var payType = searchForm.getForm().findField('payType').getValue();

            var orderNo = searchForm.getForm().findField('orderNo').getValue();
            var tradeNo = searchForm.getForm().findField('tradeNo').getValue();

            var startDate = searchForm.getForm().findField('startDate').getValue();
            var endDate = searchForm.getForm().findField('endDate').getValue();

            window.open("trade/export?bizType=" + bizType + "&payType=" + payType +
                "&orderNo=" + orderNo + "&tradeNo=" + tradeNo +
                "&startDate=" + Ext.util.Format.date(startDate, 'Y-m-d H:i:s') +
                "&endDate=" + Ext.util.Format.date(endDate, 'Y-m-d H:i:s'), "_blank");
        };

        var today = new Date();
        var year = today.getFullYear();
        var data = today.getDate();
        var month = today.getMonth() + 1;
        var now = year + '-' + month + '-' + data + ' 23:59:59';

        today.setTime(today.getTime() - 24 * 60 * 60 * 1000);
        year = today.getFullYear();
        data = today.getDate();
        month = today.getMonth() + 1;
        var time = year + '-' + month + '-' + data + ' 00:00:01';
        var searchForm = new Ext.FormPanel({
            title: '交易查询',
            bodyStyle: 'padding:1px',
            items: [
                {
                    layout: 'form',
                    border: false,
                    buttonAlign: 'center',
                    keys: [{
                        key: Ext.EventObject.ENTER,
                        fn: searchOrder //执行的方法
                    }],
                    buttons: [
                        { text: '查询', handler: searchOrder },
                        { text: '重置', handler: function () { searchForm.getForm().reset(); } },
                        { text: '导出', handler: exportOrder }
                    ],
                    items: [
                        {
                            layout: 'column',
                            border: false,
                            labelWidth: 75,
                            items: [
                                {
                                    layout: 'form',
                                    border: false,
                                    columnWidth: 0.5,
                                    items: [
                                        {
                                            xtype: 'textfield',
                                            anchor: '60%',
                                            fieldLabel: '开始时间',
                                            name: 'startDate',
                                            emptyText: time,
                                            value: ''
                                        },
                                        {
                                            anchor: '60%',
                                            id: 'bizTypeCombo',
                                            xtype: 'combo',
                                            name: 'bizType',
                                            fieldLabel: '成交类型',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            store: new Ext.data.ArrayStore({
                                                fields: [ 'type', 'desc' ],
                                                data: [
                                                    [ '', '全部' ],
                                                    [ 'Order', '订单支付' ],
                                                    [ 'Coupon', '购买现金券' ]
                                                ]
                                            }),
                                            displayField: 'desc',
                                            valueField: 'type',
                                            enableKeyEvents: true
                                        },
                                        {
                                            anchor: '60%',
                                            xtype: 'textfield',
                                            fieldLabel: '交易号',
                                            name: 'tradeNo'
                                        }
                                    ]
                                } ,
                                {
                                    layout: 'form',
                                    border: false,
                                    columnWidth: 0.5,
                                    items: [
                                        {
                                            xtype: 'textfield',
                                            anchor: '60%',
                                            fieldLabel: '结束时间',
                                            name: 'endDate',
                                            emptyText: now,
                                            value: ''
                                        },
                                        {
                                            anchor: '60%',
                                            id: 'payTypeCombo',
                                            xtype: 'combo',
                                            name: 'payType',
                                            fieldLabel: '支付方式',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            store: new Ext.data.ArrayStore({
                                                fields: [ 'type', 'desc' ],
                                                data: [
                                                    [ '', '全部' ],
                                                    [ 'directPay', '支付宝' ],
                                                    [ 'Tenpay', '财付通' ],
                                                    [ 'bankPay', '银行卡' ],
                                                    [ 'creditPay', '信用卡' ]
                                                ]
                                            }),
                                            displayField: 'desc',
                                            valueField: 'type',
                                            enableKeyEvents: true
                                        },
                                        {
                                            anchor: '60%',
                                            xtype: 'textfield',
                                            fieldLabel: '订单号',
                                            name: 'orderNo'
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                orderGrid
            ]
        });

        var menuTab = new Ext.TabPanel({
            id: 'menuTab',
            region: 'center',
            activeTab: 0,
            items: [searchForm]
        });

        var height = document.body.clientHeight;
        return this.app.getDesktop().createWindow({
            id: this.id,
            title: this.title,
            width: 850,
            pageX: document.body.clientWidth * 0.2,
            pageY: document.body.clientHeight * 0.08,
            height: 520,
            layout: 'border',
            items: [menuTab]
        });
    }
});
