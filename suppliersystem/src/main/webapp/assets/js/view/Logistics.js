/**
 * Created with JetBrains WebStorm.
 * Company: Ejs
 * Author: King
 * Date: 13-9-2
 * Time: 下午5:50
 * Information: 物流单设计-View
 */

Ext.define('Supplier.view.Logistics', {
    extend: 'Ext.Panel',
    alias: 'widget.logistics',
    id: 'logistics',
    autoScroll: true,
    border: false,
    title: '物流单设计',
    iconCls: 'icon-design',
    initComponent: function () {

        var createFrame = function (url) {
            return '<iframe scrolling="auto" frameborder="0"  src="' + url + '?' + new Date().getTime() +
                '" style="width:100%;height:99%;"></iframe>';
        };

        this.html = createFrame('/logisticDesign/page');
        this.callParent(arguments);

    }

});

