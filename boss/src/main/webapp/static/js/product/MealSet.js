/**
 * 商品套餐
 * @type {*}
 */

Desktop.MealSetWindow = Ext.extend(Ext.app.Module, {

    id: 'MealSet-win',

    title: '套餐搭配',

    createMealSetGrid: function () {
        var store = createJsonStore('product/mealSet/list', [
            {
                name: 'id',
                type: 'int'
            },
            {
                name: 'name',
                type: 'string'
            },{
                name:'recommendReason',
                type:'string'
            }
        ]);

        store.load();

        var grid = new Ext.grid.GridPanel({
            id: 'mealSetGird',
            store: store,
            region: 'center',
            loadMask: true,
            tbar: [
                {
                    text: '新建套餐',
                    iconCls: 'add',
                    handler: function () {

                        var form = buildForm('product/mealSet/add', [
                            {
                                text: '提交',
                                handler: function () {
                                    commitForm(form, function () {
                                        form.ownerCt.close();
                                        grid.getStore().reload();
                                    })
                                }
                            }
                        ], [
                            {
                                fieldLabel: '套餐名',
                                name: 'name',
                                allowBlank: false
                            },{
                                fieldLabel: '推荐理由',
                                name: 'recommendReason',
                                xtype:'textarea'
                            }
                        ]);

                        buildWin('新建套餐', 400, form).show(this.id)

                    }
                },
                {
                    text: '选择商品',
                    iconCls: 'run',
                    handler: function () {

                        var selModel = grid.getSelectionModel();
                        var mealSet = selModel.getSelected();
                        if (!mealSet) {
                            Ext.Msg.alert("错误", "请选择套餐");
                            return;
                        }


                        var productSelector = buildProductSelector();

                        var win = new Ext.Window({
                            title: '选择套餐商品',
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
                                        text: '加入套餐',
                                        handler: function () {
                                            if (!record.get('online')) {
                                                Ext.Msg.alert("错误", "此商品还没有上架");
                                                return;
                                            }

                                            doAjax("product/skuList/" + productId, function (obj) {

                                                var form = buildForm('product/mealSet/assign/sku', [
                                                    {
                                                        text: '提交',
                                                        handler: function () {
                                                            commitForm(form, function () {
                                                                form.ownerCt.close();
                                                                Ext.Msg.alert("成功", "加入成功，点击套餐即可查看");
                                                            })
                                                        }
                                                    }
                                                ], [
                                                    {
                                                        xtype: 'hidden',
                                                        name: 'mealId',
                                                        value: mealSet.get('id')
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
                                                        fieldLabel: '价格',
                                                        name: 'skuPrice',
                                                        allowBlank: false
                                                    },
                                                    {
                                                        fieldLabel: '数量',
                                                        name: 'number',
                                                        xtype: 'numberfield',
                                                        allowBlank: false
                                                    }
                                                ]);

                                                buildWin('新建套餐', 400, form).show(this.id)
                                            });


                                        }
                                    }
                                ]
                            });
                            menu.showAt(e.getXY());
                        });
                    }
                },
                new Ext.ux.CopyButton({
                    text: '复制链接',
                    iconCls: 'config',
                    getValue: function () {
                        var sm = grid.getSelectionModel();
                        if (!sm.hasSelection()) {
                            Ext.Msg.alert('错误', '请选择套餐');
                            return;
                        } else if (sm.getSelections().length > 1) {
                            Ext.Msg.alert('错误', '请只选择一个套餐');
                            return;
                        }

                        var record = sm.getSelected();

                        return window.BuyHome + '/product/meal/' + record.get('id');
                    }
                }),
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        grid.getStore().reload();
                    }
                },
                {
                    text: '删除',
                    iconCls: 'delete',
                    handler: function () {
                        doGridRowDelete(grid, 'product/mealSet/delete', function () {
                            grid.getStore().reload()
                            Ext.getCmp("mealSetSkuGird").getStore().reload();
                        })
                    }
                }
            ],
            viewConfig: {
                forceFit: true
            },
            columns: [
                {
                    header: 'ID',
                    dataIndex: 'id'
                },
                {
                    header: '套餐名',
                    dataIndex: 'name'
                }
            ]
        });

        grid.on('rowclick', function (grid, rowIndex, event) {

            var record = grid.getStore().getAt(rowIndex);

            var store = Ext.getCmp('mealSetSkuGird').getStore();

            store.proxy = new Ext.data.HttpProxy({
                url: 'product/mealSet/sku/list/' + record.get('id'),
                failure: function (response, opts) {
                    if (response.status == 403) {
                        Ext.Msg.alert('错误', '没有设置访问权限');
                        return;
                    }
                    Ext.Msg.alert('错误', '服务器出错');
                }
            });

            store.reload();
            Ext.getCmp('mealSetSkuGird').show();
            Ext.getCmp('MealSet-win').setHeight(document.body.clientHeight*0.85);
            Ext.getCmp('MealSet-win').doLayout();

        });

        grid.on('rowdblclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            var form = buildForm('product/mealSet/update', [
                {
                    text: '提交',
                    handler: function () {
                        commitForm(form, function () {
                            form.ownerCt.close();
                            grid.getStore().reload();
                        })
                    }
                }
            ], [
                {
                    xtype: 'hidden',
                    name: 'id',
                    value: record.get('id')
                },
                {
                    fieldLabel: '套餐名',
                    name: 'name',
                    value: record.get('name'),
                    allowBlank: false
                },{
                    fieldLabel: '推荐理由',
                    name: 'recommendReason',
                    xtype:'textarea',
                    value: record.get('recommendReason')
                }
            ]);

            buildWin('修改套餐', 400, form).show(this.id)

        });

        return grid;
    },

    createSkuList: function () {

        var store = createJsonStore('product/mealSet/sku/list/-1', [
            {
                name: 'sku',
                type: 'string'
            },
            {
                name: 'productName',
                type: 'string'
            },
            {
                name: 'skuId',
                type: 'long'
            },
            {
                name: 'price',
                type: 'string'
            },
            {
                name: 'marketPrice',
                type: 'string'
            },
            {
                name: 'mealPrice',
                type: 'string'
            },
            {
                name: 'skuLocation',
                type: 'string'
            },
            {
                name: 'stockQuantity',
                type: 'int'
            },
            {
                name: 'mealNumber',
                type: 'int'
            },
            {
                name: 'id',
                type: 'int'
            }
        ]);

        store.load();

        var grid = new Ext.grid.GridPanel({
            id: 'mealSetSkuGird',
            store: store,
            loadMask: true,
            viewConfig: {forceFit: true},
            columns: [
                { header: 'skuID', sortable: true, dataIndex: 'skuId', width: 60 },
                { header: '商品', sortable: true, dataIndex: 'productName', width: 100 },
                { header: 'SKU', sortable: true, dataIndex: 'sku', width: 180 },
                { header: '市场价', sortable: true, dataIndex: 'marketPrice', width: 75 },
                { header: '易居价', sortable: true, dataIndex: 'price', width: 75 },
                { header: '套餐价', sortable: true, dataIndex: 'mealPrice', width: 75 },
                { header: '库存', sortable: true, dataIndex: 'stockQuantity', width: 60 },
                { header: '套餐数量', sortable: true, dataIndex: 'mealNumber', width: 60 },
                { header: '位置', sortable: true, dataIndex: 'skuLocation', width: 130 }
            ],
            tbar: [
                {
                    text: '刷新',
                    iconCls: 'refresh',
                    handler: function () {
                        grid.getStore().reload();
                    }
                },
                {
                    text: '删除',
                    iconCls: 'delete',
                    handler: function () {
                        doGridRowDelete(grid, 'product/mealItem/delete', function () {
                            grid.getStore().reload()
                        })
                    }
                }
            ],
            region: 'south',
            hidden:true,
            height: 300
        });

        return grid;

    },

    createWindow: function () {
        return this.app.getDesktop().createWindow({
            id: this.id,
            title: this.title,
            pageX:document.body.clientWidth*0.3,
            pageY:document.body.clientHeight*0.1,
            height: document.body.clientHeight * 0.6,
            width: 750,
            layout: 'border',
            items: [this.createMealSetGrid(), this.createSkuList()]
        });
    }
})