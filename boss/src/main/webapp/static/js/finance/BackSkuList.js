BackSkuDetail = function (backId) {

    var skuStore = new Ext.data.JsonStore({
        url:'order/backOrder/' + backId,
        root:'data.result',
        fields:['productName', 'number', 'skuPrice', 'attribute']
    });

    skuStore.load();

    BackSkuDetail.superclass.constructor.call(this, {
        title:'退货清单',
        border:false,
        store:skuStore,
        columns:[
            {header:"商品名称", width:250, sortable:true, dataIndex:'productName'},
            {header:"商品单价", width:100, sortable:true, dataIndex:'skuPrice'},
            {header:"退货数量", width:100, sortable:true, dataIndex:'number'},
            {header:"商品属性", width:150, sortable:true, dataIndex:'attribute', id:'attribute'}
        ],
        viewConfig:{forceFit:true},
        height:400,
        loadMask:true,
        tbar:[
            {
                iconCls:'refresh',
                text:'刷新',
                handler:function () {
                    skuStore.reload();
                }
            }
        ]
    });
}
Ext.extend(BackSkuDetail, Ext.grid.GridPanel);