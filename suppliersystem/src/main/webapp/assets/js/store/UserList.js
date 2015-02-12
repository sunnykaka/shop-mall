/**
 * User: Hualei Du
 * Date: 13-10-10
 * Time: 下午2:17
 */
Ext.define('Supplier.store.UserList', {
    //不要忘了继承
    extend: 'Ext.data.Store',
    //记得设置model
    model: 'Supplier.model.UserList',
    proxy: {
        type: 'ajax',
        url: '/account',
        reader: {
            type: 'json',
            successProperty: 'success',
            root: 'data.list',
            messageProperty: 'message'
        }
    },
    autoLoad: true
});