/**
 * 订单管理JS
 */

Ext.Loader.load(['/static/js/order/SkuList.js','/static/js/order/GiftList.js',
    '/static/js/order/AddressInfo.js', '/static/js/order/PayDeliveryInfo.js',
    '/static/js/order/InvoiceInfo.js', '/static/js/order/OrderMessage.js',
    '/static/js/order/OrderPriceMessage.js', '/static/js/order/LogisticsInfo.js']);

Desktop.OrderWindow = Ext.extend(Ext.app.Module, {

    id: 'Order-win',

    title: '订单管理',

    //创建安全管理的窗口
    createWindow: function () {

        //命名空间
        Ext.namespace("Ext.EJS.Order");


        var customerStore = new Ext.data.Store({
            reader: new Ext.data.JsonReader({
                fields: [ 'id', 'name' ],
                root: 'data.customersList'
            }),
            proxy: new Ext.data.HttpProxy({
                url: 'order/list/customer',
                method: 'GET'
            })
        });
        customerStore.load();
        var customerComboBox = new Ext.form.ComboBox({
            anchor: '80%',
            hiddenName: 'customerId',
            fieldLabel: '商家',
            mode: 'local',
            editable: false,
            store: customerStore,
            triggerAction: 'all',
            emptyText: '请选择',
            displayField: 'name',
            valueField: 'id',
            // allowBlank: false,
            listeners: {
                'select': function (combo, record, index) {
                    storageComboBox.reset();
                    storageStore.load({
                        params: {
                            customerId: combo.getValue()
                        }
                    });
                }
            }
        });
        var storageStore = new Ext.data.Store({
            reader: new Ext.data.JsonReader({
                fields: [ 'id', 'name' ],
                root: 'data.storageList'
            }),
            proxy: new Ext.data.HttpProxy({
                url: 'order/list/storage',
                method: 'GET'
            })
        });
        var storageComboBox = new Ext.form.ComboBox({
            anchor: '80%',
            hiddenName: 'storageId',
            fieldLabel: '库位',
            mode: 'local',
            editable: false,
            store: storageStore,
            triggerAction: 'all',
            emptyText: '请选择',
            displayField: 'name',
            valueField: 'id'
        });


        var store = createJsonStore('order/search', [
            {
                name: 'orderId',
                type: 'long'
            },
            {
                name: 'userId',
                type: 'int'
            },
            {
                name: 'userName',
                type: 'String'
            },
            {
                name: 'goodToUserName',
                type: 'String'
            },
            {
                name: 'startDate',
                type: 'String'
            },
            {
                name: 'endDate',
                type: 'String'
            } ,
            {
                name: 'accountType',
                type: 'String'
            },
            {
                name: 'orderState',
                type: 'String'
            },
            {
                name: 'orderStateDesc',
                type: 'String'
            },
            {
                name: 'invoice',
                type: 'bool'
            },
            {
                name: 'orderNo',
                type: 'long'
            },
            {
                name: 'invoice',
                type: 'boolean'
            }
        ]);


        store.load({
            params: {
                orderState: 'Pay',
                start: 0,
                limit: 11
            }
        });

        var orderGrid = new Ext.grid.GridPanel({
            id: 'orderList',
            store: store,
            autoScroll: true,
            height: document.body.clientHeight * 0.35,
            sm: new Ext.grid.RowSelectionModel({
                singleSelect: true
            }),
            loadMask: true,
            viewConfig: {
                forceFit: true
            },
            columns: [
                {name: 'orderId', hidden: true, dataIndex: 'orderId'} ,
                {id: 'orderNo', name: 'orderNo', header: "订单编号", width: 90, sortable: true, dataIndex: 'orderNo'},
                {id: 'userId', name: 'userId', header: "下单人Id", width: 90, sortable: true, dataIndex: 'userId'},
                {header: "收货人", width: 70, name: 'goodToUserName', sortable: true, dataIndex: 'goodToUserName'},
                {header: "下单人", width: 70, name: 'userName', sortable: true, dataIndex: 'userName'},
                {header: "订单状态", width: 140, sortable: true, dataIndex: 'orderStateDesc'},
                {header: "开发票", width: 50, sortable: true, dataIndex: 'invoice', renderer: function (value) {
                    return value ? "开" : "不开";
                }},
                {header: "下单时间", width: 140, sortable: true, dataIndex: 'startDate'},
                {header: "结束时间", width: 140, sortable: true, dataIndex: 'endDate'}
            ],
            bbar: new Ext.PagingToolbar({
                pageSize: 11,
                store: store,
                displayInfo: true
            }),
            tbar: [
                {
                    iconCls: 'refresh',
                    text: '刷新',
                    handler: function () {
                        orderGrid.getStore().reload();
                    }
                },{
                    iconCls: 'add',
                    text: '加赠品',
                    handler: function () {
                        var selModel = orderGrid.getSelectionModel();
                        var mealSet = selModel.getSelected();
                        if (!mealSet) {
                            Ext.Msg.alert("错误", "请选择订单");
                            return;
                        }


                        var productSelector = buildProductSelector();

                        var win = new Ext.Window({
                            title: '选择礼品商品',
                            width: 450,
                            height: 700,
                            layout: 'fit',
                            plain: true,
                            items: productSelector
                        });
                        win.show();

                        productGrid.on('rowcontextmenu', function (grid, rowIndex, e) {
                            e.preventDefault();//取消默认的浏览器右键事件
                            var record = grid.getStore().getAt(rowIndex);
                            var productId = record.get('id');
                            var menu = new Ext.menu.Menu({
                                items: [
                                    {
                                        text: '加入礼品列表',
                                        handler: function () {
                                            if (!record.get('online')) {
                                                Ext.Msg.alert("错误", "此商品还没有上架");
                                                return;
                                            }

                                            doAjax("product/skuList/" + productId, function (obj) {

                                                var form = buildForm('/order/gift', [
                                                    {
                                                        text: '提交',
                                                        handler: function () {
                                                            commitForm(form, function () {
                                                                form.ownerCt.close();
                                                                Ext.Msg.alert("成功", "加入成功，点击订单，在赠品列表中即可查看");
                                                            })
                                                        }
                                                    }
                                                ], [
                                                    {
                                                        xtype: 'hidden',
                                                        name: 'orderId',
                                                        value: mealSet.get('orderId')
                                                    },
                                                    {
                                                        xtype: 'combo',
                                                        hiddenName: 'skuId',
                                                        fieldLabel: 'SKU',
                                                        mode: 'local',
                                                        editable: false,
                                                        triggerAction: 'all',
                                                        emptyText: '请选择',
                                                        store: obj.data.skuList,
                                                        allowBlank: false
                                                    },
                                                    {
                                                        fieldLabel: '数量',
                                                        name: 'number',
                                                        xtype: 'numberfield',
                                                        allowBlank: false
                                                    }
                                                ]);
                                                buildWin('赠送礼品', 400, form).show(this.id)
                                            });
                                        }
                                    }
                                ]
                            });
                            menu.showAt(e.getXY());
                        });
                    }
                },
                {
                    iconCls: 'accept',
                    text: '确认订单',
                    handler: function () {
                        var sm = orderGrid.getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert('错误', '请选择要操作的记录！');
                        } else {
                            var record = sm.getSelected();
                            if (record.get('orderState') != 'Pay') {
                                Ext.Msg.alert('错误', '该订单状态不允许此操作！');
                                return;
                            }

                            doAjax('order/state/confirm', function () {
                                Ext.Msg.alert('成功', "更新成功");
                                orderPanel.removeAll();
                                store.reload();
                            }, {
                                orderId: record.get('orderId')
                            }, "确认后商家就可发货，确定吗？");

                        }
                    }
                },
                {
                    iconCls: 'comments',
                    text: '添加备注',
                    handler: function () {
                        var sm = orderGrid.getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert('错误', '请选择订单！');
                        } else {
                            var record = sm.getSelected();
                            FB([
                                {
                                    xtype: 'hidden',
                                    name: 'orderId',
                                    value: record.get('orderId')
                                },
                                {
                                    xtype: 'hidden',
                                    name: 'orderNo',
                                    value: record.get('orderNo')
                                },
                                {
                                    xtype: 'textarea',
                                    name: 'messageInfo',
                                    anchor: '90%',
                                    fieldLabel: '备注内容',
                                    allowBlank: false
                                }
                            ], 'order/comments/add', '添加评论', false, function () {
                                Ext.EJS.Order.orderMessageStore.reload();
                            });
                        }

                    }

                },
                '->',
                {
                    xtype: 'textfield',
                    name: 'orderOwner',
                    emptyText: '下单人',
                    width: 100,
                    enableKeyEvents: true,
                    listeners: {
                        keyup: function (field) {
                            store.baseParams = {
                                start: 0,
                                limit: 11,
                                orderOwner: field.getValue()
                            };

                            store.load({
                                params: {
                                    start: 0,
                                    limit: 11,
                                    orderOwner: field.getValue()
                                }
                            });

                            orderPanel.removeAll();
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'consignee',
                    emptyText: '收货人',
                    width: 100,
                    enableKeyEvents: true,
                    listeners: {
                        keyup: function (field) {
                            store.baseParams = {
                                start: 0,
                                limit: 11,
                                consignee: field.getValue()
                            };

                            store.load({
                                params: {
                                    start: 0,
                                    limit: 11,
                                    consignee: field.getValue()
                                }
                            });

                            orderPanel.removeAll();
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'mobile',
                    emptyText: '手机号',
                    width: 100,
                    enableKeyEvents: true,
                    listeners: {
                        keyup: function (field) {
                            store.baseParams = {
                                start: 0,
                                limit: 11,
                                mobile: field.getValue()
                            };

                            store.load({
                                params: {
                                    start: 0,
                                    limit: 11,
                                    mobile: field.getValue()
                                }
                            });

                            orderPanel.removeAll();
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'orderNo',
                    emptyText: '订单号',
                    width: 100,
                    enableKeyEvents: true,
                    listeners: {
                        keyup: function (field) {
                            store.baseParams = {
                                start: 0,
                                limit: 11,
                                orderNo: field.getValue()
                            };

                            store.load({
                                params: {
                                    start: 0,
                                    limit: 11,
                                    orderNo: field.getValue()
                                }
                            });

                            orderPanel.removeAll();
                        }
                    }
                }
            ]
        });

        var orderPanel = new Ext.TabPanel({
            plain: true,
            hidden: true,
            border: false
        });

        orderGrid.on('rowclick', function () {
            var selModel = orderGrid.getSelectionModel();
            var record = selModel.getSelected();
            orderPanel.reDisplayProductInfo(record);
            orderPanel.activate(0);
            orderPanel.show();
            searchForm.doLayout();
            Ext.getCmp('Order-win').setHeight(document.body.clientHeight * 0.9);
            Ext.getCmp('Order-win').doLayout();
        });

        orderPanel.reDisplayProductInfo = function (record) {
            this.removeAll(true);
            var orderId = record.get("orderId");
            this.add(new ProductDetail(orderId));
            var giftList = new GiftList(orderId);
            GiftList.instacneGrid = giftList;
            this.add(giftList);
            this.add(AddressInfo(orderId, record.get('orderState') != 'Success'));
            if (record.get('invoice')) {
                this.add(InvoiceInfo(orderId, record.get('orderState') != 'Success'));
            }
            this.add(new OrderMessage(orderId));
            this.add(new OrderPriceMessage(orderId));
            this.add(PayDeliveryInfo(orderId, record.get('orderState') != 'Success'));
            this.add(new LogisticsDetail(orderId));
        };


        var searchOrder = function () {
            store.proxy = new Ext.data.HttpProxy({url: 'order/search'});
            var customerId = searchForm.getForm().findField('customerId').getValue();
            if (customerId == null || customerId == '') {
                customerId = 0
            }
            var storageId = searchForm.getForm().findField('storageId').getValue();
            if (storageId == null || storageId == '') {
                storageId = 0
            }
            var myStore = orderGrid.getStore();
            var oss = Ext.getCmp('orderStateCombo').getValue();
            var orderState = [];
            for (var i = 0; i < oss.length; i++) {
                orderState.push(oss[i].value);
            }
            var appraise = Ext.getCmp('appraiseCombo').getValue();

            var startDate = searchForm.getForm().findField('startDate').getValue();
            var endDate = searchForm.getForm().findField('endDate').getValue();
            var skuName = searchForm.getForm().findField('skuName').getValue();
            var brushOrder = searchForm.getForm().findField('brushOrder').getValue();

            myStore.baseParams = {
                orderState: orderState,
                customerId: customerId,
                storageId: storageId,
                startDate: startDate,
                endDate: endDate,
                skuName: skuName,
                appraise: appraise,
                brush: brushOrder
            };
            myStore.load({
                params: {
                    start: 0,
                    limit: 11,
                    orderState: orderState,
                    customerId: customerId,
                    storageId: storageId,
                    startDate: startDate,
                    endDate: endDate,
                    skuName: skuName,
                    appraise: appraise,
                    brush: brushOrder
                }
            });
            orderPanel.removeAll();
        };

        var exportOrder = function () {
            var customerId = searchForm.getForm().findField('customerId').getValue();
            if (customerId == null || customerId == '') {
                customerId = 0;
            }
            var storageId = searchForm.getForm().findField('storageId').getValue();
            if (storageId == null || storageId == '') {
                storageId = 0;
            }
            var oss = Ext.getCmp('orderStateCombo').getValue();
            var orderState = [];
            for (var i = 0; i < oss.length; i++) {
                orderState.push(oss[i].value);
            }
            if (orderState.length == 0) {
                orderState.push('Send');
                orderState.push('Success');
            }
            var appraise = Ext.getCmp('appraiseCombo').getValue();

            var startDate = searchForm.getForm().findField('startDate').getValue();
            var endDate = searchForm.getForm().findField('endDate').getValue();
            var skuName = searchForm.getForm().findField('skuName').getValue();
            var brushOrder = searchForm.getForm().findField('brushOrder').getValue();

            // Ext.Msg.alert("", "date: " + Ext.util.Format.date(startDate, 'YmdHis'));
            window.open('order/export?orderState=' + orderState + "&customerId=" + customerId + "&storageId=" + storageId
                + "&startDate=" + Ext.util.Format.date(startDate, 'Y-m-d H:i:s')
                + "&endDate=" + Ext.util.Format.date(endDate, 'Y-m-d H:i:s')
                + "&skuName=" + skuName + "&appraise=" + appraise + "&brush=" + brushOrder, '_blank');
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
            title: '订单筛选',
            bodyStyle: 'padding:1px',
            items: [
                {
                    layout: 'form',
                    border: false,
                    buttonAlign: 'center',
                    keys: [
                        {
                            key: Ext.EventObject.ENTER,
                            fn: searchOrder //执行的方法
                        }
                    ],
                    buttons: [
                        {
                            text: '查询',
                            handler: searchOrder
                        },
                        {
                            text: '重置',
                            handler: function () {
                                searchForm.getForm().reset();
                            }
                        },
                        {
                            text: '导出',
                            handler: exportOrder
                        }
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
                                            anchor: '80%',
                                            fieldLabel: '开始时间',
                                            name: 'startDate',
                                            emptyText: time,
                                            value: ''
                                        },
                                        customerComboBox,
                                        {
                                            anchor: '80%',
                                            xtype: 'textfield',
                                            fieldLabel: '商品名',
                                            name: 'skuName'
                                        },
                                        {
                                            id: 'orderStateCombo',
                                            xtype: 'checkboxgroup',
                                            name: 'orderState',
                                            fieldLabel: '订单状态',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            columns: 5,
                                            width: 330,
                                            items: [
                                                { boxLabel: '已创建', value: 'Create' },
                                                { boxLabel: '已付款', value: 'Pay', checked : true },
                                                { boxLabel: '已审核', value: 'Confirm' },
                                                { boxLabel: '已打单', value: 'Print' },
                                                { boxLabel: '已验货', value: 'Verify' },
                                                { boxLabel: '已发货', value: 'Send' },
                                                { boxLabel: '已完成', value: 'Success' },
                                                { boxLabel: '已退货', value: 'Back' },
                                                { boxLabel: '已取消', value: 'Cancel' }
                                            ],
                                            enableKeyEvents: true
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
                                            anchor: '80%',
                                            fieldLabel: '结束时间',
                                            name: 'endDate',
                                            emptyText: now,
                                            value: ''
                                        },
                                        storageComboBox,
                                        {
                                            id: 'appraiseCombo',
                                            anchor: '80%',
                                            xtype: 'combo',
                                            name: 'appraise',
                                            fieldLabel: '评价状态',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            store: new Ext.data.ArrayStore({
                                                fields: [ 'type', 'desc' ],
                                                data: [
                                                    [ '', '全部' ],
                                                    [ 'no', '未评价' ],
                                                    [ 'yes', '已评价' ],
                                                    [ 'mutual', '双方互评' ]
                                                ]
                                            }),
                                            displayField: 'desc',
                                            valueField: 'type',
                                            enableKeyEvents: true
                                        },
                                        {
                                            name: 'brushOrder',
                                            anchor: '80%',
                                            xtype: 'combo',
                                            hiddenName: 'brushOrder',
                                            fieldLabel: '洗唰唰',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            emptyText: '请选择',

                                            store: new Ext.data.ArrayStore({
                                                fields: ['id', 'type' ],
                                                data: [
                                                    [ '0', '不包含' ],
                                                    [ '1', '包含' ]
                                                ]
                                            }),
                                            displayField: 'type',
                                            valueField: 'id'
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                orderGrid ,
                orderPanel
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
            title: '订单管理',
            width: 850,
            pageX: document.body.clientWidth * 0.2,
            pageY: document.body.clientHeight * 0.03,
            height: height * 0.50,
            layout: 'border',
            items: [menuTab]
        });

    }
});
