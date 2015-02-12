
Lottery =  function (rotaryId) {
    var lotteryStore = getStore('/rotary/lottery/' + rotaryId, Ext.Model.Lottery);

    lotteryStore.load({
        params: {
            start: 0,
            limit: 13
        }
    });

    function isSend(value, p, record) {
        if (!record.get('really') || record.get('meedType') != 'Product') return '<span style=\"color:#008000;\">勿需发货</span>';

        return value ? "已发货" : "<span style=\"color:#ff0000;\">未发货</span>";
    }

    function meedInfo(value, p, record) {
        if (record.get('really') && record.get('meedType') == 'Product')
            return String.format('<a href="{0}/product/{1}" target="_blank">{2}</a>', window.BuyHome, record.get('meedValue'), value);
        return value;
    }

    this.cm = new Ext.grid.ColumnModel({
        columns: [
            { header: 'id', sortable: true, dataIndex: 'id', width: 65 },
            { header: '奖品项id', sortable: true, dataIndex: 'rotaryMeedId', width: 65 },
            { header: '中奖人', sortable: true, dataIndex: 'userName', width: 65 },
            {
                header: '奖品类型',
                sortable: true,
                dataIndex: 'meedType',
                width: 70,
                renderer:function(value) {
                    if (value == 'Null') return "无";
                    if (value == 'Product') return "商品";
                    if (value == 'Coupon') return "现金券";
                    return "积分";
                }
            },
            { header: '奖品值', sortable: true, dataIndex: 'value', width: 120, renderer: meedInfo },
            { header: '中奖时间', sortable: true, dataIndex: 'create', width: 200 },
            { header: '已否已发货', sortable: true, dataIndex: 'sendOut', width: 80, renderer: isSend },
            { header: '收货人名字', sortable: true, dataIndex: 'consigneeName', width: 80, editor: new Ext.form.TextField() },
            { header: '收货人电话', sortable: true, dataIndex: 'consigneePhone', width: 100, editor: new Ext.form.TextField() },
            { header: '收货人地址', sortable: true, dataIndex: 'consigneeAddress', width: 150, editor: new Ext.form.TextField() },
            { header: '是否真实', sortable: true, dataIndex: 'really', width: 60, renderer: function(value) {return value ? "Yes" : "No";} }
        ]
    });

    Lottery.superclass.constructor.call(this, {
        title: '中奖数据',
        border: false,
        store: lotteryStore,
        autoScroll: true,
        height: 338,
        loadMask: true,
        // 双击编辑
        clicksToEdit: 2,
        bbar: new Ext.PagingToolbar({
            pageSize: 13,
            store: lotteryStore,
            displayInfo: true
        }),
        tbar: [
            {
                text: '刷新',
                iconCls: 'refresh',
                handler: function () {
                    lotteryStore.reload();
                }
            },
            '->',
            {
                xtype: 'textfield',
                name: 'userName',
                emptyText: '中奖人',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        lotteryStore.baseParams = {
                            start: 0,
                            limit: 11,
                            userName: field.getValue()
                        };

                        lotteryStore.load({
                            params: {
                                start: 0,
                                limit: 11,
                                userName: field.getValue()
                            }
                        });
                    }
                }
            },
            {
                xtype: 'combo',
                name: 'needSend',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                emptyText: '是否需要发货',
                store: new Ext.data.ArrayStore({
                    fields: [ 'type', 'value' ],
                    data: [
                        [ '是否需要发货', '' ],
                        [ '需要', 'true' ],
                        [ '不需要', 'false' ]
                    ]
                }),
                displayField: 'type',
                valueField: 'value',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    select: function (field) {
                        lotteryStore.baseParams = {
                            start: 0,
                            limit: 11,
                            needSend: field.getValue()
                        };

                        lotteryStore.load({
                            params: {
                                start: 0,
                                limit: 11,
                                needSend: field.getValue()
                            }
                        });
                    }
                }
            },
            {
                xtype: 'combo',
                name: 'sendOut',
                mode: 'local',
                editable: false,
                triggerAction: 'all',
                emptyText: '是否已发货',
                store: new Ext.data.ArrayStore({
                    fields: [ 'type', 'value' ],
                    data: [
                        [ '是否已发货', '' ],
                        [ '已发货', 'true' ],
                        [ '未发货', 'false' ]
                    ]
                }),
                displayField: 'type',
                valueField: 'value',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    select: function (field) {
                        lotteryStore.baseParams = {
                            start: 0,
                            limit: 11,
                            sendOut: field.getValue()
                        };

                        lotteryStore.load({
                            params: {
                                start: 0,
                                limit: 11,
                                sendOut: field.getValue()
                            }
                        });
                    }
                }
            }
        ],
        cm: this.cm,
        listeners: {
            // 右键
            'rowcontextmenu' : function (grid, rowIndex, event) {
                event.preventDefault();// 取消默认的浏览器右键事件
                var record = grid.getStore().getAt(rowIndex);
                var menu = new Ext.menu.Menu({
                    items: [
                        {
                            text: '置为发货',
                            handler: function () {
                                if (record.get('really') && record.get('meedType') == 'Product' && !record.get('sendOut')) {
                                    doAjax('rotary/lottery/sendOut', function () { lotteryStore.reload(); }, { ids: record.get('id') }, "你确定要置为发货吗");
                                    return;
                                }
                                Ext.Msg.alert("说明", "此中奖数据不需要发货!");
                            }
                        }
                    ]
                });
                menu.showAt(event.getXY());
            },
            'afteredit' : function (e) {
                var record = e.record;
                var field = e.field;
                if (e.value.trim() == '') return;
                if (!record.get('really') || record.get('meedType') != 'Product') {
                    Ext.Msg.alert("信息", "当前行不需要发货");
                    lotteryStore.reload();
                    return;
                }
                if (record.get('sendOut')) {
                    Ext.Msg.alert("信息", "当前行已经发货了");
                    return;
                }

                var url = '/rotary/lottery/update';
                if (field == "consigneeName") {
                    doAjax(url, function() {
                        Ext.Msg.alert('成功', "更新成功");
                        lotteryStore.reload();
                    }, 'id=' + record.get('id') + '&consigneeName=' + e.value);
                }
                if (field == "consigneePhone") {
                    doAjax(url, function() {
                        Ext.Msg.alert('成功', "更新成功");
                        lotteryStore.reload();
                    }, 'id=' + record.get('id') + '&consigneePhone=' + e.value);
                }
                if (field == "consigneeAddress") {
                    doAjax(url, function() {
                        Ext.Msg.alert('成功', "更新成功");
                        lotteryStore.reload();
                    }, 'id=' + record.get('id') + '&consigneeAddress=' + e.value);
                }
            }
        }
    });
}

Ext.extend(Lottery, Ext.grid.EditorGridPanel);
