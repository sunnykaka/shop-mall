Ext.define('Supplier.store.Inventory', {
    //不要忘了继承
    extend: 'Ext.data.Store',
    //记得设置model
    model: 'Supplier.model.Inventory',
    proxy: {
        type: 'ajax',
        url: '/product/stock',
        reader: {
            type: 'json',
            successProperty: 'success',
            root: 'data.list',
            totalProperty: 'data.totalCount',
            messageProperty: 'message'
        }
    },
    autoLoad: true,
    pageSize: 50
});