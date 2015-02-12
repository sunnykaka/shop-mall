/**
 * 退货订单的管理JS
 */


Ext.Loader.load(['/static/js/finance/BackSkuList.js']);


Desktop.BackGoodsWindow = Ext.extend(Ext.app.Module, {

    id: 'BackGoods-win',

    title: '退货订单',

    //创建退单管理的窗口
    createWindow: function () {

        var backStore = getStore('order/backGoods/search', Ext.Model.BackGoods);

        var backGrid = new Ext.grid.GridPanel({
            autoScroll: true,
            store: backStore,
            region: 'center',
            loadMask: true,
            sm: new Ext.grid.RowSelectionModel({
                singleSelect: true
            }),
            viewConfig: {
                forceFit: true
            },
            columns: [
                {header: '退单号', name: 'id', width: 130, sortable: true, dataIndex: 'id'},
                {header: '订单号', name: 'orderNo', width: 200, sortable: true, dataIndex: 'orderNo'},
                {header: '订单状态', name: 'orderStateDesc', width: 140, sortable: true, dataIndex: 'orderStateDesc'},
                {header: '退单状态', name: 'backGoodsStateDesc', width: 200, sortable: true, dataIndex: 'backGoodsStateDesc'},
                {header: "退货人", width: 150, sortable: true, dataIndex: 'userName'},
                {header: "退货人号码", width: 180, sortable: true, dataIndex: 'backPhone'},
                {header: "退款金额", width: 150, sortable: true, dataIndex: 'price'},
                {header: "物流编号", width: 150, sortable: true, dataIndex: 'waybillNumber'},
                {header: "支付银行", width: 150, sortable: true, dataIndex: 'paybank'},
                {header: "退款日期", width: 350, sortable: true, dataIndex: 'backDate', id: 'backDate'}
            ],
            bbar: new Ext.PagingToolbar({
                pageSize: 28,
                store: backStore,
                displayInfo: true
            }),

            tbar: [
                {
                    iconCls: 'accept',
                    text: '同意',
                    handler: function () {
                        var selModel = backGrid.getSelectionModel();
                        var record = selModel.getSelected();
                        if (!record) {
                            Ext.Msg.alert("错误", "请选择要同意退货的记录");
                            return;
                        }
                        Ext.Msg.confirm("确认", "你确定同意退货吗？", function (btn) {
                            if (btn == 'yes') {
                                if (record.get("backGoodsState") == "Create") {
                                    FB([
                                        {
                                            xtype: 'hidden',
                                            name: 'backId',
                                            value: record.get('id')
                                        },
                                        {
                                            xtype: 'combo',
                                            hiddenName: 'notSend',
                                            fieldLabel: '同意类型',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            emptyText: '请选择',
                                            store: new Ext.data.ArrayStore({
                                                fields: [ 'type', 'value' ],
                                                data: [
                                                    [ '同意，然后等待收货', 'false' ],
                                                    [ '同意，直接退款', 'true']
                                                ]
                                            }),
                                            displayField: 'type',
                                            valueField: 'value',
                                            allowBlank: false
                                        },
                                        {
                                            xtype: 'textarea',
                                            name: 'remark',
                                            fieldLabel: '客服备注',
                                            allowBlank: false,
                                            anchor: '100%',
                                            maxLength: 45
                                        }
                                    ], 'order/backOrder/accept', '选择同意类型', false, function () {
                                        backStore.reload();
                                        detailPanel.removeAll();
                                    });
                                } else {
                                    Ext.Msg.alert("错误", "当前状态不能操作");
                                }
                            }
                        });
                    }
                },
                {
                    iconCls: 'cancel',
                    text: '取消',
                    handler: function () {
                        var selModel = backGrid.getSelectionModel();
                        var record = selModel.getSelected();
                        if (!record) {
                            Ext.Msg.alert("错误", "请选择退单记录");
                            return;
                        }
                        Ext.Msg.confirm("确认", "你确定取消吗？", function (btn) {
                            if (btn == 'yes') {
                                FB([
                                    {
                                        xtype: 'hidden',
                                        name: 'backId',
                                        value: record.get('id')
                                    },
                                    {
                                        xtype: 'textarea',
                                        name: 'remark',
                                        fieldLabel: '客服备注',
                                        allowBlank: false,
                                        anchor: '100%',
                                        maxLength: 45
                                    }
                                ], 'order/backOrder/cancel', '添加取消备注', false, function () {
                                    backStore.reload();
                                    detailPanel.removeAll();
                                });
                            }
                        });
                    }
                },
                {
                    iconCls: 'run',
                    text: '输入运单号',
                    handler: function () {
                        Ext.Msg.confirm("确认", "你确定收到用户的退货了吗？", function (btn) {
                            if (btn == 'yes') {
                                var selModel = backGrid.getSelectionModel();
                                var record = selModel.getSelected();
                                if (record) {

                                    if (record.get("backGoodsState") != "Verify") {
                                        Ext.Msg.alert("错误", "只有审核通过的退货单才能输入运单号");
                                        return;
                                    }

                                    FB([
                                        {
                                            xtype: 'hidden',
                                            name: 'backId',
                                            value: record.get('id')
                                        },
                                        {
                                            xtype: 'textfield',
                                            name: 'expressNo',
                                            fieldLabel: '运单号',
                                            allowBlank: false,
                                            anchor: '100%',
                                            maxLength: 45
                                        }
                                    ], 'order/backOrder/getGood', '输入退货运单号', false, function () {
                                        backStore.reload();
                                        detailPanel.removeAll();
                                    });

                                } else {
                                    Ext.Msg.alert("错误", "请选择记录");
                                }
                            }
                        });

                    }
                },
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        backGrid.getStore().reload();
                    }
                },
                '->',
                {
                    xtype: 'combo',
                    name: 'backState',
                    fieldLabel: '状态查询',
                    mode: 'local',
                    editable: false,
                    triggerAction: 'all',
                    emptyText: '选择退单状态',
                    store: new Ext.data.ArrayStore({
                        fields: [ 'type', 'desc' ],
                        data: [
                            [ 'Create', '等待审核' ],
                            [ 'Verify', '审核通过' ],
                            [ 'Receive', '确认收货' ],
                            [ 'Cancel', '取消' ],
                            [ 'Success', '退款成功' ]
                        ]
                    }),
                    displayField: 'desc',
                    valueField: 'type',
                    enableKeyEvents: true,
                    listeners: {
                        select: function (field) {
                            backStore.baseParams = {
                                orderState: field.getValue()
                            };

                            backStore.load({
                                params: {
                                    backState: field.getValue()
                                }
                            });
                            detailPanel.removeAll();
                        }
                    }
                }
            ]
        });

        var detailPanel = new Ext.TabPanel({
            plain: true,
            region: 'south',
            height: document.body.clientHeight * 0.3,
            hidden: true,
            border: false
        });

        backGrid.on('rowclick', function () {
            var selModel = backGrid.getSelectionModel();
            var record = selModel.getSelected();
            detailPanel.displayDetailInfo(record);
            detailPanel.activate(0);
            detailPanel.show();
            Ext.getCmp('BackGoods-win').setHeight(document.body.clientHeight * 0.85);
            Ext.getCmp('BackGoods-win').doLayout();
        });

        detailPanel.displayDetailInfo = function (record) {
            this.removeAll(true);
            this.add(new BackSkuDetail(record.get('id')));
            this.add(new ProductDetail(record.get('orderId')));
            this.add(new AddressInfo(record.get('orderId'), false));
            this.add(new PayDeliveryInfo(record.get('orderId'), false));
            this.add({
                title: '退货理由',
                height: 450,
                bodyStyle: 'padding:10px;',
                html: "退货原因：" + record.get('backReasonReal') + "<br/>退货详细描述：" + record.get('backReason')
            });
            if (record.get('invoice')) {
                this.add(new InvoiceInfo(record.get('orderId'), false));
            }
            if (record.get('hasAttach')) {
                var filesURL = record.get('uploadFiles');
                var html = "";
                for (var i = 0; i < filesURL.split(">").length; i++) {
                    var src = filesURL.split(">")[i].split("|")[1];
                    var alt = filesURL.split(">")[i].split("|")[0];
                    html += "<img src='" + src + "' alt='" + alt + "' title='" + alt + "'/>";
                }
                this.add({
                    title: '查看附件',
                    html: html,
                    autoScroll: true,
                    bodyStyle: 'padding:10px;'
                });
            }
        };

        var height = document.body.clientHeight;
        return this.app.getDesktop().createWindow({
            id: this.id,
            title: '退货订单',
            pageX: document.body.clientWidth * 0.1,
            pageY: height * 0.05,
            height: height * 0.6,
            width: 1020,
            layout: 'border',
            items: [backGrid, detailPanel]
        });
    }
});