GiftList = function (orderId) {
    var skuStore = new Ext.data.JsonStore({
        url: '/order/' + orderId + '/gift',
        root: 'data.list',
        fields: [ 'id', 'skuId','orderId','operateUserId','number', 'date', 'productId','productName']
    });
    skuStore.load();
    function linkSku(value, p, record){
        return String.format(
            '<b><a href="{0}/product/{1}-{2}" target="_blank">{3}</a>',window.BuyHome,
            record.get('productId'),record.get('skuId'),value);
    }
    GiftList.superclass.constructor.call(this, {
        title: '赠品清单',
        border: false,
        store: skuStore,
        autoScroll: true,
        columns: [
            {dataIndex: 'id', hidden: true, name: 'id'},
            {dataIndex: 'skuId', hidden: true, name: 'skuId'},
            {dataIndex: 'productId', hidden: true, name: 'productId'},
            {dataIndex: 'orderId', hidden: true, name: 'orderId'},
            {dataIndex: 'operateUserId', hidden: true, name: 'operateUserId'},
            {header: "商品名称", width: 350, sortable: true, dataIndex: 'productName',name:'productName',renderer: linkSku},
            {header: "赠送数量", width: 150, sortable: true, dataIndex: 'number'},
            {header: "赠送时间", width: 200, sortable: true, dataIndex: 'date'},
        ],
        height: 400,
        loadMask: true,
        tbar: [
            {
                iconCls: 'refresh',
                text: '刷新',
                handler: function () {
                    skuStore.reload();
                }
            } ,
            {
                text:'删除',
                iconCls:'remove',
                handler:function () {
                    doGridRowDelete(GiftList.instacneGrid, '/order/gift/delete', function () {
                        GiftList.instacneGrid.getStore().reload();
                    });
                }
            }
        ]
    });
}
Ext.extend(GiftList, Ext.grid.GridPanel);