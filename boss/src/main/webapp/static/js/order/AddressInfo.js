AddressInfo = function (orderId, edit) {

    var store = createJsonStore('order/addressInfo/' + orderId, [ 'consignee', 'mobile', 'province', 'city', 'districts', 'location', 'orderState', 'zipCode']);

    store.load();

    var fm = Ext.form;
    this.cm = new Ext.grid.ColumnModel({
        columns: [
            {header: "收货人", width: 100, sortable: true, dataIndex: 'consignee', editor: new fm.TextField({
                allowBlank: false,
                maxLength: 20
            })},
            {header: "手机号码", width: 100, sortable: true, dataIndex: 'mobile', editor: new fm.TextField({
                allowBlank: false,
                maxLength: 20
            })},
            {header: "省份地址", width: 80, sortable: true, dataIndex: 'province', id: 'province', editor: new fm.TextField({
                allowBlank: false
            })},
            {header: "城市地址", width: 80, sortable: true, dataIndex: 'city', id: 'city', editor: new fm.TextField({
                allowBlank: false
            })},
            {header: "市区地址", width: 80, sortable: true, dataIndex: 'districts', id: 'districts', editor: new fm.TextField({
                allowBlank: false
            })},
            {header: "邮政编码", width: 80, sortable: true, dataIndex: 'zipCode', id: 'zipCode', editor: new fm.TextField({
                allowBlank: false
            })},
            {header: "街道地址", width: 180, sortable: true, dataIndex: 'location', id: 'location', editor: new fm.TextField({
                allowBlank: false
            })}
        ]
    });

    if (edit) {
        var consigneeInfoGrid = new Ext.grid.EditorGridPanel({
            border: false,
            store: store,
            cm: this.cm,
            iconCls: 'edit',
            title: '收货信息',
            viewConfig: {forceFit: true},
            loadMask: true,
            height: 400,
            tbar: [
                {
                    iconCls: 'refresh',
                    text: '刷新',
                    handler: function () {
                        store.reload();
                    }
                }
            ],
            clicksToEdit: 1
        });
        consigneeInfoGrid.on("afteredit", function (e) {
            var record = e.record;

            var field = e.field;

            var changeParam = '&' + field + '=' + e.value;

            //如果省市区有改动，三个字段一起发送到后台
            if (field == "province") {
                changeParam = '&province=' + e.value + "&city=" + record.get("city") + "&districts=" + record.get("districts")
            }
            if (field == "city") {
                changeParam = '&city=' + e.value + "&province=" + record.get("province") + "&districts=" + record.get("districts")
            }
            if (field == "districts") {
                changeParam = '&districts=' + e.value + "&province=" + record.get("province") + "&city=" + record.get("city")
            }

            doAjax('order/update/addressInfo', function () {
                store.reload();
                Ext.Msg.alert('成功', "更新成功");
            }, 'orderId=' + orderId + changeParam);

        }, this);
        return consigneeInfoGrid;
    } else {
        var consigneeInfoGrid = new Ext.grid.GridPanel({
            border: false,
            store: store,
            cm: this.cm,
            title: '收货信息',
            viewConfig: {forceFit: true},
            loadMask: true,
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
        return consigneeInfoGrid;
    }

};
