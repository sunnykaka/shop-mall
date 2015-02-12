ProductDetail = function (orderId) {
    var skuStore = new Ext.data.JsonStore({
        url: 'order/sku/' + orderId,
        root: 'data.result',
        fields: [ 'skuId', 'productId','productName', 'number', 'skuPrice', 'attribute', 'shipmentNum', 'skuState', 'backNumber', 'productCode', 'barCode']
    });
    skuStore.load();
    function linkSku(value, p, record){
        return String.format(
            '<b><a href="{0}/product/{1}-{2}" target="_blank">{3}</a>',window.BuyHome,record.get('productId'),record.get('skuId'),value);
    }
    ProductDetail.superclass.constructor.call(this, {
        title: '商品清单',
        border: false,
        store: skuStore,
        autoScroll: true,
        columns: [
            {dataIndex: 'skuId', hidden: true, name: 'skuId'},
            {dataIndex: 'productId', hidden: true, name: 'productId'},
            {header: "商品名称", width: 250, sortable: true, dataIndex: 'productName',name:'productName',renderer: linkSku},
            {header: "商品编号", width: 100, sortable: true, dataIndex: 'productCode'},
            {header: "条形码", width: 100, sortable: true, dataIndex: 'barCode'},
            {header: "商品单价", width: 100, sortable: true, dataIndex: 'skuPrice'},
            {header: "商品数量", width: 100, sortable: true, dataIndex: 'number'},
            {header: "配送状态", width: 100, sortable: true, dataIndex: 'skuState'},
            {header: "退货数量", width: 100, sortable: true, dataIndex: 'backNumber'},
            {header: "配送数量", width: 100, sortable: true, dataIndex: 'shipmentNum'},
            {header: "商品属性", width: 150, sortable: true, dataIndex: 'attribute', id: 'attribute'}
        ],
        viewConfig: {
            forceFit: true,
            getRowClass: function (record, index, p, ds) {
                var cls = '';
                if (record.data['skuState'] == '退货中') {
                    cls = 'x-grid-record-red';
                }
                if (record.data['number'] != record.data['shipmentNum']) {
                    cls = 'x-grid-record-yellow';
                }
                return cls;
            }},
        height: 400,
        loadMask: true,
        tbar: [
            {
                iconCls: 'refresh',
                text: '刷新',
                handler: function () {
                    skuStore.reload();
                }
            }
        ]
    });
}
Ext.extend(ProductDetail, Ext.grid.GridPanel);