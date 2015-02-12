StockPrice = function (product) {

    var sm = new Ext.grid.CheckboxSelectionModel();

    this.store = createJsonStore('product/sku/stock/price/list/' + product.get('id'), [
        {
            name:'sku',
            type:'string'
        },
        {
            name:'skuId',
            type:'long'
        },
        {
            name:'price',
            type:'string'
        },
        {
            name:'marketPrice',
            type:'string'
        },
        {
            name:'skuLocation',
            type:'string'
        },
        {
            name:'stockQuantity',
            type:'int'
        },
        {
            name:'tradeMaxNumber',
            type:'int'
        },
        {
            name:'storeId',
            type:'int'
        },
        {
            name:'validStatus',
            type:'string'
        },
        {
            name:'barCode',
            type:'string'
        },
        {
            name:'skuCode',
            type:'string'
        }
    ]);


    // 工具栏
    var tb = new Ext.Toolbar({
        items:[
            {
                icon:'static/images/drop-no.gif',
                text:'sku失效',
                handler:function () {
                    changeSkuType("REMOVED");
                }
            },
            {
                icon:'static/images/drop-yes.gif',
                text:'sku有效',
                handler:function () {
                    changeSkuType("NORMAL");
                }
            },
            {
                iconCls:'refresh',
                text:'刷新',
                handler:function () {
                    Ext.getCmp("stockPriceGrid").getStore().reload();
                }
            }
        ]
    });


    var grid = new Ext.grid.GridPanel({
        id:'stockPriceGrid',
        border:false,
        store:this.store,
        loadMask:true,
        columns:[
            { header:'SkuID', sortable:true, dataIndex:'skuId', width:60 },
            { header:'SKU', sortable:true, dataIndex:'sku', width:180 },
            { header:'市场价', sortable:true, dataIndex:'marketPrice', width:75 },
            { header:'易居价', sortable:true, dataIndex:'price', width:75 },
            { header:'库存', sortable:true, dataIndex:'stockQuantity', width:60 },
            { header:'位置', sortable:true, dataIndex:'skuLocation', width:130 },
            { header:'状态', sortable:true, dataIndex:'validStatus', width:45 }
        ],
        sm:sm,
        tbar:tb,
        region:'center',
        viewConfig:{forceFit:true,
            getRowClass:function (record, index, p, ds) {
                var cls = '';
                if (record.data['validStatus'] == '无效') {
                    cls = 'x-grid-record-red';
                }

                return cls;
            }
        }
    });
    var mark=0;
    grid.on('rowcontextmenu', function (grid, rowIndex, event) {
        event.preventDefault();
        var record = grid.getStore().getAt(rowIndex);
        var skuId = record.get('skuId');
        var productId = product.get('id');
        var items = [
            {
                text:'SKU图片绑定',
                handler:function () {
                    var store = new Ext.data.JsonStore({
                        url:'product/skuImg/' + productId + '/' + skuId,
                        root:'data.pictureList',
                        fields:['id', 'name', 'pictureUrl', 'productId', 'checkde', 'skuId', 'number']
                    });
                    mark=0;
                    store.load();
                    var tpl = new Ext.XTemplate(
                        '<tpl for=".">{notCheckedImg}{checkedImg}',
                        '</tpl>',
                        '<div class="x-clear"></div>'
                    );
                    var dataView = new Ext.DataView({
                        store:store,
                        tpl:tpl,
                        autoHeight:true,
                        multiSelect:true,
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap',
                        emptyText:'该商品没有图片',
                        prepareData:function (data) {
                            var clear='';
                            data.checkedImg= '';
                            data.notCheckedImg= '';
                            if (data.checkde) {
                                if(mark==0){
                                    clear='<hr class="x-clear">';
                                    mark=mark+1
                                }
                                data.checkedImg=clear+
                                    '<div class="thumb-wrap" style="position:relative" id="'
                                    +data.name+'"><img src="static/images/drop-yes.gif" style="position: absolute;right:3px;top:3px;"><div class="thumb"><img src="'
                                    +data.pictureUrl+'?'+(new Date()).getTime()+'"></div><span class="x-editable" style="color:#3ecd2d">'
                                    +data.number+'</span></div>'

                            }else{
                                data.notCheckedImg ='<div class="thumb-wrap" style="position:relative" id="'
                                    +data.name+'"><div class="thumb"><img src="'+data.pictureUrl+'?'
                                    +(new Date()).getTime()+'"></div><span class="x-editable">'
                                    +data.number+'</span></div>'
                            }
                            return data;
                        },
                        listeners:{
                            dblclick:function (dataView, index, node, e) {
                                var select = dataView.getStore().getAt(index);
                                var selectId = select.get('id');
                                FB([
                                    {
                                        name:'id',
                                        value:selectId,
                                        xtype:'hidden'
                                    },
                                    {
                                        fieldLabel:'标题',
                                        name:'num',
                                        value:select.get('number'),
                                        allowBlank:false,
                                        xtype:'numberfield',
                                        regex:/^[+]?[\d]+$/,
                                        regexText:"只能输入正整数",
                                        maxLength:1
                                    }
                                ], 'product/skuImg/updateNum', '修改' + selectId + '图片的编号', false, function () {
                                    mark=0;
                                    store.load();
                                });
                            },
                            scope:this
                        }
                    });

                    var win = new Ext.Window({
                        id:'stockPriceImages-view',
                        width:520,
                        height:400,
                        modal:true,
                        closeAction:'close',
                        title:'SKU图片管理',
                        tbar:[
                            {
                                text:'设置绑定',
                                iconCls:'add',
                                handler:function () {
                                    var ids = [];
                                    if (dataView.getSelectedRecords().length == 0) {
                                        Ext.Msg.alert("信息", "请选择要绑定的图片");
                                        return;
                                    }
                                    Ext.each(dataView.getSelectedRecords(), function (r) {
                                        ids.push(r.get('id'));
                                    });

                                    doAjax('product/skuImg/updateSkuId', function () {
                                        mark=0;
                                        store.load();
                                    }, {
                                        ids:ids,
                                        skuId:skuId,
                                        updateFlag:"add"
                                    }, "确定绑定这些图片吗？");
                                }
                            },
                            '-',
                            {
                                text:'解除绑定',
                                iconCls:'remove',
                                handler:function () {
                                    var ids = [];

                                    if (dataView.getSelectedRecords().length == 0) {
                                        Ext.Msg.alert("信息", "请选择要解除的图片");
                                        return;
                                    }
                                    Ext.each(dataView.getSelectedRecords(), function (r) {
                                        ids.push(r.get('id'));
                                    });

                                    doAjax('product/skuImg/updateSkuId', function () {
                                        mark=0;
                                        store.load();
                                    }, {
                                        ids:ids,
                                        skuId:skuId,
                                        updateFlag:"remove"
                                    }, "确定解除这些图片吗？");
                                }
                            },
                            '-',
                            {

                                iconCls:'refresh',
                                text:'刷新',
                                handler:function () {
                                    mark=0;
                                    store.load();
                                }
                            } ,
                            '-'
                        ],
                        items:dataView
                    });
                    win.show();
                }
            }
        ];
        var treeMenu = new Ext.menu.Menu({
            items:items
        });
        treeMenu.showAt(event.getXY());

    });
    function changeSkuType(status) {

        if (!sm.hasSelection()) {
            Ext.Msg.alert("错误", "请选择要操作的sku");
        }
        else {
            var records = sm.getSelections();
            var ids = [];

            for (var i = 0; i < records.length; i++) {
                ids.push(records[i].get('skuId'));
            }

            doAjax('product/sku/updateState', function () {
                Ext.getCmp("stockPriceGrid").getStore().reload();
            }, { ids:ids, skuState:status});

        }
    }

    grid.on('rowdblclick', function (grid, rowIndex, event) {
        var record = grid.getStore().getAt(rowIndex);
        var storeForSkuStore = new Ext.data.Store({
            autoLoad:true,
            reader:new Ext.data.JsonReader({
                fields:[ 'name', 'id'],
                root:'data.stores'
            }),
            proxy:new Ext.data.HttpProxy({
                url:'product/customer/store/list/' + product.get('customerId'),
                method:'GET'
            }),
            listeners:{
                load:function (store) { //加载完毕设置combox的值，表单中显示的就是数据库中的值
                    var storeId = record.get('storeId');
                    if (storeId != 0) {
                        skuStoreCombo.setValue(storeId);
                    }
                }
            }
        });

        var skuStoreCombo = new Ext.form.ComboBox({
            hiddenName:'productStorageId',
            fieldLabel:'选择库位',
            mode:'remote',
            editable:false,
            triggerAction:'all',
            emptyText:'请选择',
            store:storeForSkuStore,
            displayField:'name',
            valueField:'id',
            allowBlank:false
        });
        var updateSku = function () {
            commitForm(stockPriceForm, function () {
                productGrid.getStore().reload();
                win.hide();
                Ext.getCmp("stockPriceGrid").getStore().reload();
            });
        };

        stockPriceForm = new Ext.FormPanel({
            labelWidth:80,
            baseCls:'x-plain',
            frame:true,
            url:'product/sku/update',
            buttonAlign:'center',
            defaults:{
                width:350
            },
            items:[
                {
                    xtype:'hidden',
                    name:'skuId',
                    value:record.get('skuId')
                },
                skuStoreCombo,
                {
                    fieldLabel:'库存',
                    name:'stockQuantity',
                    value:record.get('stockQuantity'),
                    allowBlank:false,
                    xtype:'numberfield',
                    regex:/^[+]?[\d]+$/,
                    regexText:"只能输入正整数"
                },
                {
                    fieldLabel:'购买上限',
                    name:'tradeMaxNumber',
                    value:record.get('tradeMaxNumber'),
                    allowBlank:false,
                    xtype:'numberfield',
                    regex:/^[+]?[\d]+$/,
                    regexText:"只能输入正整数"
                },
                {
                    fieldLabel:'市场价格',
                    name:'marketPrice',
                    value:record.get('marketPrice'),
                    allowBlank:false,
                    xtype:'numberfield',
                    regex:/^(?:[1-9]+\d*?|0)(.[0-9]{1,10})?$/,
                    regexText:"只能输入正小数或正整数"
                },
                {
                    fieldLabel:'易居尚价格',
                    name:'price',
                    value:record.get('price'),
                    allowBlank:false,
                    xtype:'numberfield',
                    regex:/^(?:[1-9]+\d*?|0)(.[0-9]{1,10})?$/,
                    regexText:"只能输入正整数"
                },
                {
                    fieldLabel:'条形码',
                    name:'barCode',
                    value:record.get('barCode'),
                    allowBlank:false,
                    xtype:'textfield'
                },
                {
                    fieldLabel:'sku编码',
                    name:'skuCode',
                    value:record.get('skuCode'),
                    xtype:'textfield'
                }
            ],
            keys:[
                {
                    key:Ext.EventObject.ENTER,
                    fn:updateSku //执行的方法
                }
            ],
            buttons:[
                {
                    text:'更新',
                    handler:updateSku

                }
            ]
        });

        var win = new Ext.Window({
            title:record.get('sku'),
            width:500,
            height:300,
            layout:'fit',
            plain:true,
            bodyStyle:'padding:5px;',
            items:stockPriceForm
        });
        win.show(this.id);

    });

    this.store.load();

    StockPrice.superclass.constructor.call(this, {
        title:'库存价格',
        layout:'border',
        items:grid
    });
}

Ext.extend(StockPrice, Ext.Panel);