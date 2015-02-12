/**
 * Created with JetBrains WebStorm.
 * Company: Ejs
 * Author: King
 * Date: 13-9-2
 * Time: 下午5:50
 * Information: 验货
 */

Ext.define('Supplier.view.ExamineGood', {
    extend: 'Ext.window.Window',
    alias: 'widget.examineGood',
    id: 'examineGood',
    title: '验货',
    iconCls: 'icon-cart',
    collapsible: true,
    maximizable: true,
    width: 1000,
    height: 450,
    fixed: true,
    layout: 'border',
    modal: true,
    initComponent: function () {

        var order = Ext.create('Ext.grid.Panel', {
            region: 'center',
            height: 150,
            itemId: 'order',
            closeAction: 'destroy',
            forceFit: true,
            viewConfig: {
                enableTextSelection: true
            },
            tbar: [
                { xtype: 'textfield', fieldLabel: '输入扫描物流单号', labelWidth: 110, name: 'order_num', allowBlank: false },
                { xtype: 'button', itemId: 'orderSearch', text: '查询'},
                { xtype: 'displayfield', itemId: 'displayMsg', fieldLabel: '提示', value: '<span style="color:#E47113;">抱歉，查询不到该订单信息！</span>', hideLabel: true, hidden: true}
            ],
            store: Ext.create('Ext.data.Store', {
                fields: ["orderNo", "waybillNumber", "orderState", "deliveryType", "name", "confirmOperator", "customerServiceRemark", "userRemark"],
                proxy: {
                    type: 'ajax',
                    url: '/assets/js/data/examinOrder.json',
                    reader: {
                        type: 'json',
                        successProperty: 'success',
                        root: 'data.list',
                        messageProperty: 'message'
                    }
                },
                idProperty: 'id',
                autoLoad: false
            }),
            columns: [
                {text: '订单编号', dataIndex: 'orderNo'},
                {text: '物流单号', dataIndex: 'waybillNumber'},
                {text: '订单状态', dataIndex: 'orderState'},
                {text: '配送方式', dataIndex: 'deliveryType'},
                {text: '收货人', dataIndex: 'name'},
                {text: '订单跟踪人员', dataIndex: 'confirmOperator'},
                {text: '订单跟踪人员备注', dataIndex: 'customerServiceRemark'},
                {text: '买家留言', dataIndex: 'userRemark'}
            ]
        });

        var good = Ext.create('Ext.grid.Panel', {
            region: 'south',
            itemId: 'good',
            bodyCls: 'colorGrid',
            forceFit: true,
            disableSelection: true,
            split: true,
            hidden: true,
            height: 250,
            autoScroll: true,
            viewConfig: {
                enableTextSelection: true
            },
            tbar: [
                { xtype: 'textfield', itemId: 'goodNum', fieldLabel: '输入商品条形码', allowBlank: false, enableKeyEvents: true },
                { xtype: 'button', itemId: 'goodSearch', text: '查询'},
                { xtype: 'displayfield', itemId: 'trueMsg', hideLabel: true, value: '<span style="color:#669900">扫描成功</span>', hidden: true},
                { xtype: 'displayfield', itemId: 'falseMsg', hideLabel: true, value: '<span style="color:#E47113">未扫描到该商品!</span>', hidden: true},
                '->',
                { xtype: 'displayfield', itemId: 'orderNum', value: '', fieldLabel: '订单编号', labelWidth: 70, hiden: true}
            ],
            store: Ext.create('Ext.data.Store', {
                fields: ["orderNo", "itemNo", "productName", "skuAttribute", "barCode", "categoryName", "unitPrice", "shipmentNum", "stockQuantity", "brandName"],
                proxy: {
                    type: 'ajax',
                    url: '/orders',
                    reader: {
                        type: 'json',
                        successProperty: 'success',
                        root: 'data.list',
                        messageProperty: 'message'
                    }
                },
                idProperty: 'id',
                autoLoad: false
            }),
            columns: [
                {text: '序号', xtype: 'rownumberer', storable: false, width: 40},
                {text: '条形码', dataIndex: 'barCode', width: 180 },
                {text: '商品名称', dataIndex: 'productName', width: 300},
                {text: '订单编号', dataIndex: 'orderNo'},
                {text: '商品编号', dataIndex: 'itemNo'},
                {text: '商品属性', dataIndex: 'skuAttribute'},
                {text: '类别', dataIndex: 'categoryName'},
                {text: '单价', dataIndex: 'unitPrice'},
                {text: '数量', dataIndex: 'shipmentNum', width: 40},
                {text: '库存', dataIndex: 'stockQuantity', width: 40},
                {text: '品牌', dataIndex: 'brandName'}
            ],
            listeners: {
                render: function(){
                    //快捷键
                    /*var mapGood = Ext.create('Ext.util.KeyMap', {
                        target: good,
                        key: Ext.EventObject.ENTER,
                        fn: function(keyCode, ev) {
                            alert('添加B');
                            ev.stopEvent();
                            return false;
                        }
                    });*/
                }
            }
        });

        this.items = [order, good];

        this.callParent(arguments);
    }
});
