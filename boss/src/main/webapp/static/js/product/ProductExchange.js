
Desktop.ExchangeWindow = Ext.extend(Ext.app.Module, {

    id:'Exchange-win',
    title:'积分换购',

    productIntegralConversionGrid:function () {
        var conversionTbarColumns = [];
        conversionTbarColumns.push(
            { header:'id', dataIndex:'id', width:15 },
            { header:'商品id', dataIndex:'productId', width:25 },
            { header:'skuId', dataIndex:'skuId', width:25 },
            /*{ header:'积分数', dataIndex:'integralCount', width:35 },*/
            { header:'积分数', dataIndex:'currency', width:35 },
            { header:'用户购买次数', dataIndex:'userBuyCount', width:35 },
            { header:'开始时间', dataIndex:'start', width:75 },
            { header:'结束时间', dataIndex:'end', width:75 },
            { header:'假销售', dataIndex:'mockSale', width:25 }
        );

        var conversionStore = getStore("product/conversion", Ext.Model.ProductIntegralConversion);

        var conversionTbar = [];
        conversionTbar.push(
            {
                text:'刷新',
                iconCls:'refresh',
                handler:function () {
                    conversionStoreGrid.getStore().reload();
                }
            },
            {
                text: '选择商品',
                iconCls: 'run',
                handler: function () {
                    var productSelector = buildProductSelector();
                    var win = new Ext.Window({
                        title: '选择积分兑换商品',
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

                    today.setTime(today.getTime() + 30 * 24 * 60 * 60 * 1000);
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
                                    text: '设置兑换',
                                    handler: function () {
                                        if (!record.get('online')) {
                                            Ext.Msg.alert("错误", "此商品还没有上架");
                                            return;
                                        }

                                        //取加sku列表
                                        var skuUrl = "product/skuWithStock/list/" + productId;
                                        var skuStore = new Ext.data.Store({
                                            reader: new Ext.data.JsonReader({
                                                fields: [ 'skuId', 'sku', 'price', 'marketPrice', 'skuLocation', 'stockQuantity', 'validStatus' ],
                                                root: 'data.result'
                                            }),
                                            proxy: new Ext.data.HttpProxy({
                                                url: skuUrl,
                                                method: 'GET'
                                            })
                                        });
                                        skuStore.load();
                                        var skuCombo = {
                                            xtype: 'combo',
                                            hiddenName: 'skuId',
                                            fieldLabel: '选择sku',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            emptyText: '请选择',
                                            store: skuStore,
                                            displayField: 'sku',
                                            valueField: 'skuId',
                                            allowBlank: false
                                        };
                                        var form  = buildForm('product/conversion/add', [
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
                                                            Ext.getCmp('conversionStoreGrid').getStore().reload();
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
                                                xtype: 'numberfield',
                                                name: 'integralCount',
                                                fieldLabel: '积分数',
                                                allowBlank: false
                                            },
                                            {
                                                xtype: 'numberfield',
                                                fieldLabel:'用户购买次数',
                                                name:'userBuyCount',
                                                allowBlank:false
                                            },
                                            {
                                                fieldLabel:'生效时间',
                                                name:'start',
                                                allowBlank:false,
                                                value:now
                                            },
                                            {
                                                fieldLabel:'过期时间',
                                                name:'end',
                                                allowBlank:false,
                                                value:time
                                            },
                                            {
                                                xtype:'numberfield',
                                                regex:/^[+]?[\d]+$/,
                                                regexText:"只能输入正整数",
                                                name: 'mockSale',
                                                fieldLabel: '假销售',
                                                allowBlank: false
                                            }
                                        ]);
                                        form.add(skuCombo);

                                        buildWin('设置积分兑换商品', 500, form).show(this.id);
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
                text:'删除',
                iconCls:'remove',
                handler:function () {
                    doGridRowDelete(conversionStoreGrid, 'product/conversion/delete', function () {
                        conversionStoreGrid.getStore().reload();
                    });
                }
            },
            '->',
            {
                xtype: 'textfield',
                name: 'productId',
                emptyText: '商品id',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        conversionStore.load({
                            params: {
                                start: 0,
                                limit: 13,
                                productId: field.getValue()
                            }
                        });
                    }
                }
            }
        );

        var conversionStoreGrid = new Ext.grid.GridPanel({
            id:'conversionStoreGrid',
            title:'积分兑换',
            loadMask:true,
            store:conversionStore,
            columns:conversionTbarColumns,
            viewConfig:{
                forceFit:true
            },
            tbar:conversionTbar,
            bbar:new Ext.PagingToolbar({
                pageSize:13,
                store:conversionStore,
                displayInfo:true
            })
        });

        // 双击
        conversionStoreGrid.on('rowdblclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            var form = new Ext.FormPanel({
                baseCls:'x-plain',
                labelWidth:80,
                url:'product/conversion/update',
                buttonAlign:'center',
                autoHeight:true,
                defaults:{
                    anchor:'100%',
                    xtype:'textfield'
                },
                buttons:[{
                    text: '提交',
                    handler: function () {
                        commitForm(form, function () {
                            form.ownerCt.close();
                            grid.getStore().reload();
                        })
                    }
                }],
                items:[
                    {
                        xtype: 'hidden',
                        name: 'id',
                        value: record.get('id')
                    },
                    {
                        xtype: 'hidden',
                        name: 'productId',
                        value: record.get('productId')
                    },
                    {
                        xtype: 'hidden',
                        name: 'skuId',
                        value: record.get('skuId')
                    },
                    {
                        xtype: 'numberfield',
                        name: 'integralCount',
                        fieldLabel: '积分数',
                        value: record.get('currency'),
                        allowBlank: false
                    },
                    {
                        xtype: 'numberfield',
                        fieldLabel:'用户购买次数',
                        name:'userBuyCount',
                        value: record.get('userBuyCount'),
                        allowBlank:false
                    },
                    {
                        fieldLabel:'生效时间',
                        name:'start',
                        allowBlank:false,
                        value: record.get('start')
                    },
                    {
                        fieldLabel:'过期时间',
                        name:'end',
                        allowBlank:false,
                        value: record.get('end')
                    },
                    {
                        xtype: 'numberfield',
                        name: 'mockSale',
                        regex:/^[+]?[\d]+$/,
                        regexText:"只能输入正整数",
                        fieldLabel: '假销售',
                        value: record.get('mockSale'),
                        allowBlank: false
                    }
                ]});

            buildWin('修改积分兑换', 400, form).show(this.id);
        });

        return conversionStoreGrid;
    },

    productSuperConversionGrid:function () {
        var conversionColumns = [];
        conversionColumns.push(
            { header:'id', dataIndex:'id', width:15 },
            { header:'商品id', dataIndex:'productId', width:25 },
            { header:'skuId', dataIndex:'skuId', width:25 },
            { header:'积分数', dataIndex:'currency', width:35 },
            { header:'支付金额', dataIndex:'moneyForPrice', width:35 },
            { header:'用户购买次数', dataIndex:'userBuyCount', width:35 },
            { header:'开始时间', dataIndex:'start', width:75 },
            { header:'结束时间', dataIndex:'end', width:75 },
            { header:'假销售', dataIndex:'mockSale', width:25 }
        );

        var conversionStore = getStore("product/superconversion", Ext.Model.ProductSuperConversion);

        var conversionTbar = [];
        conversionTbar.push(
            {
                text:'刷新',
                iconCls:'refresh',
                handler:function () {
                    conversionGrid.getStore().reload();
                }
            },
            {
                text: '选择商品',
                iconCls: 'run',
                handler: function () {
                    var productSelector = buildProductSelector();
                    var win = new Ext.Window({
                        title: '设置积分优惠购',
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

                    today.setTime(today.getTime() + 30 * 24 * 60 * 60 * 1000);
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
                                    text: '设置积分优惠购',
                                    handler: function () {
                                        if (!record.get('online')) {
                                            Ext.Msg.alert("错误", "此商品还没有上架");
                                            return;
                                        }

                                        //取加sku列表
                                        var skuUrl = "product/skuWithStock/list/" + productId;
                                        var skuStore = new Ext.data.Store({
                                            reader: new Ext.data.JsonReader({
                                                fields: [ 'skuId', 'sku', 'price', 'marketPrice', 'skuLocation', 'stockQuantity', 'validStatus' ],
                                                root: 'data.result'
                                            }),
                                            proxy: new Ext.data.HttpProxy({
                                                url: skuUrl,
                                                method: 'GET'
                                            })
                                        });
                                        skuStore.load();
                                        var skuCombo = {
                                            xtype: 'combo',
                                            hiddenName: 'skuId',
                                            fieldLabel: '选择sku',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            emptyText: '请选择',
                                            store: skuStore,
                                            displayField: 'sku',
                                            valueField: 'skuId',
                                            allowBlank: false
                                        };
                                        var form = buildForm('product/superconversion/add', [
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
                                                            Ext.getCmp('conversionGrid').getStore().reload();
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
                                                xtype: 'numberfield',
                                                name: 'integralCount',
                                                fieldLabel: '积分数',
                                                allowBlank: false
                                            },
                                            {
                                                name: 'moneyForPrice',
                                                fieldLabel: '付款金额',
                                                allowBlank: false
                                            },
                                            {
                                                xtype: 'numberfield',
                                                fieldLabel:'用户购买次数',
                                                name:'userBuyCount',
                                                allowBlank:false
                                            },
                                            {
                                                fieldLabel:'生效时间',
                                                name:'start',
                                                allowBlank:false,
                                                value:now
                                            },
                                            {
                                                fieldLabel:'过期时间',
                                                name:'end',
                                                allowBlank:false,
                                                value:time
                                            },
                                            {
                                                xtype:'numberfield',
                                                regex:/^[+]?[\d]+$/,
                                                regexText:"只能输入正整数",
                                                name: 'mockSale',
                                                fieldLabel: '假销售',
                                                allowBlank: false
                                            }
                                        ]);
                                        form.add(skuCombo);

                                        buildWin('设置积分优惠购', 500, form).show(this.id);
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
                text:'删除',
                iconCls:'remove',
                handler:function () {
                    doGridRowDelete(conversionGrid, 'product/superconversion/delete', function () {
                        conversionGrid.getStore().reload();
                    });
                }
            },
            '->',
            {
                xtype: 'textfield',
                name: 'productId',
                emptyText: '商品id',
                width: 100,
                enableKeyEvents: true,
                listeners: {
                    keyup: function (field) {
                        conversionStore.load({
                            params: {
                                start: 0,
                                limit: 13,
                                productId: field.getValue()
                            }
                        });
                    }
                }
            }
        );

        var conversionGrid = new Ext.grid.GridPanel({
            id:'conversionGrid',
            title:'积分优惠购',
            loadMask:true,
            store:conversionStore,
            columns:conversionColumns,
            viewConfig:{
                forceFit:true
            },
            tbar:conversionTbar,
            bbar:new Ext.PagingToolbar({
                pageSize:13,
                store:conversionStore,
                displayInfo:true
            })
        });

        // 双击
        conversionGrid.on('rowdblclick', function (grid, rowIndex, event) {
            var record = grid.getStore().getAt(rowIndex);
            var form = new Ext.FormPanel({
                baseCls:'x-plain',
                labelWidth:80,
                url:'product/superconversion/update',
                buttonAlign:'center',
                autoHeight:true,
                defaults:{
                    anchor:'100%',
                    xtype:'textfield'
                },
                buttons:[{
                    text: '提交',
                    handler: function () {
                        commitForm(form, function () {
                            form.ownerCt.close();
                            grid.getStore().reload();
                        })
                    }
                }],
                items:[
                    {
                        xtype: 'hidden',
                        name: 'id',
                        value: record.get('id')
                    },
                    {
                        xtype: 'hidden',
                        name: 'productId',
                        value: record.get('productId')
                    },
                    {
                        xtype: 'hidden',
                        name: 'skuId',
                        value: record.get('skuId')
                    },
                    {
                        xtype: 'numberfield',
                        name: 'integralCount',
                        fieldLabel: '积分数',
                        value: record.get('currency'),
                        allowBlank: false
                    },
                    {
                        name: 'moneyForPrice',
                        fieldLabel: '付款金额',
                        value: record.get('moneyForPrice'),
                        allowBlank: false
                    },
                    {
                        xtype: 'numberfield',
                        fieldLabel:'用户购买次数',
                        name:'userBuyCount',
                        value: record.get('userBuyCount'),
                        allowBlank:false
                    },
                    {
                        fieldLabel:'生效时间',
                        name:'start',
                        allowBlank:false,
                        value: record.get('start')
                    },
                    {
                        fieldLabel:'过期时间',
                        name:'end',
                        allowBlank:false,
                        value: record.get('end')
                    },
                    {
                        xtype: 'numberfield',
                        regex:/^[+]?[\d]+$/,
                        regexText:"只能输入正整数",
                        name: 'mockSale',
                        fieldLabel: '假销售',
                        value: record.get('mockSale'),
                        allowBlank: false
                    }
                ]});

            buildWin('修改积分优惠购', 400, form).show(this.id);
        });

        return conversionGrid;
    },

    createWindow:function () {
        return this.app.getDesktop().createWindow({
            id:this.id,
            title:this.title,
            width:800,
            border:false,
            height:document.body.clientHeight * 0.85,
            layout:'border',
            items:new Ext.TabPanel({
                region:'center',
                activeTab:0,
                items:[this.productIntegralConversionGrid(), this.productSuperConversionGrid()]
            })
        });
    }
});