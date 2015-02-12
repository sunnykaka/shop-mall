OrderPriceMessage = function (orderId) {
    var store = createJsonStore('order/priceMessageDetail/' + orderId, [ 'priceMessageDetail']);

    var tpl = new Ext.XTemplate(
        '<tpl for="."><div class="view-text">{priceMessageDetail}</div></tpl>'
    );

    var view = new Ext.DataView({
        store: store,
        tpl: tpl,
        loadingText: '加载进行中....',
        multiSelect: true,
        emptyText: '目前没有订单价格备注'
    });

    OrderPriceMessage.superclass.constructor.call(this, {
        title: '订单价格备注',
        border: false,
        autoScroll: true,
        height: 400,
        items: view,
        tbar: [
            {
                iconCls: 'refresh',
                text: '刷新',
                handler: function () {
                    store.reload();
                }
            }
        ],
        listeners:{'render':function(){
            view.getStore().reload();
        }}
    });
}
Ext.extend(OrderPriceMessage, Ext.Panel);