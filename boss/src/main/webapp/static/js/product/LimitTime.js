Desktop.LimitTimeWindow = Ext.extend(Ext.app.Module, {

    id: 'LimitTime-win',

    title: '限时折扣',


    createLimitTimeGrid: function () {

        var store = getStore("product/limitTime/discount", Ext.Model.LimitedTimeDiscount);

        Ext.apply(Ext.form.VTypes, {
            daterange: function (val, field) {
                var date = field.parseDate(val);

                if (!date) {
                    return false;
                }
                if (field.startDateField) {
                    var start = Ext.getCmp(field.startDateField);
                    if (!start.maxValue || (date.getTime() != start.maxValue.getTime())) {
                        start.setMaxValue(date);
                        start.validate();
                    }
                }
                else if (field.endDateField) {
                    var end = Ext.getCmp(field.endDateField);
                    if (!end.minValue || (date.getTime() != end.minValue.getTime())) {
                        end.setMinValue(date);
                        end.validate();
                    }
                }
                return true;
            }
        });

        function linkSku(value, p, record){
            return String.format('<a href="{0}/product/{1}" target="_blank">{2}</a>', window.BuyHome, record.get('productId'), value);
        }
        var grid = new Ext.grid.GridPanel({
                id: 'limitTimeGrid',
                loadMask: true,
                store: store,
                region: 'center',
                columns: [
                    { header: '商品ID', width: 33, dataIndex: 'productId' },
                    { header: "商品名", dataIndex: 'productName',name:'productName',renderer: linkSku },
                    { header: '折扣类型', width: 40, dataIndex: 'discountType' },
                    { header: '折扣值', width: 50, dataIndex: 'discount' },
                    { header: '开始时间', width: 80, dataIndex: 'startDate' },
                    { header: '结束时间', width: 80, dataIndex: 'endDate' }
                ],
                viewConfig: {
                    forceFit: true
                },

                tbar: [
                    {
                        text: '刷新',
                        iconCls: 'refresh',
                        handler: function () {
                            store.reload();
                        }
                    },
                    {
                        text: '选择商品',
                        iconCls: 'run',
                        handler: function () {

                            var productSelector = buildProductSelector();

                            var win = new Ext.Window({
                                title: '选择折扣商品',
                                width: 450,
                                height: 700,
                                layout: 'fit',
                                plain: true,
                                items: productSelector
                            });

                            var today = new Date();
                            var year = today.getFullYear();
                            var data = today.getDate();
                            var month = today.getMonth() + 1;
                            var now = year + '年' + month + '月' + data + '日 00时00分01秒';

                            today.setTime(today.getTime() + 24 * 60 * 60 * 1000);
                            year = today.getFullYear();
                            data = today.getDate();
                            month = today.getMonth() + 1;
                            var time = year + '年' + month + '月' + data + '日 23时59分59秒';

                            productGrid.on('rowcontextmenu', function (grid, rowIndex, e) {
                                e.preventDefault();//取消默认的浏览器右键事件
                                var record = grid.getStore().getAt(rowIndex);
                                var productId = record.get('id');
                                var menu = new Ext.menu.Menu({
                                    items: [
                                        {
                                            text: '设置折扣',
                                            handler: function () {
                                                if (!record.get('online')) {
                                                    Ext.Msg.alert("错误", "此商品还没有上架");
                                                    return;
                                                }

                                                buildWin('设置商品折扣', 500, buildForm('product/limitTime/discount/add', [
                                                    {
                                                        text:'保存',
                                                        handler:function (button) {
                                                            var form = button.ownerCt.ownerCt;
                                                            if (!form.getForm().isValid()) {
                                                                return;
                                                            }
                                                            form.getForm().submit({
                                                                waitTitle:'进度',
                                                                // 动作发生期间显示的文本信息
                                                                waitMsg:'正在提交数据, 请稍候...',
                                                                // 表单提交方式
                                                                method:'post',
                                                                // 数据验证通过时发生的动作
                                                                success:function (form, action) {
                                                                    Ext.getCmp('limitTimeGrid').getStore().reload();
                                                                    button.ownerCt.ownerCt.ownerCt.close();
                                                                    var result = Ext.util.JSON.decode(action.response.responseText);
                                                                    if (result.msg != "") {
                                                                        Ext.Msg.alert(result.success ? '成功' : '错误', result.msg);
                                                                    }
                                                                },
                                                                failure:function (form, action) {
                                                                    if (action.response.status == 200) {
                                                                        var result = Ext.util.JSON.decode(action.response.responseText);
                                                                        Ext.Msg.alert('错误', result.msg);
                                                                        return;
                                                                    }
                                                                    Ext.Msg.alert('错误', '服务器出错，错误码:' + action.response.status);
                                                                }
                                                            });
                                                        }
                                                    },
                                                    {
                                                        text: '取消',
                                                        handler: function () {
                                                            win.close();
                                                        }
                                                    }
                                                ], [
                                                    {
                                                        xtype: 'hidden',
                                                        name: 'productId',
                                                        value: productId
                                                    },
                                                    {
                                                        xtype: 'combo',
                                                        hiddenName: 'discountType',
                                                        fieldLabel: '折扣类型',
                                                        mode: 'local',
                                                        editable: false,
                                                        triggerAction: 'all',
                                                        emptyText: '请选择',
                                                        store: new Ext.data.ArrayStore({
                                                            fields: [ 'type', 'value' ],
                                                            data: [
                                                                [ '比例(若价格是100元,设置成40%,则商品价格是40元)', 'Ratio' ],
                                                                [ '金额(若价格是100元,设置成40,则商品价格是60元)', 'Money' ]
                                                            ]
                                                        }),
                                                        displayField: 'type',
                                                        valueField: 'value',
                                                        allowBlank: false
                                                    },
                                                    {
                                                        xtype: 'numberfield',
                                                        name: 'discount',
                                                        fieldLabel: '折扣数值',
                                                        allowBlank: false
                                                    },
                                                    {
                                                        fieldLabel: '开始时间',
                                                        name: 'start',
                                                        value: now, // '2013年11月01日 00时00分01秒'
                                                        allowBlank: false
                                                    },
                                                    {
                                                        fieldLabel: '结束时间',
                                                        name: 'end',
                                                        value: time, // '2013年11月10日 23时59分59秒'
                                                        allowBlank: false
                                                    }
                                                ])).show(this.id);
                                            }
                                        }
                                    ]
                                });
                                menu.showAt(e.getXY());
                            });
                            win.show();
                        }
                    },
                    {
                        iconCls: 'remove',
                        text: '删除',
                        handler: function () {
                            doGridRowDelete(grid, 'product/limitTime/discount/delete', function () {
                                grid.getStore().reload();
                            });
                        }
                    },
                    '->',
                    {
                        xtype: 'textfield',
                        name: 'productId',
                        emptyText: '商品Id(完全匹配)',
                        width: 120,
                        enableKeyEvents: true,
                        listeners: {
                            keyup: function (field) {
                                store.load({
                                    params: {
                                        start: 0,
                                        limit: 18,
                                        productId: field.getValue()
                                    }
                                });
                            }
                        }
                    }
                ],
                bbar: new Ext.PagingToolbar({
                    pageSize: 20,
                    store: store,
                    displayInfo: true
                })
            }
        );

        return grid;
    },

    createWindow: function () {
        return this.app.getDesktop().createWindow({
            id: this.id,
            title: this.title,
            border: false,
            height: document.body.clientHeight * 0.85,
            width: 750,
            layout: 'border',
            items: [this.createLimitTimeGrid()]
        });
    }

});
