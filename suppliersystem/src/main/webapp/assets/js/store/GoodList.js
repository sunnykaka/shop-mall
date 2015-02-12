Ext.define('Supplier.store.GoodList', {
    //不要忘了继承
    extend: 'Ext.data.Store',
    //记得设置model
    model: 'Supplier.model.GoodList',
    proxy: {
        type: 'ajax',
        //url: '/assets/js/data/GoodList.json',
        url: '/orders',

        reader: {
            type: 'json',
            successProperty: 'success',
            root: 'data.list',
            totalProperty: 'data.totalCount',
            messageProperty: 'message'
        }
    },
    autoLoad: false
});