/**
 * User: Hualei Du
 * Date: 13-10-10
 * Time: 下午2:17
 */
Ext.define('Supplier.model.UserList', {
    extend: 'Ext.data.Model',
    fields: ['id', 'accountName', 'normal', 'mainAccount', 'email'],
    idProperty: 'id'
});