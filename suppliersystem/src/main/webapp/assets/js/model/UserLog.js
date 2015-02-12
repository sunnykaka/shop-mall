/**
 * User: Hualei Du
 * Date: 13-10-10
 * Time: 上午9:34
 */
Ext.define('Supplier.model.UserLog', {
    extend: 'Ext.data.Model',
    fields: ['id', 'operator', 'date', 'ip', 'title', 'content'],
    idProperty: 'id'
});