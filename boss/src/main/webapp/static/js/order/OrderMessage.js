OrderMessage = function (orderId) {
    var store = new Ext.data.JsonStore({
        url: 'order/message/' + orderId,
        root: 'data.result',
        fields: [ 'messageInfo', 'id']
    });

    Ext.EJS.Order.orderMessageStore = store;

    var tpl = new Ext.XTemplate(
        '<tpl for="."><div class="view-text">{messageInfo}</div></tpl>'
    );

    var view = new Ext.DataView({
        id: 'OrderMessage',
        store: store,
        tpl: tpl,
        loadingText: '加载进行中....',
        multiSelect: true,
        emptyText: '目前没有备注'
    });

    OrderMessage.superclass.constructor.call(this, {
        id:'OrderMessagePanel',
        title: '备注列表',
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
            store.reload();
        }}
    });
}
Ext.extend(OrderMessage, Ext.Panel);