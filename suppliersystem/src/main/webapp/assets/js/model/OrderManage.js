Ext.define('Supplier.model.OrderManage',
    {
        //不要忘了继承
        extend:'Ext.data.Model',
        fields:['id','orderNo', 'deliveryTypePY','invoice', 'waybillNumber', 'name', 'userName', 'orderState', 'deliveryType', 'storageName', 'totalPrice', 'sendTime', 'customerServiceRemark', 'userRemark', 'createTime', 'payDate'],
        idProperty: 'id'
    }
)