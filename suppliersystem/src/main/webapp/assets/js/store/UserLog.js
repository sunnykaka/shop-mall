/**
 * User: Hualei Du
 * Date: 13-10-10
 * Time: 上午9:36
 */
Ext.define('Supplier.store.UserLog', {
    //不要忘了继承
    extend: 'Ext.data.Store',
    //记得设置model
    model: 'Supplier.model.UserLog',
    proxy: {
        type: 'ajax',
        url: '/log',
        reader: {
            type: 'json',
            successProperty: 'success',
            root: 'data.list',
            totalProperty: 'data.obj',
            messageProperty: 'message'
        }
    },
    autoLoad: true,
    pageSize: 50
});