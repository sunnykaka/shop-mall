/**
 * Created with JetBrains WebStorm.
 * Company: Ejs
 * Author: King
 * Date: 13-9-2
 * Time: 下午5:50
 * Information: 店铺所有仓库store
 */

Ext.define('Supplier.store.Storage', {
    //不要忘了继承
    extend: 'Ext.data.Store',

    fields: ['id', 'name'],
    proxy: {
        type: 'ajax',
        url: '/resp',
        reader: {
            type: 'json',
            root: 'data.list'
        }
    },
    autoLoad: true

});