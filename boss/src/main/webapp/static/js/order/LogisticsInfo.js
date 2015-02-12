LogisticsDetail = function (orderId) {
    var logisticsStore = new Ext.data.JsonStore({
        url: 'order/logistics/' + orderId,
        root: 'data.result',
        fields: ['date', 'operator', 'detail']
    });
    logisticsStore.load();
    LogisticsDetail.superclass.constructor.call(this, {
        title: '订单历史状态及物流信息',
        border: false,
        store: logisticsStore,
        autoScroll: true,

        columns: [
            { header: "时间", width: 180, sortable: true, dataIndex: 'date' },
            { header: "处理人", width: 120, sortable: true, dataIndex: 'operator' },
            { header: "信息", width: 480, sortable: true, dataIndex: 'detail' }
        ],
        height: 225,
        loadMask: true,
        tbar: [
            {
                iconCls: 'refresh',
                text: '刷新',
                handler: function () {
                    logisticsStore.reload();
                }
            }
        ]
    });
}
Ext.extend(LogisticsDetail, Ext.grid.GridPanel);