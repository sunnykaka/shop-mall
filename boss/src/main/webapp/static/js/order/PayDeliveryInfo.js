PayDeliveryInfo = function (orderId, edit) {
    var store = new Ext.data.JsonStore({
        url: 'order/pay/delivery/' + orderId,
        root: 'data.result',
        fields: [ 'payType', 'deliveryType', 'payBank', 'totalPrice', 'orderState', 'waybillNumber']
    });

    store.load();

    function payTypeShow(value) {
        if (value == "OnLine") {
            return "在线付款";
        } else if (value == "OnDelivery_POS") {
            return "POS机刷卡";
        } else if (value == "OnDelivery_Cash") {
            return "现金付款";
        }
    }
    var fm = Ext.form;
    this.cm = new Ext.grid.ColumnModel({
        columns: [
            {
                header: "支付方式",
                width: 120,
                sortable: true,
                dataIndex: 'payType',
                renderer: payTypeShow
            },
            {
                header: '支付银行',
                dataIndex: 'payBank',
                width: 130
            },
            {
                header: '送货方式',
                id: 'deliveryType',
                dataIndex: 'deliveryType',
                renderer: EJS.companyRender,
                editor: {
                    xtype: 'combo',
                    mode: 'local',
                    editable: false,
                    triggerAction: 'all',
                    emptyText: '请选择',
                    store: new Ext.data.ArrayStore({
                        fields: [ 'type', 'desc' ],
                        data: EJS.companyData
                    }),
                    displayField: 'desc',
                    valueField: 'type',
                    allowBlank: false
                },
                width: 130
            },
            {
                header: '物流编号',
                id: 'waybillNumber',
                dataIndex: 'waybillNumber',
                width: 130,
                sortable: true,
                editor: new fm.TextField({
                allowBlank: false
                })
            },
            {
                header: "总金额",
                width: 100,
                sortable: true,
                dataIndex: 'totalPrice'
            }
        ]
    });

    if (edit) {
        var controlFashionGrid = new Ext.grid.EditorGridPanel({
            border: false,
            cm: this.cm,
            height: 400,
            iconCls: 'edit',
            title: '支付配送',
            store: store,
            loadMask: true,
            viewConfig: {forceFit: true},
            tbar: [
                {
                    iconCls: 'refresh',
                    text: '刷新',
                    handler: function () {
                        store.reload();
                    }
                }
            ]
        });

        controlFashionGrid.on("afteredit", function (e) {
            var record = e.record;

            var field = e.field;

            if (field == "deliveryType") {
                doAjax('order/delivery/update',function(){
                    store.reload();
                    Ext.Msg.alert('成功', "更新成功");
                },'orderId=' + orderId + '&deliveryType=' + e.value);
            }

            if (field == "waybillNumber") {
                doAjax('order/waybillNumber/update',function(){
                    store.reload();
                    Ext.Msg.alert('成功', "更新成功");
                },'orderId=' + orderId + '&waybillNumber=' + e.value);
            }

        }, this);

        return controlFashionGrid;
    } else {
        var controlFashionGrid = new Ext.grid.GridPanel({
            border: false,
            cm: this.cm,
            height: 400,
            store: store,
            title: '支付配送',
            loadMask: true,
            viewConfig: {forceFit: true},
            tbar: [
                {
                    iconCls: 'refresh',
                    text: '刷新',
                    handler: function () {
                        store.reload();
                    }
                }
            ]
        });
        return controlFashionGrid;
    }
};
