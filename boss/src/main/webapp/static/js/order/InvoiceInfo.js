InvoiceInfo = function (orderId, edit) {

    var store = createJsonStore('order/invoice/' + orderId, [ 'invoiceType', 'invoiceTitle', 'companyName', 'invoiceContent', 'orderState']);

    store.load();

    var fm = Ext.form;

    this.cm = new Ext.grid.ColumnModel({
        columns: [
            {header: "发票类型", width: 100, dataIndex: 'invoiceType', editor: new fm.TextField({
                allowBlank: false,
                maxLength: 20
            })},
            {header: "发票抬头", width: 100, dataIndex: 'invoiceTitle', editor: new fm.TextField({
                allowBlank: false,
                maxLength: 100
            })},
            {header: "单位名称", width: 100, dataIndex: 'companyName', editor: new fm.TextField({
                allowBlank: false,
                maxLength: 100
            })},
            {header: "发票内容", width: 150, dataIndex: 'invoiceContent', id: 'invoiceContent', editor: new fm.TextField({
                allowBlank: false,
                maxLength: 20
            })}
        ]
    });

    if (edit) {
        var invoiceInfoGrid = new Ext.grid.EditorGridPanel({
            border: false,
            cm: this.cm,
            store: store,
            iconCls: 'edit',
            loadMask: true,
            title: '发票信息',
            viewConfig: {forceFit: true},
            height: 400,
            clicksToEdit: 1,
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
        invoiceInfoGrid.on("afteredit", function (e) {
            var rec = e.record;

            var invoiceType = rec.get('invoiceType');
            var invoiceTitle = rec.get('invoiceTitle');
            var companyName = rec.get('companyName');
            var invoiceContent = rec.get('invoiceContent');

            doAjax('order/update/invoiceInfo', function () {
                store.reload();
                Ext.Msg.alert('成功', "更新成功");
            }, {
                orderId: orderId,
                invoiceType: invoiceType,
                invoiceTitle: invoiceTitle,
                companyName: companyName,
                invoiceContent: invoiceContent
            });

        }, this);
        return invoiceInfoGrid;
    } else {
        var invoiceInfoGrid = new Ext.grid.GridPanel({
            border: false,
            cm: this.cm,
            store: store,
            loadMask: true,
            title: '发票信息',
            viewConfig: {forceFit: true},
            height: 400,
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
        return invoiceInfoGrid;
    }
};
