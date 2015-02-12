/**
 * 财务退款管理JS
 */

Desktop.FinanceWindow = Ext.extend(Ext.app.Module, {

    id: 'Finance-win',

    title: '退款管理',


    //创建退款管理的窗口
    createWindow: function () {


        var financeStore = createJsonStore('finance/refundment/search', [
            {
                name: 'id',
                type: 'long'
            },
            {
                name: 'backGoodsState',
                type: 'String'
            },
            {
                name: 'canRefund',
                type: 'String'
            },
            {
                name: 'orderNo',
                type: 'long'
            },
            {
                name: 'userName',
                type: 'String'
            },
            {
                name: 'price',
                type: 'String'
            },
            {
                name: 'paybank',
                type: 'String'
            },
            {
                name: 'outerTradeNo',
                type: 'String'
            },
            {
                name: 'backReason',
                type: 'String'
            },
            {
                name: 'backDate',
                type: 'String'
            }
        ]);

        financeStore.load({
            params: {
                action: 'WaitRefund',
                start: 0,
                limit: 28
            }
        });

        var financeGrid = new Ext.grid.GridPanel({
            autoScroll: true,
            border: false,
            store: financeStore,
            loadMask: true,
            sm: new Ext.grid.RowSelectionModel({
                singleSelect: true
            }),
            viewConfig: {
                forceFit: true
            },
            columns: [
                {header: '退单号', name: 'id', width: 150, sortable: true, dataIndex: 'id'},
                {header: '订单号', name: 'orderNo', width: 200, sortable: true, dataIndex: 'orderNo'},
                {header: "退款人", width: 100, sortable: true, dataIndex: 'userName'},
                {header: "退款金额", width: 100, sortable: true, dataIndex: 'price'},
                {header: "交易号", width: 150, sortable: true, dataIndex: 'outerTradeNo'},
                {header: "理由", width: 150, sortable: true, dataIndex: 'backReason', id: 'backReason'},
                {header: "支付银行", width: 100, sortable: true, dataIndex: 'paybank'},
                {header: "申请时间", width: 250, sortable: true, dataIndex: 'backDate'}
            ],
            bbar: new Ext.PagingToolbar({
                pageSize: 28,
                store: financeStore,
                displayInfo: true
            }),

            tbar: [
                {
                    iconCls: 'accept',
                    text: '确认退款',
                    handler: function () {
                        var sm = financeGrid.getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert('错误', '请选择要退款的记录');
                            return;
                        }
                        var record = sm.getSelected();
                        if (record.get("canRefund") != "true") {
                            Ext.Msg.alert("错误", "当前状态不允许退款操作");
                            return;
                        }
                        if (record.get("paybank") == "财付通") {
                            Ext.Msg.alert("错误", "退款都是通过支付宝完成的, 财付通并不支持, 目前只能手动转账到用户的财付通账户这一种方式");
                            return;
                        }

                        window.open('finance/refundment/submit?outerTradeNo=' + record.get('outerTradeNo') + '&backId=' + record.get('id') + '&price=' + record.get('price') + '&reason=' + record.get('backReason'));
                        var delay = new Ext.util.DelayedTask(function () {
                            financeGrid.getStore().reload();
                        });
                        delay.delay(2000);
                    }
                },
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        financeGrid.getStore().reload();
                    }
                },
                {
                    text: '模拟退款',
                    iconCls: 'accept',
                    handler: function () {

                        var form = new Ext.FormPanel({
                            baseCls: 'x-plain',
                            labelWidth: 80,
                            url: 'alipay/refund/notify',
                            frame: true,
                            autoHeight: true,
                            items: [
                                {
                                    xtype: 'textfield',
                                    name: 'batch_no',
                                    fieldLabel: '批次号',
                                    allowBlank: false,
                                    anchor: '100%',
                                    maxLength: 45
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'success_num',
                                    fieldLabel: '成功笔数',
                                    allowBlank: false,
                                    anchor: '100%',
                                    maxLength: 45
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'result_details',
                                    fieldLabel: '交易号^金额^SUCCESS',
                                    allowBlank: false,
                                    anchor: '100%',
                                    maxLength: 45
                                }
                            ]
                        });

                        var win = new Ext.Window({
                            title: '模拟退款输入信息',
                            width: 400,
                            autoHeight: true,
                            layout: 'fit',
                            plain: true,
                            buttonAlign: 'center',
                            bodyStyle: 'padding:5px;',
                            items: form,
                            buttons: [
                                {
                                    text: '保存',
                                    handler: function () {
                                        commitForm(form, function () {
                                            win.close();
                                            financeGrid.getStore().reload();
                                        });
                                    }
                                },
                                {
                                    text: '取消',
                                    handler: function () {
                                        win.close();
                                    }
                                }
                            ]
                        });
                        win.show(this.id)
                    }
                },
                '->',
                {
                    xtype: 'combo',
                    name: 'action',
                    fieldLabel: '状态查询',
                    mode: 'local',
                    editable: false,
                    triggerAction: 'all',
                    emptyText: '选择退单状态',
                    store: new Ext.data.ArrayStore({
                        fields: [ 'type', 'desc' ],
                        data: [
                            [ 'WaitRefund', '等待退款' ],
                            [ 'Success', '退款成功' ]
                        ]
                    }),
                    displayField: 'desc',
                    valueField: 'type',
                    enableKeyEvents: true,
                    listeners: {
                        select: function (field) {
                            financeStore.baseParams = {
                                action: field.getValue()
                            };

                            financeStore.load({
                                params: {
                                    action: field.getValue()
                                }
                            });
                        }
                    }
                }
            ]
        });

        var height = document.body.clientHeight;
        return this.app.getDesktop().createWindow({
            id: this.id,
            title: '退款管理',
            height: height * 0.85,
            width: 900,
            layout: 'fit',
            items: [financeGrid]

        });
    }
});