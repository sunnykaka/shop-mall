Ext.define('Supplier.store.Statement', {
    //不要忘了继承
    extend: 'Ext.data.Store',
    //记得设置model
    model: 'Supplier.model.Statement',
    proxy: {
        type: 'ajax',
        url: '/product/report',
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